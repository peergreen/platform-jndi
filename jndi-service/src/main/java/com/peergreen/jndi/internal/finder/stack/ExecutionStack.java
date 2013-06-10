package com.peergreen.jndi.internal.finder.stack;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A {@code CallerStack} is ...
 *
 * @author Guillaume Sauthier
 */
public class ExecutionStack implements Iterable<Frame> {


    /**
     * Classes representing the stack of execution at a given time.
     * Notice that some "internal" classes are omitted (reflection related it seems).
     */
    private Class<?>[] classesStack;

    /**
     * Snapshot of the stack trace.
     */
    private StackTraceElement[] stackTraceElements;

    /**
     * Start from 1 to synchronize elements.
     */
    private int cursorIndex = 1;

    public ExecutionStack(final Class<?>[] classesStack,
                          final StackTraceElement[] stackTraceElements) {
        this.classesStack = classesStack;
        this.stackTraceElements = stackTraceElements;
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Frame> iterator() {
        return new Iterator<Frame>() {

            public boolean hasNext() {
                return cursorIndex < classesStack.length;
            }

            public Frame next() {
                Frame frame = new Frame(classesStack[cursorIndex], stackTraceElements[cursorIndex]);
                cursorIndex++;
                return frame;
            }

            public void remove() {
                throw new UnsupportedOperationException("Cannot execute remove() on this Iterator");
            }
        };
    }

    @Override
    public String toString() {
        return "ExecutionStack{" +
                "classesStack=" + (classesStack == null ? null : Arrays.asList(classesStack)) +
                ", stackTraceElements=" + (stackTraceElements == null ? null : Arrays.asList(stackTraceElements)) +
                ", cursorIndex=" + cursorIndex +
                '}';
    }
}
