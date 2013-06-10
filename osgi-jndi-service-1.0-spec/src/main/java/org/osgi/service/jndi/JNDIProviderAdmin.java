package org.osgi.service.jndi;

import java.util.Map;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;

/**
 * This interface defines the OSGi service interface for the {@code JNDIProviderAdmin} service.
 * This service provides the ability to resolve JNDI {@code Reference}s in a dynamic fashion
 * that does not requires call to {@link javax.naming.spi.NamingManager#getObjectInstance()}.
 * The methods of this service provide similar reference resolution, but rely on the OSGi
 * Service Registry in order to find {@code ObjectFactory} instances that can convert a
 * {@code Reference} to an {@code Object}. This service will typically be used by OSGi-aware
 * JNDI Service Providers.
 *
 * @see javax.naming.Reference
 * @see javax.naming.spi.NamingManager
 *
 * @concurreny Thread-safe
 * @author Guillaume Sauthier
 * @since 1.0
 */
public interface JNDIProviderAdmin {

    /**
     * Resolve the object from the given reference.
     * @param reference reference info (may be a {@code Reference})
     * @param name the JNDI name associated with this reference
     * @param context the JNDI context associated with this reference
     * @param environment the JNDI environment associated with this reference
     * @return an {@code Object} based on the reference passed in, or the original reference object
     *         if the reference could not be resolved.
     * @throws Exception in the event that an error occurs while attempting to resolve the JNDI reference.
     */
    Object getObjectInstance(Object reference, Name name, Context context, Map environment) throws Exception;

    /**
     * Resolve the object from the given reference.
     * @param reference reference info (may be a {@code Reference})
     * @param name the JNDI name associated with this reference
     * @param context the JNDI context associated with this reference
     * @param environment the JNDI environment associated with this reference
     * @param attributes the naming attributes to use when resolving this object 
     * @return an {@code Object} based on the reference passed in, or the original reference object
     *         if the reference could not be resolved.
     * @throws Exception in the event that an error occurs while attempting to resolve the JNDI reference.
     */
    Object getObjectInstance(Object reference, Name name, Context context, Map environment, Attributes attributes) throws Exception;
}
