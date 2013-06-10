package org.osgi.service.jndi;

import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

/**
 * This interface defines the OSGi service interface for the {@code JNDIContextManager}.
 * This service provides the ability to create new JNDI Context instances without relying
 * on the {@code InitialContext} constructor.
 *
 * @concurrency Thread-safe
 * @author Guillaume Sauthier
 * @since 1.0
 */
public interface JNDIContextManager {

    /**
     * Creates a new JNDI initial context with the default JNDI environment properties.
     * @return an instance of {@code javax.naming.Context}.
     * @throws NamingException upon any errors that occurs during {@code Context} creation.
     */
    Context newInitialContext() throws NamingException;

    /**
     * Creates a new JNDI initial context with the specified JNDI environment properties.
     * @param environment given JNDI environment
     * @return an instance of {@code javax.naming.Context}.
     * @throws NamingException upon any errors that occurs during {@code Context} creation.
     */
    Context newInitialContext(Map environment) throws NamingException;

    /**
     * Creates a new initial {@code DirContext} with the default JNDI environment properties.
     * @return an instance of {@code javax.naming.directory.DirContext}.
     * @throws NamingException upon any errors that occurs during {@code Context} creation.
     */
    DirContext newInitialDirContext() throws NamingException;

    /**
     * Creates a new initial {@code DirContext} with the specified JNDI environment properties.
     * @param environment given JNDI environment
     * @return an instance of {@code javax.naming.directory.DirContext}.
     * @throws NamingException upon any errors that occurs during {@code Context} creation.
     */
    DirContext newInitialDirContext(Map environment) throws NamingException;
}
