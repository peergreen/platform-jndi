package com.peergreen.osgi.toolkit.finder.impl;

import java.util.Collections;
import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import com.peergreen.osgi.toolkit.ServiceRankingComparator;
import com.peergreen.osgi.toolkit.finder.IFindable;

/**
 * A {@code Finders} is ...
 *
 * @author Guillaume Sauthier
 */
public class Finders {
    public static <T> IFindable<T> find(BundleContext bundleContext, Class<T> type) {
        return new ServiceFinder<T>(bundleContext, type);
    }

    public static IFindable<Object> find(BundleContext bundleContext) {
        return find(bundleContext, (String) null);
    }

    public static IFindable<Object> find(BundleContext bundleContext, String interfaceName) {
        return new ServiceFinder<Object>(bundleContext, Object.class, interfaceName);
    }

    public static Comparator<ServiceReference> serviceRanking() {
        return Collections.reverseOrder(new ServiceRankingComparator());
    }
}
