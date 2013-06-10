package com.peergreen.jndi.it.components.hello;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

/**
 * A {@code HelloReferenceable} is ...
 *
 * @author Guillaume Sauthier
 */
public class Hello implements Referenceable {

    /**
     * Serialized message.
     */
    private String message;

    private String urlAddressType;

    public Hello(final String message) {
        this(message, null);

    }

    public Hello(final String message,
                 final String address) {
        this.message = message;
        this.urlAddressType = address;
    }

    public String getMessage() {
        return message;
    }

    public Reference getReference() throws NamingException {

        // Create Reference
        Reference ref = new Reference(Hello.class.getName(),
                                      HelloObjectFactory.class.getName(),
                                      null);

        // Message
        ref.add(new StringRefAddr("message", message));

        // Url Address Type
        if (urlAddressType != null) {
            ref.add(new StringRefAddr("URL", urlAddressType));
        }

        return ref;
    }
}
