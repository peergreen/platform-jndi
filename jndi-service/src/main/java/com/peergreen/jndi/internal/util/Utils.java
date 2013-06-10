package com.peergreen.jndi.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.Name;

/**
 * A {@code Utils} is ...
 *
 * @author Guillaume Sauthier
 */
public class Utils {

    /**
     * Empty default constructor for utility class.
     */
    private Utils() {}

    /**
     * Return a Properties instance filled from the given URL.
     * The URL should points to a <tt>".properties"</tt> file.
     * @param resource Where the properties are stored
     * @return a filled Properties instance (empty if there was an error loading it)
     */
    public static Properties getProperties(final URL resource) {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = resource.openStream();
            properties.load(is);
        } catch (IOException e) {
            // do nothing, we will return an empty Properties
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }
        return properties;
    }

    /**
     * Convert a Properties instance to a typed Map.
     * @param properties properties to convert
     * @return a typed Map with a content equivalent to the given Properties
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(final Properties properties) {
        Map<String, Object> map = new Hashtable<String, Object>();

        if (properties != null) {
            Collection<String> keys = (Collection<String>) Collections.list(properties.propertyNames());
            for (String key : keys) {
                map.put(key, properties.getProperty(key));
            }
        }

        return map;
    }

    /**
     * Convert an untyped Hashtable instance to a typed Map.
     * @param table table to convert (should contains String -> Object)
     * @return a typed Map with a content equivalent to the given table
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(final Hashtable<?, ?> table) {
        Map<String, Object> map = new Hashtable<String, Object>();

        if (table != null) {
            for (Map.Entry<?, ?> entry : table.entrySet()) {
                map.put((String) entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public static boolean isNullOrEmpty(final String value) {
        return (value == null) || ("".equals(value));
    }

    public static String asString(final Object o) {

        if (o != null) {
            if (o instanceof String) {
                return (String) o;
            } else {
                throw new IllegalArgumentException("Expecting a String, was a " + o.getClass());
            }
        }
        
        return null;

    }

    public static Hashtable<?, ?> asHashtable(final Map<String, Object> map) {

        // Do not recreate the Object if it's already the expected type
        if (map instanceof Hashtable) {
            return (Hashtable<?, ?>) map;
        }

        // Otherwise, convert the Map
        Hashtable<String, Object> table = new Hashtable<String, Object>();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            table.put(entry.getKey(), entry.getValue());
        }

        return table;

    }

    /**
     * Get the scheme out of the given URL.
     * @param url URL to be "parsed"
     * @return the URL scheme or null if the URL is not valid
     */
    public static String getScheme(final String url) {

        if (url != null) {
            // Find if this is an URL context
            int indexOfColon = url.indexOf(':');
            if (indexOfColon != -1) {
                // There is an URL-like Context
                return url.substring(0, indexOfColon);
            }
        }
        return null;
    }

    public static boolean isUrlScheme(String name) {
        return (name != null) && (name.indexOf(':') != -1);
    }

    public static boolean isUrlScheme(Name name) {
        return (name != null) && isUrlScheme(name.toString());
    }
}
