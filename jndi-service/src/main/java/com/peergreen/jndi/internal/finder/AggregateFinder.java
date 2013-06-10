package com.peergreen.jndi.internal.finder;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.IBundleContextFinder;

/**
 * A {@code AggregateFinder} is ...
 *
 * @author Guillaume Sauthier
 */
public class AggregateFinder implements IBundleContextFinder {

    private List<IBundleContextFinder> finders;

    public AggregateFinder() {
        finders = new ArrayList<IBundleContextFinder>();
    }

    public List<IBundleContextFinder> getFinders() {
        return finders;
    }

    public BundleContext findContext() {

        for(IBundleContextFinder finder : finders) {
            BundleContext context = finder.findContext();
            if (context != null) {
                return context;
            }
        }
        
        return null;
    }
}
