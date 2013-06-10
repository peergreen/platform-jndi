package com.peergreen.jndi.internal.builtin.jre;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.jndi.JNDIConstants;

/**
 * A {@code ObjectFactoryDelegate} is a component that will delegate all its
 * methods to a wrapped ObjectFactory loaded before hand.
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class ObjectFactoryDelegate implements ObjectFactory {

    /**
     * Simple enumeration of the acceptable value for the selected loader configuration property.
     */
    public enum SelectedLoader {SYSTEM, THREAD, BUNDLE}

    /**
     * Support for the service property that holds the URL scheme.
     */
    @ServiceProperty(name = JNDIConstants.JNDI_URLSCHEME,
                     mandatory = true)
    private String jndiUrlScheme;

    /**
     * The delegate ObjectFactory fully-qualified classname.
     */
    @Property (name = "object.factory.classname",
               mandatory = true)
    private String objectFactoryClassname;

    /**
     * Specify which ClassLoader will be used to load the ObjectFactory class.
     * Possible values are the one of the {@link SelectedLoader} enum.
     */
    @Property(name = "selected.loader",
              value = "SYSTEM")
    private String selectedLoader;

    /**
     * Indication of which ClassLoader to use for loading the delegate factory.
     */
    private SelectedLoader selected;

    /**
     * Delegate ObjectFactory (lazy init).
     */
    private ObjectFactory delegate;

    @Validate
    private void init() {
        // Convert the selected loader into an enum
        // TODO with iPOJO 1.8.0, enum will be supported as properties
        selected = SelectedLoader.valueOf(selectedLoader.toUpperCase().trim());
    }

    public Object getObjectInstance(final Object obj,
                                    final Name name,
                                    final Context nameCtx,
                                    final Hashtable<?, ?> environment) throws Exception {

        // Lazy init of the delegate
        if (delegate == null) {
            delegate = createUrlContextFactory();
        }

        // Delegation
        return delegate.getObjectInstance(obj, name, nameCtx, environment);
    }

    /**
     * Creates an URL ObjectFactory from the given className.
     * Class will be loaded from the System ClassLoader.
     * @return an instance of the given class name
     * @throws Exception if something went wrong during class loading
     */
    private ObjectFactory createUrlContextFactory() throws Exception {

        try {
            ClassLoader loader = getSelectedLoader();
            Class<? extends ObjectFactory> clazz = loader.loadClass(objectFactoryClassname)
                                                         .asSubclass(ObjectFactory.class);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (InstantiationException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        }
    }

    /**
     * @return the wanted ClassLoader
     */
    private ClassLoader getSelectedLoader() {
        switch (selected) {
            case SYSTEM:
                return ClassLoader.getSystemClassLoader();
            case THREAD:
                return Thread.currentThread().getContextClassLoader();
            case BUNDLE:
                return ObjectFactoryDelegate.class.getClassLoader();
        }

        // Should never get here, anyway, the default is the system loader ...
        return ClassLoader.getSystemClassLoader();
    }


}
