package com.peergreen.jndi.it.components.context;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * A {@code HelloInitialContextFactory} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides(specifications = {InitialContextFactory.class,
                            HelloInitialContextFactory.class})
public class HelloInitialContextFactory implements InitialContextFactory {
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return new HelloContext();
    }
}
