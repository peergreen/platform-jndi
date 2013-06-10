package com.peergreen.jndi.internal.factory;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;
import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;
import static org.ow2.util.osgi.toolkit.filter.Filters.not;
import static org.ow2.util.osgi.toolkit.filter.Filters.objectClass;
import static org.ow2.util.osgi.toolkit.filter.Filters.present;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;

/**
 * A {@code FactoryNameSpecifiedObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class FactoryNameSpecifiedObjectFactory implements ObjectFactory {

    protected BundleContext bundleContext;
    protected String factoryName;

    public FactoryNameSpecifiedObjectFactory(BundleContext bundleContext, String factoryName) {
        this.bundleContext = bundleContext;
        this.factoryName = factoryName;
    }

    public Object getObjectInstance(Object reference, Name name, Context context, Hashtable<?, ?> table) throws Exception {
        // Step 3
        // -------------------------------------------------------------------
        // If a factory class name is specified, the JNDI Provider Admin service
        // uses its own Bundle Context search for a service registered under the
        // Reference’s factory class name. If a matching Object Factory is found
        // then it is used to create the object from the Reference object and the
        // algorithm stops here.
        // -------------------------------------------------------------------
        IFindable<ObjectFactory> findObjectFactory = find(bundleContext, ObjectFactory.class)
                                                            .with(objectClass(factoryName))
                                                            .with(not(present(JNDIConstants.JNDI_URLSCHEME)))
                                                            .orderWith(serviceRanking());
        IFind<ObjectFactory> find = findObjectFactory.firstFind();
        if (find != null) {
            ObjectFactory factory = find.getService();
            try {
                return factory.getObjectInstance(reference, name, context, table);
            } finally {
                bundleContext.ungetService(find.getReference());
            }
        }

        return null;
    }
}
