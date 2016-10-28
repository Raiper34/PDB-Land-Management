/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import javafx.scene.shape.Circle;

/**
 *
 * @author jan
 */
public class ImprovedCircle extends Circle {
    private Entity entityReference;
    
    public ImprovedCircle(Entity entityReference) {
        super();
        this.entityReference = entityReference;
    }
}
