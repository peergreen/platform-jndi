package com.peergreen.jndi.internal.factory.dir;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.factory.NoFactoryNameObjectFactory;

/**
 * A {@code NoFactoryNameDirObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class NoFactoryNameDirObjectFactory extends NoFactoryNameObjectFactory implements DirObjectFactory {
    public NoFactoryNameDirObjectFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        return getObjectInstance(obj, name, nameCtx, environment);
    }
}
