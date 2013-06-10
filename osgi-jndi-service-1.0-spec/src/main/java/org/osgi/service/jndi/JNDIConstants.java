package org.osgi.service.jndi;

/**
 * Constants for the JNDI implementation.
 *
 * @concurrency Immutable
 * @author Guillaume Sauthier
 * @since 1.0
 */
public class JNDIConstants {

    /**
     * This JNDI environment property can be used by a JNDI client to indicate the
     * caller's {@code BundleContext}. This property can be set and passed to an
     * {@code InitialContext} constructor. This property is only useful in the
     * "traditional" mode of JNDI.
     */
    public static final String BUNDLE_CONTEXT = "osgi.service.jndi.bundleContext";

    /**
     * This service property is set on an OSGi service to provide a name that can
     * be used to locate the service other than the service interface name.
     */
    public static final String JNDI_SERVICENAME = "osgi.jndi.service.name";

    /**
     * This service property is set by JNDI Providers that publish URL Context
     * Factories as OSGi Services. The value of this property should be the
     * URL scheme that is supported by the published service.
     */
    public static final String JNDI_URLSCHEME = "osgi.jndi.url.scheme";

}
