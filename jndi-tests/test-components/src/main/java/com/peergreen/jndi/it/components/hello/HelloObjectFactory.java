package com.peergreen.jndi.it.components.hello;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * A {@code TestableObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
@Provides(specifications = {ObjectFactory.class,
                            HelloObjectFactory.class})
public class HelloObjectFactory implements ObjectFactory {

    public Object getObjectInstance(final Object obj,
                                    final Name name,
                                    final Context nameCtx,
                                    final Hashtable<?, ?> environment) throws Exception {

        if (!(obj instanceof Reference)) {
            return null;
        }

        Reference ref = (Reference) obj;
        if (Hello.class.getName().equals(ref.getClassName())) {

            // This ref is for us
            String message = (String) ((StringRefAddr) ref.get("message")).getContent();

            return new Hello(message);

        }
        return null;
    }
}
