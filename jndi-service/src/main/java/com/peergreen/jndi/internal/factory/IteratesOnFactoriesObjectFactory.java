package com.peergreen.jndi.internal.factory;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;
import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;
import static org.ow2.util.osgi.toolkit.filter.Filters.not;
import static org.ow2.util.osgi.toolkit.filter.Filters.present;

import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code FallbackObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class IteratesOnFactoriesObjectFactory implements ObjectFactory {

    protected BundleContext bundleContext;

    public IteratesOnFactoriesObjectFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Object getObjectInstance(Object refInfo, Name name, Context context, Hashtable<?, ?> table) throws Exception {
        // Step 6
        // -------------------------------------------------------------------
        // If the description was a Reference and without a factory class name
        // specified, or if the description was not of type Reference, then
        // attempt to convert the object with each Object Factory service (or
        // Dir Object Factory service for directories) service in ranking order
        // until a non-null value is returned.
        // -------------------------------------------------------------------
        if (isReferenceWithoutFactoryClassname(refInfo) || !isReference(refInfo)) {
            List<IFind<ObjectFactory>> findList = find(bundleContext, ObjectFactory.class)
                                                   .with(not(present(JNDIConstants.JNDI_URLSCHEME)))
                                                   .orderWith(serviceRanking())
                                                   .listFinds();
            for(IFind<ObjectFactory> find : findList) {
                ObjectFactory factory = find.getService();
                try {
                    Object converted = factory.getObjectInstance(refInfo, name, context, table);
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

        return null;
    }

    /**
     * Return {@code true} if the given Object is a {@code Reference}.
     * @param refInfo tested instance
     * @return {@code true} if the given Object is a {@code Reference}.
     */
    protected boolean isReference(final Object refInfo) {
        return (refInfo instanceof Reference);
    }

    /**
     * Return {@code true} if the given Object is a {@code Reference} but
     * without any specified factory class name.
     * @param refInfo tested instance
     * @return {@code true} if the given Object is a {@code Reference} without
     *         any specified factory class name.
     */
    protected boolean isReferenceWithoutFactoryClassname(final Object refInfo) {

        // If it's not a Reference, quickly exit with false
        if (!isReference(refInfo)) {
            return false;
        }

        // Otherwise test if factory classname was provided
        Reference reference = (Reference) refInfo;
        return (reference.getFactoryClassName() == null);
    }

}
