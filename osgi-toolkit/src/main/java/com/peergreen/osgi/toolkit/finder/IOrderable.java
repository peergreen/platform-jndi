package com.peergreen.osgi.toolkit.finder;

import java.util.Comparator;

import org.osgi.framework.ServiceReference;

/**
 * A {@code Orderable} is ...
 *
 * @author Guillaume Sauthier
 */
public interface IOrderable<T> {
    IFindable<T> orderWith(Comparator<ServiceReference> comparator);
}
