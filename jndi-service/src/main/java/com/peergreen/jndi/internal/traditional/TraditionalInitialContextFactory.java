package com.peergreen.jndi.internal.traditional;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;

import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIContextManager;
import com.peergreen.jndi.internal.IBundleContextFinder;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.finder.DefaultContextFinder;
import com.peergreen.jndi.internal.util.Utils;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code TraditionalInitialContextFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class TraditionalInitialContextFactory implements InitialContextFactory {

    /**
     * Is this factory respecting strictly the spec or not ?
     * If not, it is more lenient and allow Bundle to be in STARTING and STOPPING state.
     */
    private boolean strict = false;

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        Map<String, Object> env = Utils.asMap(environment);

        IFind<JNDIContextManager> manager = findContextManager(env);
        Context returned = manager.getService().newInitialContext(env);
        if (returned instanceof DirContext) {
            return new TraditionalDirContext(manager, (DirContext) returned);
        }
        return new TraditionalContext(manager, returned);

        // Note: The manager is never released, so the returned object contains
        // a reference to the ContextManager instance.
        // If we force release at this time, we get a loop: "ServiceFactory.ungetService() resulted in a cycle."
    }

    /**
     * Find JNDI ContextManager service in behalf of the caller's BundleContext (if any)
     * @param env user environment
     * @return the found ContextManager
     * @throws javax.naming.NoInitialContextException if no BundleContext cannot be found, or
     *         if the Bundle is not ACTIVE, or if no JNDIContextManager service is found.
     */
    private IFind<JNDIContextManager> findContextManager(final Map<String, Object> env) throws NoInitialContextException {
        // Find a valid BundleContext
        BundleContext bundleContext = finBundleContext(env);

        // Find the manager in behalf of the caller BundleContext
        IFind<JNDIContextManager> manager = find(bundleContext, JNDIContextManager.class).firstFind();
        if (manager == null) {
            throw new NoInitialContextException("Cannot find any JNDIContextManager service.");
        }
        return manager;
    }

    /**
     * Find the client's BundleContext from the given environment.
     * @param env JNDI env
     * @return BundleContext of the client
     * @throws NoInitialContextException if no BundleContext found or if Bundle is not in the required state.
     */
    private BundleContext finBundleContext(Map<String, Object> env) throws NoInitialContextException {
        IBundleContextFinder finder = new DefaultContextFinder(new Environment(env));
        BundleContext bundleContext = finder.findContext();
        if (bundleContext == null) {
            throw new NoInitialContextException("Cannot find any BundleContext in given environment, in TCCL or in execution stack.");
        }

        // Check Bundle's state before returning
        Bundle bundle = bundleContext.getBundle();
        if (strict) {
            // The spec says (§126.7.1) that the found BundleContext should be ACTIVE
            if (bundle.getState() != Bundle.ACTIVE) {
                throw new NoInitialContextException("Found BundleContext (id=" + bundleContext.getBundle().getBundleId()
                        + ") is not ACTIVE.");
            }
        } else {
            // Lenient mode: accept STARTING and STOPPING states in addition to ACTIVE
            if ((bundle.getState() & (Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING)) == 0) {
                throw new NoInitialContextException("Found BundleContext (id=" + bundleContext.getBundle().getBundleId()
                        + ") is not STARTING|ACTIVE|STOPPING.");
            }
        }

        return bundleContext;
    }

}
