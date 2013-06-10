package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * A {@code OsgiJndiSpecBundlesOption} is a wrapper around the jndi spec bundle
 *
 * @author Guillaume Sauthier
 */
public class OsgiJndiSpecBundlesOption extends AbstractDelegateMavenBundlesOption<OsgiJndiSpecBundlesOption>  {

    /**
     * Constructor.
     *
     * @throws IllegalArgumentException - If delegate is null
     */
    public OsgiJndiSpecBundlesOption() {
        super(
            mavenBundle()
                .groupId( "com.peergreen.jndi" )
                .artifactId( "osgi-jndi-service-1.0-spec" )
                .version( "1.0.0-SNAPSHOT" )
        );
    }

    @Override
    protected OsgiJndiSpecBundlesOption itself() {
        return this;
    }
}