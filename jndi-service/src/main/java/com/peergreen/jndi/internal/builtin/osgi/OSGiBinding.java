package com.peergreen.jndi.internal.builtin.osgi;

import javax.naming.Binding;

/**
 * A {@code OSGiBinding} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiBinding extends Binding {
    public OSGiBinding(final Long serviceId,
                       final String className,
                       final Object obj) {
        // The name is the OSGi <service.id> property value, so it is not a relative binding
        super(String.valueOf(serviceId), className, obj, false);
    }
}
