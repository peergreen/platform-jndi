package com.peergreen.jndi.internal.finder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.spi.DirectoryManager;
import javax.naming.spi.NamingManager;

import org.osgi.framework.BundleContext;
import com.peergreen.jndi.internal.IBundleContextFinder;
import com.peergreen.jndi.internal.finder.stack.ExecutionStack;
import com.peergreen.jndi.internal.finder.stack.FilteredExecutionStack;
import com.peergreen.jndi.internal.finder.stack.Frame;

/**
 * A {@code CallerStackFinder} is responsible to find a {@code BundleContext} from the caller's stack.<br/>
 * See JNDI Service Specification §126.7.3:
 * <p>
 *  Walk the call stack until the invoker is found. The invoker can be the caller of the
 *  {@code InitialContext} class constructor or the {@code NamingManager} or {@code DirectoryManager}
 *  {@code getObjectInstance()} methods.
 *  <ul>
 *   <li>
 *    Get the class loader of the caller and see if it, or an ancestor, implements the
 *    {@code BundleReference} interface.
 *   </li>
 *   <li>
 *    If a Class Loader implementing the {@code BundleReference} interface is found call the
 *    {@code getBundle()} method to get the client's {@code Bundle}; then call the
 *    {@code getBundleContext()} method on the Bundle to get the client's {@code BundleContext}.
 *   </li>
 *   <li>
 *    If the Bundle Context has been found stop, else continue with the next stack frame.
 *   </li>
 *  </ul>
 * </p>
 *
 * @author Guillaume Sauthier
 */
public class ExecutionStackFinder implements IBundleContextFinder {

    /**
     * Simply used to exposes the getClassContext() method (protected by default).
     */
    public static final class Finder extends SecurityManager {
        @Override
        public Class[] getClassContext() {
            return super.getClassContext();
        }
    }

    /**
     * Default invokers.
     */
    private static Map<Class<?>, Set<String>> DEFAULT_INVOKERS;

    static {
        // Define default invokers
        Map<Class<?>, Set<String>> invokers = new Hashtable<>();

        // All InitialContext public declared methods
        Set<String> methods = getDeclaredPublicMethodsOf(InitialContext.class);
        methods.add("<init>");

        invokers.put(InitialContext.class, methods);

        // All InitialDirContext public declared methods
        Set<String> methods2 = getDeclaredPublicMethodsOf(InitialDirContext.class);
        methods2.add("<init>");
        invokers.put(InitialDirContext.class, methods2);


        // NamingManager.getObjectInstance
        invokers.put(NamingManager.class, Collections.singleton("getObjectInstance"));
        // DirectoryManager.getObjectInstance
        invokers.put(DirectoryManager.class, Collections.singleton("getObjectInstance"));

        // Fix the Map's content
        DEFAULT_INVOKERS = Collections.unmodifiableMap(invokers);
    }

    private static Set<String> getDeclaredPublicMethodsOf(final Class<?> type) {
        Set<String> names = new HashSet<>();
        for (Method method : type.getMethods()) {
            names.add(method.getName());
        }
        return names;
    }

    /**
     * Invokers [Class]->[method-name] patterns.
     */
    private Map<Class<?>, Set<String>> invokers;

    /**
     * Thread to be searched.
     */
    private Thread thread;

    public ExecutionStackFinder(final Thread thread) {
        this(thread, DEFAULT_INVOKERS);
    }

    public ExecutionStackFinder(final Thread thread,
                             final Map<Class<?>, Set<String>> invokers) {
        this.thread = thread;
        this.invokers = invokers;
    }

    /**
     * Tries to find a {@code BundleContext}.
     *
     * @return a {@code BundleContext} or <tt>null</tt> if none was found.
     */
    public BundleContext findContext() {

        // Take the snapshot
        ExecutionStack stack = getExecutionStack();

        boolean invokerFound = false;
        for (Frame frame : stack) {
            if (!invokerFound) {
                invokerFound = isInvoker(frame);
                // do nothing else in the loop, and go to the next frame
            } else {

                // It may be possible that we still have an invoker above the last invoker:
                // Ignore it and move to the next frame
                if (isInvoker(frame)) {
                    continue;
                }

                // Analyze the current frame
                ClassLoader loader = frame.getClazz().getClassLoader();
                // Loader may be null because classes loaded from the System loader have a null ClassLoader
                if (loader != null) {
                    IBundleContextFinder loaderFinder = createBundleContextFinder(loader);
                    BundleContext context = loaderFinder.findContext();
                    if (context != null) {
                        return context;
                    }
                }
                // continue with next frame
            }
        }

        // Nothing was found
        return null;
    }

    protected ExecutionStack getExecutionStack() {
        return new FilteredExecutionStack(new Finder().getClassContext(),
                                          thread.getStackTrace());
    }

    /**
     * Create a new IBundleContextFinder
     * @param loader Classloader to be used for the delegating search
     * @return a new finder
     */
    protected IBundleContextFinder createBundleContextFinder(final ClassLoader loader) {
        return new ClassLoaderFinder(loader);
    }

    private boolean isInvoker(final Frame frame) {

        Class<?> currentClass = frame.getClazz();
        String currentMethodName = frame.getElement().getMethodName();

        Set<String> methods = invokers.get(currentClass);
        if (methods != null) {
            return methods.contains(currentMethodName);
        }

        return false;
    }

}
