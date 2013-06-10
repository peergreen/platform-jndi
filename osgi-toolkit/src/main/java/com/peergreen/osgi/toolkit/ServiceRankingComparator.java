package com.peergreen.osgi.toolkit;

import java.util.Collections;
import java.util.Comparator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * A {@code ServiceRankingComparator} is ...
 *
 * @author Guillaume Sauthier
 */
public class ServiceRankingComparator implements Comparator<ServiceReference> {

    /**
     * Singleton value.
     */
    private static ServiceRankingComparator comparator;

    /**
     * @return the comparator singleton.
     */
    public static Comparator<ServiceReference> getComparator() {
        if (comparator == null) {
            comparator = new ServiceRankingComparator();
        }
        return comparator;
    }


    /**
     * @return the comparator singleton.
     */
    public static Comparator<ServiceReference> getReverseComparator() {
        if (comparator == null) {
            comparator = new ServiceRankingComparator();
        }
        return Collections.reverseOrder(comparator);
    }

    /**
     * Enum storing defined return value of a Comparator.
     */
    private static enum Rank {
        LOWER(-1), EQUALS(0), HIGHER(1);

        /** Enumeration value. */
        private int value;

        Rank(final int value) {
            this.value = value;
        }

    }

    /**
     * Default Ranking value when not specified or incorrect value.
     */
    private static final int DEFAULT_RANKING = 0;

    /**
     * Extract from the OSGi core specification (§5.2.5):<br />
     *
     * <p>
     *  When registering a service object, a bundle may optionally specify a
     *  {@code service.ranking} number as one of the service object’s properties.
     *  If multiple qualifying service interfaces exist, a service with the
     *  highest {@code SERVICE_RANKING} number, or when equal to the lowest
     *  {@code SERVICE_ID}, determines which service object is returned by
     *  the Framework.
     * </p>

     * @param first first compared reference
     * @param second second compared reference
     * @return <tt>0</tt> if this is the same service, <tt>-1</tt> if first service
     *         is ranked lower than the second, <tt>1</tt> if the first service is
     *         ranked higher than the second.
     */
    public int compare(final ServiceReference first, final ServiceReference second) {

        // Get service ids
        Long firstServiceId = (Long) first.getProperty(Constants.SERVICE_ID);
        Long secondServiceId = (Long) second.getProperty(Constants.SERVICE_ID);

        if (firstServiceId.equals(secondServiceId)) {
            // That's the same service
            return Rank.EQUALS.value;
        }

        // Get service rankings
        Integer firstServiceRanking = getServiceRanking(first);
        Integer secondServiceRanking = getServiceRanking(second);

        if (firstServiceRanking.equals(secondServiceRanking)) {
            // Ranking are equals, sort by service.id in descending order (so the negation)
            // Means that we prefers service with lower ID (registered before the other)
            return -(firstServiceId.compareTo(secondServiceId));
        } else {
            // Sort by rank in ascending order.
            if (firstServiceRanking.compareTo(secondServiceRanking) < 0) {
                // First ranking is lower than second
                return Rank.LOWER.value;
            } else {
                // First ranking is higher than second
                return Rank.HIGHER.value;
            }
        }
    }

    private int getServiceRanking(final ServiceReference reference) {

        Object ranking = reference.getProperty(Constants.SERVICE_RANKING);

        // No ranking specified: return default ranking
        if (ranking == null) {
            return DEFAULT_RANKING;
        }

        // If ranking is not an integer, spec says to return default ranking
        if (ranking instanceof Integer) {
            return (Integer) ranking;
        }

        return DEFAULT_RANKING;

    }

}
