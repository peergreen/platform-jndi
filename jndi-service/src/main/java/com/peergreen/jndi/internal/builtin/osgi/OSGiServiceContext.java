package com.peergreen.jndi.internal.builtin.osgi;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.builtin.parser.OSGiName;
import com.peergreen.jndi.internal.builtin.parser.Path;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.finder.DefaultContextFinder;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code OSGiServiceContext} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiServiceContext extends AbstractOSGiContext {

    /**
     * Only query accepted for "osgi:framework" lookup.
     */
    private static final String QUERY_BUNDLE_CONTEXT = "bundleContext";

    public OSGiServiceContext(final Environment environment) {
        this(null, environment);
    }

    public OSGiServiceContext(final OSGiName parent, final Environment environment) {
        super(environment, parent);
    }

    @Override
    public Object lookup(Name name) throws NamingException {

        OSGiName osgiName = asOSGiName(name);

        // We'll need the owning BundleContext for next operations
        BundleContext bundleContext = (new DefaultContextFinder(environment).findContext());

        Object returned = null;
        Path path = osgiName.getPath();
        switch (path) {

            case FRAMEWORK:
                if (osgiName.hasQuery()) {
                    if (!QUERY_BUNDLE_CONTEXT.equals(osgiName.getQuery())) {
                        throw new InvalidNameException("'" + osgiName + "' is invalid, when using 'osgi:framework'" +
                                                       ", only 'osgi:framework/bundleContext' is accepted");
                    }
                    // Return the owning BundleContext
                    returned = bundleContext;
                } else {
                    // osgiName is 'osgi:framework', we must return a sub Context
                    returned = new OSGiServiceContext(osgiName, environment);
                }
                break;
            case SERVICE:
                if (osgiName.hasQuery()) {
                    // Return a Proxy to the found service (either with JNDI service name or interface name)
                    returned = getProxyService(bundleContext, osgiName);
                } else {
                    // osgiName is 'osgi:service', we must return a sub Context
                    returned = new OSGiServiceContext(osgiName, environment);
                }
                break;
            case SERVICELIST:
                // Return a special osgi:servicelist Context
                returned = new OSGiServiceListContext(osgiName, environment);
                break;
        }

        return returned;
    }

    @Override
    public Object lookup(String name) throws NamingException {
        return lookup(parse(name));
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        // lookupLink == lookup
        return super.lookup(name);
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        // lookupLink == lookup
        return super.lookup(name);
    }

    /**
     * Get a proxy on the searched object (if any is found).
     * @param bundleContext owning bundle context
     * @param name name to be searched.
     * @return a proxy for the first found service
     * @throws NameNotFoundException if no available service is found
     */
    private Object getProxyService(final BundleContext bundleContext,
                                   final OSGiName name) throws NameNotFoundException {

        LookupResult result = doLookup(bundleContext, name);

        // If the list was empty, an Exception has already been throw,
        // so we can safely access the first element
        IFind<Object> first = result.getFinds().iterator().next();

        return proxify(bundleContext, result.getRequest(), first);
    }

}
