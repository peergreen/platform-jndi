package com.peergreen.jndi.internal.factory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * A {@code CompositeObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class CompositeObjectFactory<T extends ObjectFactory> implements ObjectFactory {

    private List<T> factories;

    public CompositeObjectFactory() {
        this.factories = new ArrayList<T>();
    }

    public List<T> getFactories() {
        return factories;
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        for (ObjectFactory factory : factories) {
            Object o = factory.getObjectInstance(obj, name, nameCtx, environment);
            if (o != null) {
                return o;
            }
        }

        return null;
    }
}
