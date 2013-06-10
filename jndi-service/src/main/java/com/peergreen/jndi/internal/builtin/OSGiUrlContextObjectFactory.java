package com.peergreen.jndi.internal.builtin;

import static org.osgi.service.jndi.JNDIConstants.JNDI_URLSCHEME;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import com.peergreen.jndi.internal.builtin.osgi.OSGiServiceContext;
import com.peergreen.jndi.internal.env.Environment;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code OSGiUrlContextObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides
public class OSGiUrlContextObjectFactory implements ObjectFactory {

    public static final String OSGI_URL_SCHEME = "osgi";

    @ServiceProperty(name = JNDI_URLSCHEME,
                     value = OSGI_URL_SCHEME)
    private String jndiUrlScheme;

    public Object getObjectInstance(final Object obj,
                                    final Name name,
                                    final Context nameCtx,
                                    final Hashtable<?, ?> environment) throws Exception {

        Environment env = new Environment(Utils.asMap(environment));

        if (obj == null) {
            return new OSGiServiceContext(env);
        }

        if (obj instanceof String) {
            String url = (String) obj;
            Context context = null;
            try {
                context = new OSGiServiceContext(env);
                return context.lookup(url);
            } finally {
                if (context != null) {
                    context.close();
                }
            }

        }

        if (obj instanceof String[]) {
            String[] urls = (String[]) obj;

            // Little check on array size
            if (urls.length == 0) {
                throw new InvalidNameException("When an array of String is used, the array size must be at least 1.");
            }

            // Try all the given URLs
            AggregateNamingException ane = null;

            Context context = null;
            try {
                context = new OSGiServiceContext(env);

                for(String url : urls) {
                    try {
                        return context.lookup(url);
                    } catch (NamingException ne) {

                        // Aggregate the Exceptions
                        if (ane == null) {
                            ane = new AggregateNamingException("NamingException(s) occurred (details below).");
                        }
                        ane.addCause(ne);
                    }
                }

                // We only reach this point if nothing could be returned
                // That means there were errors
                throw ane;


            } finally {
                if (context != null) {
                    context.close();
                }
            }
        }
        return null;
    }
}
