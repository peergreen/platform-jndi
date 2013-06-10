package com.peergreen.jndi.internal;

import org.osgi.framework.BundleContext;

/**
 * A {@code IBundleContextFinder} is ...
 *
 * @author Guillaume Sauthier
 */
public interface IBundleContextFinder {

    /**
     * Tries to find a {@code BundleContext}.
     * @return a {@code BundleContext} or <tt>null</tt> if none was found.
     */
    BundleContext findContext();
}
