/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pdb.model.DatabaseModel;
import pdb.model.SpatialEntitiesModel;

/**
 *
 * @author jan
 */
public class SpatialModel {
    private DatabaseModel DB;
    private Connection conn;

    public SpatialModel() {
        DB = DatabaseModel.getInstance();
        conn = DB.getConnection();
    }
    
    public double getLengthOrPerimeter(SpatialEntity spatialEntity) {
        
        String sqlQuery = "";
        double length = 0.0;

        if (spatialEntity instanceof Entity) {
            String validFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Entity) spatialEntity).validFrom );
            String validToFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Entity) spatialEntity).validTo );
            
            sqlQuery += "SELECT SDO_GEOM.SDO_LENGTH(related_spatial_entities.geometry, user_sdo_geom_metadata.diminfo) as length" +
                        "  FROM related_spatial_entities, user_sdo_geom_metadata" +
                        "  WHERE user_sdo_geom_metadata.table_name = 'RELATED_SPATIAL_ENTITIES' " +
                        "    AND user_sdo_geom_metadata.column_name = 'GEOMETRY' " +
                        "    AND related_spatial_entities.id = "+ ((Entity) spatialEntity).id +"" +
                        "    AND related_spatial_entities.valid_from = TO_DATE('"+ validFromFormatted +"', 'dd-mm-yyyy') " +
                        "    AND related_spatial_entities.valid_to = TO_DATE('"+ validToFormatted +"', 'dd-mm-yyyy')";
        } 
        else if ( spatialEntity instanceof Estate) {
            String validFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Estate) spatialEntity).validFrom );
            String validToFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Estate) spatialEntity).validTo );
            
            sqlQuery += "SELECT SDO_GEOM.SDO_LENGTH(estates.geometry, user_sdo_geom_metadata.diminfo) as length" +
                        "  FROM estates, user_sdo_geom_metadata" +
                        "  WHERE user_sdo_geom_metadata.table_name = 'ESTATES' " +
                        "    AND user_sdo_geom_metadata.column_name = 'GEOMETRY' " +
                        "    AND estates.id = "+ ((Estate) spatialEntity).id +"" +
                        "    AND estates.valid_from = TO_DATE('"+ validFromFormatted +"', 'dd-mm-yyyy') " +
                        "    AND estates.valid_to = TO_DATE('"+ validToFormatted +"', 'dd-mm-yyyy')";
        } 

        try {

            try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                
                try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                    if (rset.next()) {
                        length = rset.getDouble("length");
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
        
        return length;
    }
    
    public double getArea(SpatialEntity spatialEntity) {
        
        String sqlQuery = "";
        double area = 0.0;

        if (spatialEntity instanceof Entity) {
            String validFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Entity) spatialEntity).validFrom );
            String validToFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Entity) spatialEntity).validTo );
            
            sqlQuery += "SELECT SDO_GEOM.SDO_AREA(related_spatial_entities.geometry, user_sdo_geom_metadata.diminfo) as area" +
                        "  FROM related_spatial_entities, user_sdo_geom_metadata" +
                        "  WHERE user_sdo_geom_metadata.table_name = 'RELATED_SPATIAL_ENTITIES' " +
                        "    AND user_sdo_geom_metadata.column_name = 'GEOMETRY' " +
                        "    AND related_spatial_entities.id = "+ ((Entity) spatialEntity).id +"" +
                        "    AND related_spatial_entities.valid_from = TO_DATE('"+ validFromFormatted +"', 'dd-mm-yyyy') " +
                        "    AND related_spatial_entities.valid_to = TO_DATE('"+ validToFormatted +"', 'dd-mm-yyyy')";
        } 
        else if ( spatialEntity instanceof Estate) {
            String validFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Estate) spatialEntity).validFrom );
            String validToFormatted = new SimpleDateFormat("dd-MM-yyyy").format( ((Estate) spatialEntity).validTo );
            
            sqlQuery += "SELECT SDO_GEOM.SDO_AREA(estates.geometry, user_sdo_geom_metadata.diminfo) as area" +
                        "  FROM estates, user_sdo_geom_metadata" +
                        "  WHERE user_sdo_geom_metadata.table_name = 'ESTATES' " +
                        "    AND user_sdo_geom_metadata.column_name = 'GEOMETRY' " +
                        "    AND estates.id = "+ ((Estate) spatialEntity).id +"" +
                        "    AND estates.valid_from = TO_DATE('"+ validFromFormatted +"', 'dd-mm-yyyy') " +
                        "    AND estates.valid_to = TO_DATE('"+ validToFormatted +"', 'dd-mm-yyyy')";
        } 

        try {

            try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                
                try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                    if (rset.next()) {
                        area = rset.getDouble("area");
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
        
        return area;
    }

    public double getDistance(SpatialEntity previousSelectedSpatialEntity, SpatialEntity selectedSpatialEntity) {
        
        String sqlQuery = "";
        double distance = 0.0;

        String previousSelectedSpatialEntityValidFromFormatted;
        String previousSelectedSpatialEntityValidToFormatted;
        
        String selectedSpatialEntityValidFromFormatted;
        String selectedSpatialEntityValidToFormatted;
        
        int previousSelectedSpatialEntityId;
        int selectedSpatialEntityId;
        
        String firstTableName;
        String firstTableUpperCaseName;
        
        String secondTableName;
        String secondTableUpperCaseName;
        
        // previousSelectedSpatialEntity is instance of Entity
        if (previousSelectedSpatialEntity instanceof Entity) {
            previousSelectedSpatialEntityId = ((Entity) previousSelectedSpatialEntity).id;
            firstTableName = "related_spatial_entities";
            firstTableUpperCaseName = "RELATED_SPATIAL_ENTITIES";
            previousSelectedSpatialEntityValidFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Entity) previousSelectedSpatialEntity).validFrom);
            previousSelectedSpatialEntityValidToFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Entity) previousSelectedSpatialEntity).validTo);
      
        }
        // previousSelectedSpatialEntity is instance of Estate
        else {
            previousSelectedSpatialEntityId = ((Estate) previousSelectedSpatialEntity).id;
            firstTableName = "estates";
            firstTableUpperCaseName = "ESTATES";
            previousSelectedSpatialEntityValidFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Estate) previousSelectedSpatialEntity).validFrom);
            previousSelectedSpatialEntityValidToFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Estate) previousSelectedSpatialEntity).validTo);
        }
        
        // selectedSpatialEntity is instance of Entity
        if (selectedSpatialEntity instanceof Entity) {
            selectedSpatialEntityId = ((Entity) selectedSpatialEntity).id;
            secondTableName = "related_spatial_entities";
            secondTableUpperCaseName = "RELATED_SPATIAL_ENTITIES";   
            selectedSpatialEntityValidFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Entity) selectedSpatialEntity).validFrom);
            selectedSpatialEntityValidToFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Entity) selectedSpatialEntity).validTo);
        }
        // selectedSpatialEntity is instance of Estate
        else {
            selectedSpatialEntityId = ((Estate) selectedSpatialEntity).id;
            secondTableName = "estates";
            secondTableUpperCaseName = "ESTATES";
            selectedSpatialEntityValidFromFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Estate) selectedSpatialEntity).validFrom);
            selectedSpatialEntityValidToFormatted = new SimpleDateFormat("dd-MM-yyyy").format(((Estate) selectedSpatialEntity).validTo);
        }
            
            
            sqlQuery += "SELECT SDO_GEOM.SDO_DISTANCE(t1.geometry, m1.diminfo, t2.geometry, m2.diminfo) as distance" +
                        "  FROM "+ firstTableName +" t1, "+ secondTableName +" t2, user_sdo_geom_metadata m1, user_sdo_geom_metadata m2" +
                        "  WHERE m1.table_name = '"+ firstTableUpperCaseName +"' " +
                        "    AND m2.table_name = '"+ secondTableUpperCaseName +"' " +
                        "    AND m1.column_name = 'GEOMETRY' " +
                        "    AND m2.column_name = 'GEOMETRY' " +
                        "    AND t1.id = "+ previousSelectedSpatialEntityId +"" +
                        "    AND t2.id = "+ selectedSpatialEntityId +"" +
                        "    AND t1.valid_from = TO_DATE('"+ previousSelectedSpatialEntityValidFromFormatted +"', 'dd-mm-yyyy') " +
                        "    AND t1.valid_to = TO_DATE('"+ previousSelectedSpatialEntityValidToFormatted +"', 'dd-mm-yyyy')" +
                        "    AND t2.valid_from = TO_DATE('"+ selectedSpatialEntityValidFromFormatted +"', 'dd-mm-yyyy') " +
                        "    AND t2.valid_to = TO_DATE('"+ selectedSpatialEntityValidToFormatted +"', 'dd-mm-yyyy')";
            
        try {

            try (Statement stmt = DatabaseModel.getInstance().getConnection().createStatement()) {
                
                try (ResultSet rset = stmt.executeQuery(sqlQuery)) {
                    if (rset.next()) {
                        distance = rset.getDouble("distance");
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
        
        return distance;
    }
    
}
