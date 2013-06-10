package com.peergreen.jndi.internal.finder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import com.peergreen.jndi.internal.IBundleContextFinder;

/**
 * A {@code ThreadContextClassLoaderFinder} is responsible to find a {@code BundleContext} from
 * the Thread's context ClassLoader.<br/>
 * See JNDI Service Specification §126.7.3:
 * <p>
 *  Obtain the Thread Context Class Loader; if it, or an ancestor class loader, implements the
 *  {@code BundleReference} interface, call its {@code getBundle} method to get the client’s
 *  {@code Bundle}; then call {@code getBundleContext} on the {@code Bundle} object to get the
 *  client’s Bundle Context.
 * </p>
 *
 * @author Guillaume Sauthier
 */
public class ClassLoaderFinder implements IBundleContextFinder {

    /**
     * Loader to be inspected.
     */
    private ClassLoader classLoader;

    public ClassLoaderFinder(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Tries to find a {@code BundleContext}.
     *
     * @return a {@code BundleContext} or <tt>null</tt> if none was found.
     */
    public BundleContext findContext() {

        ClassLoader tested = classLoader;
        do {

            // The loader is an OSGi loader ...
            if (BundleReference.class.isAssignableFrom(tested.getClass())) {

                // ... we can find the BundleContext from it
                BundleReference bundleReference = (BundleReference) tested;
                return bundleReference.getBundle().getBundleContext();
            }

            // Tested loader was not OSGi aware, try its parent
            tested = tested.getParent();

        } while (tested != null);

        // No BundleContext found
        return null;
    }
}
