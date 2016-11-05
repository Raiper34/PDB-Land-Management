/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import pdb.controller.MainController;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import pdb.model.spatial.ImprovedCircle;
import pdb.model.spatial.ImprovedPath;
import pdb.model.spatial.ImprovedPolygon;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
//import javafx.stage.Window;
import oracle.spatial.geometry.JGeometry;
import pdb.model.SpatialEntitiesModel;
import pdb.model.spatial.ShapesColorsDefinition;
import pdb.model.spatial.SpatialEntity;

/**
 * FXML Controller class
 *
 * @author gulan
 */
public class MapPaneController implements Initializable {

    SpatialEntitiesModel spatialEntitiesModel;
    
    List<Estate> estates;
    
    List<Entity> entities;
    
    List<Shape> shapes;
    
    @FXML
    public AnchorPane mapa;

    public int cislo;

    public MainController mainController;

    @FXML
    private MainController fXMLController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapa.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>()
        {
            @Override
            public void handle(InputEvent t) {
                mainController.handleInputEventForMap(t);
            }
        });
        drawMap();
    }

    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    public void drawMap() {
        /*Rectangle r = new Rectangle();
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
        mapa.getChildren().add(r);
        mapa.getChildren().add(r1);*/
    }
    
    public void initializeSpatialEntitiesModel(){
        spatialEntitiesModel = new SpatialEntitiesModel();
    }
    
    public void loadEstates() {
        estates = spatialEntitiesModel.getEstates();
    }
    
    public void loadEstates(String dateSnapshot) {
        estates = spatialEntitiesModel.getEstates(dateSnapshot);
    }

    public void loadEntities() {
        entities = spatialEntitiesModel.getEntities();
    }
    
    public void loadEntities(String dateSnapshot) {
        entities = spatialEntitiesModel.getEntities(dateSnapshot);
    }
    
    public void addShapeToMapAndSetListeners(Shape shape) {
        mapa.getChildren().add(shape);
        shape.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>()
        {
            @Override
            public void handle(InputEvent t) {
                try {
                    mainController.handleInputEventForShape(t, shape);
                    //System.out.println( t.getEventType());
                } catch (SQLException ex) {
                    Logger.getLogger(MapPaneController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MapPaneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // more bright to current object on mouseenter
        shape.setOnMouseEntered(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {
                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon polygon = (ImprovedPolygon) shape;
                    if (!polygon.isEstate()) {
                        String entityType = polygon.getEntityReference().getEntityType();
                        if (entityType.equals("house")) {
                            shape.setFill(ShapesColorsDefinition.houseFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.houseStroke.brighter());
                        }
                    }
                    else {
                        shape.setFill(ShapesColorsDefinition.estateFill);
                        shape.setStroke(ShapesColorsDefinition.estateStroke.darker().darker().darker());
                    }
                }
                else if (shape instanceof ImprovedCircle) {
                    ImprovedCircle circle = (ImprovedCircle) shape;
                        String entityType = circle.getEntityReference().getEntityType();
                        if (entityType.equals("water area")) {
                            shape.setFill(ShapesColorsDefinition.waterAreaFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.waterAreaStroke.brighter());
                        }
                        else if (entityType.equals("water connection")) {
                            shape.setFill(ShapesColorsDefinition.waterConnectionFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.waterConnectionStroke.brighter());
                        }
                        else if (entityType.equals("connection to gas")) {
                            shape.setFill(ShapesColorsDefinition.connectionToGasFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.connectionToGasStroke.brighter());
                        }
                        else if (entityType.equals("connection to electricity")) {
                            shape.setFill(ShapesColorsDefinition.connectionToElectricityFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.connectionToElectricityStroke.brighter());
                        }
                        else if (entityType.equals("bushes")) {
                            shape.setFill(ShapesColorsDefinition.bushesFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.bushesStroke.brighter());
                        }
                        else if (entityType.equals("trees")) {
                            shape.setFill(ShapesColorsDefinition.treesFill.brighter());
                            shape.setStroke(ShapesColorsDefinition.treesStroke.brighter());
                        }
                    }
                
                else if (shape instanceof ImprovedPath) {
                    ImprovedPath path = (ImprovedPath) shape;
                        String entityType = path.getEntityReference().getEntityType();
                        if (entityType.equals("water pipes")) {
                            shape.setStroke(ShapesColorsDefinition.waterPipesStroke.brighter());
                        }
                        else if (entityType.equals("gas pipes")) {
                            shape.setStroke(ShapesColorsDefinition.gasPipesStroke.brighter());
                        }
                        else if (entityType.equals("power lines")) {
                            shape.setStroke(ShapesColorsDefinition.powerLinesStroke.brighter());
                        }
                        else if (entityType.equals("path")) {
                            shape.setStroke(ShapesColorsDefinition.pathStroke.brighter());
                        }
                                        
                } 
            }
        });
        
        // redraw shape with origin color on mouseleave
        shape.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {
                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon polygon = (ImprovedPolygon) shape;
                    if (!polygon.isEstate()) {
                        String entityType = polygon.getEntityReference().getEntityType();
                        if (entityType.equals("house")) {
                            shape.setFill(ShapesColorsDefinition.houseFill);
                            shape.setStroke(ShapesColorsDefinition.houseStroke);
                        }
                    }
                    else {
                        shape.setFill(ShapesColorsDefinition.estateFill);
                        shape.setStroke(ShapesColorsDefinition.estateStroke);
                    }
                }
                else if (shape instanceof ImprovedCircle) {
                    ImprovedCircle circle = (ImprovedCircle) shape;
                        String entityType = circle.getEntityReference().getEntityType();
                        if (entityType.equals("water area")) {
                            shape.setFill(ShapesColorsDefinition.waterAreaFill);
                            shape.setStroke(ShapesColorsDefinition.waterAreaStroke);
                        }
                        else if (entityType.equals("water connection")) {
                            shape.setFill(ShapesColorsDefinition.waterConnectionFill);
                            shape.setStroke(ShapesColorsDefinition.waterConnectionStroke);
                        }
                        else if (entityType.equals("connection to gas")) {
                            shape.setFill(ShapesColorsDefinition.connectionToGasFill);
                            shape.setStroke(ShapesColorsDefinition.connectionToGasStroke);
                        }
                        else if (entityType.equals("connection to electricity")) {
                            shape.setFill(ShapesColorsDefinition.connectionToElectricityFill);
                            shape.setStroke(ShapesColorsDefinition.connectionToElectricityStroke);
                        }
                        else if (entityType.equals("bushes")) {
                            shape.setFill(ShapesColorsDefinition.bushesFill);
                            shape.setStroke(ShapesColorsDefinition.bushesStroke);
                        }
                        else if (entityType.equals("trees")) {
                            shape.setFill(ShapesColorsDefinition.treesFill);
                            shape.setStroke(ShapesColorsDefinition.treesStroke);
                        }
                    }
                
                else if (shape instanceof ImprovedPath) {
                    ImprovedPath path = (ImprovedPath) shape;
                        String entityType = path.getEntityReference().getEntityType();
                        if (entityType.equals("water pipes")) {
                            shape.setStroke(ShapesColorsDefinition.waterPipesStroke);
                        }
                        else if (entityType.equals("gas pipes")) {
                            shape.setStroke(ShapesColorsDefinition.gasPipesStroke);
                        }
                        else if (entityType.equals("power lines")) {
                            shape.setStroke(ShapesColorsDefinition.powerLinesStroke);
                        }
                        else if (entityType.equals("path")) {
                            shape.setStroke(ShapesColorsDefinition.pathStroke);
                        }
                                        
                } 
            }
        });
    }
    
    public void clearMemoryAndMap()
    {
        this.clearMap();
        //this.entities.clear();
        //this.estates.clear();
        //this.shapes.clear();
    }
    
    public void clearMap()
    {
        this.mapa.getChildren().clear();
    }
    
    public void drawSpatialEntities(){
        this.drawSpatialEntitiesByLayer(true, true, true);
    }
    
    public void drawSpatialEntities(boolean underground, boolean ground, boolean overground){
        this.drawSpatialEntitiesByLayer(underground, ground, overground);
    }
    
    public void drawSpatialEntitiesByLayer(boolean underground, boolean ground, boolean overground)
    {
        clearMap();
        // first print underground object
        for (Entity entity : entities){
            // underground circle type objects
            for (ImprovedCircle shape : entity.toShapes().circles){
                if (shape.getEntityReference().getLayer().equals("underground")) {
                    if(underground)
                    {
                        // water connection
                        if (shape.getEntityReference().getEntityType().equals("water connection")) {
                            shape.setFill(ShapesColorsDefinition.waterConnectionFill);
                            shape.setStroke(ShapesColorsDefinition.waterConnectionStroke);
                        }
                        // connection to gas
                        else if (shape.getEntityReference().getEntityType().equals("connection to gas")) {
                            shape.setFill(ShapesColorsDefinition.connectionToGasFill);
                            shape.setStroke(ShapesColorsDefinition.connectionToGasStroke);
                        }
                        this.addShapeToMapAndSetListeners(shape);
                    }
                }
            }
            // underground path type objects
            for (ImprovedPath shape : entity.toShapes().paths){
                if (shape.getEntityReference().getLayer().equals("underground")) {
                    if(underground)
                    {
                        // water pipes
                        if (shape.getEntityReference().getEntityType().equals("water pipes")) {
                            shape.setStroke(ShapesColorsDefinition.waterPipesStroke);
                            shape.setStrokeWidth(2.0);
                        }
                        // gas pipes
                        else if (shape.getEntityReference().getEntityType().equals("gas pipes")) {
                            shape.setStroke(ShapesColorsDefinition.gasPipesStroke);
                            shape.setStrokeWidth(2.0);
                        }
                        this.addShapeToMapAndSetListeners(shape);
                    }
                }
            }
        }
        
        // next print estaes
        for (Estate estate : estates){
            for (ImprovedPolygon shape : estate.toShapes().polygons){
                if(ground)
                {
                    shape.setFill(ShapesColorsDefinition.estateFill);
                    shape.setStroke(ShapesColorsDefinition.estateStroke);
                    shape.setStrokeWidth(1.0);
                    this.addShapeToMapAndSetListeners(shape);
                }
            }
        }
        
        // next print overground objects
        for (Entity entity : entities){
            // overground polygon type objects
            for (ImprovedPolygon shape : entity.toShapes().polygons){
                if (shape.getEntityReference().getLayer().equals("overground")) {
                    if(overground)
                    {
                        // house
                        if (shape.getEntityReference().getEntityType().equals("house")) {
                            shape.setFill(ShapesColorsDefinition.houseFill);
                            shape.setStroke(ShapesColorsDefinition.houseStroke);
                        }

                        this.addShapeToMapAndSetListeners(shape);
                    }
                }
            }
            // overground circle type objects
            for (ImprovedCircle shape : entity.toShapes().circles){
                if (shape.getEntityReference().getLayer().equals("overground")) {
                    if(overground)
                    {
                        // water area
                        if (shape.getEntityReference().getEntityType().equals("water area")) {
                            shape.setFill(ShapesColorsDefinition.waterAreaFill);
                            shape.setStroke(ShapesColorsDefinition.waterAreaStroke);
                        }
                        // connection to electricity
                        else if (shape.getEntityReference().getEntityType().equals("connection to electricity")) {
                            shape.setFill(Color.rgb(186, 1, 29, 0.6));
                            shape.setStroke(Color.rgb(186, 1, 29, 0.7));
                        }
                        // bushes
                        else if (shape.getEntityReference().getEntityType().equals("bushes")) {
                            shape.setFill(ShapesColorsDefinition.bushesFill);
                            shape.setStroke(ShapesColorsDefinition.bushesStroke);
                        }
                        // trees
                        else if (shape.getEntityReference().getEntityType().equals("trees")) {
                            shape.setFill(ShapesColorsDefinition.treesFill);
                            shape.setStroke(ShapesColorsDefinition.treesStroke);
                        }

                        this.addShapeToMapAndSetListeners(shape);
                    }
                }
            }
            // overground path type objects
            for (ImprovedPath shape : entity.toShapes().paths){
                if (shape.getEntityReference().getLayer().equals("overground")) {
                    if(overground)
                    {
                        // power lines
                        if (shape.getEntityReference().getEntityType().equals("power lines")) {
                            shape.setStroke(ShapesColorsDefinition.powerLinesStroke);
                            shape.setStrokeWidth(2.0);
                        }
                        // path
                        else if (shape.getEntityReference().getEntityType().equals("path")) {
                            shape.setStroke(ShapesColorsDefinition.pathStroke);
                            shape.setStrokeWidth(10.0);
                        }

                        this.addShapeToMapAndSetListeners(shape);
                    }
                }
            }
        }
    }
    
    public void addSpatialEntityToMap(Estate spatialEntityToAdd) {
        estates.add(spatialEntityToAdd);
        drawSpatialEntities();
    }
    
    public void addSpatialEntityToMap(Entity spatialEntityToAdd) {
        entities.add(spatialEntityToAdd);
        drawSpatialEntities();
    }
    
    public void removeShapeFromMap(Node shapeToRemove) {
        mapa.getChildren().remove(shapeToRemove);
    }
}
