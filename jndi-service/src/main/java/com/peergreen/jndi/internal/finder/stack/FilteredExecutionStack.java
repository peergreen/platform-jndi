package com.peergreen.jndi.internal.finder.stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@code FilteredCallerStack} is ...
 *
 * @author Guillaume Sauthier
 */
public class FilteredExecutionStack extends ExecutionStack {

    /**
     * List of [class-name].[method-name] that have to be excluded from the stack trace.
     * TODO Maybe complete this listServices ?
     */
    private static final List<String> DEFAULT_EXCLUDED = Arrays.asList(
            "sun.reflect.NativeMethodAccessorImpl.invoke0",
            "sun.reflect.NativeMethodAccessorImpl.invoke",
            "sun.reflect.DelegatingMethodAccessorImpl.invoke",
            "java.lang.reflect.Method.invoke",
            "java.lang.Thread.dumpThreads");

    public FilteredExecutionStack(final Class<?>[] classesStack,
                                  final StackTraceElement[] stackTraceElements) {
        this(classesStack, stackTraceElements, DEFAULT_EXCLUDED);
    }

    public FilteredExecutionStack(final Class<?>[] classesStack,
                                  final StackTraceElement[] stackTraceElements,
                                  final List<String> exclusionList) {
        super(classesStack, filter(stackTraceElements, exclusionList));
    }

    /**
     * Filter the given stack trace: this method will remove from the stack the elements
     * whose classname and method name are in the filteredValues listServices.
     * @param stackTraceElements elements to be filtered
     * @param filteredValues values to be excluded from the stack
     * @return a filtered stack
     */
    private static StackTraceElement[] filter(final StackTraceElement[] stackTraceElements,
                                              final List<String> filteredValues) {

        List<StackTraceElement> filteredStack = new ArrayList<StackTraceElement>();

        // Filter each element of the stack
        for(StackTraceElement element : stackTraceElements) {

            // If the element is matching one of the filtered values,
            // do not copy the value in the final listServices.
            boolean includeIt = true;
            for(String value : filteredValues) {
                String elementValue = element.getClassName() + "." + element.getMethodName();
                if (elementValue.equals(value)) {
                    includeIt = false;
                }
            }

            // Only transit the value if it was not filtered
            if (includeIt) {
                filteredStack.add(element);
            }
        }

        // Return the filtered array
        return filteredStack.toArray(new StackTraceElement[filteredStack.size()]);
    }

}
