/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import javafx.scene.shape.Polygon;

/**
 *
 * @author jan
 */
public class ImprovedPolygon extends Polygon {
    private Entity entityReference;
    private Estate estateReference;
    private boolean isEstate;
    
    public ImprovedPolygon(boolean isEstate, Entity entityReference, Estate estateReference) {
        super();
        this.entityReference = entityReference;
        this.estateReference = estateReference;
        this.isEstate = isEstate;
    }
    
    public Entity getEntityReference() {
        return this.entityReference;
    }
    
    public boolean isEstate() {
        return isEstate;
    }
}
