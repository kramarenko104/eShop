package com.gmail.kramarenko104.factoryDao;

import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MySqlJDBCDaoFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySqlJDBCDaoFactory.class);
    private String connStr;

    public MySqlJDBCDaoFactory() {
        ResourceBundle config = null;
        try {
            config = ResourceBundle.getBundle("jdbc");
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(config.getString("driverClassName")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connStr = new StringBuilder().append(config.getString("url"))
                .append("?").append("user=").append(config.getString("username"))
                .append("&password=").append(config.getString("password")).toString();
    }

    @Override
    public boolean openConnection() {
        try {
            Connection conn = DriverManager.getConnection(connStr);
            super.setConnection(conn);
            logger.debug("Connection obtained");
            return true;
        } catch (SQLException e) {
            logger.debug("Connection failed. SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
        logger.debug("Connection to db is closed");
    }
}
