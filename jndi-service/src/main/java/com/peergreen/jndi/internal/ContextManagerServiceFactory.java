package com.peergreen.jndi.internal;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jndi.JNDIContextManager;

/**
 * A {@code ContextManagerServiceFactory} is ...
 *
 * @author Guillaume Sauthier
 */
@Component
public class ContextManagerServiceFactory implements ServiceFactory {

    private BundleContext bundleContext;

    public ContextManagerServiceFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Validate
    public void start() {
        bundleContext.registerService(JNDIContextManager.class.getName(), this, null);
    }

    public Object getService(Bundle bundle, ServiceRegistration registration) {
        return new ContextManager(bundle.getBundleContext());
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        ContextManager manager = (ContextManager) service;
        manager.release();
    }
}
