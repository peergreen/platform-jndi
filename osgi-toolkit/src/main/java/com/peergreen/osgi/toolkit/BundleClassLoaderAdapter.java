package com.peergreen.osgi.toolkit;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * A {@code BundleClassLoaderAdapter} is ...
 *
 * @author Guillaume Sauthier
 */
public class BundleClassLoaderAdapter extends ClassLoader implements BundleReference {

    /**
     * Underlying Bundle.
     */
    private Bundle bundle;

    public BundleClassLoaderAdapter(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Returns the <code>Bundle</code> object associated with this
     * <code>BundleReference</code>.
     *
     * @return The <code>Bundle</code> object associated with this
     *         <code>BundleReference</code>.
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Load the given Class using the underlying Bundle.
     *
     * @param name The binary name of the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class could not be found
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return bundle.loadClass(name);
    }

    /**
     * Find the given resource using the underlying Bundle.
     *
     * @param name The resource name
     * @return A <tt>URL</tt> object for reading the resource, or
     *         <tt>null</tt> if the resource could not be found
     */
    @Override
    protected URL findResource(String name) {
        return bundle.getResource(name);
    }

    /**
     * Find a collection of resource with the given name using the underlying Bundle.
     *
     * @param name The resource name
     * @return An enumeration of {@link java.net.URL <tt>URL</tt>} objects for
     *         the resources
     * @throws IOException If I/O errors occur
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Enumeration<URL> findResources(String name) throws IOException {
        return bundle.getResources(name);
    }
}
