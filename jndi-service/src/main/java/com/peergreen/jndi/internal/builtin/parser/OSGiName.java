package com.peergreen.jndi.internal.builtin.parser;

import java.util.Enumeration;
import javax.naming.CompositeName;
import javax.naming.Name;

/**
 * A {@code OSGiName} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiName extends CompositeName {

    /**
     * Path element.
     */
    private Path path;

    /**
     * Is there a filter ?
     */
    private boolean filter;

    public OSGiName(final Enumeration<String> elements,
                    final Path path,
                    final boolean filter) {
        super(elements);
        this.path = path;
        this.filter = filter;
    }

    public Path getPath() {
        return path;
    }

    public boolean hasQuery() {
        // If the name has more than 1 element
        // [path] / [query]
        return size() > 1;
    }

    public boolean hasFilter() {
        return filter;
    }

    public String getServiceName() {
        if (hasFilter()) {
            // When using a JNDI service name, there is no filters
            return null;
        }

        // Otherwise, return all except the Path (1st) element
        if (size() > 1) {
            return toString(getSuffix(1));
        }
        return null;
    }

    public String getInterfaceName() {
        // I can say that there is no interface in that name if the
        // length is superior to 3
        //   -> service / javax.sql.DataSource / (a=b) <- cannot have more elements
        if (size() > 3) {
            return null;
        }

        if (size() == 3) {
            // service / javax.sql.DataSource / (a=b)
            // service / jdbc / MyDataSource
            if (!hasFilter()) {
                return null;
            }
        }

        if (size() == 2) {
            // service / javax.sql.DataSource
            // service / myDataSource

            // Cannot distinguish surely between the 2 :-(
            // Let's continue with the general case ...
        }

        // General case:
        // Strip the 1st element and the last one (if this is a filter)
        if (size() > 1) {
            Name tmp = getSuffix(1);
            if (hasFilter()) {
                tmp = tmp.getPrefix(size() - 2);
            }
            return toString(tmp);
        }
        return null;
    }

    public String getFilter() {
        if (filter) {
            // Return last element
            return get(size() - 1);
        }
        // no filter
        return null;
    }


    public String getQuery() {
        if (!hasQuery()) {
            return null;
        }
        return toString(getSuffix(1));
    }

    private String toString(final Name name) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for(int i = 0; i < name.size(); i++) {
            // Do not append the separator for the first element
            if (!first) {
                builder.append('/');
            }
            first = false;

            builder.append(name.get(i));
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "osgi:" + toString(this);
    }
}
