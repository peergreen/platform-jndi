package com.peergreen.jndi.internal.builtin.osgi;

import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import com.peergreen.jndi.internal.builtin.parser.OSGiName;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.finder.DefaultContextFinder;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code OSGiServiceContext} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiServiceListContext extends AbstractOSGiContext {

    public OSGiServiceListContext(final OSGiName parent, final Environment environment) {
        super(environment, parent);
    }

    @Override
    public Object lookup(Name name) throws NamingException {

        // We need to handle lookup operations as well in 'osgi:servicelist'
        // because it's perfectly valid to have that kind of scenario:
        //
        // Context oslContext = ctx.lookup("osgi:servicelist");
        // NamingEnumeration<NameClassPair> pairs = oslContext.listBindings("javax.sql.DataSource");
        // Context dsContext = oslContext.lookup("javax.sql.DataSource");
        // NamingEnumeration<Binding> bindings = dsContext.listBindings("(ds.type=MySQL)");

        return new OSGiServiceListContext(asOSGiName(name), environment);
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

    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        OSGiName osgiName = asOSGiName(name);

        // We'll need the owning BundleContext for next operations
        BundleContext bundleContext = (new DefaultContextFinder(environment).findContext());

        LookupResult result = doLookup(bundleContext, osgiName);
        return new OSGiNamingEnumeration<NameClassPair>(result.getFinds()) {

            @Override
            public NameClassPair nextElement() {
                // Get the next IFind
                IFind<Object> find = iterator.next();
                // Get information about it
                Long serviceId = (Long) find.getReference().getProperty(Constants.SERVICE_ID);
                String className = null;
                Object service = find.getService();
                if (service != null) {
                    className = service.getClass().getName();
                }
                // And release it
                find.release();

                // Construct a new NameClassPair using retrieved data
                return new OSGiNameClassPair(serviceId, className);
            }
        };
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return list(parse(name));
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        OSGiName osgiName = asOSGiName(name);

        // We'll need the owning BundleContext for next operations
        final BundleContext bundleContext = (new DefaultContextFinder(environment).findContext());

        LookupResult result = doLookup(bundleContext, osgiName);
        return new OSGiNamingEnumeration<Binding>(result.getFinds()) {

            @Override
            public Binding nextElement() {
                // Get the next IFind
                IFind<Object> find = iterator.next();
                // Get information about it
                Long serviceId = (Long) find.getReference().getProperty(Constants.SERVICE_ID);
                String className = null;
                Object service = find.getService();
                if (service != null) {
                    className = service.getClass().getName();
                }

                // Construct a new Binding using retrieved data
                return new OSGiBinding(serviceId, className, proxify(bundleContext, null, find));
            }
        };
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return listBindings(parse(name));
    }

    private static abstract class OSGiNamingEnumeration<T> implements NamingEnumeration<T> {

        /**
         * List of founds services (is not empty).
         */
        private Iterable<IFind<Object>> finds;
        protected Iterator<IFind<Object>> iterator;

        public OSGiNamingEnumeration(final Iterable<IFind<Object>> finds) {
            this.finds = finds;
            iterator = finds.iterator();
        }

        public T next() throws NamingException {
            return nextElement();
        }

        public boolean hasMore() throws NamingException {
            return hasMoreElements();
        }

        public void close() throws NamingException {
            // Release all services
            for (IFind<Object> find : finds) {
                find.release();
            }
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        /**
         * Has to be implemented for Binding and NameClassPair.
         */
        public abstract T nextElement();
    }
}
