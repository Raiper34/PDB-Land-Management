/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author archie
 */
public class Shapes {
    public List<ImprovedCircle> circles;
    public List<ImprovedPath> paths;
    public List<ImprovedPolygon> polygons;
    
    public Shapes() {
        this.circles = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.polygons = new ArrayList<>();
    }
    
}
