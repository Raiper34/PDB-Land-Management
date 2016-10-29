/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;
import pdb.model.SqlParser;

/**
 * Database Model - singleton patern
 * 
 * @author gulan
 */
public class DatabaseModel {

    private static DatabaseModel instance = null;
    private boolean isConnected;
    private OracleDataSource ods;
    private Connection connection;

    /**
     * Constructor
     */
    protected DatabaseModel() {

    }

    /**
     * Get instance of singleton object
     * @return 
     */
    public static DatabaseModel getInstance() {
        if (instance == null) {
            instance = new DatabaseModel();
        }
        return instance;
    }

    /**
     * Connect to database and store connection
     * @param host
     * @param port
     * @param serviceName
     * @param username
     * @param password
     * @return boolean
     */
    public boolean connectDatabase(String host, String port, String serviceName, String username, String password) {
        if(!this.isConnected) 
        {
            try 
            {
                String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
                this.ods = new OracleDataSource();
                this.ods.setURL(url);

                this.ods.setUser(username);
                this.ods.setPassword(password);
                this.connection = this.ods.getConnection();
                this.isConnected = true;

                //conn.close();
            } catch (Exception ex) 
            {
                this.isConnected = false;
                System.err.println("Exception: " + ex.getMessage());
                return false;
            }
        }
        return true;
    }
    
    /**
     * Disconnect from database
     */
    public void disconnectDatabase()
    {
        if(this.isConnected)
        {
            try 
            {
                this.connection.close();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.isConnected = false;
        }
    }
    
    /**
     * Check if is connected to db
     * @return boolean
     */
    public boolean isConnected()
    {
        return this.isConnected;
    }
    
    /**
     * Initialize database with initialization script
     */
    public void initializeDatabase()
    {
        SqlParser sqlParser = new SqlParser();
        sqlParser.parseSql("initilizingScript.sql");
        sqlParser.queries.forEach((query) -> {
            try 
            {
                Statement stmt = this.connection.createStatement();
                try 
                {
                    ResultSet rset;
                    try
                    {
                        rset = stmt.executeQuery(query); 
                        rset.close();
                    }
                    catch (SQLException ex)
                    {
                        Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } finally
                {
                    stmt.close();
                }
            }   
            catch (SQLException ex) 
            {
                Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * Return actual connection
     * @return 
     */
    public Connection getConnection()
    {
        return this.connection;
    }

}
