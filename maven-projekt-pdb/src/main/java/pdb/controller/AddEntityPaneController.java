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
    
    
    public void addNewSpatialEntity(InputEvent event) {
        //"point", "multipoint", "line", "multiline",  "rectangle", "polygon", "circle" 
        switch (shapeOfNewSpatialEntity) {
            case "point":
                if ( newPoints.size() == 0 ){
                    addPointOnClick(event);   
                }
                return;
//                break;
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
                break;
            case "circle":
                addCircleEventHandler(event);
                break;
        }
        
    }
    
    public void saveNewSpatialEntity() {
        //Create the new entity and jgeometry from the points I have got
        
        //JGeometry geometry = new JGeometry();
        
//        //getLastID of entities
//        int id = mainController.mapPaneController.spatialEntitiesModel.getMaxId("related_spatial_entities") + 1;
//        
//        JGeometry geometry = new JGeometry(x, y, GTYPE_POINT);
//        Date validFrom =  new Date();
//        Date validTo = new Date();
//        
//        //int id, String name, String description, JGeometry geometry, Date valid_from, Date valid_to, String entityType,String layer
//        Entity newEntity = new Entity(id , "NewBushesPoint", "NewBushesPoint", geometry, validFrom, validTo, "bushes", "overground");
//        mainController.mapPaneController.entities.add(newEntity);
//        mainController.mapPaneController.drawSpatialEntities();


//RECTANGLE
//JGeometry(double minX,
//         double minY,
//         double maxX,
//         double maxY,
//         int srid)
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
