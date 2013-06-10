package com.peergreen.jndi.internal.context;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.Name;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.peergreen.jndi.internal.IContextManagerAssociation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A {@code AssociatedContextTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class AssociatedContextTestCase {

    @Mock
    private Context delegate;

    @Mock
    private IContextManagerAssociation association;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLookupStringReturningNonContext() throws Exception {

        String name = "get";
        when(delegate.lookup(eq(name))).thenReturn(new Object());

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookup(name);

        assertThat( o, is( notNullValue() ));
        assertThat(o, is(not(instanceOf(AssociatedContext.class))));

        verify(delegate).lookup(eq(name));
    }

    @Test
    public void testLookupStringReturningContext() throws Exception {

        Context returned = mock(Context.class);
        String name = "get";
        when(delegate.lookup(eq(name))).thenReturn(returned);

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookup(name);

        assertThat( o, is( notNullValue() ));
        assertThat(o, is( instanceOf( AssociatedContext.class)));
        verify(association).associate(same(associated));
        verify(delegate).lookup(eq(name));
    }

    @Test
    public void testLookupNameReturningNonContext() throws Exception {

        Name name = new CompoundName("get", new Properties());
        when(delegate.lookup(eq(name))).thenReturn(new Object());

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookup(name);

        assertThat( o, is( notNullValue() ));
        assertThat(o, is(not(instanceOf(AssociatedContext.class))));
        verify(delegate).lookup(eq(name));
    }

    @Test
    public void testLookupNameReturningContext() throws Exception {

        Context returned = mock(Context.class);
        Name name = new CompoundName("get", new Properties());
        when(delegate.lookup(eq(name))).thenReturn(returned);

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookup(name);

        assertThat( o, is( notNullValue() ));
        assertThat( o ,  is ( instanceOf(AssociatedContext.class)));
        verify(association).associate(same(associated));
        verify(delegate).lookup(eq(name));
    }

    @Test
    public void testLookupLinkStringReturningNonContext() throws Exception {

        String name = "get";
        when(delegate.lookupLink(eq(name))).thenReturn(new Object());

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookupLink(name);

        assertThat( o, is( notNullValue() ));
        assertThat(o, is(not(instanceOf(AssociatedContext.class))));
        verify(delegate).lookupLink(eq(name));
    }

    @Test
    public void testLookupLinkStringReturningContext() throws Exception {

        Context returned = mock(Context.class);
        String name = "get";
        when(delegate.lookupLink(eq(name))).thenReturn(returned);

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookupLink(name);

        assertThat( o, is( notNullValue() ));
        assertThat(o, is( instanceOf( AssociatedContext.class)));
        verify(association).associate(same(associated));
        verify(delegate).lookupLink(eq(name));
    }

    @Test
    public void testLookupLinkNameReturningNonContext() throws Exception {

        Name name = new CompoundName("get", new Properties());
        when(delegate.lookupLink(eq(name))).thenReturn(new Object());

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookupLink(name);

        assertThat( o, is( notNullValue() ));
        assertThat(o, is(not(instanceOf(AssociatedContext.class))));
        verify(delegate).lookupLink(eq(name));
    }

    @Test
    public void testLookupLinkNameReturningContext() throws Exception {

        Context returned = mock(Context.class);
        Name name = new CompoundName("get", new Properties());
        when(delegate.lookupLink(eq(name))).thenReturn(returned);

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);

        Object o = associated.lookupLink(name);

        assertThat( o, is( notNullValue() ));
        assertThat( o ,  is ( instanceOf(AssociatedContext.class)));
        verify(association).associate(same(associated));
        verify(delegate).lookupLink(eq(name));
    }

    @Test
    public void testDissociateIsCalledOnExit() throws Exception {

        AssociatedContext<Context> associated = new AssociatedContext<Context>(delegate, association);
        associated.close();
        verify(delegate).close();
        verify(association).dissociate(same(associated));
    }
}
