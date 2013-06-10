package com.peergreen.osgi.toolkit;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.springframework.osgi.mock.MockServiceReference;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A {@code ServiceRankingComparatorTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class ServiceRankingComparatorTestCase {

    private ServiceRankingComparator comparator = new ServiceRankingComparator();

    private void assertEquality(final int result) {
        Assert.assertEquals(result, 0, "Reference comparison result should be equals.");
    }

    private void assertOneIsPrefered(final int result) {
        Assert.assertEquals(result, 1, "First reference should be preferred.");
    }

    private void assertTwoIsPrefered(final int result) {
        Assert.assertEquals(result, -1, "Second reference should be preferred.");
    }

    @Test
    public void testServiceIdEquality() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 1l);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);
        
        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 1l);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertEquality(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasLowerServiceId() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 1l);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 2l);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertOneIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasHigherServiceId() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 2l);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 1l);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertTwoIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasLowerServiceIdWithEqualsServiceRanking() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 1l);
        properties1.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 2l);
        properties2.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertOneIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasHigherServiceIdWithEqualsServiceRanking() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 2l);
        properties1.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 1l);
        properties2.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertTwoIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasHigherServiceRanking() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 1l);
        properties1.put(Constants.SERVICE_RANKING, 10);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 2l);
        properties2.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertOneIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasLowerServiceRanking() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 2l);
        properties1.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 1l);
        properties2.put(Constants.SERVICE_RANKING, 10);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertTwoIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefOneHasNoServiceRanking() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 1l);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 2l);
        properties2.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertTwoIsPrefered(comparator.compare(ref1, ref2));
    }

    @Test
    public void testRefTwoHasNoServiceRanking() throws Exception {
        Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put(Constants.SERVICE_ID, 2l);
        properties1.put(Constants.SERVICE_RANKING, 5);
        MockServiceReference ref1 = new MockServiceReference(null, properties1, null);

        Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put(Constants.SERVICE_ID, 1l);
        MockServiceReference ref2 = new MockServiceReference(null, properties2, null);

        assertOneIsPrefered(comparator.compare(ref1, ref2));

    }

}
