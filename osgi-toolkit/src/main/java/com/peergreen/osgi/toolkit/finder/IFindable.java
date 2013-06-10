package com.peergreen.osgi.toolkit.finder;

import java.util.List;

/**
 * A {@code Findable} is ...
 *
 * @author Guillaume Sauthier
 */
public interface IFindable<T> extends IOrderable<T>, IFilterable<T> {
    T firstService();
    List<T> listServices();

    IFind<T> firstFind();
    List<IFind<T>> listFinds();
}
