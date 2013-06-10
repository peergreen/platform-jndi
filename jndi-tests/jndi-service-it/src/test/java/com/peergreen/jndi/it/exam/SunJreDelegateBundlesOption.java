package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * A {@code OsgiToolkitBundlesOption} is an OSGi toolkit.
 *
 * @author Guillaume Sauthier
 */
public class SunJreDelegateBundlesOption extends AbstractDelegateMavenBundlesOption<SunJreDelegateBundlesOption>  {

    /**
     * Constructor.
     *
     * @throws IllegalArgumentException - If delegate is null
     */
    public SunJreDelegateBundlesOption() {
        super(
            mavenBundle()
                .groupId( "com.peergreen.jndi" )
                .artifactId( "sun-jre-delegate" )
                .version( "1.0.0-SNAPSHOT" )
        );
    }

    @Override
    protected SunJreDelegateBundlesOption itself() {
        return this;
    }
}