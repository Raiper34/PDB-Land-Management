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
    
    public int getFreeholderId() { 
        if ( this.freeholder != null)
            return this.freeholder.id;
        else
            return 1;
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
    
    public void setInfo(String fName) 
    {
        //Todo
    }
}
