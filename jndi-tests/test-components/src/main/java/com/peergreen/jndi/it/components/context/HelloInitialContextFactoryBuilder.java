package com.peergreen.jndi.it.components.context;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * A {@code HelloInitialContextFactoryBuilder} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class HelloInitialContextFactoryBuilder implements InitialContextFactoryBuilder {
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
        return new HelloInitialContextFactory();
    }
}
