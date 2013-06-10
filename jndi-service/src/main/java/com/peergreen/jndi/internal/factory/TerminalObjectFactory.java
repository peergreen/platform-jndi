package com.peergreen.jndi.internal.factory;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * A {@code TerminalObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class TerminalObjectFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        // Step 7
        // -------------------------------------------------------------------
        // If no ObjectFactory implementations can be located to resolve the
        // given description object, the description object is returned.
        // -------------------------------------------------------------------
        return obj;

    }
}
