package com.peergreen.jndi.internal.factory.dir;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import com.peergreen.jndi.internal.factory.CompositeObjectFactory;

/**
 * A {@code CompositeDirObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class CompositeDirObjectFactory extends CompositeObjectFactory<DirObjectFactory> implements DirObjectFactory {

    public CompositeDirObjectFactory() {
        super();
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        for (DirObjectFactory factory : getFactories()) {
            Object o = factory.getObjectInstance(obj, name, nameCtx, environment, attrs);
            if (o != null) {
                return o;
            }
        }

        return null;
    }
}
