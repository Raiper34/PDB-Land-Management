/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import java.util.Date;
import oracle.spatial.geometry.JGeometry;
import pdb.model.freeholder.Freeholder;

/**
 *
 * @author archie
 */
public class Estate extends SpatialEntity{
    public Freeholder freeholder;
    
        
        public Estate(
            int id, 
            String name, 
            String description, 
            JGeometry geometry,
            Date validFrom,
            Date validTo,
            Freeholder freeholder
            ) 
    {
        super(id, name, description, geometry, validFrom, validTo);
        this.freeholder = freeholder; 
    }
        
    public Shapes toShapes(){
        SpatialEntity spatialEntity = (SpatialEntity) this;
        return super.toShapes(spatialEntity, "Estate");
    }
    
    public String getInfo() 
    {
        String name = (this.name == null)? "" : this.name;
        return this.id + " " + name  + " from:" + this.validFrom.toString() + " to:" + this.validTo.toString();
    }
    
    public String getId()
    {
        return this.id + "";
    }
    
    public String getFrom()
    {
        return this.validFrom.toString();
    }
    
    public String getTo()
    {
        return this.validTo.toString();
    }
    
    public String getName()
    {
        return this.name;
    }
}
