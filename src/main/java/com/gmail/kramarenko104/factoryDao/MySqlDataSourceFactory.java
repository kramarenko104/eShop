package com.gmail.kramarenko104.factoryDao;

import org.apache.log4j.Logger;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class MySqlDataSourceFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySqlDataSourceFactory.class);
    private BasicDataSource dataSource;

    public MySqlDataSourceFactory() {
        if (dataSource == null) {
            ResourceBundle config = null;
            try {
                config = ResourceBundle.getBundle("jdbc");
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(config.getString("driverClassName"));
            ds.setUrl(config.getString("url"));
            ds.setUsername(config.getString("username"));
            ds.setPassword(config.getString("password"));
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);
            dataSource = ds;
        }
    }

    @Override
    public boolean openConnection() {
        try {
            Connection conn = dataSource.getConnection();
            super.setConnection(conn);
            logger.debug("Connection obtained...");
            return true;
        } catch (SQLException e) {
            logger.debug("Connection failed. SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
        logger.debug("Connection closed...");
    }
}
