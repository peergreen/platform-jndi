package com.peergreen.jndi.internal.context;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;

import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.util.Utils;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.impl.Finders;
import org.ow2.util.osgi.toolkit.filter.Filters;

public class UrlContextFinder {

    /**
     * BundleContext used for search.
     */
    private BundleContext bundleContext;

    /**
     * Environment of the hosting Context.
     */
    private Environment environment;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Context find(final String name) throws NamingException {

        assert bundleContext != null;
        assert environment != null;

        if (name != null) {

            // Find if this is an URL context
            int indexOfColon = name.indexOf(':');
            if (indexOfColon != -1) {
                // There is an URL-like Context
                String scheme = name.substring(0, indexOfColon);
                // Is it empty ?
                if (!Utils.isNullOrEmpty(scheme)) {

                    // Find an URL Context Factory (ObjectFactory) for the given scheme
                    List<IFind<ObjectFactory>> finds = Finders.find(bundleContext, ObjectFactory.class)
                            .with(Filters.equal(JNDIConstants.JNDI_URLSCHEME, scheme))
                            .orderWith(serviceRanking())
                            .listFinds();

                    for (IFind<ObjectFactory> find : finds) {
                        ObjectFactory factory = find.getService();
                        try {
                            Object object = factory.getObjectInstance(null, null, null, environment.getAsHashtable());
                            if (object instanceof Context) {
                                // TODO Should we Proxify this reference ?
                                return (Context) object;
                            }
                        } catch (Exception e) {
                            // TODO Log or Exception ?
                            NamingException ne = new NamingException("Cannot construct URL Context for scheme '" + scheme + "'");
                            ne.initCause(e);
                            throw ne;
                        } finally {
                            bundleContext.ungetService(find.getReference());
                        }
                    }
                }
            }
        }

        return null;

    }
}