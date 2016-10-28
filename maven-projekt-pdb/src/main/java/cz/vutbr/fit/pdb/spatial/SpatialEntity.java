/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import cz.vutbr.fit.pdb.projekt.Freeholder;
import cz.vutbr.fit.pdb.projekt.Photo;
import oracle.spatial.geometry.JGeometry;
import java.util.Date;
/**
 *
 * @author archie
 */
public class SpatialEntity {
    private int id;
    private String name;
    private String description; 
    private JGeometry geometry;
    private Date valid_from;
    private Date valid_to;
    
    public SpatialEntity(
            int id, 
            String name, 
            String description, 
            JGeometry geometry,
            Date valid_from,
            Date valid_to
            ) 
    {
        this.id = id;
        this.name = name;
        this.description = description; 
        this.geometry = geometry;
        this.valid_from = valid_from;
        this.valid_to = valid_to;
        
    }
    
    public Shapes toShapes(SpatialEntity spatialEntity, String entityOrEstate){
        Shapes shapes = new Shapes();
        return shapes;
    }
}
