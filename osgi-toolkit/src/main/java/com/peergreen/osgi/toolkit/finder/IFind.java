package com.peergreen.osgi.toolkit.finder;

import org.osgi.framework.ServiceReference;

/**
 * A {@code IFind} is ...
 *
 * @author Guillaume Sauthier
 */
public interface IFind<T> {

    /**
     * @return the reference of this service.
     */
    ServiceReference getReference();

    /**
     * The result of this method is cached
     * @return the service instance
     */
    T getService();

    /**
     * Release the underlying service object.
     */
    void release();
}
