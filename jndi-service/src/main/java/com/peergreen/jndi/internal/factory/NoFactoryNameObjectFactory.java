package com.peergreen.jndi.internal.factory;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;
import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;
import static org.ow2.util.osgi.toolkit.filter.Filters.equal;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.util.Utils;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;
import org.ow2.util.osgi.toolkit.filter.IFilter;

/**
 * A {@code FactoryNameSpecifiedObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class NoFactoryNameObjectFactory implements ObjectFactory {

    /**
     * Reference Address of Type 'URL'.
     */
    private static final String URL_ADDRESS_TYPE = "URL";

    private BundleContext bundleContext;

    public NoFactoryNameObjectFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Object getObjectInstance(Object obj, Name name, Context context, Hashtable<?, ?> table) throws Exception {

        assert (obj instanceof Reference);
        Reference reference = (Reference) obj;

        // Step 4
        // -------------------------------------------------------------------
        // If no factory class name is specified, iterate over all the Reference
        // object’s StringRefAddrs objects with the address type of URL. For each
        // matching address type, use the value to find a matching URL Context,
        // see URL Context Provider on page 393, and use it to recreate the object.
        // See the Naming Manager for details. If an object is created then it is
        // returned and the algorithm stops here.
        // -------------------------------------------------------------------
        List<RefAddr> addresses = Collections.list(reference.getAll());
        for (RefAddr address : addresses) {
            if (address instanceof StringRefAddr) {
                StringRefAddr stringAddress = (StringRefAddr) address;
                if (URL_ADDRESS_TYPE.equals(stringAddress.getType())) {
                    String url = (String) stringAddress.getContent();
                    String scheme = Utils.getScheme(url);
                    if (scheme != null) {
                        IFilter urlSchemeFilter = equal(JNDIConstants.JNDI_URLSCHEME, scheme);
                        IFindable<ObjectFactory> findObjectFactory = find(bundleContext, ObjectFactory.class)
                                                                            .with(urlSchemeFilter)
                                                                            .orderWith(serviceRanking());
                        IFind<ObjectFactory> find = findObjectFactory.firstFind();
                        if (find != null) {
                            ObjectFactory objectFactory = find.getService();
                            try {
                                Object converted = objectFactory.getObjectInstance(url, name, context, table);
                                if (converted != null) {
                                    return converted;
                                }
                            } finally {
                                bundleContext.ungetService(find.getReference());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
