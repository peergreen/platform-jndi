package com.peergreen.jndi.internal.strategy;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;
import com.peergreen.osgi.toolkit.finder.impl.Finders;

/**
 * A {@code InitialContextFactoryBackingStrategy} implements the following algorithm (§126.3.2.2):
 *
 * <p>
 *  If the environment does not contain a value for the {@code java.naming.factory.initial} property
 *  then the following steps must be used to find a backing {@code Context} object.
 *  <ol>
 *   <li>
 *    Get all the {@code InitialContextFactoryBuilder} services. For each such service, in
 *    ranking order, do:
 *    <ul>
 *     <li>
 *      Ask the {@code InitialContextFactoryBuilder} service to create a new {@code InitialContextFactory}
 *      object. If this is null, then continue with the next service.
 *     </li>
 *     <li>Create the backing Context object with the found Initial Context Factory service and return it.</li>
 *    </ul>
 *   </li>
 *   <li>
 *    Get all the {@code InitialContextFactory} services. For each such service, in ranking order, do:
 *    <ul>
 *     <li>
 *      Ask the {@code InitialContextFactory} service to create a new {@code Context} object. If this
 *      is null then continue with the next service otherwise create a new {@code Context} with the
 *      created {@code Context} as the backing {@code Context}.
 *     </li>
 *    </ul>
 *   </li>
 *   <li>
 *    If no {@code Context} has been found, an initial {@code Context} is returned without any backing.
 *    This returned initial {@code Context} can then only be used to perform URL based lookups.
 *   </li>
 *  </ol>
 * </p>
 *
 * @author Guillaume Sauthier
 */
public class NoImplementationClassSpecifiedStrategy<T extends Context> extends AbstractBackingContextStrategy<T> {

    /**
     * Expected Context type (Context/DirContext)
     */
    private Class<T> expectedType;

    public NoImplementationClassSpecifiedStrategy(Class<T> expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    protected T findBackingContext() throws NamingException {

        Hashtable<?, ?> table = getEnvironment().getAsHashtable();

        // 1. Iterates through all the InitialContextFactoryBuilder(s)
        // -----------------------------------------------------------------------
        IFindable<InitialContextFactoryBuilder> request2 = Finders.find(getBundleContext(), InitialContextFactoryBuilder.class)
                                                          .orderWith(serviceRanking());

        List<IFind<InitialContextFactoryBuilder>> finds2 = request2.listFinds();
        for (IFind<InitialContextFactoryBuilder> find : finds2) {
            InitialContextFactoryBuilder builder = find.getService();
            try {
                InitialContextFactory factory = builder.createInitialContextFactory(table);
                if (factory != null) {
                    Context context = factory.getInitialContext(table);
                    if ((context != null) && (expectedType.isAssignableFrom(context.getClass()))) {
                        // Store the Find
                        builderFind = find;
                        return expectedType.cast(context);
                    }
                }
                // release the service
                find.release();
            } catch (NamingException ne) {
                // release the service
                find.release();
            }
        }

        // 2. Iterates through all the InitialContextFactory(ies)
        // -----------------------------------------------------------------------
        IFindable<InitialContextFactory> request = Finders.find(getBundleContext(), InitialContextFactory.class)
                                                          .orderWith(serviceRanking());
        List<IFind<InitialContextFactory>> finds = request.listFinds();
        for (IFind<InitialContextFactory> find : finds) {
            InitialContextFactory factory = find.getService();
            try {
                Context context = factory.getInitialContext(table);
                if ((context != null) && (expectedType.isAssignableFrom(context.getClass()))) {
                    // Store the Find
                    factoryFind = find;
                    return expectedType.cast(context);
                }
                // release the service
                find.release();
            } catch (NamingException ne) {
                // release the service
                find.release();
                throw ne;
            }
        }

        // 3. Return a limited view Context
        // -----------------------------------------------------------------------
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                                      new Class<?>[] {expectedType},
                                      new UnsupportedInvocationHandler());
        return expectedType.cast(proxy);
    }

    private class UnsupportedInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            throw new NamingException("Method " + method + " is unsupported for this Context");
        }
    }
}