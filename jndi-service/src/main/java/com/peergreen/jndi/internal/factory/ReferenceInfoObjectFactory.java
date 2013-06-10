package com.peergreen.jndi.internal.factory;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * A {@code ReferenceInfoObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class ReferenceInfoObjectFactory implements ObjectFactory {

    private Reference reference;

    private ObjectFactory delegate;

    public ReferenceInfoObjectFactory(Reference reference, ObjectFactory delegate) {
        this.reference = reference;
        this.delegate = delegate;
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        return delegate.getObjectInstance(reference, name, nameCtx, environment);
    }
}
