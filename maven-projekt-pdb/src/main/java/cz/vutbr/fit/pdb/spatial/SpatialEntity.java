/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

import cz.vutbr.fit.pdb.projekt.Freeholder;
import cz.vutbr.fit.pdb.projekt.Photo;
import oracle.spatial.geometry.JGeometry;
import java.util.Date;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import java.util.Arrays;
/**
 *
 * @author archie
 */
public class SpatialEntity {
    private int id;
    private String name;
    private String description; 
    private JGeometry geometry;
    private Date validFrom;
    private Date validTo;
    
    public SpatialEntity(
            int id, 
            String name, 
            String description, 
            JGeometry geometry,
            Date validFrom,
            Date validTo
            ) 
    {
        this.id = id;
        this.name = name;
        this.description = description; 
        this.geometry = geometry;
        this.validFrom = validFrom;
        this.validTo = validTo;
        
    }
    
    public Shapes toShapes(SpatialEntity spatialEntity, String entityOrEstate){
        Shapes shapes = new Shapes();
        
        // general polygon and is not circle
        if (this.geometry.getType() == JGeometry.GTYPE_POLYGON && !this.geometry.isCircle()) {
            ImprovedPolygon improvedPolygon;
            // entity is estate
            if (entityOrEstate.equals("Estate")) {
                improvedPolygon = new ImprovedPolygon(true, null, (Estate) spatialEntity);
            }
            // entity is entity
            else {
                improvedPolygon = new ImprovedPolygon(true, (Entity) spatialEntity, null);
            }
            
            // slice original polygon points (remove 2 last points which close polygon)
            double[] polygonPoints = Arrays.copyOfRange(this.geometry.getOrdinatesArray(), 0, this.geometry.getOrdinatesArray().length - 3);
            for (double point: polygonPoints) {
                improvedPolygon.getPoints().add(point);
            }            
           
            
            shapes.polygons.add(improvedPolygon);
        }
        
        // entity is circle
        else if (this.geometry.isCircle()) {
            ImprovedCircle improvedCircle = new ImprovedCircle((Entity) spatialEntity);

            double centerX = this.geometry.getOrdinatesArray()[0];
            double centerY = this.geometry.getOrdinatesArray()[3] - this.geometry.getOrdinatesArray()[1];
            improvedCircle.setCenterX(centerX);
            improvedCircle.setCenterY(centerY);
            improvedCircle.setRadius(centerY - this.geometry.getOrdinatesArray()[1]);
            shapes.circles.add(improvedCircle);
        }
        
        // entity is point
        else if (this.geometry.getType() == JGeometry.GTYPE_POINT) {
            ImprovedCircle improvedCircle = new ImprovedCircle((Entity) spatialEntity);
  
            improvedCircle.setCenterX(this.geometry.getPoint()[0]);
            improvedCircle.setCenterY(this.geometry.getPoint()[1]);
            improvedCircle.setRadius(5.0f);
            shapes.circles.add(improvedCircle);
        }
        
        // entity is multipoint
        else if (this.geometry.getType() == JGeometry.GTYPE_MULTIPOINT) {
            ImprovedCircle improvedCircle = new ImprovedCircle((Entity) spatialEntity);
            
            JGeometry[] elements = this.geometry.getElements();
            for (JGeometry element : elements) {
  
                improvedCircle.setCenterX(element.getPoint()[0]);
                improvedCircle.setCenterY(element.getPoint()[1]);
                improvedCircle.setRadius(5.0f);
                shapes.circles.add(improvedCircle);
            }
            
        }
        
        // entity is curve/line
        else if (this.geometry.getType() == JGeometry.GTYPE_CURVE) {
            ImprovedPath improvedPath = new ImprovedPath((Entity) spatialEntity);

            for (int i = 0; i < this.geometry.getNumPoints();i = i+2) {
               
                // in first iteration we iniliaize path start
                if (i == 0) {
                    MoveTo moveTo = new MoveTo();
                    moveTo.setX(this.geometry.getOrdinatesArray()[i]);
                    moveTo.setY(this.geometry.getOrdinatesArray()[i+1]);
                    improvedPath.getElements().add(moveTo);
                }
                // in others iteration we set next points
                else {
                    LineTo lineTo = new LineTo();
                    lineTo.setX(this.geometry.getOrdinatesArray()[i]);
                    lineTo.setY(this.geometry.getOrdinatesArray()[i+1]);
                    improvedPath.getElements().add(lineTo);
                }
            }
            
            shapes.paths.add(improvedPath);
        }
        
        // entity is multi curve/line
        else if (this.geometry.getType() == JGeometry.GTYPE_MULTICURVE) {
            ImprovedPath improvedPath = new ImprovedPath((Entity) spatialEntity);
            
            JGeometry[] elements = this.geometry.getElements();
            for (JGeometry element : elements) {
  
                for (int i = 0; i < element.getNumPoints();i = i+2) {
               
                    // in first iteration we iniliaize path start
                    if (i == 0) {
                        MoveTo moveTo = new MoveTo();
                        moveTo.setX(element.getOrdinatesArray()[i]);
                        moveTo.setY(element.getOrdinatesArray()[i+1]);
                        improvedPath.getElements().add(moveTo);
                    }
                    // in others iteration we set next points
                    else {
                        LineTo lineTo = new LineTo();
                        lineTo.setX(element.getOrdinatesArray()[i]);
                        lineTo.setY(element.getOrdinatesArray()[i+1]);
                        improvedPath.getElements().add(lineTo);
                    }
                }
                
                shapes.paths.add(improvedPath);
            }
            
        }
        
        return shapes;
    }
}
