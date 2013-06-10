package com.peergreen.jndi.internal.builtin.osgi.mock;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * A {@code MockDataSource} is ...
 *
 * @author Guillaume Sauthier
 */
public class MockDataSource implements DataSource {
    public Connection getConnection() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getLoginTimeout() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
