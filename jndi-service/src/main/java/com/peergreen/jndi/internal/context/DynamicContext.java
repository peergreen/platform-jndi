package com.peergreen.jndi.internal.context;

import javax.naming.Context;
import javax.naming.NamingException;

import com.peergreen.jndi.internal.IBackingContextStrategy;

/**
 * A {@code DynamicContext} is ...
 *
 * @author Guillaume Sauthier
 */
public class DynamicContext<T extends Context> extends ForwardingContext<T> {

    /**
     * The backing Context strategy.
     */
    private IBackingContextStrategy<T> strategy;

    public void setStrategy(final IBackingContextStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public void start() throws NamingException {
        // Find the backing context
        this.delegate();
    }

    @Override
    protected T delegate() throws NamingException {
        // Fallback to default Context
        return strategy.getBackingContext();
    }

    @Override
    public void close() throws NamingException {
        try {
            super.close();
        } finally {
            // In all cases, releases resources
            strategy.releaseBackingContext();
        }
    }
}
