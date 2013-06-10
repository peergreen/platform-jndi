package com.peergreen.jndi.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.naming.spi.ObjectFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import com.peergreen.jndi.it.exam.TestingEnvironment;


@RunWith(JUnit4TestRunner.class)
public class JreDelegateTestCase {

   /*
    @Configuration
    @AppliesTo("testSunJreDelegatePresence")
    public static Option[] activateDebug() {
        return options(
                Options.debug()
        );
    }     */


    @Configuration(extend = TestingEnvironment.class)
    public static Option[] configure() {
        return options(
                mavenBundle().groupId("org.ow2.chameleon.testing")
                             .artifactId("osgi-helpers")
                             .versionAsInProject()
        );
    }

    @Inject
    private BundleContext bundleContext;

    private IPOJOHelper helper;

    @Test
    public void testSunJreDelegatePresence() throws Exception {

        helper = new IPOJOHelper(bundleContext);
        try {

            // rmi
            ServiceReference rmi = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                    "rmi-url-context-factory");
            assertThat(rmi, is(notNullValue()));
            assertThat(getUrlScheme(rmi), is(equalTo("rmi")));

            // iiop
            ServiceReference iiop = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                     "iiop-url-context-factory");
            assertThat(iiop, is(notNullValue()));
            assertThat(getUrlScheme(iiop), is(equalTo("iiop")));

            // iiopname
            ServiceReference iiopname = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                         "iiopname-url-context-factory");
            assertThat(iiopname, is(notNullValue()));
            assertThat(getUrlScheme(iiopname), is(equalTo("iiopname")));

            // corbaname
            ServiceReference corbaname = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                          "corbaname-url-context-factory");
            assertThat(corbaname, is(notNullValue()));
            assertThat(getUrlScheme(corbaname), is(equalTo("corbaname")));

            // dns
            ServiceReference dns = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                    "dns-url-context-factory");
            assertThat(dns, is(notNullValue()));
            assertThat(getUrlScheme(dns), is(equalTo("dns")));

            // ldap
            ServiceReference ldap = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                     "ldap-url-context-factory");
            assertThat(ldap, is(notNullValue()));
            assertThat(getUrlScheme(ldap), is(equalTo("ldap")));

            // ldaps
            ServiceReference ldaps = helper.getServiceReferenceByName(ObjectFactory.class.getName(),
                                                                      "ldaps-url-context-factory");
            assertThat(ldaps, is(notNullValue()));
            assertThat(getUrlScheme(ldaps), is(equalTo("ldaps")));

        } finally {
            helper.dispose();
        }
    }

    private String getUrlScheme(ServiceReference reference) {
        return (String) reference.getProperty(JNDIConstants.JNDI_URLSCHEME);
    }

}