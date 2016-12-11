/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import java.util.ArrayList;
import oracle.spatial.geometry.JGeometry;
import java.util.Date;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import java.util.Arrays;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
/**
 * Class which is inherited in other spatial entities
 * @author mmarus and others
 */
public class SpatialEntity {
    private static final int SRID = 0;
    private static final int DIMENSION = 2;

    /**
     * id of Spatial entity in db
     */
    public int id;

    /**
     * name of Spatial entity in db
     */
    public String name;

    /**
     * description of Spatial entity in db
     */
    public String description; 

    /**
     * geometry of Spatial entity in db
     */
    public JGeometry geometry;

    /**
     * valid_from of Spatial entity in db
     */
    public Date validFrom;

    /**
     * valid_to of Spatial entity in db
     */
    public Date validTo;
    
    /**
     * Constructor initialize internal attributes
     * @param id id of Spatial entity in db
     * @param name name of Spatial entity in db
     * @param description description of Spatial entity in db
     * @param geometry geometry of Spatial entity in db
     * @param validFrom valid_from of Spatial entity in db
     * @param validTo valid_to of Spatial entity in db
     */
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
    
    /** create JGeometry From Shapes
     * @param newShapes Shapes instance which contain Shapes from which we create new JGeometry
     * @return created jGeometry
    */
    public static JGeometry createJGeometryFromShapes(Shapes newShapes) {
        if( newShapes == null)
            return null;
        
        int startingOffset = 1;
        
        List<Double> points = new ArrayList<>();
        List<Integer> sdoElemInfo = new ArrayList<>();
        
        if (!newShapes.paths.isEmpty()) {
            for (ImprovedPath newShape : newShapes.paths) {
                if( newShape.getElements().size() > 1) {
                    sdoElemInfo.add(startingOffset);
                    sdoElemInfo.add(2);
                    sdoElemInfo.add(1);

                    for (PathElement element : newShape.getElements()) {
                        if (element instanceof MoveTo) {
                            points.add(((MoveTo) element).getX());
                            points.add(((MoveTo) element).getY());
                        } else if (element instanceof LineTo) {
                            points.add(((LineTo) element).getX());
                            points.add(((LineTo) element).getY());
                        }   
                        startingOffset++;
                    }
                }
            }
            if(points.isEmpty() || sdoElemInfo.isEmpty())
                return null;

            int valuesOfSdoElemInfo[] = sdoElemInfo.stream().mapToInt(d -> d).toArray();
            double valuesOfPoints[] = points.stream().mapToDouble(d -> d).toArray();

            JGeometry newJgeometry = new JGeometry(
                2, SRID, valuesOfSdoElemInfo, valuesOfPoints
            );
            
            System.out.println(Arrays.toString(valuesOfSdoElemInfo));
            System.out.println(sdoElemInfo.size());
            System.out.println(Arrays.toString(valuesOfPoints));
            System.out.println(points.size());
            return newJgeometry;
        }
        return null;
    }

    /** create JGeometry From Shapes which are points and lines
     * @param newPoints points from which we create JGeometry
     * @param linesOrPoints lines or points from which we create JGeometry
     * @return created JGeometry
    */
    public static JGeometry createJGeometryFromShapes(List<Circle> newPoints, String linesOrPoints) {
        if( newPoints.size() < 1)
            return null;
        double valuesOfPoints[] = new double[newPoints.size()*DIMENSION];
        JGeometry newJgeometry;
        
        if ( linesOrPoints.equals("points") ) {
            int index = 1;
            int sdoElemInfo[] = new int[newPoints.size()*3];                
            for ( int i = 0; i < newPoints.size(); i++) {
                sdoElemInfo[i*3] = index;
                sdoElemInfo[i*3+1] = 1;
                sdoElemInfo[i*3+2] = 1;
                index += 2;
                valuesOfPoints[i*DIMENSION] = newPoints.get(i).getCenterX();
                valuesOfPoints[i*DIMENSION+1] = newPoints.get(i).getCenterY();
            }
            newJgeometry = new JGeometry(
                5, SRID, 
                sdoElemInfo, 
                valuesOfPoints
            );
        }
        else {
            for ( int i = 0; i < newPoints.size(); i++) {
                valuesOfPoints[i*DIMENSION] = newPoints.get(i).getCenterX();
                valuesOfPoints[i*DIMENSION+1] = newPoints.get(i).getCenterY();
            }
            newJgeometry = new JGeometry(
                    2, SRID, 
                    new int[]{1, 2, 1}, // exterior polygon
                    valuesOfPoints
            );
        }
        
        return newJgeometry;
    }
    
    /**
     * create JGeometry From Rectangle
     * @param newRectangle REctangle from which we create JGeometry
     * @return created JGeometry
     */
    public static JGeometry createJGeometryFromShapes(Rectangle newRectangle) {
        if( newRectangle == null)
            return null;
        JGeometry newJgeometry = new JGeometry(
            3, SRID, 
            new int[]{1, 1003, 1},
            new double[]{
                newRectangle.getX(), newRectangle.getY(), //Start point
                newRectangle.getX()+newRectangle.getWidth(),newRectangle.getY(), 
                newRectangle.getX()+newRectangle.getWidth(),newRectangle.getY()+newRectangle.getHeight(), 
                newRectangle.getX(), newRectangle.getY()+newRectangle.getHeight(),                            
                newRectangle.getX(), newRectangle.getY() //Start point again
            }
        );
        return newJgeometry;
    }
    
