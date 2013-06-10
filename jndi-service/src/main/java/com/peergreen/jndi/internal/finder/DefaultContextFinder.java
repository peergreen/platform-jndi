package com.peergreen.jndi.internal.finder;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.env.Environment;

/**
 * A {@code DefaultContextFinder} is ...
 *
 * @author Guillaume Sauthier
 */
public class DefaultContextFinder extends AggregateFinder {

    /**
     * A BundleContextFinder can be used only once.
     */
    private boolean used = false;

    public DefaultContextFinder(final Environment environment) {
        super();

        // Spec mandates us to search for a BundleContext in:
        // 1. Environment
        // 2. Thread Context ClassLoader of the current Thread
        // 3. Execution stack of the current Thread
        EnvironmentFinder envFinder = new EnvironmentFinder(environment);
        ClassLoaderFinder loaderFinder = new ClassLoaderFinder(Thread.currentThread().getContextClassLoader());
        ExecutionStackFinder executionFinder = new ExecutionStackFinder(Thread.currentThread());

        // Aggregate all theses finders
        getFinders().add(envFinder);
        getFinders().add(loaderFinder);
        getFinders().add(executionFinder);
    }

    @Override
    public BundleContext findContext() {
        assert !used : "DefaultBundleContext can only be used once (relative to the current execution stack)";
        return super.findContext();
    }
}
