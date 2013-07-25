package com.peergreen.jndi.internal.context;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.jndi.internal.env.Environment;

/**
 * User: guillaume
 * Date: 25/07/13
 * Time: 15:36
 */
public class UrlLookupsSupportContextTestCase {

    @Mock
    private Context delegate;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    @Mock
    private ServiceReference reference;

    @Mock
    private ObjectFactory factory;

    @Mock
    private Context urlContext;

    private UrlContextFinder finder;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        finder = new UrlContextFinder();
        finder.setBundleContext(bundleContext);
        finder.setEnvironment(new Environment());

        when(bundleContext.getAllServiceReferences(anyString(), anyString())).thenReturn(new ServiceReference[] {reference});
        when(reference.isAssignableTo(any(Bundle.class), anyString())).thenReturn(true);
        when(reference.getBundle()).thenReturn(bundle);
        when(bundleContext.getService(reference)).thenReturn(factory);
        when(factory.getObjectInstance(anyObject(), any(Name.class), any(Context.class), any(Hashtable.class))).thenReturn(urlContext);
    }

    @Test
    public void testRebind() throws Exception {
        UrlLookupsSupportContext<Context> context = new UrlLookupsSupportContext<>(delegate);
        context.setUrlContextFinder(finder);

        context.rebind("scm:titi", "Coucou");

        verify(urlContext).rebind("scm:titi", "Coucou");
        verifyZeroInteractions(delegate);
    }

    @Test
    public void testUnbind() throws Exception {
        UrlLookupsSupportContext<Context> context = new UrlLookupsSupportContext<>(delegate);
        context.setUrlContextFinder(finder);

        context.unbind("scm:titi");

        verify(urlContext).unbind("scm:titi");
        verifyZeroInteractions(delegate);
    }

    @Test
    public void testBind() throws Exception {
        UrlLookupsSupportContext<Context> context = new UrlLookupsSupportContext<>(delegate);
        context.setUrlContextFinder(finder);

        context.bind("scm:titi", "Coucou");

        verify(urlContext).bind("scm:titi", "Coucou");
        verifyZeroInteractions(delegate);
    }
}
