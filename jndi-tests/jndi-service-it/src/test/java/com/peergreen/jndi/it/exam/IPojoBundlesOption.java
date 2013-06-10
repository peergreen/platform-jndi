package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * A {@code IPojoBundlesOption} is ...
 *
 * @author Guillaume Sauthier
 */
public class IPojoBundlesOption extends AbstractDelegateMavenBundlesOption<IPojoBundlesOption>  {
    /**
     * Constructor.
     *
     * @throws IllegalArgumentException - If delegate is null
     */
    public IPojoBundlesOption() {
        super(
            mavenBundle()
                .groupId( "org.apache.felix" )
                .artifactId( "org.apache.felix.ipojo" )
                .version( "1.6.2" )
        );
    }

    @Override
    protected IPojoBundlesOption itself() {
        return this;
    }
}
