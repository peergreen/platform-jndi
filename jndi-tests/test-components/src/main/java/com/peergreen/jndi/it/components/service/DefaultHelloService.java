package com.peergreen.jndi.it.components.service;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * A {@code DefaultHelloService} is ...
 *
 * @author Guillaume Sauthier
 */
@Component(propagation = true)
@Provides
public class DefaultHelloService implements IHelloService {

    @Property(value = "Hello")
    private String message;

    public String hello(String someone) {
        return message + " " + someone;
    }
}
