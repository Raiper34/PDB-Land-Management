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
    private List<ImprovedCircle> circles;
    private List<ImprovedPath> paths;
    private List<ImprovedPolygon> polygons;
    
    public Shapes() {
        this.circles = new ArrayList<ImprovedCircle>();
        this.paths = new ArrayList<ImprovedPath>();
        this.polygons = new ArrayList<ImprovedPolygon>();
    }
    
}
