package com.peergreen.jndi.internal.strategy;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.mock.naming.ControllableInitialContextFactory;
import com.peergreen.jndi.internal.mock.naming.ControllableInitialContextFactoryBuilder;
import com.peergreen.jndi.internal.mock.naming.MockContext;
import com.peergreen.jndi.internal.mock.osgi.AssignableMockServiceReference;
import org.springframework.osgi.mock.MockBundleContext;
import org.springframework.osgi.mock.MockServiceReference;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A {@code InitialContextFactoryBackingStrategyTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class ImplementationClassPresentInEnvironmentStrategyTestCase {

    private ImplementationClassPresentInEnvironmentStrategy<Context> strategy;

    @BeforeMethod
    public void setUp() throws Exception {
        strategy = new ImplementationClassPresentInEnvironmentStrategy<Context>(Context.class);
        strategy.setEnvironment(new Environment());
    }

    @Test(expectedExceptions = NoInitialContextException.class)
    public void testNoBackingContextFound() throws Exception {
        strategy.setBundleContext(new MockBundleContext());
        strategy.setFactoryName("unavailable.ICF");
        strategy.getBackingContext();
    }

    @Test
    public void testRequestedFactoryAvailable() throws Exception {

        // Prepare BundleContext
        // -----------------------------------------------------------

        // 1. Prepare the ServiceReference(s) returned by the BundleContext
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_ID, 1l);
        final MockServiceReference reference = new AssignableMockServiceReference(properties);

        // 2. Create the service instance
        final ControllableInitialContextFactory factory = new ControllableInitialContextFactory(new MockContext("one"));

        // 3. Create a custom BundleContext
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference};
            }

            @Override
            public Object getService(ServiceReference reference) {
                return factory;
            }
        };

        // Prepare the Strategy
        // -----------------------------------------------------------
        strategy.setBundleContext(bundleContext);
        strategy.setFactoryName(ControllableInitialContextFactory.class.getName());

        // Tests
        // -----------------------------------------------------------

        Context backing = strategy.getBackingContext();

        Assert.assertNotNull(backing);
        Assert.assertTrue(backing instanceof MockContext);
        Assert.assertEquals(((MockContext) backing).getId(), "one");

    }
    @Test
    public void testRequestedFactoryMultipleAvailables() throws Exception {

        // Prepare BundleContext
        // -----------------------------------------------------------

        // 1. Prepare the ServiceReference(s) returned by the BundleContext
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 1l);
        final MockServiceReference reference1 = new AssignableMockServiceReference(properties1);
        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 2l);
        final MockServiceReference reference2 = new AssignableMockServiceReference(properties2);

        // 2. Create the service instance(s)
        final ControllableInitialContextFactory factory1 = new ControllableInitialContextFactory(null);
        final ControllableInitialContextFactory factory2 = new ControllableInitialContextFactory(new MockContext("one"));

        // 3. Create a custom BundleContext
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference2, reference1};
            }

            @Override
            public Object getService(ServiceReference reference) {
                if (reference.equals(reference1)) {
                    return factory1;
                } else {
                    return factory2;
                }
            }
        };

        // Prepare the Strategy
        // -----------------------------------------------------------
        strategy.setBundleContext(bundleContext);
        strategy.setFactoryName(ControllableInitialContextFactory.class.getName());

        // Tests
        // -----------------------------------------------------------
        //
        // The purpose of this test is to check that if multiple ICF are available,
        // it will use the first one that return a NON NULL Context.

        Context backing = strategy.getBackingContext();

        Assert.assertNotNull(backing);
        Assert.assertTrue(backing instanceof MockContext);
        Assert.assertEquals(((MockContext) backing).getId(), "one");

    }

    @Test
    public void testRequestedFactoryMultipleAvailablesInverted() throws Exception {

        // Prepare BundleContext
        // -----------------------------------------------------------

        // 1. Prepare the ServiceReference(s) returned by the BundleContext
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 3l);
        final MockServiceReference reference1 = new AssignableMockServiceReference(properties1);
        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 2l);
        final MockServiceReference reference2 = new AssignableMockServiceReference(properties2);

        // 2. Create the service instance(s)
        final ControllableInitialContextFactory factory1 = new ControllableInitialContextFactory(new MockContext("one"));
        final ControllableInitialContextFactory factory2 = new ControllableInitialContextFactory(new MockContext("two"));

        // 3. Create a custom BundleContext
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference1, reference2};
            }

            @Override
            public Object getService(ServiceReference reference) {
                if (reference.equals(reference1)) {
                    return factory1;
                } else {
                    return factory2;
                }
            }
        };

        // Prepare the Strategy
        // -----------------------------------------------------------
        strategy.setBundleContext(bundleContext);
        strategy.setFactoryName(ControllableInitialContextFactory.class.getName());

        // Tests
        // -----------------------------------------------------------
        //
        // The purpose of this test is to check that if multiple ICF are available,
        // it will use the one ranked highest (#2 in our case because it has the lower service.id).

        Context backing = strategy.getBackingContext();

        Assert.assertNotNull(backing);
        Assert.assertTrue(backing instanceof MockContext);
        Assert.assertEquals(((MockContext) backing).getId(), "two");

    }

    @Test
    public void testFactoryNotAvailableOneBuilder() throws Exception {

        // Prepare BundleContext
        // -----------------------------------------------------------

        // 1. Prepare the ServiceReference(s) returned by the BundleContext
        final MockServiceReference reference = new AssignableMockServiceReference();

        // 2. Create the service instance
        ControllableInitialContextFactory factory = new ControllableInitialContextFactory(new MockContext("one"));
        final InitialContextFactoryBuilder builder = new ControllableInitialContextFactoryBuilder(factory);

        // 3. Create a custom BundleContext
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                if (InitialContextFactoryBuilder.class.getName().equals(clazz)) {
                    return new ServiceReference[] {reference};
                }
                return null;
            }

            @Override
            public Object getService(ServiceReference reference) {
                return builder;
            }
        };

        // Prepare the Strategy
        // -----------------------------------------------------------
        strategy.setBundleContext(bundleContext);
        strategy.setFactoryName(ControllableInitialContextFactory.class.getName());

        // Tests
        // -----------------------------------------------------------
        //
        // The purpose of this test is to check that if no ICF are available,
        // it will use the first ICFBuilder the produces a factory that produces a NON NULL Context.

        Context backing = strategy.getBackingContext();

        Assert.assertNotNull(backing);
        Assert.assertTrue(backing instanceof MockContext);
        Assert.assertEquals(((MockContext) backing).getId(), "one");

    }

    @Test
    public void testFactoryNotAvailableTwoBuilders() throws Exception {

        // Prepare BundleContext
        // -----------------------------------------------------------

        // 1. Prepare the ServiceReference(s) returned by the BundleContext
        final MockServiceReference reference1 = new AssignableMockServiceReference();

        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_RANKING,  Integer.MAX_VALUE);
        final MockServiceReference reference2 = new AssignableMockServiceReference(properties);

        // 2. Create the service instance
        ControllableInitialContextFactory factory1 = new ControllableInitialContextFactory(new MockContext("one"));
        final InitialContextFactoryBuilder builder1 = new ControllableInitialContextFactoryBuilder(factory1);

        ControllableInitialContextFactory factory2 = new ControllableInitialContextFactory(new MockContext("two"));
        final InitialContextFactoryBuilder builder2 = new ControllableInitialContextFactoryBuilder(factory2);

        // 3. Create a custom BundleContext
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                if (InitialContextFactoryBuilder.class.getName().equals(clazz)) {
                    return new ServiceReference[] {reference1, reference2};
                }
                return null;
            }

            @Override
            public Object getService(ServiceReference reference) {
                if (reference.equals(reference1)) {
                    return builder1;
                }
                return builder2;
            }
        };

        // Prepare the Strategy
        // -----------------------------------------------------------
        strategy.setBundleContext(bundleContext);
        strategy.setFactoryName(ControllableInitialContextFactory.class.getName());

        // Tests
        // -----------------------------------------------------------
        //
        // The purpose of this test is to check that if no ICF are available,
        // it will use the first valid ICFBuilder (ordered using service ranking).

        Context backing = strategy.getBackingContext();

        Assert.assertNotNull(backing);
        Assert.assertTrue(backing instanceof MockContext);
        Assert.assertEquals(((MockContext) backing).getId(), "two");

    }

    @Test
    public void testReleasingBackingContext() throws Exception {
        final MockServiceReference reference = new AssignableMockServiceReference();

        // 2. Create the service instance
        final ControllableInitialContextFactory factory = new ControllableInitialContextFactory(new MockContext("one"));

        final Holder<Boolean> serviceReleased = new Holder<Boolean>();
        serviceReleased.value = false;

        // 3. Create a custom BundleContext
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference};
            }

            @Override
            public Object getService(ServiceReference reference) {
                return factory;
            }

            @Override
            public boolean ungetService(ServiceReference reference) {
                serviceReleased.value = true;
                return true;
            }
        };

        // Prepare the Strategy
        // -----------------------------------------------------------
        strategy.setBundleContext(bundleContext);
        strategy.setFactoryName(ControllableInitialContextFactory.class.getName());

        // Tests
        // -----------------------------------------------------------
        //
        // The purpose of this test is to check that the Context releasing mechanism is working. 

        Context backing = strategy.getBackingContext();
        Assert.assertNotNull(backing);

        strategy.releaseBackingContext();

        Assert.assertTrue(serviceReleased.value);

    }

    private static class Holder<T> {
        public T value;
    }

}
