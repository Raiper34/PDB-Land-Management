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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.spatial.geometry.JGeometry;
import pdb.model.spatial.Estate;

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
            try (Statement stmt = conn.createStatement()) {
                
                String query = "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy");
                
                if (considerGroundLayer) {
                    query += "Select valid_from from estates  Where ID in(Select Distinct Min(ID) from estates Group By valid_from)" +
                               " union " +
                              "Select valid_from from related_spatial_entities Where ID in(Select Distinct Min(ID) from related_spatial_entities WHERE layer in ("+ layers +") Group By valid_from)";
                    //System.out.println(query);
                }
                else {
                    query += "Select valid_from from related_spatial_entities Where ID in(Select Distinct Min(ID) from related_spatial_entities WHERE layer in ("+ layers +") Group By valid_from)";
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
}
