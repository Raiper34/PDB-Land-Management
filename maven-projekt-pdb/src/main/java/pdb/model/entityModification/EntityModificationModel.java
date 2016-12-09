/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.entityModification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
public class EntityModificationModel {
    
    private DatabaseModel DB;
    private Connection conn;

    public EntityModificationModel() {
        DB = DatabaseModel.getInstance();
        conn = DB.getConnection();
    }
    
    public void deleteObjectInInterval(SpatialEntity spatialEntity, LocalDate from, LocalDate to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        String deleteFrom = formatter.format(from);
        String deleteTo = formatter.format(to);
        
        String spatialEntityType = "";
        int idOfSpatialEntity = 0;

        if (spatialEntity instanceof Entity) {
            
            spatialEntityType = "related_spatial_entities";
            idOfSpatialEntity = ((Entity) spatialEntity).id;
        } 
        else if ( spatialEntity instanceof Estate) {
            
            spatialEntityType = "estates";
            idOfSpatialEntity = ((Estate) spatialEntity).id;
        } 
        
                
        String sqlQuery = "DELETE FROM "+ spatialEntityType +" " +
                "WHERE id = "+ idOfSpatialEntity +" " + 
                "AND valid_from = TO_DATE('"+ deleteFrom +"', 'dd. mm. yyyy') " +
                "AND valid_to = TO_DATE('"+ deleteTo +"', 'dd. mm. yyyy')";

        try {

            try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                stmt.executeUpdate(sqlQuery);
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
       
    }
}
