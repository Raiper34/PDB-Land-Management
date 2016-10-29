/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import pdb.model.Photo;
import pdb.model.Freeholder;
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
            Date validFrom,
            Date validTo,
            Photo photo,
            Freeholder freeholder
            ) 
    {
        super(id, name, description, geometry, validFrom, validTo);
        this.photo = photo;
        this.freeholder = freeholder; 
    }
        
    public Shapes toShapes(){
        SpatialEntity spatialEntity = (SpatialEntity) this;
        return super.toShapes(spatialEntity, "Estate");
    }
}
