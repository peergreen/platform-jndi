package com.peergreen.jndi.internal.env;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Hashtable;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * A {@code EnvironmentTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class EnvironmentTestCase {

    private Environment simplePropertyEnv;
    private Environment listPropertyEnv;

    @BeforeTest
    public void setUp() throws Exception {
        simplePropertyEnv = new Environment();
        simplePropertyEnv.getAsMap().put("my.property", "is.present");

        listPropertyEnv = new Environment();
        listPropertyEnv.getAsMap().put("my.list.property", "one,two");

    }

    @Test
    public void testEmptyEnvironment() throws Exception {
        Environment empty = new Environment();
        assertNotNull(empty.getAsMap());
        assertEquals(empty.getAsMap().size(), 0);
    }

    @Test
    public void testEmptyEnvironmentMerge() throws Exception {
        Environment merged = new Environment();
        merged.merge(new Environment());

        assertNotNull(merged.getAsMap());
        assertEquals(merged.getAsMap().size(), 0);

    }

    @Test
    public void testSimplePropertyMerge() throws Exception {
        Environment merged = new Environment();
        merged.merge(simplePropertyEnv);

        assertNotNull(merged.getAsMap());
        assertEquals(merged.getAsMap().size(), 1);
        assertEquals(merged.getAsMap().get("my.property"), "is.present");

    }

    @Test
    public void testSimplePropertyOverrideMerge() throws Exception {
        Environment merged = new Environment();
        merged.getAsMap().put("my.property", "is.overriden");
        merged.merge(simplePropertyEnv);

        assertNotNull(merged.getAsMap());
        assertEquals(merged.getAsMap().size(), 1);
        assertEquals(merged.getAsMap().get("my.property"), "is.present");

    }

    @Test
    public void testListPropertyMerge() throws Exception {
        Environment merged = new Environment(new Hashtable<String, Object>(),
                                             Arrays.asList("my.list.property"));
        merged.merge(listPropertyEnv);

        assertNotNull(merged.getAsMap());
        assertEquals(merged.getAsMap().size(), 1);
        assertEquals(merged.getAsMap().get("my.list.property"), "one,two");

    }

    @Test
    public void testListPropertyOverrideMerge() throws Exception {
        Environment merged = new Environment(new Hashtable<String, Object>(),
                                             Arrays.asList("my.list.property"));
        merged.getAsMap().put("my.list.property", "three");
        merged.merge(listPropertyEnv);

        assertNotNull(merged.getAsMap());
        assertEquals(merged.getAsMap().size(), 1);
        assertEquals(merged.getAsMap().get("my.list.property"), "one,two,three");

    }

}
