/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;

import pdb.model.DatabaseModel;
import pdb.model.spatial.ImprovedCircle;

/**
 *
 * @author gulan
 */
public class FreeholderModel {
    
    DatabaseModel database;
    Connection connection;
    
    public ObservableList<Freeholder> freeholders;
    
    public FreeholderModel()
    {
        this.database = DatabaseModel.getInstance();
        this.connection = this.database.getConnection();
        this.freeholders = FXCollections.observableArrayList();
    }
    
    public void createNewFreeholder(String name, String surname, String birthDate)
    {
        try 
        {
            try (Statement stmt = this.connection.createStatement()) 
            {
                ResultSet rset = stmt.executeQuery(
                        "INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(" + (this.getMaxId() + 1) + ",'" + name + "', '" + surname + "', TO_DATE('" + birthDate +"', 'dd-mm-yyyy'))"
                );
            }

        } 
        catch (SQLException sqlEx) 
        {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
    }
    
    public ObservableList<Freeholder> getListAllFreeHolders()
    {
        return this.freeholders;
    }
    
    public void getFreeHoldersFromDatabase() throws SQLException
    {
        Freeholder freeholder = null;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select * from freeholders"
        );
        try 
        {
            OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
            try 
            {
                while (rset.next()) 
                {
                    int id = (int) rset.getInt("id");
                    String name = (String) rset.getString("first_name");
                    String surname = (String) rset.getString("surname");
                    String birthDate = (String) rset.getString("birth_date");
                    freeholder = new Freeholder(id, name, surname, birthDate);
                    this.freeholders.add(freeholder);
                }
            } 
            finally 
            {
                rset.close();
            }
            } 
        finally 
        {
            pstmtSelect.close();
        }
    }
    
    public Freeholder getFreeholderById(int number) throws SQLException
    {
        Freeholder freeholder = null;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select * from freeholders where id = " + number
        );
        try 
        {
            OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
            try 
            {
                if (rset.next()) 
                {
                    int id = (int) rset.getInt("id");
                    String name = (String) rset.getString("first_name");
                    String surname = (String) rset.getString("surname");
                    String birthDate = (String) rset.getString("birth_date");
                    freeholder = new Freeholder(id, name, surname, birthDate);
                }
            } 
            finally 
            {
                rset.close();
            }
            } 
        finally 
        {
            pstmtSelect.close();
        }
        return freeholder;
    }
    
    private int getMaxId() throws SQLException
    {
        int max = 0;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select MAX(id) as max from freeholders"
        );
        try 
        {
            OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
            try 
            {
                if (rset.next()) 
                {
                     max = (int) rset.getInt("max");
                }
            } 
            finally 
            {
                rset.close();
            }
            } 
        finally 
        {
            pstmtSelect.close();
        }
        return max;
    }
    
    
}
