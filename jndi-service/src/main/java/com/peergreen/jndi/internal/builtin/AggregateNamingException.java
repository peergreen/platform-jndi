package com.peergreen.jndi.internal.builtin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;

/**
 * A {@code AggregateNamingException} is ...
 *
 * @author Guillaume Sauthier
 */
public class AggregateNamingException extends NamingException {

    private List<NamingException> causes;

    public AggregateNamingException(final String explanation) {
        super(explanation);
        causes = new ArrayList<NamingException>();
    }

    public void addCause(final NamingException ne) {
        causes.add(ne);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(super.toString());

        int i = 0;
        for(NamingException cause : causes) {
            builder.append('\n');
            builder.append(i);
            builder.append(") ");
            builder.append(cause.toString());
            i++;
        }

        return builder.toString();
    }

    @Override
    public void printStackTrace(PrintStream s) {

        // Print class name + message (but no details)
        s.println(super.toString());

        // Print details
        int i = 0;
        for(NamingException cause : causes) {
            s.println();
            s.println(i);
            s.println(") ");
            cause.printStackTrace(s);
            i++;
        }
    }
}
