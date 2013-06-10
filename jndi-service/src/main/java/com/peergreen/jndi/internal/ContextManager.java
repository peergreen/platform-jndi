package com.peergreen.jndi.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIContextManager;
import com.peergreen.jndi.internal.context.AssociatedContext;
import com.peergreen.jndi.internal.context.AssociatedDirContext;
import com.peergreen.jndi.internal.context.DynamicContext;
import com.peergreen.jndi.internal.context.DynamicDirContext;
import com.peergreen.jndi.internal.context.UrlContextFinder;
import com.peergreen.jndi.internal.context.UrlLookupsSupportContext;
import com.peergreen.jndi.internal.context.UrlLookupsSupportDirContext;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.env.Environments;
import com.peergreen.jndi.internal.strategy.ImplementationClassPresentInEnvironmentStrategy;
import com.peergreen.jndi.internal.strategy.NoImplementationClassSpecifiedStrategy;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code ContextManager} is ...
 *
 * @author Guillaume Sauthier
 */
public class ContextManager implements JNDIContextManager, IContextManagerAssociation {

    /**
     * The BundleContext of the caller.
     */
    private BundleContext bundleContext;

    /**
     * All the managed/returned JNDI context for a given Bundle consumer.
     */
    private List<Context> contexts;

    public ContextManager(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.contexts = new ArrayList<Context>();
    }

    /**
     * Creates a new JNDI initial context with the default JNDI environment properties.
     *
     * @return an instance of {@code javax.naming.Context}.
     * @throws javax.naming.NamingException upon any errors that occurs during {@code Context} creation.
     */
    public Context newInitialContext() throws NamingException {
        return newInitialContext(new Environment());
    }

    /**
     * Creates a new JNDI initial context with the specified JNDI environment properties.
     *
     * @param environment given JNDI environment
     * @return an instance of {@code javax.naming.Context}.
     * @throws javax.naming.NamingException upon any errors that occurs during {@code Context} creation.
     */
    @SuppressWarnings("unchecked")
    public Context newInitialContext(final Map environment) throws NamingException {
        return newInitialContext(new Environment(environment));
    }

    /**
     *
     * @param userEnvironment
     * @return
     * @throws NamingException
     */
    private Context newInitialContext(final Environment userEnvironment) throws NamingException {

        // Construct merged Environment
        Environment mergedEnvironment = getEnvironment(userEnvironment, bundleContext);

        // Construct the returned Context
        String initialContextFactoryName = Utils.asString(mergedEnvironment.get(Context.INITIAL_CONTEXT_FACTORY));

        // Creates a dynamic Context (supports backing Context dynamism)
        DynamicContext<Context> dynamicContext = new DynamicContext<Context>();

        UrlLookupsSupportContext<Context> urlSupport = new UrlLookupsSupportContext<Context>(dynamicContext);

        if (!Utils.isNullOrEmpty(initialContextFactoryName)) {
            // Implementation class is present in Environment

            // Configure the strategy
            ImplementationClassPresentInEnvironmentStrategy<Context> strategy = new ImplementationClassPresentInEnvironmentStrategy<Context>(Context.class);
            strategy.setEnvironment(mergedEnvironment);
            strategy.setBundleContext(bundleContext);
            strategy.setFactoryName(initialContextFactoryName);
            dynamicContext.setStrategy(strategy);

            // Eager initialisation
            dynamicContext.start();

        } else {
            // No implementation class specified

            // Configure the strategy
            NoImplementationClassSpecifiedStrategy<Context> strategy = new NoImplementationClassSpecifiedStrategy<Context>(Context.class);
            strategy.setEnvironment(mergedEnvironment);
            strategy.setBundleContext(bundleContext);
            dynamicContext.setStrategy(strategy);

        }

        UrlContextFinder urlContextFinder = new UrlContextFinder();
        urlContextFinder.setBundleContext(bundleContext);
        urlContextFinder.setEnvironment(mergedEnvironment);
        urlSupport.setUrlContextFinder(urlContextFinder);

        return new AssociatedContext<Context>(urlSupport, this);
    }

    /**
     * Creates a new initial {@code DirContext} with the default JNDI environment properties.
     *
     * @return an instance of {@code javax.naming.directory.DirContext}.
     * @throws javax.naming.NamingException upon any errors that occurs during {@code Context} creation.
     */
    public DirContext newInitialDirContext() throws NamingException {
        return newInitialDirContext(new Environment());
    }

