/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import pdb.controller.DatabaseSettingsController;

/**
 * Database acces info, aka class to get and put db access informations to file
 * 
 * @author gulan
 */
public class DatabaseAccessInfo {
    
    public String username = "";
    public String password = "";
    public String host = "";
    public String port = "";
    public String serviceName = "";
    
    /**
     * 
     */
    public DatabaseAccessInfo()
    {
        
    }
    
    /**
     * Save Acces info to file
     * @param username
     * @param password
     * @param host
     * @param port
     * @param serviceName 
     */
    public void saveAccessInfo(String username, String password, String host, String port, String serviceName)
    {

        PrintWriter file = null;
        try 
        {
            file = new PrintWriter("dbAccessInfo", "UTF-8");
            file.println(username);
            file.println(password);
            file.println(host);
            file.println(port);
            file.println(serviceName);
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(DatabaseAccessInfo.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(DatabaseAccessInfo.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally 
        {
            file.close();
        }

    }
    
    /**
     * Load acces info to attributes, you can get username, password etc, direct from theese attributes
     */
    public void loadAccessInfoToAttributes()
    {
        try 
        {
            InputStream suborStream = new FileInputStream("dbAccessInfo");
            InputStreamReader reader = new InputStreamReader(suborStream, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(reader);
            try 
            {
                this.username = br.readLine();
                this.password = br.readLine();
                this.host = br.readLine();
                this.port = br.readLine();
                this.serviceName = br.readLine(); 
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(DatabaseSettingsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(DatabaseSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
