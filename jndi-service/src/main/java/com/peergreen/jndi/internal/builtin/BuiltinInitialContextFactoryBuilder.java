package com.peergreen.jndi.internal.builtin;

import static org.osgi.service.log.LogService.LOG_WARNING;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.log.LogService;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code BuiltinInitialContextFactoryBuilder} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class BuiltinInitialContextFactoryBuilder implements InitialContextFactoryBuilder {

    /**
     * Logger.
     */
    @Requires
    private LogService logger;

    /**
     * System ClassLoader.
     */
    private ClassLoader systemClassLoader;

    /**
     * Creates an initial context factory using the specified
     * environment.
     * <p/>
     * The environment parameter is owned by the caller.
     * The implementation will not modify the object or keep a reference
     * to it, although it may keep a reference to a clone or copy.
     *
     * @param environment Environment used in creating an initial
     *                    context implementation. Can be null.
     * @return A non-null initial context factory.
     * @throws javax.naming.NamingException If an initial context factory could not be created.
     */
    public InitialContextFactory createInitialContextFactory(final Hashtable<?, ?> environment) throws NamingException {

        // Specification extract (§126.5.6: JRE Context Providers)
        // ------------------------------------------------------------------------------
        // When this built-in Initial Context Factory Builder is called to create an
        // InitialContextFactory object it must look in the environment properties that
        // were given as an argument and extract the java.naming.factory.initial property;
        // this property contains the name of the class of a provider. The built-in Initial
        // Context Factory Builder then must use the bootstrap class loader to load the
        // given InitialContextFactory class and creates a new instance with the no
        // arguments constructor and return it. If this fails, it must return null. This
        // mechanism will allow loading of any built-in providers.

        String name = Utils.asString(environment.get(Context.INITIAL_CONTEXT_FACTORY));

        if (!Utils.isNullOrEmpty(name)) {
            // Got an InitialContextFactory class name to load
            try {
                Class<? extends InitialContextFactory> factoryClass = systemClassLoader.loadClass(name)
                        .asSubclass(InitialContextFactory.class);
                return factoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                logger.log(LOG_WARNING,
                           "Cannot load '" + name + "' from System ClassLoader",
                           e);
            } catch (InstantiationException e) {
                logger.log(LOG_WARNING,
                           "Cannot instantiate '" + name + "'",
                           e);
            } catch (IllegalAccessException e) {
                logger.log(LOG_WARNING,
                           "Cannot instantiate '" + name + "'",
                           e);
            }


        }
        return null;
    }

    /**
     * Start the component.
     */
    @Validate
    private void start() {

        // Init the System ClassLoader
        systemClassLoader = ClassLoader.getSystemClassLoader();
    }
}
