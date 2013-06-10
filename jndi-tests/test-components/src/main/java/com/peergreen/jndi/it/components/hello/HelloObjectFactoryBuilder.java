package com.peergreen.jndi.it.components.hello;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * A {@code HelloObjectFactoryBuilder} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class HelloObjectFactoryBuilder implements ObjectFactoryBuilder {

    public ObjectFactory createObjectFactory(final Object obj,
                                             final Hashtable<?, ?> environment) throws NamingException {
        return new HelloObjectFactory();
    }
}
