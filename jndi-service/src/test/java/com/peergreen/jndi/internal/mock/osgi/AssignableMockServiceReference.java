package com.peergreen.jndi.internal.mock.osgi;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.springframework.osgi.mock.MockServiceReference;

/**
 * A {@code AssignableMockServiceReference} is ...
 *
 * @author Guillaume Sauthier
 */
public class AssignableMockServiceReference extends MockServiceReference {
    
    public AssignableMockServiceReference() {
        this(null);
    }

    public AssignableMockServiceReference(Dictionary properties) {
        super(null, properties, null);
    }

    @Override
    public boolean isAssignableTo(Bundle bundle, String className) {
        return true;
    }
}
