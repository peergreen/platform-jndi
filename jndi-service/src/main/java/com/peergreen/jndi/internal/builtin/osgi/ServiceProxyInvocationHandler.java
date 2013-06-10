package com.peergreen.jndi.internal.builtin.osgi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.framework.ServiceException;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;

/**
 * A {@code ServiceProxyInvocationHandler} is ...
 *
 * @author Guillaume Sauthier
 */
public class ServiceProxyInvocationHandler implements InvocationHandler {

    /**
     * Currently used find.
     */
    private IFind<Object> find;

    /**
     * Finder request (denotes a dynamic proxy supporting bind/rebinds of underlying service).
     */
    private IFindable<Object> request;

    public ServiceProxyInvocationHandler(final IFind<Object> find) {
        this(find, null);
    }

    public ServiceProxyInvocationHandler(final IFind<Object> find,
                                         final IFindable<Object> request) {
        this.find = find;
        this.request = request;
    }

    /**
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {

        if ((find == null) || (find.getService() == null)) {
            // Service was unregistered
            if (request != null) {
                // Dynamically find another compatible service
                find = request.firstFind();

                if (find == null) {
                    // There is no available matching service at the time of execution
                    throw new ServiceException("No available compatible service",
                                               ServiceException.UNREGISTERED);
                }
            } else {
                // This is not a dynamic proxy, simply throw a ServiceException
                throw new ServiceException("Bound service was unregistered, dynamic proxy disabled",
                                           ServiceException.UNREGISTERED);
            }
        }

        return method.invoke(find.getService(), args);
    }
}
