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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pdb.model.DatabaseModel;
import pdb.model.spatial.SpatialEntitiesModel;
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

    /**
     *
     */
    public EntityModificationModel() {
        DB = DatabaseModel.getInstance();
        conn = DB.getConnection();
    }
    
    /**
     *
     * @param spatialEntity
     * @param from
     * @param to
     */
    public void deleteObjectInInterval(SpatialEntity spatialEntity, LocalDate from, LocalDate to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        String deleteFrom = formatter.format(from);
        String deleteTo = formatter.format(to);
        
        String spatialEntityType = "";
        int idOfSpatialEntity = 0;
        String sqlQuery = "";
        
        Entity entity1 = null;
        Estate estate1 = null;
        SpatialEntitiesModel spatialEntitiesModel = new SpatialEntitiesModel();

        if (spatialEntity instanceof Entity) {
            
            spatialEntityType = "related_spatial_entities";
            idOfSpatialEntity = ((Entity) spatialEntity).id;
        } 
        else if ( spatialEntity instanceof Estate) {
            
            spatialEntityType = "estates";
            idOfSpatialEntity = ((Estate) spatialEntity).id;
        } 

        try {
            int numAffectedRows = 0;
            
           // try to delete objects which are inside deletion interval                
            sqlQuery = "DELETE FROM "+ spatialEntityType +" " +
                "WHERE id = "+ idOfSpatialEntity +" " + 
                "AND valid_from >= TO_DATE('"+ deleteFrom +"', 'dd. mm. yyyy') " +
                "AND valid_to <= TO_DATE('"+ deleteTo +"', 'dd. mm. yyyy')";
            
            try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                numAffectedRows = stmt.executeUpdate(sqlQuery);
            }
            
            
            // if previous deletion delete 0 rows, then try deletion inside one object
            boolean someResult = false;
            if (numAffectedRows == 0) {
            
                // if previous deletion delete 0 rows, then try deletion inside one object
                sqlQuery = "SELECT id, valid_from, valid_to FROM "+ spatialEntityType +" " +
                    "WHERE id = "+ idOfSpatialEntity +" " + 
                    "AND valid_from < TO_DATE('"+ deleteFrom +"', 'dd. mm. yyyy') " +
                    "AND valid_to > TO_DATE('"+ deleteTo +"', 'dd. mm. yyyy')";

                // get object inside which we do deletion
                try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                    try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                        while (rset.next()) {
                            someResult = true;
                            int id = rset.getInt("id");
                            Date valid_from = rset.getDate("valid_from");
                            Date valid_to = rset.getDate("valid_to");

                            if (spatialEntityType.equals("related_spatial_entities")) {
                                entity1 = spatialEntitiesModel.createEntity(id, valid_from, valid_to);
                            }
                            else {
                                estate1 = spatialEntitiesModel.createEstate(id, valid_from, valid_to);
                            }
                        }

                    }
                }

                // split objet to 2 parts, then delete original object in db and save splitted parts to db
                if (someResult) {
                    if (spatialEntityType.equals("related_spatial_entities")) {

                        Entity entity2 = spatialEntitiesModel.createEntity(entity1.id, entity1.validFrom, entity1.validTo);
                        entity2.validFrom = new SimpleDateFormat("dd. MM. yyyy").parse(deleteTo);
                        
                        this.deleteSpatialEntity(entity1);
                        entity1.validTo =  new SimpleDateFormat("dd. MM. yyyy").parse(deleteFrom);
                        
                        spatialEntitiesModel.saveSpatialEntityToDB(entity1);
                        spatialEntitiesModel.saveSpatialEntityToDB(entity2);

                    }
                    else {

                        Estate estate2 = spatialEntitiesModel.createEstate(idOfSpatialEntity, estate1.validFrom, estate1.validTo);
                        estate2.validFrom = new SimpleDateFormat("dd. MM. yyyy").parse(deleteTo);

                        this.deleteSpatialEntity(estate1);
                        estate1.validTo =  new SimpleDateFormat("dd. MM. yyyy").parse(deleteFrom);

                        spatialEntitiesModel.saveSpatialEntityToDB(estate1);
                        spatialEntitiesModel.saveSpatialEntityToDB(estate2);

                    }
                }
            }
            // if we didnt delete interval inside object then we can try delete time validity of object from left and right
            if (someResult == false) {
                // first try delete from right
                sqlQuery = "SELECT id, valid_from, valid_to FROM "+ spatialEntityType +" " +
                    "WHERE id = "+ idOfSpatialEntity +" " + 
                    "AND valid_from < TO_DATE('"+ deleteFrom +"', 'dd. mm. yyyy') " +
                    "AND valid_to <= TO_DATE('"+ deleteTo +"', 'dd. mm. yyyy') " +
                    "AND valid_to > TO_DATE('"+ deleteFrom +"', 'dd. mm. yyyy')";

                
                try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                    try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                        while (rset.next()) {
                            someResult = true;
                            int id = rset.getInt("id");
                            Date valid_from = rset.getDate("valid_from");
                            Date valid_to = rset.getDate("valid_to");

                            if (spatialEntityType.equals("related_spatial_entities")) {
                                entity1 = spatialEntitiesModel.createEntity(id, valid_from, valid_to);
                            }
                            else {
                                estate1 = spatialEntitiesModel.createEstate(id, valid_from, valid_to);
                            }
                        }

                    }
                }

                // delete original object in db and save modified object
                if (someResult) {
                    if (spatialEntityType.equals("related_spatial_entities")) {
                        this.deleteSpatialEntity(entity1);

                        entity1.validTo =  new SimpleDateFormat("dd. MM. yyyy").parse(deleteFrom);

                        spatialEntitiesModel.saveSpatialEntityToDB(entity1);

                    }
                    else {
                        this.deleteSpatialEntity(estate1);

                        estate1.validTo =  new SimpleDateFormat("dd. MM. yyyy").parse(deleteFrom);

                        spatialEntitiesModel.saveSpatialEntityToDB(estate1);
                    }
                }
            
                // -------------------------------------------------------------------------------------
                // second we try delete from left
                someResult = false;
                
                sqlQuery = "SELECT id, valid_from, valid_to FROM "+ spatialEntityType +" " +
                    "WHERE id = "+ idOfSpatialEntity +" " + 
                    "AND valid_from >= TO_DATE('"+ deleteFrom +"', 'dd. mm. yyyy') " +
                    "AND valid_to > TO_DATE('"+ deleteTo +"', 'dd. mm. yyyy') " + 
                    "AND valid_from < TO_DATE('"+ deleteTo +"', 'dd. mm. yyyy')";

                
                try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                    try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                        while (rset.next()) {
                            someResult = true;
                            int id = rset.getInt("id");
                            Date valid_from = rset.getDate("valid_from");
                            Date valid_to = rset.getDate("valid_to");

                            if (spatialEntityType.equals("related_spatial_entities")) {
                                entity1 = spatialEntitiesModel.createEntity(id, valid_from, valid_to);
                            }
                            else {
                                estate1 = spatialEntitiesModel.createEstate(id, valid_from, valid_to);
                            }
                        }

                    }
                }

                // delete original object in db and save modified object
                if (someResult) {
                    if (spatialEntityType.equals("related_spatial_entities")) {
                        this.deleteSpatialEntity(entity1);

                        entity1.validFrom =  new SimpleDateFormat("dd. MM. yyyy").parse(deleteTo);

                        spatialEntitiesModel.saveSpatialEntityToDB(entity1);

                    }
                    else {
                        this.deleteSpatialEntity(estate1);

                        estate1.validFrom =  new SimpleDateFormat("dd. MM. yyyy").parse(deleteTo);

                        spatialEntitiesModel.saveSpatialEntityToDB(estate1);
                    }
                }
            
            }
            

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
       
    }
    
    /**
     *
     * @param entity
     */
    public void deleteSpatialEntity(Entity entity) {
        try {
            DateFormat df = new SimpleDateFormat("dd. MM. yyyy");

            String sqlQuery = "DELETE FROM related_spatial_entities " +
                    "WHERE id = "+ entity.id +" " + 
                    "AND valid_from = TO_DATE('"+ df.format(entity.validFrom) +"', 'dd. mm. yyyy') " +
                    "AND valid_to = TO_DATE('"+ df.format(entity.validTo) +"', 'dd. mm. yyyy')";

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
    
    /**
     *
     * @param entity
     */
    public void deleteSpatialEntity(Estate entity) {
        try {
            DateFormat df = new SimpleDateFormat("dd. MM. yyyy");

            String sqlQuery = "DELETE FROM estates " +
                    "WHERE id = "+ entity.id +" " + 
                    "AND valid_from = TO_DATE('"+ df.format(entity.validFrom) +"', 'dd. mm. yyyy') " +
                    "AND valid_to = TO_DATE('"+ df.format(entity.validTo) +"', 'dd. mm. yyyy')";

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
