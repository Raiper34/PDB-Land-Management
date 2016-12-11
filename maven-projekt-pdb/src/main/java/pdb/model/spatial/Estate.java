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
     * Freeholder reference
     */
    public Freeholder freeholder;
    
    /**
     * Constructor for the estate
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
     * Create shapes for javafx anchor pane (to draw on map pane)
     * @return shapes
     */
    public Shapes toShapes(){
        SpatialEntity spatialEntity = (SpatialEntity) this;
        return super.toShapes(spatialEntity, "Estate");
    }
    
    /**
     * Return informations about the estate
     * @return string info
     */
    public String getInfo() 
    {
        String name = (this.name == null)? "" : this.name;
        return this.id + " " + name  + " from:" + this.validFrom.toString() + " to:" + this.validTo.toString();
    }
    
    /**
     * Get id of the estate
     * @return id
     */
    public String getId()
    {
        return this.id + "";
    }
    
    /**
     * Get time valid from
     * @return validFrom
     */
    public String getFrom()
    {
        return this.validFrom.toString();
    }
    
    /**
     * * Get time valid to
     * @return validTo
     */
    public String getTo()
    {
        return this.validTo.toString();
    }
    
    /**
     * Get name of the estate
     * @return name
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Set freeholder of the estate
     * @param holder
     */
    public void setFreeholder(Freeholder holder){
        freeholder = holder;
    }
}
