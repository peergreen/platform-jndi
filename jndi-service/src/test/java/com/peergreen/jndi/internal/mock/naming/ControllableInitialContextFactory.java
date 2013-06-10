package com.peergreen.jndi.internal.mock.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * A {@code TestInitialContextFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class ControllableInitialContextFactory implements InitialContextFactory {

    /**
     * Context to be returned (can be null).
     */
    private Context backing;

    public ControllableInitialContextFactory(final Context backing) {
        this.backing = backing;
    }

    public Context getInitialContext(final Hashtable<?, ?> environment) throws NamingException {
        return backing;
    }
}
