/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.time;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oracle.spatial.geometry.JGeometry;
import pdb.model.DatabaseModel;
import pdb.model.SpatialEntitiesModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import pdb.model.spatial.SpatialEntity;
import pdb.model.time.TableViewItem;

/**
 *
 * @author jan
 */
public class TimeModel {
    
    private DatabaseModel DB;
    private Connection conn;

    public TimeModel() {
        DB = DatabaseModel.getInstance();
        conn = DB.getConnection();
    }
    
    public List<String> getListOfDateWhenSomethingSpatialObjectChanges(boolean considerUndergroundLayer, boolean considerGroundLayer, boolean considerOverGroundLayer) {
        String layers = "''";
        if (considerUndergroundLayer) {
            layers += ", 'underground'";
        }
        if (considerOverGroundLayer) {
            layers += ", 'overground'";
        }
        if (considerGroundLayer) {
            layers += ", 'ground'";
            
        }
        
        List<String> listOfDateWhenSomethingSpatialObjectChanges = new ArrayList<>();

        try {
            try (Statement stmt = DB.getConnection().createStatement()) {
                
                String query = "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy");
                
                if (considerGroundLayer) {
                    query += "SELECT valid_from from (" +
                                "Select valid_from from estates  Where ID in(Select Distinct Min(ID) from estates Group By valid_from)" +
                                " union " +
                                "Select valid_from from related_spatial_entities Where ID in(Select Distinct Min(ID) from related_spatial_entities WHERE layer in ("+ layers +") Group By valid_from)" +
                             ") ORDER BY valid_from ASC";
                    //System.out.println(query);
                }
                else {
                    query += "Select valid_from from related_spatial_entities Where ID in(Select Distinct Min(ID) from related_spatial_entities WHERE layer in ("+ layers +") Group By valid_from) ORDER BY valid_from ASC";
                    //System.out.println(query);
                }
                
                try (ResultSet rset = stmt.executeQuery(query)) {
                    while (rset.next()) {
                        listOfDateWhenSomethingSpatialObjectChanges.add(sdf.format(rset.getDate("valid_from")));
                        
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

        return listOfDateWhenSomethingSpatialObjectChanges;
        
    }
    
    public ObservableList<TableViewItem> getHistoryOfObjecWithSpecifiedId(SpatialEntity spatialEntity) {
        ObservableList<TableViewItem> data = FXCollections.observableArrayList();
        
        String sqlQuery = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy");
        
        String spatialEntityType = "";

        if (spatialEntity instanceof Entity) {
   
            sqlQuery += "SELECT valid_from, valid_to, id" +
                        "  FROM related_spatial_entities" +
                        "  WHERE id = "+ ((Entity) spatialEntity).id +"";
            
            spatialEntityType = "related spatial entity";
        } 
        else if ( spatialEntity instanceof Estate) {
        
            sqlQuery += "SELECT valid_from, valid_to, id" +
                        "  FROM estates" +
                        "  WHERE id = "+ ((Estate) spatialEntity).id +"";
            
            spatialEntityType = "estate";
        } 

        try {

            try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                
                try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                    while (rset.next()) {
                        data.add(new TableViewItem(sdf.format(rset.getDate("valid_from")), sdf.format(rset.getDate("valid_to")), rset.getInt("id"), spatialEntityType));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
        
        return data;
    }

    public ArrayList<PreparedStatement> createSqlQueriesToGetObjectInHistotory(String validFrom, String validTo, int id, String spatialEntityType) {

        ArrayList<PreparedStatement> sqlQueriesToGetObjectInHistotory = new ArrayList<PreparedStatement>();
        
        String entityCondition = "";
        String estateCondition = "";
        
        String sqlQueryEntities = "SELECT * from related_spatial_entities WHERE id IS NULL"; // get empty result set
        String sqlQueryEstates = "SELECT * from estates WHERE id IS NULL"; // get empty result set
             
        // spatialEntityType is related entity
        if (spatialEntityType.equals("related spatial entity")) {
            sqlQueryEntities = "select * from related_spatial_entities " +
                "WHERE id = "+ id +" " +
                    "AND valid_from = TO_DATE('"+ validFrom +"', 'dd. mm. yyyy') " + 
                    "AND valid_to = TO_DATE('"+ validTo +"', 'dd. mm. yy')";
      
        }
        // selectedSpatialEntity is instance of Estate
        else {
            sqlQueryEstates = "select * from estates " +
                "WHERE id = "+ id +" " +
                    "AND valid_from = TO_DATE('"+ validFrom +"', 'dd. mm. yyyy') " + 
                    "AND valid_to = TO_DATE('"+ validTo +"', 'dd. mm. yyyy')";
        
        }
        
        //System.out.println("w" + sqlQueryEstates);
        //System.out.println("e" + sqlQueryEntities);
        //System.out.println(spatialEntityType)
                
        try {
            PreparedStatement psSelectEntities = DatabaseModel.getInstance().getConnection().prepareStatement(sqlQueryEntities);
            PreparedStatement psSelectEstates = DatabaseModel.getInstance().getConnection().prepareStatement(sqlQueryEstates);

            sqlQueriesToGetObjectInHistotory.add(psSelectEntities);
            sqlQueriesToGetObjectInHistotory.add(psSelectEstates);
        }
        catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }

        return sqlQueriesToGetObjectInHistotory;
    }
}
