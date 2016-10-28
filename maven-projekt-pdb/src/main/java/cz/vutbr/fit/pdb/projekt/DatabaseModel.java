/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.projekt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;

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
     * @param url
     * @param username
     * @param password 
     */
    public void connectDatabase(String url, String username, String password) {
        if(!this.isConnected) 
        {
            try 
            {
                this.ods = new OracleDataSource();
                this.ods.setURL(url);

                this.ods.setUser(username);//XHEREC00
                this.ods.setPassword(password);//0oxxz6gs
                this.connection = this.ods.getConnection();
                this.isConnected = true;

                //conn.close();
            } catch (Exception ex) 
            {
                this.isConnected = false;
                System.err.println("Exception: " + ex.getMessage());
            }
        }
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
        //todo
    }

}
