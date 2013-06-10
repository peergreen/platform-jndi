package com.peergreen.jndi.internal.builtin.osgi;

import javax.naming.NameClassPair;

/**
 * A {@code OSGiNameClassPair} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiNameClassPair extends NameClassPair {
    public OSGiNameClassPair(final Long serviceId,
                             final String className) {
        super(String.valueOf(serviceId), className);
    }
}
