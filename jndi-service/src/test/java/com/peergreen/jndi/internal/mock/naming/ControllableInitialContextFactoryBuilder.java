package com.peergreen.jndi.internal.mock.naming;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

/**
 * A {@code ControllableInitialContextFactoryBuilder} is ...
 *
 * @author Guillaume Sauthier
 */
public class ControllableInitialContextFactoryBuilder implements InitialContextFactoryBuilder {
    /**
     * Factory to be returned.
     */
    private InitialContextFactory factory;

    public ControllableInitialContextFactoryBuilder(InitialContextFactory factory) {
        this.factory = factory;
    }

    public InitialContextFactory createInitialContextFactory(final Hashtable<?, ?> environment) throws NamingException {
        return factory;
    }
}
