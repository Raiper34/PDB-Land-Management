/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import java.util.Date;
import oracle.spatial.geometry.JGeometry;
import pdb.model.freeholder.Freeholder;

/** Class Estate represents estates and its values
 * Can generate shapes from its geometry
 * @author mmarus
 */
public class Estate extends SpatialEntity{

    /**
     *
     */
    public Freeholder freeholder;
    
    /**
     *
     * @param id
     * @param name
     * @param description
     * @param geometry
     * @param validFrom
     * @param validTo
     * @param freeholder
     */
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
        
    /**
     *
     * @return
     */
    public Shapes toShapes(){
        SpatialEntity spatialEntity = (SpatialEntity) this;
        return super.toShapes(spatialEntity, "Estate");
    }
    
    /**
     *
     * @return
     */
    public String getInfo() 
    {
        String name = (this.name == null)? "" : this.name;
        return this.id + " " + name  + " from:" + this.validFrom.toString() + " to:" + this.validTo.toString();
    }
    
    /**
     *
     * @return
     */
    public String getId()
    {
        return this.id + "";
    }
    
    /**
     *
     * @return
     */
    public String getFrom()
    {
        return this.validFrom.toString();
    }
    
    /**
     *
     * @return
     */
    public String getTo()
    {
        return this.validTo.toString();
    }
    
    /**
     *
     * @return
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     *
     * @param holder
     */
    public void setFreeholder(Freeholder holder){
        freeholder = holder;
    }
}
