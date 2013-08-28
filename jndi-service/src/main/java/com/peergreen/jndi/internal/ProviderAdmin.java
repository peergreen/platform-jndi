package com.peergreen.jndi.internal;

import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIProviderAdmin;
import org.osgi.service.log.LogService;
import com.peergreen.jndi.internal.factory.CompositeObjectFactoryBuilder;
import com.peergreen.jndi.internal.factory.dir.CompositeDirObjectFactoryBuilder;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code ProviderAdmin} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class ProviderAdmin implements JNDIProviderAdmin {

    /**
     * Marker only interface to ensure JNDI system is started.
     */
    @Requires
    private Started started;

    @Requires
    private LogService logger;

    /**
     * BundleContext used to search factories and builders.
     */
    private BundleContext bundleContext;

    public ProviderAdmin(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Resolve the object from the given reference.
     *
     * @param refInfo   reference info (may be a {@code Reference})
     * @param name        the JNDI name associated with this reference
     * @param context     the JNDI context associated with this reference
     * @param environment the JNDI environment associated with this reference
     * @return an {@code Object} based on the reference passed in, or the original reference object
     *         if the reference could not be resolved.
     * @throws Exception in the event that an error occurs while attempting to resolve the JNDI reference.
     */
    public Object getObjectInstance(final Object refInfo,
                                    final Name name,
                                    final Context context,
                                    final Map environment) throws Exception {

        // Needed for all JNDI operations
        Hashtable<?, ?> table = null;
        if (environment != null) {
            table = Utils.asHashtable(environment);
        }

        CompositeObjectFactoryBuilder builder = new CompositeObjectFactoryBuilder(bundleContext);
        ObjectFactory factory = builder.createObjectFactory(refInfo, table);

        return factory.getObjectInstance(refInfo, name, context, table);

    }

    /**
     * Resolve the object from the given reference.
     *
     * @param reference   reference info (may be a {@code Reference})
     * @param name        the JNDI name associated with this reference
     * @param context     the JNDI context associated with this reference
     * @param environment the JNDI environment associated with this reference
     * @param attributes  the naming attributes to use when resolving this object
     * @return an {@code Object} based on the reference passed in, or the original reference object
     *         if the reference could not be resolved.
     * @throws Exception in the event that an error occurs while attempting to resolve the JNDI reference.
     */
    public Object getObjectInstance(final Object reference,
                                    final Name name,
                                    final Context context,final Map environment,
                                    final Attributes attributes) throws Exception {

        // Needed for all JNDI operations
        Hashtable<?, ?> table = null;
        if (environment != null) {
            table = Utils.asHashtable(environment);
        }

        CompositeDirObjectFactoryBuilder builder = new CompositeDirObjectFactoryBuilder(bundleContext);
        DirObjectFactory factory = (DirObjectFactory) builder.createObjectFactory(reference, table);

        return factory.getObjectInstance(reference, name, context, table, attributes);
    }

}
