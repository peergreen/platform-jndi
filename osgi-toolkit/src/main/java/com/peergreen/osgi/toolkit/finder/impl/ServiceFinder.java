package com.peergreen.osgi.toolkit.finder.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import com.peergreen.osgi.toolkit.finder.IFind;
import com.peergreen.osgi.toolkit.finder.IFindable;
import org.ow2.util.osgi.toolkit.filter.Filters;
import org.ow2.util.osgi.toolkit.filter.IFilter;

/**
 * A {@code ServiceFinder} is ...
 *
 * @author Guillaume Sauthier
 */
public class ServiceFinder<T> implements IFindable<T> {

    private BundleContext bundleContext;
    private Class<T> type;
    private IFilter filter;
    private String interfaceName;
    private Comparator<ServiceReference> comparator;

    public ServiceFinder(BundleContext bundleContext, Class<T> type, String interfaceName) {
        this(bundleContext, type);
        this.interfaceName = interfaceName;
    }

    public ServiceFinder(BundleContext bundleContext, Class<T> type) {
        this.bundleContext = bundleContext;
        this.type = type;
        this.interfaceName = type.getName();
    }

    public IFindable<T> with(IFilter and) {
        if (filter == null) {
            filter = and;
        } else {
            filter = Filters.and(filter, and);
        }
        return this;
    }

    public IFindable<T> with(IFilter... ands) {
        for(IFilter and : ands) {
            with(and);
        }
        return this;
    }

    public IFindable<T> orderWith(Comparator<ServiceReference> comparator) {
        this.comparator = comparator;
        return this;
    }

    public T firstService() {
        List<T> services = listServices();
        if (services.isEmpty()) {
            return null;
        } else {
            return services.iterator().next();
        }
    }

    public List<T> listServices() {

        ServiceReference[] refs = new ServiceReference[0];
        try {
            refs = bundleContext.getAllServiceReferences(interfaceName, getFilter());
        } catch (InvalidSyntaxException e) {
            // Should not happen
        }
        if (refs != null) {

            List<ServiceReference> references = filterServiceReferences(refs);

            // Convert to services
            List<T> services = new ArrayList<T>();
            for(ServiceReference reference : references) {
                Object service = bundleContext.getService(reference);
                if (service != null) {
                    services.add(type.cast(service));
                }
            }

            // Fix the collection
            return Collections.unmodifiableList(services);

        }

        return Collections.emptyList();
    }

    private String getFilter() {
        if (filter == null) {
            return null;
        }
        return filter.asText();
    }

    public IFind<T> firstFind() {
        List<IFind<T>> services = listFinds();
        if (services.isEmpty()) {
            return null;
        } else {
            return services.iterator().next();
        }
    }

    public List<IFind<T>> listFinds() {
        ServiceReference[] refs = new ServiceReference[0];
        try {
            refs = bundleContext.getAllServiceReferences(interfaceName, getFilter());
        } catch (InvalidSyntaxException e) {
            // Should not happen
        }
        if (refs != null) {

            List<ServiceReference> references = filterServiceReferences(refs);

            // Convert to services
            List<IFind<T>> services = new ArrayList<IFind<T>>();
            for(ServiceReference reference : references) {
                services.add(new Find<T>(bundleContext, reference, type));
            }

            // Fix the collection
            return Collections.unmodifiableList(services);

        }

        return Collections.emptyList();
    }

    private List<ServiceReference> filterServiceReferences(ServiceReference[] refs) {
        List<ServiceReference> references;

        // Filter
        if (interfaceName != null) {
            references = new ArrayList<ServiceReference>();
            Bundle bundle = bundleContext.getBundle();
            for (ServiceReference reference : refs) {
                if (reference.isAssignableTo(bundle, interfaceName)) {
                    references.add(reference);
                }
            }
        } else {
            references = Arrays.asList(refs);
        }

        // Sort the resulting array
        if (comparator != null) {
            Collections.sort(references, comparator);
        }
        return references;
    }

    /**
     * A {@code Find} is ...
     */
    private static class Find<T> implements IFind<T> {

        private BundleContext bundleContext;
        private ServiceReference reference;
        private Class<T> type;
        private T service;

        public Find(final BundleContext bundleContext,
                    final ServiceReference reference,
                    final Class<T> type) {
            this.bundleContext = bundleContext;
            this.reference = reference;
            this.type = type;
        }

        public ServiceReference getReference() {
            return this.reference;
        }

        public T getService() {
            // Check that the service was not unregistered
            if (this.reference.getBundle() == null) {
                // Service unregistered: releasing service ...
                release();
                // ... and return null
                return null;
            }

            if (this.service == null) {
                // First access
                // Service was not in cache
                Object o = this.bundleContext.getService(this.reference);
                this.service = this.type.cast(o);
            }
            return this.service;
        }

        public void release() {
            // Release any handlers on service instance
            this.service = null;

            // Only unget the service if the Bundle in use is not being stopped
            // That prevents loop when using ServiceFactory
            if (this.bundleContext.getBundle().getState() != Bundle.STOPPING) {
                this.bundleContext.ungetService(this.reference);
            }
        }
    }
}
