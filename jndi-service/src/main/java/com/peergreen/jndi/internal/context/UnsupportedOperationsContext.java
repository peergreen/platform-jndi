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
 * A {@code UnsupportedOperationsContext} throws an Exception for all its methods.
 *
 * @author Guillaume Sauthier
 */
public class UnsupportedOperationsContext implements Context {

    /**
     * Message of the Exception thrown by all the methods of this class.
     */
    public static final String UNSUPPORTED_OPERATION_MESSAGE = "Unsupported operation.";

    public Object lookup(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Object lookup(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void bind(Name name, Object obj) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void bind(String name, Object obj) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void rebind(Name name, Object obj) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void rebind(String name, Object obj) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void unbind(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void unbind(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void rename(String oldName, String newName) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void destroySubcontext(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void destroySubcontext(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Context createSubcontext(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Context createSubcontext(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Object lookupLink(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Object lookupLink(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public NameParser getNameParser(Name name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public NameParser getNameParser(String name) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public String composeName(String name, String prefix) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void close() throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public String getNameInNamespace() throws NamingException {
        throw new NamingException(UNSUPPORTED_OPERATION_MESSAGE);
    }
}
