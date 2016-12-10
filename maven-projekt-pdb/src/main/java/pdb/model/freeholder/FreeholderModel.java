/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.freeholder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;

import pdb.model.DatabaseModel;
import pdb.model.DatabaseModel;
import pdb.model.spatial.Estate;
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
    
    public ObservableList<Freeholder> getEstatesFreeholdersFromDatabase(int estateId) throws SQLException
    {
        ObservableList<Freeholder> estatesFreeholders = FXCollections.observableArrayList();
        Freeholder freeholder = null;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select * from estates, freeholders where estates.freeholders_id = freeholders.id and estates.id = " + estateId
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
                    Date validFrom = rset.getDate("valid_from");
                    Date validTo = rset.getDate("valid_to");
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                    freeholder.wasFreeholderOfEstateFrom = df.format(validFrom); 
                    freeholder.wasFreeholderOfEstateTo = df.format(validTo);
                    estatesFreeholders.add(freeholder);
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
        return estatesFreeholders;
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

    public int getNumberOfFreeholdersOwnedEstateInInterval(Estate estate, Date pickerFrom, Date pickerTo) {
        
        int numberOfFreeholdersOwnedEstateInInterval = 0;
//SELECT COUNT(*) as cnt from
//(
//SELECT FREEHOLDERS.ID as FID, FREEHOLDERS.FIRST_NAME, FREEHOLDERS.SURNAME, ESTATES.ID as EID, ESTATES.NAME FROM FREEHOLDERS
//LEFT JOIN ESTATES ON FREEHOLDERS.ID=ESTATES.FREEHOLDERS_ID
//WHERE VALID_TO BETWEEN TO_DATE('1-1-1900', 'dd-mm-yyyy') AND TO_DATE('22-12-2116', 'dd-mm-yyyy') AND ESTATES.ID = 8
//GROUP BY FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME, FREEHOLDERS.SURNAME, ESTATES.ID, ESTATES.NAME
//) GROUP BY EID;  

        try {
            try (PreparedStatement stmt = this.connection.prepareStatement("" + 
                    "SELECT COUNT(*) as cnt from " +
                    "( " +
                    "SELECT FREEHOLDERS.ID as FID, FREEHOLDERS.FIRST_NAME, FREEHOLDERS.SURNAME, ESTATES.ID as EID, ESTATES.NAME FROM FREEHOLDERS " +
                    "LEFT JOIN ESTATES ON FREEHOLDERS.ID=ESTATES.FREEHOLDERS_ID " +
                    "WHERE VALID_TO BETWEEN ? AND ? " + 
                    "AND ESTATES.ID = ? " +
                    "GROUP BY FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME, FREEHOLDERS.SURNAME, ESTATES.ID, ESTATES.NAME " +
                    ") GROUP BY EID"
            )) {
                stmt.setDate(1, new java.sql.Date(pickerFrom.getTime()));
                stmt.setDate(2, new java.sql.Date(pickerTo.getTime()));
                stmt.setInt(3, estate.id);
                
                try (ResultSet rset = stmt.executeQuery()) {
                     if (rset.next()) {
                        numberOfFreeholdersOwnedEstateInInterval = (int) rset.getInt("cnt");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        
        return numberOfFreeholdersOwnedEstateInInterval;
    }

    public int getNumberOfOwnedEstatesOfFreeholdersWithSameFirstName(Freeholder selectedFreeholder, Date pickerFrom, Date pickerTo){
        int numberOfOwnedEstatesOfFreeholdersWithSameFirstName = 0;
//SELECT COUNT(*) as cnt from
//(
//SELECT  FREEHOLDERS.FIRST_NAME as FNAME, ESTATES.ID as EID FROM FREEHOLDERS
//LEFT JOIN ESTATES ON FREEHOLDERS.ID=ESTATES.FREEHOLDERS_ID
//WHERE VALID_TO BETWEEN TO_DATE('1-1-1900', 'dd-mm-yyyy') AND TO_DATE('22-12-2116', 'dd-mm-yyyy') AND FREEHOLDERS.FIRST_NAME = 'Filip'
//GROUP BY FREEHOLDERS.FIRST_NAME, ESTATES.ID
//) GROUP BY FNAME;

        try {
            try (PreparedStatement stmt = this.connection.prepareStatement("" +
                    "SELECT COUNT(*) as cnt from " +
                    "( " +
                    "SELECT  FREEHOLDERS.FIRST_NAME as FNAME, ESTATES.ID as EID FROM FREEHOLDERS " +
                    "LEFT JOIN ESTATES ON FREEHOLDERS.ID=ESTATES.FREEHOLDERS_ID " +
                    "WHERE VALID_TO BETWEEN ? AND ? AND FREEHOLDERS.FIRST_NAME = ? " +
                    "GROUP BY FREEHOLDERS.FIRST_NAME, ESTATES.ID " +
                    ") GROUP BY FNAME"
                    )) {
                stmt.setDate(1, new java.sql.Date(pickerFrom.getTime()));
                stmt.setDate(2, new java.sql.Date(pickerTo.getTime()));
                stmt.setString(3, selectedFreeholder.first_name);

                try (ResultSet rset = stmt.executeQuery()) {
                     if (rset.next()) {
                        numberOfOwnedEstatesOfFreeholdersWithSameFirstName = (int) rset.getInt("cnt");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        
        return numberOfOwnedEstatesOfFreeholdersWithSameFirstName;
    }
    
    public int getNumberOfOwnedTimesEstateBy(Freeholder selectedFreeholder, Estate estate, Date pickerFrom, Date pickerTo) {
        int numberOfOwnedTimesEstatesBy = 0;
//SELECT ESTATES.ID, ESTATES.NAME, FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME, COUNT(ESTATES.ID) as cnt FROM ESTATES
//LEFT JOIN FREEHOLDERS ON ESTATES.FREEHOLDERS_ID=FREEHOLDERS.ID
//WHERE VALID_TO BETWEEN TO_DATE('22-12-2006', 'dd-mm-yyyy') AND TO_DATE('22-12-2116', 'dd-mm-yyyy')
//AND FREEHOLDERS.ID = 1 AND ESTATES.ID = 8
//GROUP BY ESTATES.ID, ESTATES.NAME, FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME; 
        try {
            try (PreparedStatement stmt = this.connection.prepareStatement(""
                    + "SELECT ESTATES.ID, ESTATES.NAME, FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME, COUNT(ESTATES.ID) as cnt "
                    + "FROM ESTATES LEFT JOIN FREEHOLDERS ON ESTATES.FREEHOLDERS_ID=FREEHOLDERS.ID "
                    + "WHERE VALID_TO BETWEEN ? AND ? "
                    + "AND FREEHOLDERS.ID = ? AND ESTATES.ID = ? "
                    + "GROUP BY ESTATES.ID, ESTATES.NAME, FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME"
                    )) {
                stmt.setDate(1, new java.sql.Date(pickerFrom.getTime()));
                stmt.setDate(2, new java.sql.Date(pickerTo.getTime()));
                stmt.setInt(3, selectedFreeholder.id);
                stmt.setInt(4, estate.id);

                try (ResultSet rset = stmt.executeQuery()) {
                     if (rset.next()) {
                        numberOfOwnedTimesEstatesBy = (int) rset.getInt("cnt");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        
        return numberOfOwnedTimesEstatesBy;
    }
    
    
    
}
