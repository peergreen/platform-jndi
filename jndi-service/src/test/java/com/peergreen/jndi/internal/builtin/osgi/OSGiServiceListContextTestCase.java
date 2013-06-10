package com.peergreen.jndi.internal.builtin.osgi;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.sql.DataSource;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.builtin.osgi.mock.MockDataSource;
import com.peergreen.jndi.internal.env.Environment;
import org.springframework.osgi.mock.MockBundleContext;
import org.springframework.osgi.mock.MockServiceRegistration;
import org.testng.annotations.Test;

/**
 * A {@code OSGiServiceContextTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiServiceListContextTestCase {

    @Test
    public void testServiceListContextLookup() throws Exception {
        Environment env = new Environment();
        env.set(JNDIConstants.BUNDLE_CONTEXT, new MockBundleContext());
        Context context = new OSGiServiceContext(env);
        Context listContext = (Context) context.lookup("osgi:servicelist");
        assertNotNull(listContext);
        assertEquals(listContext.getClass(), OSGiServiceListContext.class);
    }

    @Test(expectedExceptions = NameNotFoundException.class)
    public void testServiceListContextWithNoAvailableServices() throws Exception {
        Environment env = new Environment();
        env.set(JNDIConstants.BUNDLE_CONTEXT, new MockBundleContext());
        Context context = new OSGiServiceListContext(null, env);
        context.listBindings("osgi:servicelist/javax.sql.DataSource");
    }

    @Test
    public void testServiceListContextListOneBinding() throws Exception {
        Environment env = new Environment();
        Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_ID, 42l);
        MockServiceRegistration registration = new MockServiceRegistration(new String[] {DataSource.class.getName()},
                                                                                props);
        final ServiceReference reference = registration.getReference();
        final DataSource ds = new MockDataSource();
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference};
            }

            @Override
            public Object getService(ServiceReference reference) {
                return ds;
            }
        };
        env.set(JNDIConstants.BUNDLE_CONTEXT, bundleContext);
        Context context = new OSGiServiceListContext(null, env);

        NamingEnumeration<Binding> bindings = context.listBindings("osgi:servicelist/javax.sql.DataSource");
        assertNotNull(bindings);
        assertTrue(bindings.hasMore());
        Binding binding = bindings.next();
        assertEquals(binding.getClassName(), MockDataSource.class.getName());
        assertEquals(binding.getName(), "42");
        assertEquals(binding.getObject(), ds);
    }

    @Test
    public void testServiceListContextListOneBindingWithIntermediateContext() throws Exception {
        Environment env = new Environment();
        Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_ID, 42l);
        MockServiceRegistration registration = new MockServiceRegistration(new String[] {DataSource.class.getName()},
                                                                                props);
        final ServiceReference reference = registration.getReference();
        final DataSource ds = new MockDataSource();
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference};
            }

            @Override
            public Object getService(ServiceReference reference) {
                return ds;
            }
        };
        env.set(JNDIConstants.BUNDLE_CONTEXT, bundleContext);
        Context baseContext = new OSGiServiceContext(env);
        Context context = (Context) baseContext.lookup("osgi:servicelist");

        NamingEnumeration<Binding> bindings = context.listBindings("javax.sql.DataSource");
        assertNotNull(bindings);
        assertTrue(bindings.hasMore());
        Binding binding = bindings.next();
        assertEquals(binding.getClassName(), MockDataSource.class.getName());
        assertEquals(binding.getName(), "42");
        assertEquals(binding.getObject(), ds);
    }

    @Test
    public void testServiceListContextListOneBindingWithIntermediateContext2() throws Exception {
        Environment env = new Environment();
        Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_ID, 42l);
        MockServiceRegistration registration = new MockServiceRegistration(new String[] {DataSource.class.getName()},
                                                                                props);
        final ServiceReference reference = registration.getReference();
        final DataSource ds = new MockDataSource();
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference};
            }

            @Override
            public Object getService(ServiceReference reference) {
                return ds;
            }
        };
        env.set(JNDIConstants.BUNDLE_CONTEXT, bundleContext);
        Context baseContext = new OSGiServiceContext(env);
        Context context = (Context) baseContext.lookup("osgi:servicelist");

        Context sub = (Context) context.lookup("javax.sql.DataSource");
        assertNotNull(sub);
        NamingEnumeration<Binding> bindings = sub.listBindings("(a=b)");
        assertNotNull(bindings);
        assertTrue(bindings.hasMore());
        Binding binding = bindings.next();
        assertEquals(binding.getClassName(), MockDataSource.class.getName());
        assertEquals(binding.getName(), "42");
        assertEquals(binding.getObject(), ds);
    }

    @Test
    public void testServiceListContextListTwoBindings() throws Exception {
        Environment env = new Environment();

        // First DS
        Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_ID, 42l);
        MockServiceRegistration registration = new MockServiceRegistration(new String[] {DataSource.class.getName()},
                                                                                props);
        final ServiceReference reference = registration.getReference();
        final DataSource ds = new MockDataSource();

        // Second DS
        Dictionary props2 = new Hashtable();
        props2.put(Constants.SERVICE_ID, 45l);
        MockServiceRegistration registration2 = new MockServiceRegistration(new String[] {DataSource.class.getName()},
                                                                                props2);
        final ServiceReference reference2 = registration2.getReference();
        final DataSource ds2 = new MockDataSource();


        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference2, reference};
            }

            @Override
            public Object getService(ServiceReference ref) {
                if (ref.equals(reference)) {
                    return ds;
                } else {
                    return ds2;
                }
            }
        };
        env.set(JNDIConstants.BUNDLE_CONTEXT, bundleContext);
        Context context = new OSGiServiceListContext(null, env);

        NamingEnumeration<Binding> bindings = context.listBindings("osgi:servicelist/javax.sql.DataSource");
        assertNotNull(bindings);

        // Check first Binding
        assertTrue(bindings.hasMore());
        Binding binding1 = bindings.next();
        assertEquals(binding1.getClassName(), MockDataSource.class.getName());
        assertEquals(binding1.getName(), "42");
        assertEquals(binding1.getObject(), ds);

        // Check Second Binding
        assertTrue(bindings.hasMore());
        Binding binding2 = bindings.next();
        assertEquals(binding2.getClassName(), MockDataSource.class.getName());
        assertEquals(binding2.getName(), "45");
        assertEquals(binding2.getObject(), ds2);
    }


    @Test(expectedExceptions = NameNotFoundException.class)
    public void testServiceListContextNameClassPairWithNoAvailableServices() throws Exception {
        Environment env = new Environment();
        env.set(JNDIConstants.BUNDLE_CONTEXT, new MockBundleContext());
        Context context = new OSGiServiceListContext(null, env);
        context.list("osgi:servicelist/javax.sql.DataSource");
    }

    @Test
    public void testServiceListContextListOneNameClassPair() throws Exception {
        Environment env = new Environment();
        Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_ID, 42l);
        MockServiceRegistration registration = new MockServiceRegistration(new String[] {DataSource.class.getName()},
                                                                                props);
        final ServiceReference reference = registration.getReference();
        final DataSource ds = new MockDataSource();
        MockBundleContext bundleContext = new MockBundleContext() {
            @Override
            public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                return new ServiceReference[] {reference};
            }

            @Override
            public Object getService(ServiceReference reference) {
                return ds;
            }
        };
        env.set(JNDIConstants.BUNDLE_CONTEXT, bundleContext);
        Context context = new OSGiServiceListContext(null, env);

        NamingEnumeration<NameClassPair> pairs = context.list("osgi:servicelist/javax.sql.DataSource");
        assertNotNull(pairs);
        assertTrue(pairs.hasMore());
        NameClassPair pair = pairs.next();
        assertEquals(pair.getClassName(), MockDataSource.class.getName());
        assertEquals(pair.getName(), "42");
    }



}