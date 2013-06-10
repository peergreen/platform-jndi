package com.peergreen.jndi.internal.finder;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.IBundleContextFinder;
import org.springframework.osgi.mock.MockBundleContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A {@code CallerStackFinderTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class ExecutionStackFinderTestCase {

    @Test
    public void testFindImmediatelyAfterTheInvoker() throws Exception {
        // Configure the invoker matcher
        Map<Class<?>, String> invokers = new Hashtable<Class<?>, String>();
        invokers.put(ExecutionStackFinderTestCase.class, "testFindImmediatelyAfterTheInvoker");

        ExecutionStackFinder finder = new ExecutionStackFinder(Thread.currentThread(), invokers) {

            @Override
            protected IBundleContextFinder createBundleContextFinder(ClassLoader loader) {
                return new IBundleContextFinder() {

                    public BundleContext findContext() {
                        return new MockBundleContext();
                    }
                };
            }
        };
        BundleContext context = finder.findContext();
        Assert.assertNotNull(context);
    }

    @Test
    public void testFindTwoLevelsUpperAfterTheInvoker() throws Exception {
        // Configure the invoker matcher
        Map<Class<?>, String> invokers = new Hashtable<Class<?>, String>();
        invokers.put(ExecutionStackFinderTestCase.class, "testFindTwoLevelsUpperAfterTheInvoker");

        ExecutionStackFinder finder = new ExecutionStackFinder(Thread.currentThread(), invokers) {

            int executions = 0;
            @Override
            protected IBundleContextFinder createBundleContextFinder(ClassLoader loader) {
                executions++;
                return new IBundleContextFinder() {

                    public BundleContext findContext() {
                        if (executions == 2) {
                            return new MockBundleContext();
                        }
                        return null;
                    }
                };
            }
        };
        BundleContext context = finder.findContext();
        Assert.assertNotNull(context);
    }

    @Test
    public void testNeverFindsAfterTheInvoker() throws Exception {
        // Configure the invoker matcher
        Map<Class<?>, String> invokers = new Hashtable<Class<?>, String>();
        invokers.put(ExecutionStackFinderTestCase.class, "testNeverFindsAfterTheInvoker");

        ExecutionStackFinder finder = new ExecutionStackFinder(Thread.currentThread(), invokers) {

            @Override
            protected IBundleContextFinder createBundleContextFinder(ClassLoader loader) {
                return new IBundleContextFinder() {
                    public BundleContext findContext() {
                        return null;
                    }
                };
            }
        };
        BundleContext context = finder.findContext();
        Assert.assertNull(context);
    }
}