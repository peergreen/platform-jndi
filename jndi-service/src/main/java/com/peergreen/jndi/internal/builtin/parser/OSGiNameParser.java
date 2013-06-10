package com.peergreen.jndi.internal.builtin.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

import com.peergreen.jndi.internal.builtin.OSGiUrlContextObjectFactory;
import com.peergreen.jndi.internal.util.Utils;

/**
 * A {@code OSGiNameParser} is ...
 *
 *
 * <h3>JNDI Services Specification</h3>
 * <p>
 *  The syntax for a service/serviceList URL lookup allowed JNDI names to be mixed with filters.
 *  This caused confusion because JNDI names have no syntax. The URL scheme grammar is changed
 *  to only allow filters on fqns.
 *  <code>
 *   service     ::= ’osgi:service/’ query
 *   servicelist ::= ’osgi:servicelist/’ query?
 *   query       ::= jndi-name | fqn ( ’/’ filter )?
 *   jndi-name   ::= &lt;any string>
 *  </code>
 *
 * Spec is not very clear about how to distinguish between a {@code jndi-name} and a {@code fqn}...
 * I've understood that we should try with interface if it's possible. If nothing is
 * found, continue with jndiServiceName ...
 *
 * @see http://www.osgi.org/Release4/Errata
 * @author Guillaume Sauthier
 */
public class OSGiNameParser implements NameParser {

    private static enum Mode {
        SCHEME, PATH, QUERY, FILTER
    }

    private static final char CHAR_SEPARATOR = '/';
    private static final char CHAR_COLON = ':';
    private static final char CHAR_OPENING_PARENTHESIS = '(';

    /**
     * Parses a name into its components.
     *
     * @param name The non-null string name to parse.
     * @return A non-null parsed form of the name using the naming convention
     *         of this parser.
     * @throws javax.naming.InvalidNameException
     *                                      If name does not conform to
     *                                      syntax defined for the namespace.
     * @throws javax.naming.NamingException If a naming exception was encountered.
     */
    public Name parse(final String name) throws NamingException {

        StringBuilder scheme = new StringBuilder();
        StringBuilder path = new StringBuilder();
        StringBuilder query = new StringBuilder();
        StringBuilder filter = new StringBuilder();

        Mode mode = Mode.SCHEME;
        // We need to parse the name char by char (cannot split using '/')
        for(int i = 0; i < name.length(); i++) {
            char currentChar = name.charAt(i);

            // Parse the scheme
            if (Mode.SCHEME.equals(mode)) {
                if (currentChar == CHAR_COLON) {
                    mode = Mode.PATH;
                    continue;
                } else {
                    scheme.append(currentChar);
                }
            }

            // Parse the path
            if (Mode.PATH.equals(mode)) {
                if (currentChar == CHAR_SEPARATOR) {
                    mode = Mode.QUERY;
                    continue;
                } else {
                    path.append(currentChar);
                }
            }

            // Parse the interface (service-name | interface-name)
            if (Mode.QUERY.equals(mode)) {
                if (currentChar == CHAR_OPENING_PARENTHESIS) {
                    mode = Mode.FILTER;
                    // Do not place a continue here as we expect to enter in the next if
                } else {
                    query.append(currentChar);
                }
            }

            // Parse the filter
            if (Mode.FILTER.equals(mode)) {
                filter.append(currentChar);
            }
        }

        if (!OSGiUrlContextObjectFactory.OSGI_URL_SCHEME.equals(scheme.toString())) {
            throw new InvalidNameException("'" + name + "' is not a valid OSGi resource name: it should starts with 'osgi:'");
        }

        Path p = null;
        try {
             p = Path.valueOf(path.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Invalid path
            NamingException ne = new InvalidNameException("'" + path.toString() + "' is an invalid path element " +
                                                          "(one of 'service', 'servicelist' or 'framework' is expected).");
            ne.initCause(e);
            throw ne;
        }

        // Special check for 'framework' path: only bundleContext is allowed in the query, and no filter
        if (Path.FRAMEWORK.equals(p)) {

            // There is a filter -> Error
            if (!Utils.isNullOrEmpty(filter.toString())) {
                throw new InvalidNameException("When using '" + Path.FRAMEWORK.name().toLowerCase()
                        + "' path, no filter is allowed (original: '" + name + "').");
            }
        }

        List<String> elements = new ArrayList<String>();

        // Pos #1: the path
        elements.add(path.toString());

        // Starting from #2: the query
        // Continue with the parsing of the query element
        if (!Utils.isNullOrEmpty(query.toString())) {
            String[] parts = query.toString().split(String.valueOf(CHAR_SEPARATOR));
            if (parts.length != 0) {
                elements.addAll(Arrays.asList(parts));
            }
        }

        // Last pos: the filter
        boolean hasFilter = !Utils.isNullOrEmpty(filter.toString());
        if (hasFilter) {
            elements.add(filter.toString());
        }

        return new OSGiName(Collections.enumeration(elements), p, hasFilter);

    }


}
