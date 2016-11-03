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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author archie
 */
public class SpatialEntitiesModel {

    private DatabaseModel DB;
    private Connection conn;

    public SpatialEntitiesModel() {
        DB = DatabaseModel.getInstance();
        conn = DB.getConnection();
    }
    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        
        try {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rset = stmt.executeQuery("select * from "
                        + "related_spatial_entities WHERE valid_to = TO_DATE('27-10-2116', 'dd-mm-yyyy')")) {
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

    public List<Estate> getEstates() {
        List<Estate> estates = new ArrayList<>();

        try {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rset = stmt.executeQuery("select * from estates WHERE valid_to = TO_DATE('27-10-2116', 'dd-mm-yyyy')")) {
                    while (rset.next()) {
                        
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Date validFrom = rset.getDate("valid_from");
                        Date validto = rset.getDate("valid_to");
                        //rset.getInt("freeholders_id");
                        //rset.getInt("photos_id");
                        FreeholderModel freeholder = new FreeholderModel();
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
    
    public List<Estate> getEstates(String dateSnapshot) {
        List<Estate> estates = new ArrayList<>();

        try {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rset = stmt.executeQuery("select * from estates WHERE valid_from <= TO_DATE('"+ dateSnapshot +"', 'dd. mm. yyyy') and valid_to >= TO_DATE('"+ dateSnapshot +"', 'dd. mm. yyyy')")) {
                    while (rset.next()) {
                        
                        byte[] image = rset.getBytes("geometry");
                        JGeometry jGeometry = JGeometry.load(image);
                        Date validFrom = rset.getDate("valid_from");
                        Date validto = rset.getDate("valid_to");
                        //rset.getInt("freeholders_id");
                        //rset.getInt("photos_id");
                        FreeholderModel freeholder = new FreeholderModel();
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
    
    public int getMaxId(String table) {
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

        return maxId;
    }

}
