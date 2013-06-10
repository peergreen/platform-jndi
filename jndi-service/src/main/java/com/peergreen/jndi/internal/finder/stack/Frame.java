package com.peergreen.jndi.internal.finder.stack;

/**
 * A {@code Frame} is ...
 *
 * @author Guillaume Sauthier
 */
public class Frame {

    /**
     * Class associated to this frame.
     */
    private Class<?> clazz;

    /**
     * Descriptive informations about this frame.
     */
    private StackTraceElement element;

    public Frame(final Class<?> clazz, final StackTraceElement element) {
        this.clazz = clazz;
        this.element = element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public StackTraceElement getElement() {
        return element;
    }
}
