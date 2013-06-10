package com.peergreen.jndi.internal.factory.dir;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

import com.peergreen.jndi.internal.factory.TerminalObjectFactory;

/**
 * A {@code TerminalDirObjectFactory} is ...
 *
 * @author Guillaume Sauthier
 */
public class TerminalDirObjectFactory extends TerminalObjectFactory implements DirObjectFactory {

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        return getObjectInstance(obj, name, nameCtx, environment);
    }
}
