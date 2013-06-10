package com.peergreen.jndi.internal.finder.stack;

import com.peergreen.jndi.internal.finder.ExecutionStackFinder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A {@code CallerStackTestCase} is ...
 *
 * @author Guillaume Sauthier
 */
public class ExecutionStackTestCase {

    private ExecutionStack stack;

    @BeforeMethod
    private void setUp() {
        Thread thread = Thread.currentThread();
        stack = new FilteredExecutionStack(new ExecutionStackFinder.Finder().getClassContext(), thread.getStackTrace());
    }

    @Test
    public void testClassContextAndStackTracesAreConsistent() throws Exception {
        for(Frame frame : stack) {
            String nameFromClass = frame.getClazz().getName();
            String nameFromStack = frame.getElement().getClassName();
            Assert.assertTrue(nameFromClass.equals(nameFromStack),
                              "Inconsistent stacks: " + frame.getClazz() + " -> " + frame.getElement());
        }
    }
}
