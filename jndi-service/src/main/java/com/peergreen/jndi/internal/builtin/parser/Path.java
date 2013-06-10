package com.peergreen.jndi.internal.builtin.parser;

/**
* A {@code Path} is ...
*
* @author Guillaume Sauthier
*/
public enum Path {

    SERVICE("service"), SERVICELIST("servicelist"), FRAMEWORK("framework");

    private String path;

    Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
