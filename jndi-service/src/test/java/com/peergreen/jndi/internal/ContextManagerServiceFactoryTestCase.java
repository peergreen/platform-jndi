package com.peergreen.jndi.internal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIContextManager;
import org.testng.annotations.Test;

/**
 * A {@code ContextManagerServiceFactoryTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class ContextManagerServiceFactoryTestCase{

    @Test
    public void testStart() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        ContextManagerServiceFactory factory = new ContextManagerServiceFactory(bundleContext);

        factory.start();
        verify(bundleContext).registerService(anyString(), anyObject(), any(Dictionary.class));
    }

    @Test
    public void testGetService() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        Bundle bundle = mock(Bundle.class);

        when(bundle.getBundleContext()).thenReturn(bundleContext);

        ContextManagerServiceFactory factory = new ContextManagerServiceFactory(bundleContext);

        Object o = factory.getService(bundle, null);
        assertThat(o, is( notNullValue() ));
        assertThat(o, is( instanceOf(JNDIContextManager.class) ));
    }

    @Test
    public void testUngetService() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        ContextManager manager = spy(new ContextManager(bundleContext));

        ContextManagerServiceFactory factory = new ContextManagerServiceFactory(bundleContext);

        factory.ungetService(null, null, manager);

        verify(manager).release();
   }
}
