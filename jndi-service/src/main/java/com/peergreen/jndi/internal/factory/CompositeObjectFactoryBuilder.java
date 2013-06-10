package com.peergreen.jndi.internal.factory;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code MyObjectFactoryBuilder} is ...
 *
 * @author Guillaume Sauthier
 */
public class CompositeObjectFactoryBuilder implements ObjectFactoryBuilder {

    private BundleContext bundleContext;

    public CompositeObjectFactoryBuilder(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public ObjectFactory createObjectFactory(Object refInfo, Hashtable<?, ?> environment) throws NamingException {

        CompositeObjectFactory<ObjectFactory> composite = new CompositeObjectFactory<ObjectFactory>();

        // Step 1
        // -------------------------------------------------------------------
        // If the description object is an instance of Referenceable, then get
        // the corresponding Reference object and use this as the description
        // object.
        // -------------------------------------------------------------------
        Reference reference = null;
        if (refInfo instanceof Reference) {
            reference = (Reference) refInfo;
        } else {
            if (refInfo instanceof Referenceable) {
                Referenceable referenceable = (Referenceable) refInfo;
                reference = referenceable.getReference();
            }
        }

        if (reference != null) {
            ObjectFactory delegate;
            String objectFactoryName = reference.getFactoryClassName();
            if (!Utils.isNullOrEmpty(objectFactoryName)) {
                delegate = new FactoryNameSpecifiedObjectFactory(bundleContext, objectFactoryName);
            } else {
                delegate = new NoFactoryNameObjectFactory(bundleContext);
            }
            composite.getFactories().add(new ReferenceInfoObjectFactory(reference, delegate));
        }

        composite.getFactories().add(new IteratesOnBuildersObjectFactory(bundleContext));

        composite.getFactories().add(new IteratesOnFactoriesObjectFactory(bundleContext));

        composite.getFactories().add(new TerminalObjectFactory());

        return composite;
    }
}
