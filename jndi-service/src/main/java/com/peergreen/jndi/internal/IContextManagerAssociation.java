package com.peergreen.jndi.internal;

import javax.naming.Context;

/**
 * A {@code IContextManagerAssociation} is used to manage association between
 * a JNDIContextManager and Context produced by it.
 *
 * It is useful for releasing resources in the scope of the ContextManager.
 *
 * @author Guillaume Sauthier
 */
public interface IContextManagerAssociation {

    /**
     * Associate the given Context with a JNDIContextManager.
     * @param context Context to be associated
     */
    void associate(Context context);

    /**
     * Dissociate the given Context from a JNDIContextManager.
     * Notice that this method do not Call {@link javax.naming.Context#close()} on the Context instance.
     * @param context Context to be dissociated
     */
    void dissociate(Context context);
}
