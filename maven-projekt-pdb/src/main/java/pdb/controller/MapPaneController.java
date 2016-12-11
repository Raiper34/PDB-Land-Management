/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import pdb.model.spatial.ImprovedCircle;
import pdb.model.spatial.ImprovedPath;
import pdb.model.spatial.ImprovedPolygon;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
//import javafx.stage.Window;
import pdb.model.spatial.SpatialEntitiesModel;
import pdb.model.spatial.Shapes;
import pdb.model.spatial.ShapesColorsDefinition;

/**
 * FXML Controller class
 * Map Pane Controller
 * @author gulan and others
 */
public class MapPaneController implements Initializable {

    SpatialEntitiesModel spatialEntitiesModel;
    
    List<Estate> estates;
    
    List<Entity> entities;
    
    List<Shape> shapes;
    
    /**
     * Anchor pane which represents map
     */
    @FXML
    public AnchorPane mapa;

    /**
     *
     */
    public int cislo;

    /**
     * MainController instance
     */
    public MainController mainController;

    @FXML
    private MainController fXMLController;
    
    /**
     * array where first object is fill of last selcted object and second object is stroke of last selected object
     */
    public Paint[] arrPaintFillAndStrokeLastSelectedObject = new Paint[2];

    /**
     * ID of last selected shape
     */
    public int lastSelectedShapeID = 0;

    /**
     * Type of last selected entity (entity or estate)
     */
    public String lastSelectedEntityType = ""; // estate or entity

    /**
     * Initializes the controller class.
     * @param url url
     * @param rb resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arrPaintFillAndStrokeLastSelectedObject[0] = null;
        arrPaintFillAndStrokeLastSelectedObject[1] = null;
        mapa.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>()
        {
            @Override
            public void handle(InputEvent t) {
                mainController.handleInputEventForMap(t);
            }
        });
    }

   /** 
    * add Entity To Map
    * @param spatialEntityToAdd
    */
    public void addSpatialEntityToMap(Estate spatialEntityToAdd) {
        estates.add(spatialEntityToAdd);
        drawSpatialEntities();
    }
    
   /** 
    * add Entity To Map
    * @param spatialEntityToAdd
    */
    public void addSpatialEntityToMap(Entity spatialEntityToAdd) {
        entities.add(spatialEntityToAdd);
        drawSpatialEntities();
    }
    
   /** 
    * remove Shape From Map
    * @param shapeToRemove instance of Node which is determinate to remove from map
    */
    public void removeShapeFromMap(Node shapeToRemove) {
        mapa.getChildren().remove(shapeToRemove);
    }
    
   /** 
    * remove Shapes From Map
    * @param shapesToRemove instance of shape which contain shapes to remove
    */
    public void removeShapesFromMap(Shapes shapesToRemove) {
        for (ImprovedCircle shape : shapesToRemove.circles)
            removeShapeFromMap(shape);
        for (ImprovedPath shape : shapesToRemove.paths)
            removeShapeFromMap(shape);
        for (ImprovedPolygon shape : shapesToRemove.polygons)
            removeShapeFromMap(shape);
    }
    
