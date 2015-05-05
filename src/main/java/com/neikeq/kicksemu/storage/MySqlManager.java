package com.neikeq.kicksemu.storage;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Localization;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;

/** MySql Connection and pooling Manager. */
public class MySqlManager {
    private static PoolingDataSource<PoolableConnection> dataSource;

    private static int minIdle;
    private static int maxIdle;
    
    public static void initialize()
            throws ClassNotFoundException, SQLException, IllegalArgumentException {
        minIdle = Configuration.getInt("mysql.idle.min");
        maxIdle = Configuration.getInt("mysql.idle.max");
        
        if (minIdle < 0) {
            throw new IllegalArgumentException(Localization.get("mysql.error.pool"));
        }

        // Load JDBC Driver. ClassNotFoundException may be thrown.
        Class.forName("com.mysql.jdbc.Driver");

        // jdbc:mysql://host:port/database/?args
        String url = "jdbc:mysql://" +
                Configuration.get("mysql.host") + ":" +
                Configuration.get("mysql.port") + "/" +
                Configuration.get("mysql.database") + "?" +
                "zeroDateTimeBehavior=convertToNull" + "&" +
                "autoReconnect=true";

        setupDataSource(url, Configuration.get("mysql.user"), Configuration.get("mysql.pass"));
        
        // Check if the connection with the database is working, otherwise SQLException is thrown
        getConnection().close();
    }
    
    private static void setupDataSource(String url, String user, String password) {
        PoolableConnectionFactory factory = new PoolableConnectionFactory(
                new DriverManagerConnectionFactory(url, user, password), null);
        
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxIdle);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(factory, config);
        
        factory.setPool(connectionPool);
        
        dataSource = new PoolingDataSource<>(connectionPool);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    private MySqlManager() {}
}
