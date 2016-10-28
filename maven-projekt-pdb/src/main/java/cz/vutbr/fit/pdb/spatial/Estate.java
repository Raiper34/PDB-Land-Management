/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import cz.vutbr.fit.pdb.projekt.Photo;
import cz.vutbr.fit.pdb.projekt.Freeholder;
import java.util.Date;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author archie
 */
public class Estate extends SpatialEntity{
    private Photo photo;
    private Freeholder freeholder;
    
        
        public Estate(
            int id, 
            String name, 
            String description, 
            JGeometry geometry,
            Date valid_from,
            Date valid_to,
            Photo photo,
            Freeholder freeholder
            ) 
    {
        super(id, name, description, geometry, valid_from, valid_to);
        this.photo = photo;
        this.freeholder = freeholder; 
    }
        
    public Shapes toShapes(){
        SpatialEntity spatialEntity = (SpatialEntity) this;
        return super.toShapes(spatialEntity, "Estate");
    }
}
