package com.peergreen.jndi.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.ComponentInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
import org.osgi.service.jndi.JNDIContextManager;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import com.peergreen.jndi.it.components.service.DefaultHelloService;
import com.peergreen.jndi.it.components.service.IHelloService;
import com.peergreen.jndi.it.exam.TestingEnvironment;


@RunWith(JUnit4TestRunner.class)
public class OSGiServiceContextTestCase {

    /*
    @Configuration()
    @AppliesTo("testProxyJndiServiceNameLookupXX")
    public static Option[] activateDebug() {
        return options(
                debug()
        );
    }
    */

    @Configuration(extend = TestingEnvironment.class)
    public static Option[] configure() {
        return options(
                mavenBundle().groupId("com.peergreen.jndi.test")
                             .artifactId("test-components")
                             .versionAsInProject(),
                mavenBundle().groupId("org.ow2.chameleon.testing")
                             .artifactId("osgi-helpers")
                             .versionAsInProject()
        );
    }

    @Inject
    private BundleContext bundleContext;

    private IPOJOHelper helper;

    @Test
    public void testOSGiUrlContextFactoryRegistered() throws Exception {
        String filter = "(" + JNDIConstants.JNDI_URLSCHEME + "=osgi)";
        ServiceReference[] references = bundleContext.getServiceReferences(ObjectFactory.class.getName(), filter);
        assertThat(references.length, is(equalTo(1)));
        assertThat(references[0], is(notNullValue()));
        ObjectFactory factory = (ObjectFactory) bundleContext.getService(references[0]);
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void testProxyServiceInterfaceLookup() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        try {

            // 0. Get ContextManager
            ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
            JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

            Context initial = manager.newInitialContext();

            // 1. Test the service interface lookup
            // ======================================================
            ComponentInstance i1 = helper.createComponentInstance(DefaultHelloService.class.getName(),
                                                                  "hello-service-1");
            i1.start();

            IHelloService service1 = (IHelloService) initial.lookup("osgi:service/" + IHelloService.class.getName());
            assertThat(service1, is(notNullValue()));
            assertThat(service1.hello("me"), is(equalTo("Hello me")));

            // 1.b Verify rebind
            // ---------------------------

            // Start another component using the same interface
            Dictionary<String, String> p2 = new Hashtable<String, String>();
            p2.put("message", "Hello to");
            ComponentInstance i2 = helper.createComponentInstance(DefaultHelloService.class.getName(),
                                                                  "hello-service-2",
                                                                  p2);
            i2.start();

            // Stop the first one
            i1.dispose();

            // Perform another call on the proxy
            assertThat(service1.hello("me"), is(equalTo("Hello to me")));

            // Cleanup
            i2.dispose();

        } finally {
            helper.dispose();
        }
    }

    @Test
    public void testProxyJndiServiceNameLookup() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        try {

            // 0. Get ContextManager
            ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
            JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

            Context initial = manager.newInitialContext();

            // 1. Test the jndi name service lookup
            // ======================================================
            Dictionary<String, String> p3 = new Hashtable<String, String>();
            p3.put("message", "Bye");
            p3.put(JNDIConstants.JNDI_SERVICENAME, "myHelloService");
            ComponentInstance i3 = helper.createComponentInstance(DefaultHelloService.class.getName(), p3);
            i3.start();

            IHelloService service2 = (IHelloService) initial.lookup("osgi:service/myHelloService");
            assertThat(service2, is(notNullValue()));
            assertThat(service2.hello("me"), is(equalTo("Bye me")));

            // 2.b Verify rebind
            // ---------------------------

            // Start another component using the same interface
            Dictionary<String, String> p4 = new Hashtable<String, String>();
            p4.put("message", "Bye to");
            p4.put(JNDIConstants.JNDI_SERVICENAME, "myHelloService");
            ComponentInstance i4 = helper.createComponentInstance(DefaultHelloService.class.getName(), p4);
            i4.start();

            // Stop the first one
            i3.dispose();

            // Perform another call on the proxy
            assertThat(service2.hello("me"), is(equalTo("Bye to me")));

            // Cleanup
            i4.dispose();


        } finally {
            helper.dispose();
        }
    }


    @Test(expected = ServiceException.class)
    public void testServiceInterfaceLookupWithServiceDisappearance() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        try {

            // 0. Get ContextManager
            ServiceReference ref = bundleContext.getServiceReference(JNDIContextManager.class.getName());
            JNDIContextManager manager = (JNDIContextManager) bundleContext.getService(ref);

            Context initial = manager.newInitialContext();

            // 1. Test the service interface lookup
            // ---------------------------------------
            ComponentInstance i1 = helper.createComponentInstance(DefaultHelloService.class.getName(),
                                                                  "hello-service-1");
            i1.start();

            IHelloService service1 = (IHelloService) initial.lookup("osgi:service/" + IHelloService.class.getName());
            assertThat(service1, is(notNullValue()));
            assertThat(service1.hello("me"), is(equalTo("Hello me")));

            // 2. Unregister the underlying service
            // ---------------------------------------
            i1.dispose();

            // 3. Perform another call on the proxy
            // ---------------------------------------
            // Should throw an Exception
            service1.hello("me");

        } finally {
            helper.dispose();
        }
    }
}