package com.peergreen.jndi.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.felix.ipojo.ComponentInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import com.peergreen.jndi.it.components.context.HelloInitialContextFactory;
import com.peergreen.jndi.it.components.service.DefaultHelloService;
import com.peergreen.jndi.it.components.service.IHelloService;
import com.peergreen.jndi.it.exam.TestingEnvironment;


@RunWith(JUnit4TestRunner.class)
public class TraditionalClientModelTestCase {

          /*
    @Configuration()
    @AppliesTo("testNewInitialContextWithInitialContextFactory")
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
    public void testNewInitialContext() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        try {

            // 0. Get an InitialContext (will only supports url style lookup)
            InitialContext initial = new InitialContext();

            // 1. Test the lookup
            // ---------------------------------------
            ComponentInstance i1 = helper.createComponentInstance(DefaultHelloService.class.getName(),
                                                                  "hello-service-1");
            i1.start();

            IHelloService service1 = (IHelloService) initial.lookup("osgi:service/" + IHelloService.class.getName());
            assertThat(service1, is(notNullValue()));
            assertThat(service1.hello("me"), is(equalTo("Hello me")));

        } finally {
            helper.dispose();
        }
    }

    @Test
    public void testNewInitialContextWithInitialContextFactory() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        try {

            // 1. Create an ICF instance
            // ---------------------------------------
            ComponentInstance i1 = helper.createComponentInstance(HelloInitialContextFactory.class.getName());
            i1.start();

            // 2. Get an InitialContext
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, HelloInitialContextFactory.class.getName());
            InitialContext initial = new InitialContext(props);

            assertThat(initial, is(notNullValue()));
            assertThat((String) initial.lookup("blabla"), is(equalTo("Hello")));

        } finally {
            helper.dispose();
        }
    }
}