package com.peergreen.jndi.internal.builtin.osgi;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.builtin.parser.OSGiName;
import com.peergreen.jndi.internal.builtin.parser.OSGiNameParser;
import com.peergreen.jndi.internal.context.ForwardingContext;
import com.peergreen.jndi.internal.context.UnsupportedOperationsContext;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.osgi.toolkit.BundleClassLoaderAdapter;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;
import com.peergreen.osgi.toolkit.finder.impl.Finders;
import org.ow2.util.osgi.toolkit.filter.Filters;
import org.ow2.util.osgi.toolkit.filter.IFilter;

/**
 * A {@code AbstractOSGiContext} is ...
 *
 * @author Guillaume Sauthier
 */
public abstract class AbstractOSGiContext extends ForwardingContext {

    /**
     * Parent's name.
     */
    protected OSGiName parent;

    /**
     * The environment.
     */
    protected Environment environment;

    /**
     * Restricted Context.
     */
    private Context delegate = new UnsupportedOperationsContext();

    /**
     * The Name Parser dedicated to OSGi scheme.
     */
    protected OSGiNameParser nameParser;

    public AbstractOSGiContext(final Environment environment, final OSGiName parent) {
        nameParser = new OSGiNameParser();
        this.environment = environment;
        this.parent = parent;
    }

    @Override
    protected Context delegate() throws NamingException {
        return delegate;
    }

    /**
     * Parse the name as an OSGiName.
     * @param name String representation of the name
     * @return the name as an OSGiName
     * @throws NamingException if the name is ill-formed
     */
    protected Name parse(final String name) throws NamingException {

        String absoluteName = name;
        if (parent != null) {
            // Treat it as a relative name to the parent
            absoluteName = parent.toString() + "/" + name;
        }
        // else this is an absolute name

        return nameParser.parse(absoluteName);
    }

    /**
     * Convert the given name into an OSGiName.
     */
    protected OSGiName asOSGiName(Name name) throws NamingException {
        if (name instanceof OSGiName) {
            return (OSGiName) name;
        }
        return (OSGiName) parse(name.toString());
    }

    protected LookupResult doLookup(final BundleContext bundleContext,
                                    final OSGiName name) throws NameNotFoundException {

        if (name.hasFilter()) {

            // That can only be an interface based lookup
            IFindable<Object> request = getServicesUsingInterfaceName(bundleContext, name);
            List<IFind<Object>> finds = request.listFinds();

            if (finds.isEmpty()) {
                throw new NameNotFoundException("Cannot retrieve an OSGi service for '" + name.toString() + "'");
            }
            return new LookupResult(request, finds);

        } else {

            // That may be either a jndi service name lookup or an interface lookup (without filter)
            if (name.size() > 2) {
                // Can only be a jndi service name based lookup (otherwise the latest element should be a filter)
                // example: 'osgi:service/jdbc/MyDataSource'

                IFindable<Object> request = getServicesUsingJndiServiceName(bundleContext, name);
                List<IFind<Object>> finds = request.listFinds();

                if (finds.isEmpty()) {
                    throw new NameNotFoundException("Cannot retrieve an OSGi service for '" + name.toString() + "'");
                }

                return new LookupResult(request, finds);
            }

            // In that last case, we cannot distinguish between a service
            // name based lookup and an interface based lookup
            // The strategy we will follow until a brilliant new idea comes is:
            //  1. Try to search with the interface semantic
            //  2. if nothing was found, try with the jndi service name semantic

            IFindable<Object> request = getServicesUsingInterfaceName(bundleContext, name);
            List<IFind<Object>> finds = request.listFinds();

            if (finds.isEmpty()) {
                // Try with JNDI Service name
                request = getServicesUsingJndiServiceName(bundleContext, name);
                finds = request.listFinds();
            }

            if (finds.isEmpty()) {
                throw new NameNotFoundException("Cannot retrieve an OSGi service for '" + name.toString() + "'");
            }

            return new LookupResult(request, finds);
        }
    }

    protected IFindable<Object> getServicesUsingInterfaceName(final BundleContext bundleContext,
                                                              final OSGiName name) throws NameNotFoundException {

        String interfaceName = name.getInterfaceName();

        IFilter aggregated = Filters.objectClass(interfaceName);
        if (name.hasFilter()) {
            IFilter filter = Filters.filter(name.getFilter());
            aggregated = Filters.and(aggregated, filter);
        }

        // Construct the request
        // And get the first found service
        IFindable<Object> request = Finders.find(bundleContext)
                                           .with(aggregated)
                                           .orderWith(serviceRanking());

        return request;
    }

    protected IFindable<Object> getServicesUsingJndiServiceName(final BundleContext bundleContext,
                                                                final OSGiName name) throws NameNotFoundException {

        // Construct the request
        IFindable<Object> request = Finders.find(bundleContext)
                                    .with(Filters.equal(JNDIConstants.JNDI_SERVICENAME, name.getServiceName()))
                                    .orderWith(serviceRanking());

        return request;
    }

    protected Object proxify(final BundleContext bundleContext,
                             final IFindable<Object> request,
                             final IFind<Object> find) {

        ServiceReference ref = find.getReference();
        String[] typeNames = (String[]) ref.getProperty(Constants.OBJECTCLASS);

        List<Class<?>> types = new ArrayList<Class<?>>(typeNames.length);

        for (String typeName : typeNames) {

            Class<?> type = null;
            Bundle bundle = bundleContext.getBundle();
            try {
                type = bundle.loadClass(typeName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Cannot load '" + typeName + "' from Bundle " + bundle, e);
            }

            if (type.isInterface()) {
                // We can only proxify interfaces
                types.add(type);
            }
        }

        InvocationHandler handler = new ServiceProxyInvocationHandler(find, request);

        // Create the proxy
        BundleClassLoaderAdapter adapter = new BundleClassLoaderAdapter(bundleContext.getBundle());
        Class<?>[] typesArray = types.toArray(new Class<?>[types.size()]);
        return Proxy.newProxyInstance(adapter, typesArray, handler);
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return environment.set(propName, propVal);
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        return environment.remove(propName);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return (Hashtable<?, ?>) environment.getAsHashtable().clone();
    }

    @Override
    public void close() throws NamingException {
        // Do nothing, but prevent an Exception to be thrown from super class :)
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        return parent.toString();
    }

    protected class LookupResult {
        private IFindable<Object> request;
        private List<IFind<Object>> finds;

        public LookupResult(IFindable<Object> request, List<IFind<Object>> finds) {
            this.request = request;
            this.finds = finds;
        }

        public IFindable<Object> getRequest() {
            return request;
        }

        public List<IFind<Object>> getFinds() {
            return finds;
        }
    }
}
