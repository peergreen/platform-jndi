package com.peergreen.jndi.internal.env;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.ldap.LdapContext;

import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code Environment} is ...
 *
 * @author Guillaume Sauthier
 */
public class Environment {

    /**
     * List of JNDI properties that are listServices.
     */
    private static String[] DEFAULT_LIST_KEYS = new String[] {
            Context.OBJECT_FACTORIES,
            Context.STATE_FACTORIES,
            Context.URL_PKG_PREFIXES,
            LdapContext.CONTROL_FACTORIES
    };

    /**
     * Managed environment.
     */
    private Map<String, Object> environment;

    /**
     * List of key names that should be managed as listServices.
     */
    private List<String> keyNames;

    /**
     * Creates an empty environment.
     */
    public Environment() {
        this(new Hashtable<String, Object>());
    }

    /**
     * Creates an environment from the given Map.
     * @param environment environment as a Map
     */
    public Environment(final Map<String, Object> environment) {
        this(environment, Arrays.asList(DEFAULT_LIST_KEYS));
    }

    /**
     * Creates an environment from the given Map.
     * @param environment environment as a Map
     * @param keyNames listServices of keys that will be managed as listServices properties
     */
    public Environment(final Map<String, Object> environment, final List<String> keyNames) {
        this.environment = environment;
        this.keyNames = keyNames;
    }

    /**
     * @return the underlying environment as a Map.
     */
    public Map<String, Object> getAsMap() {
        return environment;
    }

    /**
     * Returns the value of the given property name in this environment.
     * @param key property name
     * @return the found value or <tt>null</tt> if not found.
     */
    public Object get(final String key) {
        return environment.get(key);
    }

    public void merge(final Environment source) {

        // There is a specific handling for some properties that are merged instead of being overwritten:
        // * java.naming.factory.object   -> listServices of ObjectFactory
        // * java.naming.factory.state    -> listServices of StateFactory
        // * java.naming.factory.control  -> listServices of ControlFactory (LDAP)
        // * java.naming.factory.url.pkgs -> listServices of package names where URL ObjectFactories are located

        for (Map.Entry<String, Object> entry : source.getAsMap().entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            // Only merge if there is some value in the source environment
            if (value != null) {

                if (isListProperty(key)) {

                    if (value instanceof String) {
                        // Append new value to the old value
                        mergeListProperty(key, (String) value);
                    } // else incorrect type

                } else {
                    // Simply overwrite value
                    environment.put(key, value);
                }
            }
        }

    }

    private boolean isListProperty(final String key) {
        return keyNames.contains(key);
    }

    private void mergeListProperty(final String key, final String sourceValue) {

        Object environmentValue = environment.get(key);
        if (environmentValue == null) {
            // No existing value, simply replace
            environment.put(key, sourceValue);
        } else {
            if (environmentValue instanceof String) {
                String value = (String) environmentValue;

                if (Utils.isNullOrEmpty(value)) {
                    // Simply replace the value
                    environment.put(key, sourceValue);
                } else {
                    // Existing value, append the source value at the beginning
                    environment.put(key, sourceValue + "," + value);
                }
            } // else : not a String (INCORRECT)
        }
    }

    public Hashtable<?, ?> getAsHashtable() {
        if (environment instanceof Hashtable) {
            return (Hashtable<?, ?>) environment;
        } else {
            return Utils.asHashtable(environment);
        }
    }

    public Object set(final String name, final Object value) {
        return environment.put(name, value);
    }

    public Object remove(String name) {
        return environment.remove(name);
    }
}
