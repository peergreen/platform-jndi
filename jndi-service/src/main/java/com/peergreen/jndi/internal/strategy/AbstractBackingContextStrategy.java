package com.peergreen.jndi.internal.strategy;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.IBackingContextStrategy;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code AbstractBackingContextStrategy} is ...
 *
 * @author Guillaume Sauthier
 */
public abstract class AbstractBackingContextStrategy<T extends Context> implements IBackingContextStrategy<T> {
    /**
     * BundleContext used to perform search.
     */
    private BundleContext bundleContext;

    /**
     * JNDI environment.
     */
    private Environment environment;

    protected IFind<InitialContextFactory> factoryFind;
    protected IFind<InitialContextFactoryBuilder> builderFind;

    /**
     * Backing Context.
     */
    private T backingContext;

    public void setBundleContext(final BundleContext context) {
        this.bundleContext = context;
    }

    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public T getBackingContext() throws NamingException {

        assert bundleContext != null;
        assert environment != null;

        if (needAnotherBackingContext()) {
            backingContext = findBackingContext();
        }

        return backingContext;
    }

    private boolean needAnotherBackingContext() {
        // Should we re-get the service ?
        boolean performLookupAgain = false;

        if (backingContext != null) {
            // Ensure that the service is still available
            if (factoryFind!= null) {
                Object service = factoryFind.getService();
                if (service == null) {
                    // Service disappear
                    performLookupAgain = true;
                }
            }
            if (builderFind != null) {
                Object service = builderFind.getService();
                if (service == null) {
                    // Service disappear
                    performLookupAgain = true;
                }
            }
        } else {
            // No service, try to retrieve it
            performLookupAgain = true;
        }
        return performLookupAgain;
    }

    protected abstract T findBackingContext() throws NamingException;

    public void releaseBackingContext() {
        backingContext = null;
        if (factoryFind != null) {
            factoryFind.release();
        }
        if (builderFind != null) {
            builderFind.release();
        }
    }
}
