package com.peergreen.jndi.internal.context;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code UrlLookupsAllowedContext} is a Context that overrides lookup
 * operations to add support of URL based lookups.
 *
 * @author Guillaume Sauthier
 */
public class UrlLookupsSupportContext<T extends Context> extends ForwardingContext<T> {

    /**
     * Delegating Context.
     */
    private T delegate;

    /**
     * Dynamically gets URL Context.
     */
    private UrlContextFinder urlContextFinder;

    public UrlLookupsSupportContext(T delegate) {
        this.delegate = delegate;
    }

    public void setUrlContextFinder(UrlContextFinder urlContextFinder) {
        this.urlContextFinder = urlContextFinder;
    }

    @Override
    protected T delegate() throws NamingException {
        return delegate;
    }

    private Context delegateToUrlContext(String name) throws NamingException {
        Context urlContext = findUrlContext(name);
        if (urlContext != null) {
            return urlContext;
        }
        // Fail fast
        throw new NamingException("Cannot find any URLContext ObjectFactory for URL '" + name + "'");
    }

    private Context findUrlContext(final String name) throws NamingException {
        return urlContextFinder.find(name);
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            return delegateToUrlContext(name.toString()).lookup(name);
        }
        return super.lookup(name);
    }

    @Override
    public Object lookup(String name) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            return delegateToUrlContext(name).lookup(name);
        }
        return super.lookup(name);
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            return delegateToUrlContext(name.toString()).lookupLink(name);
        }
        return super.lookupLink(name);
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            return delegateToUrlContext(name).lookupLink(name);
        }
        return super.lookupLink(name);
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            delegateToUrlContext(name.toString()).bind(name, obj);
            return;
        }
        super.bind(name, obj);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            delegateToUrlContext(name).bind(name, obj);
            return;
        }
        super.bind(name, obj);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            delegateToUrlContext(name.toString()).rebind(name, obj);
            return;
        }
        super.rebind(name, obj);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            delegateToUrlContext(name).rebind(name, obj);
            return;
        }
        super.rebind(name, obj);
    }

    @Override
    public void unbind(Name name) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            delegateToUrlContext(name.toString()).unbind(name);
            return;
        }
        super.unbind(name);
    }

    @Override
    public void unbind(String name) throws NamingException {
        if (Utils.isUrlScheme(name)) {
            delegateToUrlContext(name).unbind(name);
            return;
        }
        super.unbind(name);
    }
}
