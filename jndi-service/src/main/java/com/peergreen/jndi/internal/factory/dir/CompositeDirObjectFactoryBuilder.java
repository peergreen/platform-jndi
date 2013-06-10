package com.peergreen.jndi.internal.factory.dir;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code MyObjectFactoryBuilder} is ...
 *
 * @author Guillaume Sauthier
 */
public class CompositeDirObjectFactoryBuilder implements ObjectFactoryBuilder {

    private BundleContext bundleContext;

    public CompositeDirObjectFactoryBuilder(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public ObjectFactory createObjectFactory(Object refInfo, Hashtable<?, ?> environment) throws NamingException {

        CompositeDirObjectFactory composite = new CompositeDirObjectFactory();

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
            DirObjectFactory delegate;
            String objectFactoryName = reference.getFactoryClassName();
            if (!Utils.isNullOrEmpty(objectFactoryName)) {
                delegate = new FactoryNameSpecifiedDirObjectFactory(bundleContext, objectFactoryName);
            } else {
                delegate = new NoFactoryNameDirObjectFactory(bundleContext);
            }
            composite.getFactories().add(new ReferenceInfoDirObjectFactory(reference, delegate));
        }

        composite.getFactories().add(new IteratesOnBuildersDirObjectFactory(bundleContext));

        composite.getFactories().add(new IteratesOnFactoriesDirObjectFactory(bundleContext));

        composite.getFactories().add(new TerminalDirObjectFactory());

        return composite;
    }
}
