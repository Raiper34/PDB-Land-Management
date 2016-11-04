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
}
