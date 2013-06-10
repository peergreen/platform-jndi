package com.peergreen.jndi.internal.traditional;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.find;

import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIProviderAdmin;
import com.peergreen.jndi.internal.IBundleContextFinder;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.finder.DefaultContextFinder;
import com.peergreen.jndi.internal.util.Utils;
import com.peergreen.osgi.toolkit.finder.IFind;

/**
 * A {@code TraditionalObjectDirFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class TraditionalDirObjectFactory implements DirObjectFactory {

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Map<String, Object> env = Utils.asMap(environment);

        IFind<JNDIProviderAdmin> provider = findProviderAdmin(env);
        if (provider == null) {
            // The description object must be returned
            return  obj;
        }

        // There is no need to wrap the returned instance
        try {
            return provider.getService().getObjectInstance(obj, name, nameCtx, env);
        } finally {
            // Release the service ASAP
            provider.release();
        }
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        Map<String, Object> env = Utils.asMap(environment);

        IFind<JNDIProviderAdmin> provider = findProviderAdmin(env);
        if (provider == null) {
            // The description object must be returned
            return  obj;
        }

        // There is no need to wrap the returned instance
        try {
            return provider.getService().getObjectInstance(obj, name, nameCtx, env, attrs);
        } finally {
            // Release the service ASAP
            provider.release();
        }
    }

    /**
     *
     * @param env the user environment.
     * @return the found ProviderAdmin (or {@literal null} if not found)
     */
    private IFind<JNDIProviderAdmin> findProviderAdmin(final Map<String, Object> env) {
        // Find a valid BundleContext
        IBundleContextFinder finder = new DefaultContextFinder(new Environment(env));
        BundleContext bundleContext = finder.findContext();
        if (bundleContext == null) {
            return null;
        }

        // Find the provider in behalf of the caller BundleContext
        return find(bundleContext, JNDIProviderAdmin.class).firstFind();
    }

}
