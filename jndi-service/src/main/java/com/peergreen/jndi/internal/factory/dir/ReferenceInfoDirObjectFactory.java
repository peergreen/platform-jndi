package com.peergreen.jndi.internal.factory.dir;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import com.peergreen.jndi.internal.factory.ReferenceInfoObjectFactory;

/**
 * A {@code ReferenceInfoDirObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class ReferenceInfoDirObjectFactory extends ReferenceInfoObjectFactory implements DirObjectFactory {

    private DirObjectFactory delegate;

    public ReferenceInfoDirObjectFactory(Reference reference, DirObjectFactory delegate) {
        super(reference, delegate);
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        return delegate.getObjectInstance(obj, name, nameCtx, environment, attrs);
    }
}
