package com.peergreen.jndi.internal.env;

import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code Environments} is ...
 *
 * @author Guillaume Sauthier
 */
public class Environments {

    /**
     * Pattern that includes all JNDI properties.
     */
    private static final Pattern JAVA_NAMING_PATTERN = Pattern.compile("java\\.naming\\..*");

    /**
     * JNDI properties filename to be searched in a Bundle.
     */
    private static final String JNDI_PROPERTIES = "/jndi.properties";

    /**
     * Empty default constructor for utility class.
     */
    private Environments() {}

    public static Environment getSystemEnvironment() {

        Map<String, Object> systemEnvironment = new Hashtable<String, Object>();

        Map<String, Object> systemProperties = Utils.asMap(System.getProperties());
        for (Map.Entry<String, Object> entry : systemProperties.entrySet()) {
            String key = entry.getKey();
            if (JAVA_NAMING_PATTERN.matcher(key).matches()) {
                // This is a java.naming.* property
                Object value = entry.getValue();
                if (value != null) {
                    systemEnvironment.put(key, value);
                }
            }
        }

        return new Environment(systemEnvironment);
    }

    public static Environment getBundleEnvironment(final BundleContext bundleContext) {

        Map<String, Object> bundleEnvironment = new Hashtable<String, Object>();
        if (bundleContext != null) {

            Bundle callerBundle = bundleContext.getBundle();
            URL resource = callerBundle.getResource(JNDI_PROPERTIES);
            if (resource != null) {
                Properties properties = Utils.getProperties(resource);
                bundleEnvironment = Utils.asMap(properties);
            } // else return an empty environment
        }

        return new Environment(bundleEnvironment);

    }

}