   /**
    * Save instance of Parent controller MainController
    * @param mainController MainController instance
    */
    public void addParent(MainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Initialize spatial entities model
     */
    public void initializeSpatialEntitiesModel(){
        spatialEntitiesModel = new SpatialEntitiesModel();
    }
    
   /** 
    * LoadEstates from DB into estates variable
    */
    public void loadEstates() {
        estates = spatialEntitiesModel.getEstates();
    }
    
   /** 
    * loadEstates from DB into estates variable on selected date
    * @param dateSnapshot
    */
    public void loadEstates(String dateSnapshot) {
        estates = spatialEntitiesModel.getEstates(dateSnapshot);
    }
    
   /** 
    * Load Estates from DB into estates with PreparedStatement
    * @param preparedSQLToGetEstatesFromDB
    */
    public void loadEstates(PreparedStatement preparedSQLToGetEstatesFromDB) {
        estates = spatialEntitiesModel.getEstates(preparedSQLToGetEstatesFromDB);
    }
   
    /** 
     * Load Entities from DB into estates variable
     */
    public void loadEntities() {
        entities = spatialEntitiesModel.getEntities();
    }
    
    /** 
     * Load entities from DB into entities variable on selected date
     * @param dateSnapshot
     */
    public void loadEntities(String dateSnapshot) {
        entities = spatialEntitiesModel.getEntities(dateSnapshot);
    }
    
    /** Load entities from DB into entities variable with PreparedStatement
     * @param preparedSQLToGetEntitiesFromDB
     */
    public void loadEntities(PreparedStatement preparedSQLToGetEntitiesFromDB) {
        entities = spatialEntitiesModel.getEntities(preparedSQLToGetEntitiesFromDB);
    }
    
    /** 
     * Add Shape To Map And Set Listeners on this shape
     * This method is called when shapes are drawing onto map
     * @param shape shape to add
     */
    public void addShapeToMapAndSetListeners(Shape shape) {
        mapa.getChildren().add(shape);
        shape.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>()
        {
            @Override
            public void handle(InputEvent t) {
                try {
                    if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
                        if (lastSelectedShapeID != 0) {
                            setOriginalColorOnPreviousSelectedObject();
                        }
                        
                        if (shape instanceof ImprovedCircle) {
                            ImprovedCircle circle = (ImprovedCircle) shape;
                            lastSelectedShapeID = circle.dbId;
                            lastSelectedEntityType = "entity";
                            
                            arrPaintFillAndStrokeLastSelectedObject[0] = shape.getFill();
                            arrPaintFillAndStrokeLastSelectedObject[1] = shape.getStroke();
                            setColorToSelectedObject();
                        } 
                        else if (shape instanceof ImprovedPolygon) {
                            ImprovedPolygon polygon = (ImprovedPolygon) shape;
                            lastSelectedShapeID = polygon.dbId;
                            if (polygon.isEstate()) {
                                lastSelectedEntityType = "estate";
                            }
                            else {
                                lastSelectedEntityType = "entity"; 
                            }
                            
                            arrPaintFillAndStrokeLastSelectedObject[0] = shape.getFill();
                            arrPaintFillAndStrokeLastSelectedObject[1] = shape.getStroke();
                            setColorToSelectedObject();
                        } 
                        else if (shape instanceof ImprovedPath) {
                            ImprovedPath path = (ImprovedPath) shape;
                            lastSelectedShapeID = path.dbId;
                            lastSelectedEntityType = "entity";
                            
                            arrPaintFillAndStrokeLastSelectedObject[0] = shape.getFill();
                            arrPaintFillAndStrokeLastSelectedObject[1] = shape.getStroke();
                            setColorToSelectedObject();
                        }

                    }
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
                    if (polygon.dbId == lastSelectedShapeID && ((lastSelectedEntityType.equals("entity") && !polygon.isEstate()) || (lastSelectedEntityType.equals("estate") && polygon.isEstate()))) {
                        if (!(shape instanceof ImprovedPath)) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill.brighter());
                        }
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke.brighter());
                    }
                    else {
                    
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
                }
                else if (shape instanceof ImprovedCircle) {
                    ImprovedCircle circle = (ImprovedCircle) shape;
                    if (circle.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                        if (!(shape instanceof ImprovedPath)) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill.brighter());
                        }
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke.brighter());
                    }
                    else {
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
                }
                
                else if (shape instanceof ImprovedPath) {
                    ImprovedPath path = (ImprovedPath) shape;
                    if (path.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                        if (!(shape instanceof ImprovedPath)) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill.brighter());
                        }
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke.brighter());
                    }
                    else {
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
            }
        });
        
        // redraw shape with origin color on mouseleave
        shape.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {

                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon polygon = (ImprovedPolygon) shape;
                    if (polygon.dbId == lastSelectedShapeID && ((lastSelectedEntityType.equals("entity") && !polygon.isEstate()) || (lastSelectedEntityType.equals("estate") && polygon.isEstate()))) {
                        if (!(shape instanceof ImprovedPath)) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                        }
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                    }
                    else {
                    
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
                }
                else if (shape instanceof ImprovedCircle) {
                    ImprovedCircle circle = (ImprovedCircle) shape;
                    if (circle.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                        if (!(shape instanceof ImprovedPath)) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                        }
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                    }
                    else {
                    
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
                }
                
                else if (shape instanceof ImprovedPath) {
                    ImprovedPath path = (ImprovedPath) shape;
                    if (path.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                        if (!(shape instanceof ImprovedPath)) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                        }
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                    }
                    else {
                    
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
            }
        });
    }
    
    /** 
     * clear map and memory
     */
    public void clearMemoryAndMap()
    {
        this.clearMap();
    }
    
    /**
     * removes all the shapes from map
     */
    public void clearMap()
    {
        this.mapa.getChildren().clear();
        arrPaintFillAndStrokeLastSelectedObject[0] = null;
        arrPaintFillAndStrokeLastSelectedObject[1] = null;
        lastSelectedShapeID = 0;
        lastSelectedEntityType = "";
    }
    
    /** 
     * Draw all the spatial entities no matter what layer they are in
     */
    public void drawSpatialEntities(){
        this.drawSpatialEntitiesByLayer(true, true, true);
    }
    
    /** 
     * Draw all the spatial entities in the selected layers
     * @param underground draw entities in undreground layer
     * @param ground draw entities in ground layer
     * @param overground draw entities in overground layer
     */
    public void drawSpatialEntities(boolean underground, boolean ground, boolean overground){
        this.drawSpatialEntitiesByLayer(underground, ground, overground);
    }
    
    /** 
     * DrawSpatialEntitiesByLayer draw all the spatial entities in the selected layers
     * @param underground draw entities in undreground layer
     * @param ground draw entities in ground layer
     * @param overground draw entities in overground layer
     */
    public void drawSpatialEntitiesByLayer(boolean underground, boolean ground, boolean overground)
    {
        int tmpID = lastSelectedShapeID;
        //System.out.println(this.mainController.dateOfCurrentlyShowedDatabaseSnapshot);
        Paint[] tmpPaintArray = {arrPaintFillAndStrokeLastSelectedObject[0], arrPaintFillAndStrokeLastSelectedObject[1]};
        String tmpEntityType = lastSelectedEntityType;
        clearMap();
        lastSelectedShapeID = tmpID;
        lastSelectedEntityType = tmpEntityType;
        arrPaintFillAndStrokeLastSelectedObject[0] = tmpPaintArray[0];
        arrPaintFillAndStrokeLastSelectedObject[1] = tmpPaintArray[1];
        // first print underground object
        for (Entity entity : entities){
            // underground circle type objects
            for (ImprovedCircle shape : entity.toShapes().circles){
                
                if (shape.getEntityReference().getLayer().equals("underground")) {
                    if(underground)
                    {
                        if (shape.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                            shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                            this.addShapeToMapAndSetListeners(shape);
                            continue;
                        }
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
                        if (shape.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                            shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                            shape.setStrokeWidth(2.0);
                            this.addShapeToMapAndSetListeners(shape);
                            continue;
                        }
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
                    if (shape.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("estate")) {
                        shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                        shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                        shape.setStrokeWidth(1.0);
                        this.addShapeToMapAndSetListeners(shape);
                        continue;
                    }
                    
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
                        if (shape.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                            shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                            this.addShapeToMapAndSetListeners(shape);
                            continue;
                        }
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
                        if (shape.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                            shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                            shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                            this.addShapeToMapAndSetListeners(shape);
                            continue;
                        }
                        // water area
                        if (shape.getEntityReference().getEntityType().equals("water area")) {
                            shape.setFill(ShapesColorsDefinition.waterAreaFill);
                            shape.setStroke(ShapesColorsDefinition.waterAreaStroke);
                        }
                        // connection to electricity
                        else if (shape.getEntityReference().getEntityType().equals("connection to electricity")) {
                            shape.setFill(ShapesColorsDefinition.connectionToElectricityFill);
                            shape.setStroke(ShapesColorsDefinition.connectionToElectricityStroke);
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
                        if (shape.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                            shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                            if (shape.getEntityReference().getEntityType().equals("power lines")) {
                                shape.setStrokeWidth(2.0);
                            }
                            else if (shape.getEntityReference().getEntityType().equals("path")) {
                                shape.setStrokeWidth(10.0);
                            }
                            this.addShapeToMapAndSetListeners(shape);
                            continue;
                        }
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
    
    /** 
     * Set Original Color (fill and stroke) On Previous Selected Object on map
     */
    public void setOriginalColorOnPreviousSelectedObject() {
        for (Node node : mapa.getChildren()) {
            Shape shape = (Shape) node;
            if (shape instanceof ImprovedCircle) {
                ImprovedCircle circle = (ImprovedCircle) shape;
                if (circle.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                    if (!(shape instanceof ImprovedPath)) {
                        shape.setFill(arrPaintFillAndStrokeLastSelectedObject[0]);
                    }
                    shape.setStroke(arrPaintFillAndStrokeLastSelectedObject[1]);
                }
            } else if (shape instanceof ImprovedPolygon) {
                ImprovedPolygon polygon = (ImprovedPolygon) shape;
                if (polygon.dbId == lastSelectedShapeID && ((lastSelectedEntityType.equals("entity") && !polygon.isEstate()) || (lastSelectedEntityType.equals("estate") && polygon.isEstate()))) {
                    if (!(shape instanceof ImprovedPath)) {
                        shape.setFill(arrPaintFillAndStrokeLastSelectedObject[0]);
                    }
                    shape.setStroke(arrPaintFillAndStrokeLastSelectedObject[1]);
                }
            } else if (shape instanceof ImprovedPath) {
                ImprovedPath path = (ImprovedPath) shape;
                if (path.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                    if (!(shape instanceof ImprovedPath)) {
                        shape.setFill(arrPaintFillAndStrokeLastSelectedObject[0]);
                    }
                    shape.setStroke(arrPaintFillAndStrokeLastSelectedObject[1]);
                }
            }

        }
    }
    
    /** 
     *  Sets color (fill and stroke) to red of the selected object on map
     */
    public void setColorToSelectedObject() {
        for (Node node : mapa.getChildren()) {
            Shape shape = (Shape) node;
            if (shape instanceof ImprovedCircle) {
                ImprovedCircle circle = (ImprovedCircle) shape;
                if (circle.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                    if (!(shape instanceof ImprovedPath)) {
                        shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                    }
                    shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                }
            } else if (shape instanceof ImprovedPolygon) {
                ImprovedPolygon polygon = (ImprovedPolygon) shape;
                if (polygon.dbId == lastSelectedShapeID && ((lastSelectedEntityType.equals("entity") && !polygon.isEstate()) || (lastSelectedEntityType.equals("estate") && polygon.isEstate()))) {
                    if (!(shape instanceof ImprovedPath)) {
                        shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                    }
                    shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                }
            } else if (shape instanceof ImprovedPath) {
                ImprovedPath path = (ImprovedPath) shape;
                if (path.dbId == lastSelectedShapeID && lastSelectedEntityType.equals("entity")) {
                    if (!(shape instanceof ImprovedPath)) {
                        shape.setFill(ShapesColorsDefinition.selectedObjectFill);
                    }
                    shape.setStroke(ShapesColorsDefinition.selectedObjectStroke);
                }
            }

        }
    }
}
