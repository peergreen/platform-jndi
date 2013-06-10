package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.frameworks;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.logProfile;
import static com.peergreen.jndi.it.exam.Options.iPojoBundle;
import static com.peergreen.jndi.it.exam.Options.jndiSpecBundle;
import static com.peergreen.jndi.it.exam.Options.jndiServiceBundle;
import static com.peergreen.jndi.it.exam.Options.sunJreDelegateBundle;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.CompositeOption;

/**
 * A {@code TestingEnvironment} is ...
 *
 * @author Guillaume Sauthier
 */
public class TestingEnvironment implements CompositeOption {
    public Option[] getOptions() {
        return options(

                // Frameworks for testing
                // ---------------------------
                frameworks(
                        felix()
                ),

                // TODO Currently, Tests are failing (due to a race condition) if we do not enable the log profile...
                // Install log service using pax runners profile abstraction (there are more profiles, like DS)
                logProfile(),
                // This is how you set the default log level when using pax logging (logProfile)
                systemProperty( "org.ops4j.pax.logging.DefaultServiceLog.level" ).value( "INFO" ),

                // Bundles to be installed/started
                // ----------------------------------
                iPojoBundle().versionAsInProject(),
                jndiSpecBundle().versionAsInProject(),
                jndiServiceBundle().versionAsInProject(),
                sunJreDelegateBundle().versionAsInProject()
        );
    }
}