    /** 
     * create JGeometry From Polygon
     * @param newPolygon Polygon from which we create JGeometry
     * @return created JGeometry
     */
    public static JGeometry createJGeometryFromShapes(Polygon newPolygon) {
        if( newPolygon == null)
            return null;
        double valuesOfPoints[] = newPolygon.getPoints().stream().mapToDouble(d -> d).toArray();
        JGeometry newJgeometry = new JGeometry(
            3, SRID, 
            new int[]{1, 1003, 1}, // exterior polygon
            valuesOfPoints
        );
        return newJgeometry;
    }
    
    /** 
     * create JGeometry From Circle
     * @param newCircle Circle from which we create JGeometry
     * @return created JGeometry
     */
    public static JGeometry createJGeometryFromShapes(Circle newCircle) {
        if( newCircle == null)
            return null;
        JGeometry newJgeometry = new JGeometry(
            3, SRID, 
            new int[]{1, 1003, 4}, // exterior polygon
            new double[]{
                newCircle.getCenterX(), newCircle.getCenterY()-newCircle.getRadius(),
                newCircle.getCenterX(), newCircle.getCenterY()+newCircle.getRadius(),
                newCircle.getCenterX()+newCircle.getRadius(), newCircle.getCenterY()
            }
        );
        return newJgeometry;
    }
            
    /** 
     * Create ImprovedShapes from the geometry, which will be used for drawing the entities on map
     * @param spatialEntity SpatialEntity instance (can be boxed Entity or Estate)
     * @param entityOrEstate specified what is boxed in spatialEntity
     * @return created Shapes
     */
    public Shapes toShapes(SpatialEntity spatialEntity, String entityOrEstate){
        Shapes shapes = new Shapes();
        
        // general polygon and is not circle
        if (this.geometry.getType() == JGeometry.GTYPE_POLYGON && !this.geometry.isCircle()) {
            ImprovedPolygon improvedPolygon;
            // entity is estate
            if (entityOrEstate.equals("Estate")) {
                improvedPolygon = new ImprovedPolygon(true, null, (Estate) spatialEntity, ((Estate) spatialEntity).id);
            }
            // entity is entity
            else {
                improvedPolygon = new ImprovedPolygon(false, (Entity) spatialEntity, null, ((Entity) spatialEntity).id);
            }
            
            // slice original polygon points (remove 2 last points which close polygon)
            //double[] polygonPoints = Arrays.copyOfRange(this.geometry.getOrdinatesArray(), 0, this.geometry.getOrdinatesArray().length - 1);
            for (double point: this.geometry.getOrdinatesArray()) {
                improvedPolygon.getPoints().add(point);
            }            
           
            shapes.polygons.add(improvedPolygon);
        }
        
        // entity is circle
        else if (this.geometry.isCircle()) {
            ImprovedCircle improvedCircle = new ImprovedCircle((Entity) spatialEntity, ((Entity) spatialEntity).id);

            double centerX = this.geometry.getOrdinatesArray()[0];
            double centerY = this.geometry.getOrdinatesArray()[1] + (this.geometry.getOrdinatesArray()[3] - this.geometry.getOrdinatesArray()[1]) / 2.0;
            improvedCircle.setCenterX(centerX);
            improvedCircle.setCenterY(centerY);
            improvedCircle.setRadius(centerY - this.geometry.getOrdinatesArray()[1]);
            improvedCircle.setFill(Color.BLUE);
            shapes.circles.add(improvedCircle);
        }
        
        // entity is point
        else if (this.geometry.getType() == JGeometry.GTYPE_POINT) {
            ImprovedCircle improvedCircle = new ImprovedCircle((Entity) spatialEntity, ((Entity) spatialEntity).id);
  
            improvedCircle.setCenterX(this.geometry.getPoint()[0]);
            improvedCircle.setCenterY(this.geometry.getPoint()[1]);
            improvedCircle.setRadius(5.0f);
            shapes.circles.add(improvedCircle);
        }
        
        // entity is multipoint
        else if (this.geometry.getType() == JGeometry.GTYPE_MULTIPOINT) {
            
            JGeometry[] elements = this.geometry.getElements();
            for (JGeometry element : elements) {
                ImprovedCircle improvedCircle = new ImprovedCircle((Entity) spatialEntity, ((Entity) spatialEntity).id);
                
                improvedCircle.setCenterX(element.getPoint()[0]);
                improvedCircle.setCenterY(element.getPoint()[1]);
                improvedCircle.setRadius(5.0f);
                shapes.circles.add(improvedCircle);
            }
            
        }
        
        // entity is curve/line
        else if (this.geometry.getType() == JGeometry.GTYPE_CURVE) {
            ImprovedPath improvedPath = new ImprovedPath((Entity) spatialEntity, ((Entity) spatialEntity).id);

            for (int i = 0; i < this.geometry.getNumPoints() * 2;) {
               
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
                
                i += 2;
            }
            
            shapes.paths.add(improvedPath);
        }
        
        // entity is multi curve/line
        else if (this.geometry.getType() == JGeometry.GTYPE_MULTICURVE) {
            
            JGeometry[] elements = this.geometry.getElements();
            for (JGeometry element : elements) {
                
                ImprovedPath improvedPath = new ImprovedPath((Entity) spatialEntity, ((Entity) spatialEntity).id);
                
                for (int i = 0; i < element.getNumPoints() * 2;i = i+2) {
               
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
