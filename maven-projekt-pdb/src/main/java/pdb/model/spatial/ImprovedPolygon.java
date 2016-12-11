/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import javafx.scene.shape.Polygon;

/** 
 * Class ImprovedPolygon represents polygon with extra reference to Entity by which it was created
 * @author jan
 */
public class ImprovedPolygon extends Polygon {
    private Entity entityReference;
    private Estate estateReference;
    private boolean isEstate;

    /**
     * id which has entity represented by polygon in db
     */
    public int dbId;
    
    /**
     * Constructor initialize internal attributes
     * @param isEstate was polygon created from estate
     * @param entityReference entity from which origin polygon
     * @param estateReference estate from which origin polygon
     * @param dbId id which has entity represented by polygon in db
     */
    public ImprovedPolygon(boolean isEstate, Entity entityReference, Estate estateReference, int dbId) {
        super();
        this.entityReference = entityReference;
        this.estateReference = estateReference;
        this.isEstate = isEstate;
        this.dbId = dbId;
    }
    
    /**
     * Return entity from which origin polygon
     * @return entity from which origin polygon
     */
    public Entity getEntityReference() {
        return this.entityReference;
    }
    
    /**
     * Return estate from which origin polygon
     * @return estate from which origin polygon
     */
    public Estate getEstateReference() {
        return this.estateReference;
    }
    
    /**
     * Return true if was polygon created from estate, otherwise return false
     * @return true if was polygon created from estate, otherwise return false
     */
    public boolean isEstate() {
        return isEstate;
    }
}
