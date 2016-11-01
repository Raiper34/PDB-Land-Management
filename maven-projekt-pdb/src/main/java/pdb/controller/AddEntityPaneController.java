/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    
    //private SpatialEntitiesModel spatialEntitiesModel;
    
    public int countOfAddedPoints = 0;
    
    private List<Line> newLines;
    private List<Circle> newPoints;
    
    public void addPoint(MouseEvent event) {
        Circle point = new Circle(event.getX(), event.getY(), 5.0f, Paint.valueOf("Black") );
        newPoints.add(point);
        mainController.mapPaneController.mapa.getChildren().add(point);
    }
    
    public void addLine(MouseEvent event) {
        Circle point = new Circle(event.getX(), event.getY(), 5.0f, Paint.valueOf("Black") );
        newPoints.add(point);
    }
    
    public void addNewSpatialEntity(MouseEvent event) {
        
        String shapeType = "Multiline";
        
        if ( shapeType == "Point" && countOfAddedPoints > 0 )
            return;

        addPoint(event);
        
        if(shapeType == "Multiline" || shapeType == "Line") {
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

        countOfAddedPoints++;
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
    }
    
    public void deleteNewEntity() {
        //Remove all the new shapes from map
        for (Circle shape : newPoints){
            mainController.mapPaneController.mapa.getChildren().remove(shape);
        }
        for (Line shape : newLines){
            mainController.mapPaneController.mapa.getChildren().remove(shape);
        }
        newLines.clear();
        newPoints.clear();
        countOfAddedPoints = 0;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newPoints = new ArrayList<>();
        newLines = new ArrayList<>();
        
        //drawTest();
        System.out.println("Hello from addentity");
        toggleNewObject.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1){
                RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                
                System.out.println("Selected Radio Button - "+chk.getId());
                if(chk.getId() == "BushesPoint") {
                    mainController.setCurrentTitledPane("AddEntity");
                }
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
    
    
    public void drawTest(){
        Rectangle r = new Rectangle();
        r.setX(50);
        r.setY(50);
        r.setWidth(200);
        r.setHeight(100);
        r.setArcWidth(20);
        r.setArcHeight(20);
                 
        Rectangle r1 = new Rectangle();

        r.setX(50);
        r.setY(50);
        r.setWidth(200);
        r.setHeight(100);
        r.setArcWidth(20);
        r.setArcHeight(20);
        r.fillProperty();
        addEntityAnchorPane.getChildren().add(r);
        addEntityAnchorPane.getChildren().add(r1);
    }
    
}
