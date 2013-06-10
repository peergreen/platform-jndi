package com.peergreen.jndi.internal.env;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Version;
import org.springframework.osgi.mock.MockBundle;
import org.springframework.osgi.mock.MockBundleContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * A {@code EnvironmentsTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class EnvironmentsTestCase {

    private Properties namingProperties;

    @BeforeClass
    public void removeExistingNamingSystemProperties() throws Exception {
        namingProperties = new Properties();

        Properties system = System.getProperties();
        Enumeration<String> names = (Enumeration<String>) system.propertyNames();
        for (; names.hasMoreElements();) {
            String name = names.nextElement();
            if (name.startsWith("java.naming.")) {
                String value = (String) system.remove(name);
                namingProperties.setProperty(name, value);
            }
        }
    }

    @AfterClass
    public void resetExistingNamingSystemProperties() throws Exception {
        Enumeration<String> names = (Enumeration<String>) namingProperties.propertyNames();
        for (; names.hasMoreElements();) {
            String name = names.nextElement();
            System.setProperty(name, namingProperties.getProperty(name));
        }
    }

    @Test
    public void testGetEmptySystemEnvironment() throws Exception {
        Environment env = Environments.getSystemEnvironment();
        assertNotNull(env);
        assertEquals(env.getAsMap().size(), 0);
    }

    @Test
    public void testGetSystemEnvironment() throws Exception {

        System.setProperty("java.naming.something", "value");
        try {
            Environment env = Environments.getSystemEnvironment();
            assertNotNull(env);
            assertEquals(env.getAsMap().size(), 1);
            assertEquals(env.getAsMap().get("java.naming.something"), "value");
        } finally {
            // Clean-up
            System.getProperties().remove("java.naming.something");
        }
    }

    @Test
    public void testGetBundleEnvironmentWithJndiProperties() throws Exception {

        MockBundle bundle = new MockBundle() {
            @Override
            public URL getResource(String name) {
                return EnvironmentsTestCase.class.getResource(name);
            }

            public Map getSignerCertificates(int signersType) {
                return null;
            }

            public Version getVersion() {
                return null;
            }
        };
        MockBundleContext context = new MockBundleContext(bundle);
        Environment env = Environments.getBundleEnvironment(context);
        assertNotNull(env);
        assertEquals(env.getAsMap().size(), 1);
        assertEquals(env.getAsMap().get("java.naming.test"), "true");
    }

    @Test
    public void testGetBundleEnvironmentWithoutJndiProperties() throws Exception {
        MockBundle bundle = new MockBundle() {
            @Override
            public URL getResource(String name) {
                return null;
            }

            public Map getSignerCertificates(int signersType) {
                return null;
            }

            public Version getVersion() {
                return null;
            }
        };
        MockBundleContext context = new MockBundleContext(bundle);
        Environment env = Environments.getBundleEnvironment(context);
        assertNotNull(env);
        assertEquals(env.getAsMap().size(), 0);
    }

}
