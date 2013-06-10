package com.peergreen.jndi.internal.mock.naming;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * A {@code MockContext} is ...
 *
 * @author Guillaume Sauthier
 */
public class MockContext implements Context {
    private String id;

    public MockContext(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object lookup(Name name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object lookup(String name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void bind(Name name, Object obj) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void bind(String name, Object obj) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rebind(Name name, Object obj) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rebind(String name, Object obj) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unbind(Name name) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unbind(String name) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rename(String oldName, String newName) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroySubcontext(Name name) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroySubcontext(String name) throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Context createSubcontext(Name name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Context createSubcontext(String name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object lookupLink(Name name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object lookupLink(String name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public NameParser getNameParser(String name) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String composeName(String name, String prefix) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws NamingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getNameInNamespace() throws NamingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
