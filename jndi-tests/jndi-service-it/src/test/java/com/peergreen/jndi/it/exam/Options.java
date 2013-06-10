package com.peergreen.jndi.it.exam;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartupFor;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import org.ops4j.pax.exam.Option;

/**
 * A {@code Options} is ...
 *
 * @author Guillaume Sauthier
 */
public class Options {

    /**
     * @return the iPOJO bundle
     */
    public static IPojoBundlesOption iPojoBundle() {
        return new IPojoBundlesOption();
    }

    /**
     * @return the JNDI Spec API bundle
     */
    public static OsgiJndiSpecBundlesOption jndiSpecBundle() {
        return new OsgiJndiSpecBundlesOption();
    }

    /**
     * @return the JNDI Implementation bundle
     */
    public static JndiServiceBundlesOption jndiServiceBundle() {
        return new JndiServiceBundlesOption();
    }

    /**
     * @return the Sun JRE Delegates bundle
     */
    public static SunJreDelegateBundlesOption sunJreDelegateBundle() {
        return new SunJreDelegateBundlesOption();
    }

    public static Option jndiBundles() {
        return composite(
                jndiSpecBundle(),
                jndiServiceBundle()
        );
    }

    public static Option debug() {
        return debug(5005, 20000);
    }

    public static Option debug(final int port, final int timeout) {
        return composite(
                vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + port),
                waitForFrameworkStartupFor(timeout)
        );
    }
}
