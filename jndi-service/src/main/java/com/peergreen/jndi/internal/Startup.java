package com.peergreen.jndi.internal;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceController;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.log.LogService;
import com.peergreen.jndi.internal.traditional.TraditionalDirObjectFactory;
import com.peergreen.jndi.internal.traditional.TraditionalInitialContextFactory;

/**
 * A {@code Startup} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class Startup implements Started {

    @Requires
    private LogService logger;

    @ServiceController(false)
    private boolean registered;

    @Validate
    public void start() {

        // Try to register the JVM wide Builders
        try {
            NamingManager.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {

                public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
                    return new TraditionalInitialContextFactory();
                }
            });
        } catch (NamingException e) {
            logger.log(LogService.LOG_WARNING, "Cannot register our JVM wide InitialContextFactoryBuilder", e);
        }

        try {
            NamingManager.setObjectFactoryBuilder(new ObjectFactoryBuilder() {

                public ObjectFactory createObjectFactory(Object obj, Hashtable<?, ?> environment) throws NamingException {
                    return new TraditionalDirObjectFactory();
                }
            });
        } catch (NamingException e) {
            logger.log(LogService.LOG_WARNING, "Cannot register our JVM wide ObjectFactoryBuilder", e);
        }

        // Publish the marker interface
        registered = true;
    }
}
