package com.peergreen.jndi.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.junit.JUnitOptions.mockitoBundles;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NoInitialContextException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jndi.JNDIContextManager;
import com.peergreen.jndi.it.exam.TestingEnvironment;


@RunWith(JUnit4TestRunner.class)
public class ContextManagerTestCase {

    /*
    @Configuration
    @AppliesTo("testFactoryDisappearance")
    public static Option[] activateDebug() {
        return options(
                debug()
        );
    }
    */

    @Configuration(extend = TestingEnvironment.class)
    public static Option[] configure() {
        return options(
                // This TestCase requires Mockito
                mockitoBundles()
        );
    }

    @Inject
    private BundleContext bundleContext;

    @Test
    public void testContextManagerRegistered() throws Exception {
        ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
        assertThat(ref, is(notNullValue()));
        JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);
        assertThat(manager, is(notNullValue()));
    }

    @Test
    public void testManagersBoundToFactory() throws Exception {

        InitialContextFactory factory = Mockito.mock(InitialContextFactory.class);
        DirContext context = Mockito.mock(DirContext.class);
        bundleContext.registerService(new String[] {InitialContextFactory.class.getName()},
                                      factory, null);
        when(factory.getInitialContext(any(Hashtable.class))).thenReturn(context);

        ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
        JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

        Properties env = new Properties();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory.class.getName());
        Context returnedContext = manager.newInitialContext(env);

        verify(factory).getInitialContext(any(Hashtable.class));
        assertThat(returnedContext, is(notNullValue()));

        DirContext returnedDirContext = manager.newInitialDirContext(env);
        verify(factory, times(2)).getInitialContext(any(Hashtable.class));
        assertThat(returnedDirContext, is(notNullValue()));
    }

    @Test
    public void testContextManagerUngetClosesAllProvidedContext() throws Exception {

        InitialContextFactory factory = Mockito.mock(InitialContextFactory.class);
        Context context = Mockito.mock(Context.class);
        bundleContext.registerService(new String[] {InitialContextFactory.class.getName()},
                                      factory, null);
        when(factory.getInitialContext(any(Hashtable.class))).thenReturn(context);
        when(context.lookup(eq("hello"))).thenReturn("world");

        ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
        JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

        Properties env = new Properties();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory.class.getName());
        Context returnedContext = manager.newInitialContext(env);

        assertThat((String) returnedContext.lookup("hello"), is( equalTo( "world" ) ));

        // Release the reference
        bundleContext.ungetService(ref);

        // Check that Context has been stopped automatically
        verify(context).close();

    }


    @Test(expected = NoInitialContextException.class)
    public void testRequiredFactoryIsNotFound() throws Exception {

        ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
        JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

        Properties env = new Properties();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory.class.getName());

        // factory was not registered so a NamingException should be thrown
        manager.newInitialContext(env);
    }

    @Test(expected = NoInitialContextException.class)
    public void testFactoryDisappearance() throws Exception {

        InitialContextFactory factory = Mockito.mock(InitialContextFactory.class);
        Context context = Mockito.mock(Context.class);
        ServiceRegistration reg = bundleContext.registerService(new String[] {InitialContextFactory.class.getName()},
                                      factory, null);

        // Behavior
        when(factory.getInitialContext(any(Hashtable.class))).thenReturn(context);
        when(context.getEnvironment()).thenReturn(null);

        ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
        JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

        Properties env = new Properties();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory.class.getName());
        Context returned = manager.newInitialContext(env);

        verify(factory).getInitialContext(any(Hashtable.class));
        assertThat(returned, is(notNullValue()));

        // Ensure that no Exceptions is thrown
        try {
            returned.getEnvironment();
        } catch (Throwable t) {
            // wraps the inner exception so that it will become a failure
            throw new Exception(t);
        }

        // ICF service goes away
        reg.unregister();

        // Ensure that an Exception is thrown
        returned.getEnvironment();

    }


}
