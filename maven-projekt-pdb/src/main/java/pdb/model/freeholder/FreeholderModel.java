package pdb.model.freeholder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;

import pdb.model.DatabaseModel;
import pdb.model.spatial.Estate;

/**
 * Class to manipulating with freeholders table
 * @author gulan
 */
public class FreeholderModel {
    
    DatabaseModel database;
    Connection connection;
    
    public ObservableList<Freeholder> freeholders;
    
    /**
     * Constructor
     */
    public FreeholderModel()
    {
        this.database = DatabaseModel.getInstance();
        this.connection = this.database.getConnection();
        this.freeholders = FXCollections.observableArrayList();
    }
    
    /**
     * Create new freeholder into database
     * @param name
     * @param surname
     * @param birthDate 
     */
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
    
    /**
     * Get all freeholders stored in atribute
     * @return observable list of freeholders
     */
    public ObservableList<Freeholder> getListAllFreeHolders()
    {
        return this.freeholders;
    }
    
    /**
     * Get freeholders from database and store into observable list of atribute
     * @throws SQLException 
     */
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
    
    /**
     * Get estates freeholders from database by estate id
     * @param estateId
     * @return observable list of freeholders of estate
     * @throws SQLException 
     */
    public ObservableList<Freeholder> getEstatesFreeholdersFromDatabase(int estateId) throws SQLException
    {
        ObservableList<Freeholder> estatesFreeholders = FXCollections.observableArrayList();
        Freeholder tmpFreeholder = null;
        Freeholder freeholder = null;
        
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select * from freeholders, estates where estates.freeholders_id = freeholders.id and estates.id = " + estateId + " ORDER BY estates.valid_from"
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
                    freeholder.dateWasFreeholderOfEstateFrom = validFrom;
                    freeholder.dateWasFreeholderOfEstateTo = validTo;
                    freeholder.wasFreeholderOfEstateFrom = df.format(validFrom); 
                    freeholder.wasFreeholderOfEstateTo = df.format(validTo);
                    
                    //estatesFreeholders.add(freeholder);
                    if(tmpFreeholder != null)
                    {
                        if(tmpFreeholder.dateWasFreeholderOfEstateTo.equals(freeholder.dateWasFreeholderOfEstateFrom) && tmpFreeholder.id == freeholder.id)
                        {
                            tmpFreeholder.dateWasFreeholderOfEstateTo = validTo;
                            tmpFreeholder.wasFreeholderOfEstateTo = df.format(validTo);
                        }
                        else
                        {
                            estatesFreeholders.add(tmpFreeholder);
                            tmpFreeholder = freeholder;
                        }
                        
                    }
                    else
                    {
                        tmpFreeholder = freeholder;
                    }
                }
                if(tmpFreeholder != null)
                {
                    estatesFreeholders.add(tmpFreeholder);
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
    
    /**
     * Get freeholder from database by id 
     * @param number
     * @return freeholder from db
     * @throws SQLException 
     */
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
    
    /**
     * Get max id of freeholders from database
     * @return max id as Integer
     * @throws SQLException 
     */
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

    /**
     * Get number of freeholders that own estete in selected interval
     * @param estate
     * @param pickerFrom
     * @param pickerTo
     * @return number of freeholders
     */
    public int getNumberOfFreeholdersOwnedEstateInInterval(Estate estate, Date pickerFrom, Date pickerTo) {
        
        int numberOfFreeholdersOwnedEstateInInterval = 0; 

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

    /**
     * Get Number of owned states of freeholders with same first name
     * @param selectedFreeholder
     * @param pickerFrom
     * @param pickerTo
     * @return number as Integer
     */
    public int getNumberOfOwnedEstatesOfFreeholdersWithSameFirstName(Freeholder selectedFreeholder, Date pickerFrom, Date pickerTo){
        int numberOfOwnedEstatesOfFreeholdersWithSameFirstName = 0;

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
    
    /**
     * Get number of owned times estate by
     * @param selectedFreeholder
     * @param estate
     * @param pickerFrom
     * @param pickerTo
     * @return number as integer
     */
    public int getNumberOfOwnedTimesEstateBy(Freeholder selectedFreeholder, Estate estate, Date pickerFrom, Date pickerTo) {
        int numberOfOwnedTimesEstatesBy = 0;

        try {
            try (PreparedStatement stmt = this.connection.prepareStatement(""
                    + "SELECT ESTATES.ID, ESTATES.NAME, FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME, COUNT(ESTATES.ID) as cnt "
                    + "FROM ESTATES LEFT JOIN FREEHOLDERS ON ESTATES.FREEHOLDERS_ID=FREEHOLDERS.ID "
                    + "WHERE VALID_TO BETWEEN ? AND ? "
                    + "AND FREEHOLDERS.ID = ? AND ESTATES.ID = ? "
                    + "AND ESTATES.VALID_FROM NOT IN (SELECT DISTINCT ESTATES.VALID_TO FROM ESTATES WHERE FREEHOLDERS.ID = ? AND ESTATES.ID = ?)"
                    + "GROUP BY ESTATES.ID, ESTATES.NAME, FREEHOLDERS.ID, FREEHOLDERS.FIRST_NAME"
                    )) {
                stmt.setDate(1, new java.sql.Date(pickerFrom.getTime()));
                stmt.setDate(2, new java.sql.Date(pickerTo.getTime()));
                stmt.setInt(3, selectedFreeholder.id);
                stmt.setInt(4, estate.id);
                stmt.setInt(5, selectedFreeholder.id);
                stmt.setInt(6, estate.id);
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
