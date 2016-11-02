/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import oracle.spatial.geometry.JGeometry;
import static oracle.spatial.geometry.JGeometry.GTYPE_POINT;
import pdb.model.SpatialEntitiesModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;

/**
 *
 * @author archie
 */
public class AddEntityPaneController implements Initializable {
    
    @FXML
    public AnchorPane addEntityAnchorPane;
    
    @FXML 
    public ToggleGroup toggleNewObject;
       
    public MainController mainController;
    
    @FXML
    private MainController fXMLController;
    
    private Entity newEntity;
    
    private Estate newEstate;
    
    private String typeOfNewSpatialEntity;
    private String shapeOfNewSpatialEntity;
    private String layerOfNewSpatialEntity;
    private final int SRID = 0;
    
    //private SpatialEntitiesModel spatialEntitiesModel; 
    private List<Line> newLines;
    private List<Circle> newPoints;
    private Rectangle newRectangle;
    private Circle newCircle;
    private Polygon newPolygon;
    
    public void addPointOnClick(InputEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            //Get the x and y of the click and create there a new circle
            MouseEvent mouseEvent = (MouseEvent) event; 
            if(mouseEvent.getButton() == MouseButton.SECONDARY)
                return;
            addPoint(mouseEvent);
        }
    }
    
    public void addPoint(MouseEvent event) {
        Circle point = new Circle(event.getX(), event.getY(), 2.0f, Paint.valueOf("Black") );
        newPoints.add(point);
        mainController.mapPaneController.mapa.getChildren().add(point);
    }
    
    public void removePoint(int index) {
        mainController.mapPaneController.mapa.getChildren().remove(newPoints.get(index));
        newPoints.remove(index);
    }
    
    public void addLine() {
        if (newPoints != null && newPoints.size() > 1 ) {    
            Line newLine = new Line(
                    newPoints.get(newPoints.size()-1).getCenterX(), 
                    newPoints.get(newPoints.size()-1).getCenterY(), 
                    newPoints.get(newPoints.size()-2).getCenterX(), 
                    newPoints.get(newPoints.size()-2).getCenterY() 
            );
            newLines.add(newLine);
            mainController.mapPaneController.mapa.getChildren().add(newLine);
        }
    }
    
    public void addRectangle() {
        double width = Math.abs(newPoints.get(0).getCenterX()-newPoints.get(1).getCenterX());
        double height = Math.abs(newPoints.get(0).getCenterY()-newPoints.get(1).getCenterY());
        double x = Math.min(newPoints.get(0).getCenterX(), newPoints.get(1).getCenterX());
        double y = Math.min(newPoints.get(0).getCenterY(), newPoints.get(1).getCenterY());

        newRectangle = new Rectangle(x, y, width, height);
        mainController.mapPaneController.mapa.getChildren().add(newRectangle);
    }
    
    public void addRectangleEventHandler(InputEvent event) {        
        MouseEvent mouseEvent = (MouseEvent) event;
        
        if(event.getEventType() == MouseEvent.MOUSE_PRESSED){
            if( newRectangle != null) {
                mainController.mapPaneController.mapa.getChildren().remove(newRectangle);
                newRectangle = null;
            }
            //Create starting point
            addPoint(mouseEvent);
            //Create ending point
            addPoint(mouseEvent);
            addRectangle();
        }
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
            //remove old point from map and list
            removePoint(1);
            //remove newRectangle from map and clear it
            mainController.mapPaneController.mapa.getChildren().remove(newRectangle);
            //create new point
            addPoint(mouseEvent);
            addRectangle();
        }
        else if(event.getEventType() == MouseEvent.MOUSE_RELEASED){
            //Remove the points
            removePoint(1);
            removePoint(0);
            newPoints.clear();
        } 
    }
    
    public void addCircle() {
        Point2D start = new Point2D(newPoints.get(0).getCenterX(), newPoints.get(0).getCenterY());
        Point2D end = new Point2D(newPoints.get(1).getCenterX(), newPoints.get(1).getCenterY());
        double radius = start.distance(end);
        newCircle = new Circle(newPoints.get(0).getCenterX(), newPoints.get(0).getCenterY(), radius);
        mainController.mapPaneController.mapa.getChildren().add(newCircle);
    }
    
    public void addCircleEventHandler(InputEvent event) {        
        MouseEvent mouseEvent = (MouseEvent) event;
        
        if(event.getEventType() == MouseEvent.MOUSE_PRESSED){
            if( newCircle != null) {
                mainController.mapPaneController.mapa.getChildren().remove(newCircle);
                newCircle = null;
            }
            //Create starting point
            addPoint(mouseEvent);
            //Create ending point
            addPoint(mouseEvent);
            addCircle();
        }
        else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
            //remove old point from map and list
            removePoint(1);
            //remove newRectangle from map and clear it
            mainController.mapPaneController.mapa.getChildren().remove(newCircle);
            //create new point
            addPoint(mouseEvent);
            addCircle();
        }
        else if(event.getEventType() == MouseEvent.MOUSE_RELEASED){
            //Remove the points
            removePoint(1);
            removePoint(0);
            newPoints.clear();
        } 
    }
    
    private void addPolygonEventHandler(InputEvent event) {
        MouseEvent mouseEvent = (MouseEvent) event;
        if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
            if ( newPoints.isEmpty()) {
                addPointOnClick(event);
                newPolygon = new Polygon(mouseEvent.getX(), mouseEvent.getY());
            }
            else {
                addPointOnClick(event);
                mainController.mapPaneController.mapa.getChildren().remove(newPolygon);
                newPolygon.getPoints().add(mouseEvent.getX());
                newPolygon.getPoints().add(mouseEvent.getY());
                System.out.println(newPolygon.getPoints().toString());
                mainController.mapPaneController.mapa.getChildren().add(newPolygon);
            }
        }
    }
    
    public void addNewSpatialEntity(InputEvent event) {
        //"point", "multipoint", "line", "multiline",  "rectangle", "polygon", "circle" 
        switch (shapeOfNewSpatialEntity) {
            case "point":
                if ( newPoints.isEmpty() ){
                    addPointOnClick(event);   
                }
                return;
            case "multipoint":
                addPointOnClick(event);
                break;
            case "line":
                if ( newPoints.size() < 2 ){
                    addPointOnClick(event);
                    addLine();
                }
                break;
            case "multiline":
                addPointOnClick(event);
                addLine();
                break;
            case "rectangle":
                addRectangleEventHandler(event);
                break;
            case "polygon":
                addPolygonEventHandler(event);
                break;
            case "circle":
                addCircleEventHandler(event);
                break;
        }
    }
    
    public JGeometry createJGeometry(){
        JGeometry geometry = null;
        double valuesOfPoints[];
        switch (shapeOfNewSpatialEntity) {
            case "point":
                geometry = new JGeometry(
                    newPoints.get(0).getCenterX(),
                    newPoints.get(0).getCenterY(),
                    SRID
                );
                break;
            case "multipoint":
                int index = 1;
                int sdoElemInfo[] = new int[newPoints.size()*3];                
                valuesOfPoints = new double[newPoints.size()*2];
                for ( int i = 0; i < newPoints.size(); i++) {
                    sdoElemInfo[i*3] = index;
                    sdoElemInfo[i*3+1] = 1;
                    sdoElemInfo[i*3+2] = 1;
                    index += 2;
                    valuesOfPoints[i*2] = newPoints.get(i).getCenterX();
                    valuesOfPoints[i*2+1] = newPoints.get(i).getCenterY();
                }
                geometry = new JGeometry(
                        5, SRID, 
                        sdoElemInfo, // exterior polygon
                        valuesOfPoints
                );
                break;
            case "line":
            case "multiline":
                valuesOfPoints = new double[newPoints.size()*2];
                for ( int i = 0; i < newPoints.size(); i++) {
                    valuesOfPoints[i*2] = newPoints.get(i).getCenterX();
                    valuesOfPoints[i*2+1] = newPoints.get(i).getCenterY();
                }
                geometry = new JGeometry(
                        2, SRID, 
                        new int[]{1, 2, 1}, // exterior polygon
                        valuesOfPoints
                );
                break;
            case "rectangle":
                geometry = new JGeometry(
                    3, SRID, 
                    new int[]{1, 1003, 1}, // exterior polygon
                    new double[]{
                        newRectangle.getX(), newRectangle.getY(), //Start point
                        newRectangle.getX()+newRectangle.getWidth(),newRectangle.getY(), 
                        newRectangle.getX()+newRectangle.getWidth(),newRectangle.getY()+newRectangle.getHeight(), 
                        newRectangle.getX(), newRectangle.getY()+newRectangle.getHeight(),                            
                        newRectangle.getX(), newRectangle.getY() //Start point
                    }
                );
                break;
            case "polygon":
                valuesOfPoints = newPolygon.getPoints().stream().mapToDouble(d -> d).toArray();
                geometry = new JGeometry(
                        3, SRID, 
                        new int[]{1, 1003, 1}, // exterior polygon
                        valuesOfPoints
                );
                break;
            case "circle":
                geometry = new JGeometry(
                        3, SRID, 
                        new int[]{1, 1003, 4}, // exterior polygon
                        new double[]{
                            newCircle.getCenterX(), newCircle.getCenterY()-newCircle.getRadius(),
                            newCircle.getCenterX(), newCircle.getCenterY()+newCircle.getRadius(),
                            newCircle.getCenterX()+newCircle.getRadius(), newCircle.getCenterY()
                        }
                );
                break;
        }
        
        return geometry;
    }
    
    public void saveNewSpatialEntity() {
        //Create the new entity and jgeometry from the points I have got
        JGeometry geometry = createJGeometry();
        
        if ( typeOfNewSpatialEntity.equals("estate")){
            //getLastID of estate
            int id = mainController.mapPaneController.spatialEntitiesModel.getMaxId("estates") + 1;  
            newEstate = new Estate(
                    id,
                    typeOfNewSpatialEntity + id,
                    typeOfNewSpatialEntity + id,
                    geometry,
                    new Date(),
                    new Date(),
                    null
            );
            
            deleteNewEntity();        
            mainController.mapPaneController.estates.add(newEstate);
            mainController.mapPaneController.drawSpatialEntities();
        } else {
            int id = mainController.mapPaneController.spatialEntitiesModel.getMaxId("related_spatial_entities") + 1;  
            newEntity = new Entity(
                    id,
                    typeOfNewSpatialEntity + id,
                    typeOfNewSpatialEntity + id,
                    geometry,
                    new Date(),
                    new Date(),
                    typeOfNewSpatialEntity,
                    layerOfNewSpatialEntity
            );
            
            deleteNewEntity();        
            mainController.mapPaneController.entities.add(newEntity);
            mainController.mapPaneController.drawSpatialEntities();
        }
    }
    
    public void deleteNewEntity() {
        //Remove all the new shapes from map
        for (Circle shape : newPoints){
            mainController.mapPaneController.mapa.getChildren().remove(shape);
        }
        for (Line shape : newLines){
            mainController.mapPaneController.mapa.getChildren().remove(shape);
        }
        mainController.mapPaneController.mapa.getChildren().remove(newRectangle);
        newRectangle = null;
        mainController.mapPaneController.mapa.getChildren().remove(newCircle);
        newCircle = null;
        mainController.mapPaneController.mapa.getChildren().remove(newPolygon);
        newPolygon = null;
        newLines.clear();
        newPoints.clear();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newPoints = new ArrayList<>();
        newLines = new ArrayList<>();
        
        typeOfNewSpatialEntity = "house";
        shapeOfNewSpatialEntity = "rectangle";
        layerOfNewSpatialEntity = "overground";
        //Toggle the shape and type of the entity
        toggleNewObject.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1){
                RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
//                System.out.println(chk.getId());
                String[] parts = chk.getId().split("-");
                typeOfNewSpatialEntity = parts[0];
                shapeOfNewSpatialEntity = parts[1];
                layerOfNewSpatialEntity = parts[2];
                deleteNewEntity();
                System.out.println(typeOfNewSpatialEntity);
                System.out.println(shapeOfNewSpatialEntity);
                System.out.println(layerOfNewSpatialEntity);
                System.out.println("---------");
            }
        });
    }
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    @FXML
    public void toggleNewObjectAction(ActionEvent action)
    {
      System.out.println("Toggled: " + toggleNewObject.getSelectedToggle().getUserData().toString());
    }

    public void handleInputEventForMap(InputEvent event) {
        this.addNewSpatialEntity(event); 
    }

    
}
