package com.peergreen.jndi.internal.builtin.parser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import javax.naming.InvalidNameException;

import org.testng.annotations.Test;

/**
 * A {@code OSGiNameParserTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class OSGiNameParserTestCase {

    private OSGiNameParser parser = new OSGiNameParser();

    @Test
    public void testParsingServiceName() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/myDataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "myDataSource");
        assertEquals(name.getInterfaceName(), "myDataSource");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingInterfaceName() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/javax.sql.DataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "javax.sql.DataSource");
        assertEquals(name.getInterfaceName(), "javax.sql.DataSource");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingInterfaceNameWithFilter() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/javax.sql.DataSource/(a=b)");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertTrue(name.hasFilter());
        assertEquals(name.getFilter(), "(a=b)");
        assertNull(name.getServiceName());
        assertEquals(name.getInterfaceName(), "javax.sql.DataSource");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingInterfaceNameWithComplexFilter() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/javax.sql.DataSource/(&(a=/b)(c=/d))");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertTrue(name.hasFilter());
        assertEquals(name.getFilter(), "(&(a=/b)(c=/d))");
        assertNull(name.getServiceName());
        assertEquals(name.getInterfaceName(), "javax.sql.DataSource");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingComposedServiceNameOfLength1() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/MyDataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "MyDataSource");
        assertEquals(name.getInterfaceName(), "MyDataSource");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingComposedServiceNameOfLength2() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/jdbc/MyDataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "jdbc/MyDataSource");
        assertTrue(name.hasQuery());
        assertNull(name.getInterfaceName());
    }

    @Test
    public void testParsingComposedServiceNameOfLength3() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/comp/jdbc/MyDataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "comp/jdbc/MyDataSource");
        assertNull(name.getInterfaceName());
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingComposedServiceNameOfLength4() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/ee/comp/jdbc/MyDataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "ee/comp/jdbc/MyDataSource");
        assertNull(name.getInterfaceName());
        assertTrue(name.hasQuery());
    }

    @Test(expectedExceptions = InvalidNameException.class)
    public void testParsingErrorWrongScheme() throws Exception {
        parser.parse("java:service/myDataSource");
    }

    @Test(expectedExceptions = InvalidNameException.class)
    public void testParsingErrorWrongPath() throws Exception {
        parser.parse("osgi:wrong/myDataSource");
    }

    @Test(expectedExceptions = InvalidNameException.class)
    public void testParsingErrorNoFilterAllowedWhenUsingFrameworkBundleContext() throws Exception {
        parser.parse("osgi:framework/bundleContext/(a=b)");
    }

    @Test
    public void testParsingWithServicelist() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:servicelist/myDataSource");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "servicelist");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "myDataSource");
        assertEquals(name.getInterfaceName(), "myDataSource");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testParsingWithEmptyService() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "service");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertNull(name.getServiceName());
        assertNull(name.getInterfaceName());
        assertFalse(name.hasQuery());
    }

    @Test
    public void testParsingWithEmptyServicelist() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:servicelist/");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "servicelist");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertNull(name.getServiceName());
        assertNull(name.getInterfaceName());
        assertFalse(name.hasQuery());
    }

    @Test
    public void testParsingWithFrameworkBundleContext() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:framework/bundleContext");
        assertNotNull(name);
        assertEquals(name.getPath().getPath(), "framework");
        assertFalse(name.hasFilter());
        assertNull(name.getFilter());
        assertEquals(name.getServiceName(), "bundleContext");
        assertEquals(name.getInterfaceName(), "bundleContext");
        assertTrue(name.hasQuery());
    }

    @Test
    public void testToString() throws Exception {
        OSGiName name = (OSGiName) parser.parse("osgi:service/javax.sql.DataSource/(&(a=/b)(c=/d))");
        assertEquals(name.toString(), "osgi:service/javax.sql.DataSource/(&(a=/b)(c=/d))");
    }
}
