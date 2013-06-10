package com.peergreen.jndi.internal.strategy;

import static com.peergreen.osgi.toolkit.finder.impl.Finders.serviceRanking;

import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.BundleContext;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;
import com.peergreen.osgi.toolkit.finder.impl.Finders;
import org.ow2.util.osgi.toolkit.filter.Filters;

/**
 * A {@code InitialContextFactoryBackingStrategy} implements the following algorithm (§126.3.2.1):
 *
 * <p>
 *  If the implementation class is specified, a JNDI Provider is searched in the
 *  service registry with the following steps, which stop when a backing {@link Context}
 *  can be created:
 * <ol>
 *  <li>Find a service <b>in ranking order</b> that has a name matching the given
 *      implementation class name as well as the {@link InitialContextFactory} class
 *      name. The searching must take place through the {@link BundleContext} of the
 *      requesting bundle but must not require that the requesting bundle imports the
 *      package of the implementation class. If such a matching Initial Context Factory
 *      service is found, it must be used to construct the {@link Context} object that
 *      will act as the backing Context.
 *  </li>
 *  <li>Get all the Initial Context Factory Builder services. For each such service,
 *      <b>in ranking order</b>:
 *      <ul>
 *       <li>Ask the Initial Context Factory Builder service to create a new
 *           {@link InitialContextFactory} object. If this is null then continue with
 *           the next service.
 *       </li>
 *       <li>Create the Context with the found Initial Context Factory and return it.</li>
 *      </ul>
 *  </li>
 *  <li>If no backing Context could be found using these steps, then the JNDI Context
 *      Manager service must throw a {@link javax.naming.NoInitialContextException}.
 *  </li>
 * </ol>
 * </p>
 *
 * @author Guillaume Sauthier
 */
public class ImplementationClassPresentInEnvironmentStrategy<T extends Context> extends AbstractBackingContextStrategy<T> {

    /**
     * InitialContextFactory fully qualified class name.
     */
    private String factoryName;

    /**
     * Expected Context type (Context/DirContext)
     */
    private Class<T> expectedType;

    public ImplementationClassPresentInEnvironmentStrategy(Class<T> expectedType) {
        this.expectedType = expectedType;
    }

    public void setFactoryName(final String factoryName) {
        this.factoryName = factoryName;
    }

    @Override
    public T getBackingContext() throws NamingException {

        // Add an assertion on factoryName presence
        assert factoryName != null;

        return super.getBackingContext();
    }

    protected T findBackingContext() throws NamingException {

        Hashtable<?, ?> table = getEnvironment().getAsHashtable();

        // 1. Find the right ICF from all the registered ICF under the given name
        // -----------------------------------------------------------------------
        IFindable<InitialContextFactory> request = Finders.find(getBundleContext(), InitialContextFactory.class)
                                                          .with(Filters.objectClass(factoryName))
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

        // 2. Iterates through all the ICFB
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

        // 3. throw a NoInitialContextException
        // -----------------------------------------------------------------------
        throw new NoInitialContextException("Cannot find a suitable Context provider from factory:" + factoryName);
    }

}
