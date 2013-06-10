package com.peergreen.jndi.internal.factory;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;
import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;

import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleContext;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;

/**
 * A {@code IteratesOnBuildersObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class IteratesOnBuildersObjectFactory implements ObjectFactory {

    protected BundleContext bundleContext;

    public IteratesOnBuildersObjectFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        // Step 5
        // -------------------------------------------------------------------
        // Iterate over the Object Factory Builder services in ranking order.
        // Attempt to use each such service to create an ObjectFactory or
        // DirObjectFactory instance. If this succeeds (non null) then use this
        // ObjectFactory or DirObjectFactory instance to recreate the object. If
        // successful, the algorithm stops here.
        // -------------------------------------------------------------------
        IFindable<ObjectFactoryBuilder> findObjectFactoryBuilder;
        findObjectFactoryBuilder = find(bundleContext, ObjectFactoryBuilder.class)
                                      .orderWith(serviceRanking());
        List<IFind<ObjectFactoryBuilder>> finds = findObjectFactoryBuilder.listFinds();
        for (IFind<ObjectFactoryBuilder> find : finds) {
            ObjectFactoryBuilder builder = find.getService();
            try {
                ObjectFactory factory = builder.createObjectFactory(obj, environment);
                if (factory != null) {
                    // TODO Maybe rework the Exception throwing for the next invocation ?
                    // The spec says that any call to builders should be logged but not fatal
                    // but failed call to factories are fatal
                    Object converted = factory.getObjectInstance(obj, name, nameCtx, environment);
                    if (converted != null) {
                        return converted;
                    }
                }
            } catch (NamingException ne) {
                // TODO Uncomment
//                logger.log(LogService.LOG_WARNING,
//                           "Cannot convert reference using builder: " + builder,
//                           ne);
                // and continue ...
            } finally {
                bundleContext.ungetService(find.getReference());
            }
        }

        return null;

    }
}
