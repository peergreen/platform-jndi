package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.Constants.START_LEVEL_SYSTEM_BUNDLES;

import org.ops4j.pax.exam.options.AbstractDelegateProvisionOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.WrappedUrlProvisionOption;

/**
 * A {@code IPojoBundlesOption} is ...
 *
 * @author Guillaume Sauthier
 */
public abstract class AbstractDelegateMavenBundlesOption<T extends AbstractDelegateMavenBundlesOption>
        extends AbstractDelegateProvisionOption<T>  {

    /**
     * Constructor.
     *
     * @param delegate wrapped provision option (cannot be null)
     * @throws IllegalArgumentException - If delegate is null
     */
    protected AbstractDelegateMavenBundlesOption(MavenArtifactProvisionOption delegate) {
        super(delegate);
        startLevel( START_LEVEL_SYSTEM_BUNDLES );
    }

    /**
     * Sets the iPOJO version.
     *
     * @param version iPOJO version.
     *
     * @return itself, for fluent api usage
     */
    public T version( final String version )
    {
        ( (MavenArtifactUrlReference) ( (WrappedUrlProvisionOption) getDelegate() ).getUrlReference() ).version(
            version
        );
        return itself();
    }

    /**
     * Sets the iPOJO version.
     *
     * @return itself, for fluent api usage
     */
    public T versionAsInProject() {
        MavenArtifactProvisionOption delegate = (MavenArtifactProvisionOption) getDelegate();
        delegate.versionAsInProject();
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( getClass().getSimpleName() );
        sb.append( "{url=" ).append( getURL() );
        sb.append( '}' );
        return sb.toString();
    }

}