    /**
     * Creates a new initial {@code DirContext} with the specified JNDI environment properties.
     *
     * @param environment given JNDI environment
     * @return an instance of {@code javax.naming.directory.DirContext}.
     * @throws javax.naming.NamingException upon any errors that occurs during {@code Context} creation.
     */
    public DirContext newInitialDirContext(Map environment) throws NamingException {
        return newInitialDirContext(new Environment(environment));
    }

    private DirContext newInitialDirContext(Environment userEnvironment) throws NamingException {

        // Construct merged Environment
        Environment mergedEnvironment = getEnvironment(userEnvironment, bundleContext);

        // Construct the returned Context
        String initialContextFactoryName = Utils.asString(mergedEnvironment.get(Context.INITIAL_CONTEXT_FACTORY));

        // Creates a dynamic Context (supports backing Context dynamism)
        DynamicDirContext dynamicContext = new DynamicDirContext();

        UrlLookupsSupportDirContext urlSupport = new UrlLookupsSupportDirContext(dynamicContext);

        if (!Utils.isNullOrEmpty(initialContextFactoryName)) {
            // Implementation class is present in Environment

            // Configure the strategy
            ImplementationClassPresentInEnvironmentStrategy<DirContext> strategy = new ImplementationClassPresentInEnvironmentStrategy<DirContext>(DirContext.class);
            strategy.setEnvironment(mergedEnvironment);
            strategy.setBundleContext(bundleContext);
            strategy.setFactoryName(initialContextFactoryName);
            dynamicContext.setStrategy(strategy);

            // Eager initialisation
            dynamicContext.start();

        } else {
            // No implementation class specified

            // Configure the strategy
            NoImplementationClassSpecifiedStrategy<DirContext> strategy = new NoImplementationClassSpecifiedStrategy<DirContext>(DirContext.class);
            strategy.setEnvironment(mergedEnvironment);
            strategy.setBundleContext(bundleContext);
            dynamicContext.setStrategy(strategy);

        }

        UrlContextFinder urlContextFinder = new UrlContextFinder();
        urlContextFinder.setBundleContext(bundleContext);
        urlContextFinder.setEnvironment(mergedEnvironment);
        urlSupport.setUrlContextFinder(urlContextFinder);

        return new AssociatedDirContext(urlSupport, this);
    }

    /**
     * Compute an {@code Environment} from the following parameters:
     * <ul>
     *  <li>User's provided Environment.</li>
     *  <li>Environment obtained from the given BundleContext.</li>
     *  <li>Environment obtained from the System properties.</li>
     * </ul>
     * @param userEnvironment User's provided environment
     * @param bundleContext Caller's BundleContext
     * @return a merged {@code Environment} obtained from parameters
     */
    private Environment getEnvironment(final Environment userEnvironment, final BundleContext bundleContext) {

        Environment systemEnvironment = Environments.getSystemEnvironment();
        Environment bundleEnvironment = Environments.getBundleEnvironment(bundleContext);

        // Priorities are the following:
        // 1. user's environment
        // 2. system's environment
        // 3. caller bundle's environment
        return mergeEnvironments(userEnvironment, systemEnvironment, bundleEnvironment);
    }

    /**
     * Merge the given {@code Environment}s. The order of parameters is important: higher priority
     * items in first place to lower priorities as last items.
     * @param environments environments to be merged
     * @return a merged Environment
     */
    private Environment mergeEnvironments(final Environment... environments) {

        // Initialize with an empty environment
        Environment mergedEnvironment = new Environment();

        // Reverse the array
        // We want lower priority first
        List<Environment> listOfEnvironments = Arrays.asList(environments);
        Collections.reverse(listOfEnvironments);

        // Do the merge (from less priority to top priority)
        for(Environment environment : listOfEnvironments) {
            mergedEnvironment.merge(environment);
        }

        return mergedEnvironment;
    }

    /**
     * Release all resources detained by this ContextManager.
     * Remember that this instance is dedicated to a consumer Bundle.
     */
    public void release() {

        // Copy the list content to avoid ConcurrentModificationException
        // since close() may call dissociate that remove the Context from contexts
        List<Context> copy = new ArrayList<Context>(contexts);

        // Force closing of all the returned contexts
        for (Context context : copy) {
            try {
                context.close();
            } catch (NamingException e) {
                // Ignored, move to next Context
            }
        }

        contexts.clear();
    }

    public void associate(Context context) {
        this.contexts.add(context);
    }

    public void dissociate(Context context) {
        this.contexts.remove(context);
    }
}
