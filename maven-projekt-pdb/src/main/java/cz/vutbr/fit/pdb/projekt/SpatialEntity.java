/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.projekt;

import oracle.spatial.geometry.JGeometry;
import java.util.Date;
/**
 *
 * @author archie
 */
public class SpatialEntity {
    private int id;
    private String name;
    private String description; 
    private String type; //Mozeme tu zadat akeho typu je to napr 1 = Pozemok, 2 = Dom, 3 = Strom atd...
    private JGeometry geometry;
    private Date valid_from;
    private Date valid_to;
    private Photo photo;
    
    public Shapes toShapes(){
        Shapes shapes = new Shapes();
        return shapes;
    }
    
}
