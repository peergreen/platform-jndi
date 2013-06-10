package com.peergreen.jndi.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.naming.Reference;

import org.apache.felix.ipojo.ComponentInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIProviderAdmin;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import com.peergreen.jndi.it.components.hello.Hello;
import com.peergreen.jndi.it.components.hello.HelloObjectFactory;
import com.peergreen.jndi.it.components.hello.HelloObjectFactoryBuilder;
import com.peergreen.jndi.it.exam.TestingEnvironment;


@RunWith(JUnit4TestRunner.class)
public class ProviderAdminTestCase {

    /*
    @Configuration
    @AppliesTo("testObjectRecreation")
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
    public void testProviderAdminRegistered() throws Exception {
        ServiceReference ref = bundleContext.getServiceReference(JNDIProviderAdmin.class.getName());
        assertThat(ref, is(notNullValue()));
        JNDIProviderAdmin providerAdmin = (JNDIProviderAdmin) bundleContext.getService(ref);
        assertThat(providerAdmin, is(notNullValue()));
    }

    @Test
    public void testObjectRecreation() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        Hello hello = new Hello("Bonjour");
        try {

            // 0. get the ProviderAdmin service
            JNDIProviderAdmin providerAdmin = getProviderAdmin();

            // 1. With nothing
            // -> Should return the refInfo 'as is'
            //  a. Using Referenceable
            Object o = providerAdmin.getObjectInstance(hello, null, null, null);
            assertThat(o, is(notNullValue()));
            assertThat(o, is(instanceOf(Hello.class)));
            assertThat((Hello) o, is(sameInstance(hello)));

            //  b. Using Reference
            Reference ref = hello.getReference();
            Object o2 = providerAdmin.getObjectInstance(ref, null, null, null);
            assertThat(o2, is(notNullValue()));
            assertThat(o2, is(instanceOf(Reference.class)));
            assertThat((Reference) o2, is(sameInstance(ref)));

            // 2. With the right ObjectFactory registered
            // -> Should return a new Hello instance with the right message

            ComponentInstance factoryInstance = helper.createComponentInstance(HelloObjectFactory.class.getName());
            factoryInstance.start();

            Object o3 = providerAdmin.getObjectInstance(hello, null, null, null);
            assertThat(o3, is(notNullValue()));
            assertThat(o3, is(instanceOf(Hello.class)));
            Hello h3 = (Hello) o3;
            assertThat(h3, is(not(sameInstance(hello))));
            assertThat(h3.getMessage(), is(equalTo("Bonjour")));

            factoryInstance.dispose();

            // 3. With a compatible ObjectFactoryBuilder
            // Will create a HelloObjectFactory (not the service) that will in turn be used to recreate the instance

            ComponentInstance builderInstance = helper.createComponentInstance(HelloObjectFactoryBuilder.class.getName());
            builderInstance.start();

            Object o4 = providerAdmin.getObjectInstance(hello.getReference(), null, null, null);
            assertThat(o4, is(notNullValue()));
            assertThat(o4, is(instanceOf(Hello.class)));
            Hello h4 = (Hello) o4;
            assertThat(h4.getMessage(), is(equalTo("Bonjour")));

            builderInstance.dispose();

        } finally {
            helper.dispose();
        }
    }

    private JNDIProviderAdmin getProviderAdmin() {

        ServiceReference ref = bundleContext.getServiceReference(JNDIProviderAdmin.class.getName());
        assertThat(ref, is(notNullValue()));
        JNDIProviderAdmin providerAdmin = (JNDIProviderAdmin) bundleContext.getService(ref);
        assertThat(providerAdmin, is(notNullValue()));

        return providerAdmin;
    }

}