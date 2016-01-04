package com.neikeq.kicksemu.storage;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.pool2.ObjectPool;

import javax.management.ObjectName;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class ConnectionRef extends PoolableConnection {

    private int refCount = 1;

    public static ConnectionRef ref(ConnectionRef ... con) throws SQLException {
        if ((con.length > 0) && (con[0] != null)) {
            ConnectionRef connectionRef = con[0];
            connectionRef.refCount++;
            return connectionRef;
        } else {
            return newRef();
        }
    }

    private static ConnectionRef newRef() throws SQLException {
        return (ConnectionRef) MySqlManager.getConnection();
    }

    public ConnectionRef(Connection conn, ObjectPool<PoolableConnection> pool, ObjectName jmxName, Collection<String> disconnectSqlCodes, boolean fastFailValidation) {
        super(conn, pool, jmxName, disconnectSqlCodes, fastFailValidation);
    }

    public ConnectionRef(Connection conn, ObjectPool<PoolableConnection> pool, ObjectName jmxName) {
        super(conn, pool, jmxName);
    }

    @Override
    public void close() throws SQLException {
        refCount--;
        if (refCount <= 0) {
            super.close();
        }
    }
}
