package com.peergreen.jndi.internal.context;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import com.peergreen.jndi.internal.IContextManagerAssociation;

/**
 * A {@code AssociatedContext} is ...
 *
 * @author Guillaume Sauthier
 */
public class AssociatedContext<T extends Context> extends ForwardingContext<T> {

    private T delegate;
    private IContextManagerAssociation association;

    public AssociatedContext(T delegate, IContextManagerAssociation association) {
        this.delegate = delegate;
        this.association = association;
        this.association.associate(this);
    }

    @Override
    protected T delegate() throws NamingException {
        return delegate;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        Object o = super.lookup(name);
        if (o instanceof Context) {
            return createAssociatedContext((Context) o, association);
        }
        return o;
    }

    @Override
    public Object lookup(String name) throws NamingException {
        Object o = super.lookup(name);
        if (o instanceof Context) {
            return createAssociatedContext((Context) o, association);
        }
        return o;
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        Object o = super.lookupLink(name);
        if (o instanceof Context) {
            return createAssociatedContext((Context) o, association);
        }
        return o;
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        Object o = super.lookupLink(name);
        if (o instanceof Context) {
            return createAssociatedContext((Context) o, association);
        }
        return o;
    }

    @Override
    public void close() throws NamingException {
        try {
            super.close();
        } finally {
            association.dissociate(this);
        }
    }

    protected AssociatedContext createAssociatedContext(Context context, IContextManagerAssociation association) {
        return new AssociatedContext<Context>(context, association);
    }

}
