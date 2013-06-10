package com.peergreen.jndi.internal.finder;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.springframework.osgi.mock.MockBundle;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * A {@code ClassLoaderFinderTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class ClassLoaderFinderTestCase {

    private ClassLoader loaderChainWithoutBundleReference;
    private ClassLoader loaderChainWithBundleReferenceAsTopParent;
    private ClassLoader loaderChainWithBundleReferenceAsFirst;
    private ClassLoader loaderChainWithBundleReferenceInMiddle;
    private ClassLoader loaderChainWithMultipleBundleReferences;

    @BeforeTest
    public void setUp() throws Exception {
        // no references
        ClassLoader one = new FakeClassLoader(ClassLoader.getSystemClassLoader());
        ClassLoader two = new FakeClassLoader(one);
        loaderChainWithoutBundleReference = new FakeClassLoader(two);

        // top is bundle
        ClassLoader three = new FakeBundleClassLoader(ClassLoader.getSystemClassLoader(), "top");
        ClassLoader four = new FakeClassLoader(three);
        loaderChainWithBundleReferenceAsTopParent = new FakeClassLoader(four);

        // first element is bundle
        ClassLoader five = new FakeClassLoader(ClassLoader.getSystemClassLoader());
        ClassLoader six = new FakeClassLoader(five);
        loaderChainWithBundleReferenceAsFirst = new FakeBundleClassLoader(six, "first");

        // bundle in the middle
        ClassLoader seven = new FakeClassLoader(ClassLoader.getSystemClassLoader());
        ClassLoader eight = new FakeBundleClassLoader(seven, "middle");
        loaderChainWithBundleReferenceInMiddle = new FakeClassLoader(eight);

        // multiple bundle references
        ClassLoader nine = new FakeClassLoader(ClassLoader.getSystemClassLoader());
        ClassLoader ten = new FakeBundleClassLoader(nine, "upper");
        ClassLoader eleven = new FakeClassLoader(ten);
        ClassLoader twelve = new FakeBundleClassLoader(eleven, "lower");
        loaderChainWithMultipleBundleReferences = new FakeClassLoader(twelve);
    }

    @Test
    public void testFindWithNoBundleLoaderInTheAncestries() throws Exception {
        ClassLoaderFinder finder = new ClassLoaderFinder(loaderChainWithoutBundleReference);
        BundleContext ctx = finder.findContext();
        Assert.assertNull(ctx);
    }

    @Test
    public void testFindWithBundleLoaderOnTheTop() throws Exception {
        ClassLoaderFinder finder = new ClassLoaderFinder(loaderChainWithBundleReferenceAsTopParent);
        BundleContext ctx = finder.findContext();
        Assert.assertNotNull(ctx);
        Assert.assertEquals(ctx.getBundle().getSymbolicName(), "top");
    }

    @Test
    public void testFindWithBundleLoaderAsFirst() throws Exception {
        ClassLoaderFinder finder = new ClassLoaderFinder(loaderChainWithBundleReferenceAsFirst);
        BundleContext ctx = finder.findContext();
        Assert.assertNotNull(ctx);
        Assert.assertEquals(ctx.getBundle().getSymbolicName(), "first");
    }

    @Test
    public void testFindWithBundleLoaderInMiddle() throws Exception {
        ClassLoaderFinder finder = new ClassLoaderFinder(loaderChainWithBundleReferenceInMiddle);
        BundleContext ctx = finder.findContext();
        Assert.assertNotNull(ctx);
        Assert.assertEquals(ctx.getBundle().getSymbolicName(), "middle");
    }

    @Test
    public void testFindWithMultipleBundleLoader() throws Exception {
        ClassLoaderFinder finder = new ClassLoaderFinder(loaderChainWithMultipleBundleReferences);
        BundleContext ctx = finder.findContext();
        Assert.assertNotNull(ctx);
        Assert.assertEquals(ctx.getBundle().getSymbolicName(), "lower");
    }

    private class FakeBundleClassLoader extends ClassLoader implements BundleReference {
        private String id;

        private FakeBundleClassLoader(ClassLoader parent, String id) {
            super(parent);
            this.id = id;
        }

        public Bundle getBundle() {
            return new MockBundle(id);
        }
    }

    private class FakeClassLoader extends ClassLoader {

        private FakeClassLoader(ClassLoader parent) {
            super(parent);
        }
    }
}