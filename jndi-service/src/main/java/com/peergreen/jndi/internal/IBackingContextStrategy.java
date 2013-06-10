package com.peergreen.jndi.internal;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * A {@code IBackingContextStrategy} is an abstract around the way to obtains a Context dynamically.
 *
 * @author Guillaume Sauthier
 */
public interface IBackingContextStrategy<T extends Context> {

    /**
     * Finds a suitable backing Context.
     * @return a backing Context
     * @throws NamingException if it cannot find a suitable backing Context
     */
    T getBackingContext() throws NamingException;

    /**
     * Release the underlying backing Context.
     */
    void releaseBackingContext();
}
