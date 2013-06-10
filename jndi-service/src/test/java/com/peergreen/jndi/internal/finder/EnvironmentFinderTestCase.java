package com.peergreen.jndi.internal.finder;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.jndi.JNDIConstants;
import com.peergreen.jndi.internal.env.Environment;
import org.springframework.osgi.mock.MockBundleContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A {@code EnvironmentFinderTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class EnvironmentFinderTestCase {

    @Test
    public void testFindIsSuccessful() throws Exception {
        Map<String, Object> map = new Hashtable<String, Object>();
        map.put(JNDIConstants.BUNDLE_CONTEXT, new MockBundleContext());
        Environment env = new Environment(map);
        EnvironmentFinder finder = new EnvironmentFinder(env);
        Assert.assertNotNull(finder.findContext());
    }

    @Test
    public void testFindHasFailed() throws Exception {
        Environment env = new Environment();
        EnvironmentFinder finder = new EnvironmentFinder(env);
        Assert.assertNull(finder.findContext());
    }
}
