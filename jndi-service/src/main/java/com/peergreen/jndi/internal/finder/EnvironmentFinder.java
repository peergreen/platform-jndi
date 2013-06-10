package com.peergreen.jndi.internal.finder;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.IBundleContextFinder;
import com.peergreen.jndi.internal.env.Environment;

/**
 * A {@code EnvironmentFinder} is responsible to find a {@code BundleContext} provided by the caller.<br/>
 * See JNDI Service Specification §126.7.3:
 * <p>
 *  Look in the JNDI environment properties for a property called {@code osgi.service.jndi.bundleContext}
 *  If a value for this property exists then use it as the Bundle Context.
 * </p>
 *
 * @see org.osgi.service.jndi.JNDIConstants.BUNDLE_CONTEXT
 * @author Guillaume Sauthier
 */
public class EnvironmentFinder implements IBundleContextFinder {

    /**
     * Search-able environment.
     */
    private Environment environment;

    public EnvironmentFinder(final Environment environment) {
        this.environment = environment;
    }

    /**
     * Tries to find a {@code BundleContext}.
     *
     * @return a {@code BundleContext} or <tt>null</tt> if none was found.
     */
    public BundleContext findContext() {
        BundleContext found = null;

        Object context = environment.get(JNDIConstants.BUNDLE_CONTEXT);
        if (context instanceof BundleContext) {
            found = (BundleContext) context;
        }

        return found;
    }
}
