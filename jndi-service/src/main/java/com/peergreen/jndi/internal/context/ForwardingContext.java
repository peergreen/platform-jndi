package com.peergreen.jndi.internal.context;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * A {@code ForwardingContext} is ...
 *
 * @author Guillaume Sauthier
 */
public abstract class ForwardingContext<T extends Context> implements Context {

    protected abstract T delegate() throws NamingException;

    public Object lookup(Name name) throws NamingException {
        return delegate().lookup(name);
    }

    public Object lookup(String name) throws NamingException {
        return delegate().lookup(name);
    }

    public void bind(Name name, Object obj) throws NamingException {
        delegate().bind(name, obj);
    }

    public void bind(String name, Object obj) throws NamingException {
        delegate().bind(name, obj);
    }

    public void rebind(Name name, Object obj) throws NamingException {
        delegate().rebind(name, obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        delegate().rebind(name, obj);
    }

    public void unbind(Name name) throws NamingException {
        delegate().unbind(name);
    }

    public void unbind(String name) throws NamingException {
        delegate().unbind(name);
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        delegate().rename(oldName, newName);
    }

    public void rename(String oldName, String newName) throws NamingException {
        delegate().rename(oldName, newName);
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        return delegate().list(name);
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return delegate().list(name);
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return delegate().listBindings(name);
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return delegate().listBindings(name);
    }

    public void destroySubcontext(Name name) throws NamingException {
        delegate().destroySubcontext(name);
    }

    public void destroySubcontext(String name) throws NamingException {
        delegate().destroySubcontext(name);
    }

    public Context createSubcontext(Name name) throws NamingException {
        return delegate().createSubcontext(name);
    }

    public Context createSubcontext(String name) throws NamingException {
        return delegate().createSubcontext(name);
    }

    public Object lookupLink(Name name) throws NamingException {
        return delegate().lookupLink(name);
    }

    public Object lookupLink(String name) throws NamingException {
        return delegate().lookupLink(name);
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return delegate().getNameParser(name);
    }

    public NameParser getNameParser(String name) throws NamingException {
        return delegate().getNameParser(name);
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        return delegate().composeName(name, prefix);
    }

    public String composeName(String name, String prefix) throws NamingException {
        return delegate().composeName(name, prefix);
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return delegate().addToEnvironment(propName, propVal);
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return delegate().removeFromEnvironment(propName);
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return delegate().getEnvironment();
    }

    public void close() throws NamingException {
        delegate().close();
    }

    public String getNameInNamespace() throws NamingException {
        return delegate().getNameInNamespace();
    }
}
