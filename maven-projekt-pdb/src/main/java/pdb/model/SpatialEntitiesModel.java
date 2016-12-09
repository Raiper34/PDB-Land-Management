/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model;

import pdb.model.freeholder.FreeholderModel;
import pdb.model.DatabaseModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.spatial.geometry.JGeometry;
import pdb.model.freeholder.Freeholder;
import pdb.model.spatial.SpatialEntity;

/**
 *
 * @author archie
 */
public class SpatialEntitiesModel {

    private DatabaseModel DB;
    private Connection conn;
    private FreeholderModel freeholdersModel;

    public SpatialEntitiesModel() {
        DB = DatabaseModel.getInstance();
        conn = DB.getConnection();
        freeholdersModel = new FreeholderModel();
    }

    public void updateSpatialEntity(SpatialEntity originalSpatialEntity, SpatialEntity spatialEntityToSave){
        if(originalSpatialEntity instanceof Entity && spatialEntityToSave instanceof Entity){
            updateSpatialEntity((Entity) originalSpatialEntity, (Entity) spatialEntityToSave);
        }
        if(originalSpatialEntity instanceof Estate && spatialEntityToSave instanceof Estate){
            updateSpatialEntity((Estate) originalSpatialEntity, (Estate) spatialEntityToSave);
        }
    }
    public void updateSpatialEntity(Entity originalSpatialEntity, Entity spatialEntityToSave){
        try{
            PreparedStatement statementUpdateSpatialEntity = conn.prepareStatement("UPDATE related_spatial_entities "
                    + "SET "
                    + "name = ?, "
                    + "description = ?, "
                    + "geometry = ?, "
                    + "valid_from = ?, "
                    + "valid_to = ? "
                    + "WHERE ID = ? AND valid_from = ? AND valid_to = ?"
            );
            try {
                statementUpdateSpatialEntity.setString(1, spatialEntityToSave.name);
                statementUpdateSpatialEntity.setString(2, spatialEntityToSave.description);
                statementUpdateSpatialEntity.setObject(3, JGeometry.storeJS(conn, spatialEntityToSave.geometry));
                statementUpdateSpatialEntity.setDate(4, new java.sql.Date(spatialEntityToSave.validFrom.getTime()));
                statementUpdateSpatialEntity.setDate(5, new java.sql.Date(spatialEntityToSave.validTo.getTime()));

                statementUpdateSpatialEntity.setInt(6, originalSpatialEntity.id);
                statementUpdateSpatialEntity.setDate(7, new java.sql.Date(originalSpatialEntity.validFrom.getTime()));
                statementUpdateSpatialEntity.setDate(8, new java.sql.Date(originalSpatialEntity.validTo.getTime()));
                statementUpdateSpatialEntity.executeUpdate();
            } finally {
                statementUpdateSpatialEntity.close();
            }
        } 
         catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    
    public void updateSpatialEntity(Estate originalSpatialEntity, Estate spatialEntityToSave){
        try{
            PreparedStatement statementUpdateSpatialEntity = conn.prepareStatement("UPDATE estates "
                    + "SET "
                    + "name = ?, "
                    + "description = ?, "
                    + "geometry = ?, "
                    + "valid_from = ?, "
                    + "valid_to = ?, "
                    + "freeholders_id = ? "
                    + "WHERE ID = ? AND valid_from = ? AND valid_to = ?"
            );
            try {
                statementUpdateSpatialEntity.setString(1, spatialEntityToSave.name);
                statementUpdateSpatialEntity.setString(2, spatialEntityToSave.description);
                statementUpdateSpatialEntity.setObject(3, JGeometry.storeJS(conn, spatialEntityToSave.geometry));
                statementUpdateSpatialEntity.setDate(4, new java.sql.Date(spatialEntityToSave.validFrom.getTime()));
                statementUpdateSpatialEntity.setDate(5, new java.sql.Date(spatialEntityToSave.validTo.getTime()));
                if(spatialEntityToSave.freeholder != null)
                    statementUpdateSpatialEntity.setInt(6, spatialEntityToSave.freeholder.id);
                else
                    statementUpdateSpatialEntity.setInt(6, 0);
                
                
                statementUpdateSpatialEntity.setInt(7, originalSpatialEntity.id);
                statementUpdateSpatialEntity.setDate(8, new java.sql.Date(originalSpatialEntity.validFrom.getTime()));
                statementUpdateSpatialEntity.setDate(9, new java.sql.Date(originalSpatialEntity.validTo.getTime()));
                statementUpdateSpatialEntity.executeUpdate();
            } finally {
                statementUpdateSpatialEntity.close();
            }
        } 
         catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    
    /*
    * @param Estate spatialEntityToSave
    */
    public void saveSpatialEntityToDB(Estate spatialEntityToSave) {
        try{
            PreparedStatement statementInsertSpatialEntity = conn.prepareStatement("INSERT INTO estates "
                    + "(id, name, description, geometry, valid_from, valid_to) "
                    + "VALUES( ?, ?, ?, ?, ?, ?)"
            );
            try {
                statementInsertSpatialEntity.setInt(1, spatialEntityToSave.id);
                statementInsertSpatialEntity.setString(2, spatialEntityToSave.name);
                statementInsertSpatialEntity.setString(3, spatialEntityToSave.description);
                // convert the JGeometry instance to a Struct
                statementInsertSpatialEntity.setObject(4, JGeometry.storeJS(conn, spatialEntityToSave.geometry));
                statementInsertSpatialEntity.setDate(5, new java.sql.Date(spatialEntityToSave.validFrom.getTime()));
                statementInsertSpatialEntity.setDate(6, new java.sql.Date(spatialEntityToSave.validTo.getTime()));
                statementInsertSpatialEntity.executeUpdate();
            } finally {
                statementInsertSpatialEntity.close();
            }
        } 
         catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    } 
    
    /*
    * @param Estate spatialEntityToSave
    */
    public void saveSpatialEntityToDB(Entity spatialEntityToSave) {
        try{
            PreparedStatement statementInsertSpatialEntity = conn.prepareStatement(""
                    + "INSERT INTO related_spatial_entities"
                    + "(id, name, geometry, layer, entity_type, valid_from, valid_to)"
                    + "VALUES( ?, ?, ?, ?, ?, ?, ?)");
            try {
                statementInsertSpatialEntity.setInt(1, spatialEntityToSave.id);
                statementInsertSpatialEntity.setString(2, spatialEntityToSave.name);
                statementInsertSpatialEntity.setObject(3, JGeometry.storeJS(conn, spatialEntityToSave.geometry));
                statementInsertSpatialEntity.setString(4, spatialEntityToSave.getLayer());
                statementInsertSpatialEntity.setString(5, spatialEntityToSave.getEntityType());
                statementInsertSpatialEntity.setDate(6, new java.sql.Date(spatialEntityToSave.validFrom.getTime()));
                statementInsertSpatialEntity.setDate(7, new java.sql.Date(spatialEntityToSave.validTo.getTime()));
                statementInsertSpatialEntity.executeUpdate();
            } finally {
                statementInsertSpatialEntity.close();
            }
        } 
         catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    
    /*
    * @return List<Entity>
    */
    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        
        try {
            try (PreparedStatement stmt = conn.prepareStatement(""
                    + "select * from related_spatial_entities WHERE "
                    + "valid_to >= ?")) {
                stmt.setDate(1, new java.sql.Date(new Date().getTime()));
                try (ResultSet rset = stmt.executeQuery()) {
                    while (rset.next()) {
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Entity newEntity = new Entity(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry, rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                rset.getString("entity_type"),
                                rset.getString("layer"));
                        entities.add(newEntity);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        
        return entities;
    }
    
    /*
    * @return List<Estate>
    */
    public List<Estate> getEstateByIdWhichIntersectsInterval(int ID, Date from, Date to) {
        List<Estate> estates = new ArrayList<>();

        try {
            try (PreparedStatement stmt = conn.prepareStatement(""
                    + "select * from estates WHERE "
                    + "("
                    + "(valid_from <= ? and valid_to >= ?)"
                    + "OR"
                    + "(valid_from <= ? and valid_to >= ?)"
                    + ")"
                    + "AND ID = ?")) {
                stmt.setDate(1, new java.sql.Date(from.getTime()));
                stmt.setDate(2, new java.sql.Date(to.getTime()));
                
                stmt.setDate(3, new java.sql.Date(to.getTime()));
                stmt.setDate(4, new java.sql.Date(from.getTime()));
                
                stmt.setInt(5, ID);
                try (ResultSet rset = stmt.executeQuery()) {
                    while (rset.next()) {
                        
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        
                        Freeholder freeholder = freeholdersModel.getFreeholderById(rset.getInt("freeholders_id"));
                        Estate newEstate = new Estate(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry,
                                rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                freeholder);
                        estates.add(newEstate);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

        return estates;
    }
    
    /*
    * @return List<Entity>
    */
    public List<Entity> getEntityByIdWhichIntersectsInterval(int ID, Date from, Date to) {
        List<Entity> entities = new ArrayList<>();
        
        try {
            try (PreparedStatement stmt = conn.prepareStatement(""
                    + "select * from related_spatial_entities WHERE"
                    + "("
                    + "(valid_from <= ? and valid_to >= ?)"
                    + "OR"
                    + "(valid_from <= ? and valid_to >= ?)"
                    + ")"
                    + "AND ID = ?")) {
                stmt.setDate(1, new java.sql.Date(from.getTime()));
                stmt.setDate(2, new java.sql.Date(to.getTime()));
                stmt.setDate(3, new java.sql.Date(to.getTime()));
                stmt.setDate(4, new java.sql.Date(from.getTime()));
                
                stmt.setInt(5, ID);
                
                try (ResultSet rset = stmt.executeQuery()) {
                    while (rset.next()) {
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Entity newEntity = new Entity(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry, rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                rset.getString("entity_type"),
                                rset.getString("layer"));
                        entities.add(newEntity);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        
        return entities;
    }
    
    /*
    * @param String dateSnapshot
    * @return List<Entity>
    */
    public List<Entity> getEntities(String dateSnapshot) {
        List<Entity> entities = new ArrayList<>();
        
        try {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rset = stmt.executeQuery("select * from "
                        + "related_spatial_entities WHERE valid_from <= TO_DATE('"+ dateSnapshot +"', 'dd. mm. yyyy') and valid_to >= TO_DATE('"+ dateSnapshot +"', 'dd. mm. yyyy')")) {
                    while (rset.next()) {
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Entity newEntity = new Entity(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry, rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                rset.getString("entity_type"),
                                rset.getString("layer"));
                        entities.add(newEntity);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        
        return entities;
    }

   /*
    * @param String dateSnapshot
    * @return List<Entity>
    */
    public List<Entity> getEntities(PreparedStatement preparedSQLToGetEntitiesFromDB) {
        List<Entity> entities = new ArrayList<>();
        
        try {
            ResultSet rset = preparedSQLToGetEntitiesFromDB.executeQuery();
                    while (rset.next()) {
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Entity newEntity = new Entity(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry, rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                rset.getString("entity_type"),
                                rset.getString("layer"));
                        entities.add(newEntity);
                    }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
        
        return entities;
    }
    
    /*
    * @return List<Estate>
    */
    public List<Estate> getEstates() {
        List<Estate> estates = new ArrayList<>();

        try {
            try (PreparedStatement stmt = conn.prepareStatement(""
                    + "select * from estates WHERE "
                    + "valid_to >= ?")) {
                stmt.setDate(1, new java.sql.Date(new Date().getTime()));
                try (ResultSet rset = stmt.executeQuery()) {
                    while (rset.next()) {
                        
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        
                        Freeholder freeholder = freeholdersModel.getFreeholderById(rset.getInt("freeholders_id"));
                        Estate newEstate = new Estate(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry,
                                rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                freeholder);
                        estates.add(newEstate);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

        return estates;
    }
    
    /*
    * @param String dateSnapshot
    * @return List<Estate>
    */
    public List<Estate> getEstates(String dateSnapshot) {
        List<Estate> estates = new ArrayList<>();

        try {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rset = stmt.executeQuery("select * from estates WHERE valid_from <= TO_DATE('"+ dateSnapshot +"', 'dd. mm. yyyy') and valid_to >= TO_DATE('"+ dateSnapshot +"', 'dd. mm. yyyy')")) {
                    while (rset.next()) {
                        
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Freeholder freeholder = freeholdersModel.getFreeholderById(rset.getInt("freeholders_id"));
                        Estate newEstate = new Estate(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry,
                                rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                freeholder);
                        estates.add(newEstate);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

        return estates;
    }
    
            /*
    * @param String dateSnapshot
    * @return List<Estate>
    */
    public List<Estate> getEstates(PreparedStatement preparedSQLToGetEstatesFromDB) {
        List<Estate> estates = new ArrayList<>();
        
        try {
            ResultSet rset = preparedSQLToGetEstatesFromDB.executeQuery();
                    while (rset.next()) {
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Freeholder freeholder = freeholdersModel.getFreeholderById(rset.getInt("freeholders_id"));
                        Estate newEstate = new Estate(rset.getInt("id"),
                                rset.getString("name"),
                                rset.getString("description"),
                                jGeometry,
                                rset.getDate("valid_from"),
                                rset.getDate("valid_to"),
                                freeholder);
                        estates.add(newEstate);
                    }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
        
        return estates;
    }
    
    /*
    * @return List<Estate>
    */
    public int getNewIdForEntity() {
        return getMaxIdFromTable("related_spatial_entities") + 1;
    }
    
    /*
    * @return List<Estate>
    */
    public int getNewIdForEstate() {
        return getMaxIdFromTable("estates") + 1;
    }
    
    /*
    * @param String table
    * @return int
    */
    private int getMaxIdFromTable(String table) {
        if (!"related_spatial_entities".equals(table) && !"estates".equals(table)){
            System.err.println("ERROR bad TABLE NAME");
            return -10;
        }
        
        int maxId = 0;
        try {
            try (Statement stmt = conn.createStatement()) {
                System.err.println("select MAX(id) as max from " + table);
                try (ResultSet rset = stmt.executeQuery("select MAX(id) as max from " + table)) {
                    if (rset.next()) {
                        maxId = rset.getInt("max");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpatialEntitiesModel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        System.out.println(maxId);
        return maxId;
    }

}
