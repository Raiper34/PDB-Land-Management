/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import java.util.Date;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author archie
 */
public class Entity extends SpatialEntity{
    private String entityType; 
    private String layer;
    
        public Entity(
            int id, 
            String name, 
            String description, 
            JGeometry geometry,
            Date valid_from,
            Date valid_to,
            String entityType,
            String layer
            ) 
    {
        super(id, name, description, geometry, valid_from, valid_to);
        this.entityType = entityType;
        this.layer = layer; 
    }
    
    
    public Shapes toShapes(){
        SpatialEntity spatialEntity = (SpatialEntity) this;
        return super.toShapes(spatialEntity, "Entity");
    }
}
