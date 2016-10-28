/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.projekt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLParser class
 * @author gulan
 */
public class SqlParser {

    public List<String> queries;
    
    /**
     * Constructor
     */
    public SqlParser() {

    }

    /**
     * Parse given sql script specified by path and save to queries attribute of object
     * @param filePath 
     */
    public void parseSql(String filePath) {
        InputStream suborStream;
        try 
        {
            suborStream = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(suborStream, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(reader);
            String sqlContent = "";
            String line = "";
            try 
            {
                while ((line = br.readLine()) != null) 
                {
                    sqlContent += line;
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(SqlParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.queries = Arrays.asList(sqlContent.split(";"));
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(SqlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
