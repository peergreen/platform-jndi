package com.peergreen.jndi.internal.builtin.osgi;

import static org.testng.Assert.assertNotNull;

import javax.naming.Context;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.env.Environment;
import org.springframework.osgi.mock.MockBundleContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * A {@code OSGiServiceContextTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiServiceContextTestCase {

    private OSGiServiceContext context;

    @BeforeTest
    private void setUp() throws Exception {
        Environment env = new Environment();
        env.set(JNDIConstants.BUNDLE_CONTEXT, new MockBundleContext());
        context = new OSGiServiceContext(env);
    }

    @Test
    public void testFrameworkBundleContextLookup() throws Exception {
        BundleContext bc = (BundleContext) context.lookup("osgi:framework/bundleContext");
        assertNotNull(bc);
    }

    @Test
    public void testFrameworkBundleContextLookupWithIntermediateContext() throws Exception {
        Context sub = (Context) context.lookup("osgi:framework");
        assertNotNull(sub);
        BundleContext bc = (BundleContext) sub.lookup("bundleContext");
        assertNotNull(bc);
    }

    @Test(expectedExceptions = NamingException.class)
    public void testFrameworkWrongLookupWithIntermediateContext() throws Exception {
        Context sub = (Context) context.lookup("osgi:framework");
        assertNotNull(sub);
        sub.lookup("anything, but not 'bundleContext'");
    }

}
