package com.peergreen.jndi.internal.factory.dir;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;
import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;
import static org.ow2.util.osgi.toolkit.filter.Filters.objectClass;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.factory.FactoryNameSpecifiedObjectFactory;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;

/**
 * A {@code FactoryNameSpecifiedDirObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class FactoryNameSpecifiedDirObjectFactory extends FactoryNameSpecifiedObjectFactory implements DirObjectFactory {

    public FactoryNameSpecifiedDirObjectFactory(BundleContext bundleContext, String factoryName) {
        super(bundleContext, factoryName);
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        // Step 3
        // -------------------------------------------------------------------
        // If a factory class name is specified, the JNDI Provider Admin service
        // uses its own Bundle Context search for a service registered under the
        // Reference’s factory class name. If a matching Object Factory is found
        // then it is used to create the object from the Reference object and the
        // algorithm stops here.
        // -------------------------------------------------------------------
        IFindable<DirObjectFactory> findObjectFactory = find(bundleContext, DirObjectFactory.class)
                                                            .with(objectClass(factoryName))
                                                            .orderWith(serviceRanking());
        IFind<DirObjectFactory> find = findObjectFactory.firstFind();
        if (find != null) {
            DirObjectFactory factory = find.getService();
            try {
                return factory.getObjectInstance(obj, name, nameCtx, environment, attrs);
            } finally {
                bundleContext.ungetService(find.getReference());
            }
        }

        // Fallback on ObjectFactory search
        return getObjectInstance(obj, name, nameCtx, environment);

    }
}
