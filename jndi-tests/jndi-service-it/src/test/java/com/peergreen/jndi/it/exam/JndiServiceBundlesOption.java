package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * A {@code JndiServiceBundlesOption} is a wrapper around the JNDI service implementation.
 *
 * @author Guillaume Sauthier
 */
public class JndiServiceBundlesOption extends AbstractDelegateMavenBundlesOption<JndiServiceBundlesOption>  {
    /**
     * Constructor.
     *
     * @throws IllegalArgumentException - If delegate is null
     */
    public JndiServiceBundlesOption() {
        super(
            mavenBundle()
                .groupId( "com.peergreen.jndi" )
                .artifactId( "jndi-service" )
                .version( "1.0.0-SNAPSHOT" )
        );
    }

    @Override
    protected JndiServiceBundlesOption itself() {
        return this;
    }
}