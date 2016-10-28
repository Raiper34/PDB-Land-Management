/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import javafx.scene.shape.Path;
/**
 *
 * @author jan
 */
public class ImprovedPath extends Path {
    public int id;
    public String name;
    public String description;
    public String layer;
    public String entityType;
    public String validFrom;
    public String validTo;
    public int freeholdersId;
    public int photosId;
    
    public ImprovedPath() {
        super();
    }
}
