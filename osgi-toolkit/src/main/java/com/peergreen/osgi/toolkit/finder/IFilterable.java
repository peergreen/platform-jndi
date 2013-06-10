package com.peergreen.osgi.toolkit.finder;


import org.ow2.util.osgi.toolkit.filter.IFilter;

/**
 * A {@code Filterable} is ...
 *
 * @author Guillaume Sauthier
 */
public interface IFilterable<T> {
    IFindable<T> with(IFilter filter);
    IFindable<T> with(IFilter... ands);
}
