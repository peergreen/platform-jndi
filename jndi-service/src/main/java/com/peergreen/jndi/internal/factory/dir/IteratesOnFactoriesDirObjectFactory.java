package com.peergreen.jndi.internal.factory.dir;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;
import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;

import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.factory.IteratesOnFactoriesObjectFactory;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code IteratesOnFactoriesDirObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class IteratesOnFactoriesDirObjectFactory extends IteratesOnFactoriesObjectFactory implements DirObjectFactory {
    public IteratesOnFactoriesDirObjectFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    public Object getObjectInstance(Object refInfo, Name name, Context context, Hashtable<?, ?> table, Attributes attrs) throws Exception {
        // Step 6
        // -------------------------------------------------------------------
        // If the description was a Reference and without a factory class name
        // specified, or if the description was not of type Reference, then
        // attempt to convert the object with each Object Factory service (or
        // Dir Object Factory service for directories) service in ranking order
        // until a non-null value is returned.
        // -------------------------------------------------------------------
        if (isReferenceWithoutFactoryClassname(refInfo) || !isReference(refInfo)) {
            List<IFind<DirObjectFactory>> findList = find(bundleContext, DirObjectFactory.class)
                                                   .orderWith(serviceRanking())
                                                   .listFinds();
            for(IFind<DirObjectFactory> find : findList) {
                DirObjectFactory factory = find.getService();
                try {
                    Object converted = factory.getObjectInstance(refInfo, name, context, table, attrs);
                    if (converted != null) {
                        return converted;
                    }
                } catch (Throwable throwable) {
                    // TODO uncomment
//                    logger.log(LogService.LOG_DEBUG,
//                               "Cannot convert reference using ObjectFactory: " + factory,
//                               throwable);
                    // and continue ...
                } finally {
                    bundleContext.ungetService(find.getReference());
                }
            }
        }

        return getObjectInstance(refInfo, name, context, table);

    }
}
