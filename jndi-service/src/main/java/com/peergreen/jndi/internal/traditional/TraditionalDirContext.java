package com.peergreen.jndi.internal.traditional;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.osgi.service.jndi.JNDIContextManager;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code TraditionalDirContext} is ...
 *
 * @author Guillaume Sauthier
 */
public class TraditionalDirContext extends TraditionalContext implements DirContext {

    private DirContext delegate;

    public TraditionalDirContext(IFind<JNDIContextManager> manager, DirContext delegate) {
        super(manager, delegate);
        this.delegate = delegate;
    }

    public Attributes getAttributes(Name name) throws NamingException {
        return delegate.getAttributes(name);
    }

    public Attributes getAttributes(String name) throws NamingException {
        return delegate.getAttributes(name);
    }

    public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
        return delegate.getAttributes(name, attrIds);
    }

    public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
        return delegate.getAttributes(name, attrIds);
    }

    public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException {
        delegate.modifyAttributes(name, mod_op, attrs);
    }

    public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
        delegate.modifyAttributes(name, mod_op, attrs);
    }

    public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
        delegate.modifyAttributes(name, mods);
    }

    public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
        delegate.modifyAttributes(name, mods);
    }

    public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
        delegate.bind(name, obj, attrs);
    }

    public void bind(String name, Object obj, Attributes attrs) throws NamingException {
        delegate.bind(name, obj, attrs);
    }

    public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
        delegate.rebind(name, obj, attrs);
    }

    public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
        delegate.rebind(name, obj, attrs);
    }

    public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
        return delegate.createSubcontext(name, attrs);
    }

    public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
        return delegate.createSubcontext(name, attrs);
    }

    public DirContext getSchema(Name name) throws NamingException {
        return delegate.getSchema(name);
    }

    public DirContext getSchema(String name) throws NamingException {
        return delegate.getSchema(name);
    }

    public DirContext getSchemaClassDefinition(Name name) throws NamingException {
        return delegate.getSchemaClassDefinition(name);
    }

    public DirContext getSchemaClassDefinition(String name) throws NamingException {
        return delegate.getSchemaClassDefinition(name);
    }

    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        return delegate.search(name, matchingAttributes, attributesToReturn);
    }

    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        return delegate.search(name, matchingAttributes, attributesToReturn);
    }

    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
        return delegate.search(name, matchingAttributes);
    }

    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
        return delegate.search(name, matchingAttributes);
    }

    public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
        return delegate.search(name, filter, cons);
    }

    public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
        return delegate.search(name, filter, cons);
    }

    public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        return delegate.search(name, filterExpr, filterArgs, cons);
    }

    public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        return delegate.search(name, filterExpr, filterArgs, cons);
    }
}
