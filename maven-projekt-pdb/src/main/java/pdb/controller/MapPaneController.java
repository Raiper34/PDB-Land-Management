/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
//import javafx.stage.Window;
import oracle.spatial.geometry.JGeometry;
import pdb.model.SpatialEntitiesModel;

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

    /*@FXML
    void checked(ActionEvent event) throws SQLException {
        DatabaseTest databaseTest = new DatabaseTest();
        JGeometry geom = databaseTest.databaseOperation();

        if (clickedCount % 2 != 0) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            label.setText("Klikni, pro vykresleni obdelniku z databaze!");
        } else {
            if (geom.isRectangle()) {
                double x1 = geom.getOrdinatesArray()[0];
                double y1 = geom.getOrdinatesArray()[1];

                double x2 = geom.getOrdinatesArray()[2];
                double y2 = geom.getOrdinatesArray()[3];

                gc.setFill(Color.AQUA);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(10);

                //gc.fill();
                gc.strokeRect(x1, y2, x2 - x1, y2 - y1);
                gc.fillRect(x1, y2, x2 - x1, y2 - y1);
            }

            label.setText("Obdelnik z databaze vykreslen!");
        }
        clickedCount++;
    }*/
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        drawMap();
    }

    public void addParent(MainController c1) {
        this.mainController = c1;
    }

    @FXML
    void clicked(MouseEvent event) {
        //this.cccc.test();
        System.out.println("Test");
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            0.0, 0.0,
            20.0, 10.0,
            10.0, 20.0});
        mapa.getChildren().add(polygon);
        polygon.setRotate(50.0);
        polygon.setScaleX(5);
        polygon.setScaleY(5);
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
    
    public void loadEntities() {
        entities = spatialEntitiesModel.getEntities();
    }
    
    public void addShapeToMapAndSetListeners(Shape shape) {
        mapa.getChildren().add(shape);
        shape.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {
                mainController.handleMouseEventForShape(t, shape);
            }
        });
    }
    
    public void drawSpatialEntities(){
        // first print underground object
        for (Entity entity : entities){
            // underground circle type objects
            for (ImprovedCircle shape : entity.toShapes().circles){
                if (shape.getEntityReference().getLayer().equals("underground")) {
                    // water connection
                    if (shape.getEntityReference().getEntityType().equals("water connection")) {
                        shape.setFill(Color.rgb(1, 186, 186, 0.6));
                        shape.setStroke(Color.rgb(1, 186, 186, 0.7));
                    }
                    // connection to gas
                    else if (shape.getEntityReference().getEntityType().equals("connection to gas")) {
                        shape.setFill(Color.rgb(214, 107, 0, 0.6));
                        shape.setStroke(Color.rgb(214, 107, 0, 0.7));
                    }
                    
                    this.addShapeToMapAndSetListeners(shape);
                }
            }
            // underground path type objects
            for (ImprovedPath shape : entity.toShapes().paths){
                if (shape.getEntityReference().getLayer().equals("underground")) {
                    // water connection
                    if (shape.getEntityReference().getEntityType().equals("water pipes")) {
                        shape.setStroke(Color.rgb(1, 186, 186, 0.6));
                    }
                    // connection to gas
                    else if (shape.getEntityReference().getEntityType().equals("gas pipes")) {
                        shape.setStroke(Color.rgb(214, 107, 0, 0.6));
                    }
                    this.addShapeToMapAndSetListeners(shape);
                }
            }
        }
        
        // next print estaes
        for (Estate estate : estates){
            for (ImprovedPolygon shape : estate.toShapes().polygons){
                shape.setFill(Color.TRANSPARENT);
                shape.setStroke(Color.rgb(160, 160, 160, 0.6));
                shape.setStrokeWidth(1.0);
                this.addShapeToMapAndSetListeners(shape);
            }
        }
        
        // next print overground objects
        for (Entity entity : entities){
            // overground polygon type objects
            for (ImprovedPolygon shape : entity.toShapes().polygons){
                if (shape.getEntityReference().getLayer().equals("overground")) {
                    // house
                    if (shape.getEntityReference().getEntityType().equals("house")) {
                        shape.setFill(Color.rgb(85, 92, 128, 0.6));
                        shape.setStroke(Color.rgb(85, 92, 128, 0.7));
                    }

                    this.addShapeToMapAndSetListeners(shape);
                }
            }
            // overground circle type objects
            for (ImprovedCircle shape : entity.toShapes().circles){
                if (shape.getEntityReference().getLayer().equals("overground")) {
                    // water area
                    if (shape.getEntityReference().getEntityType().equals("water area")) {
                        shape.setFill(Color.rgb(1, 51, 186, 0.6));
                        shape.setStroke(Color.rgb(1, 51, 186, 0.7));
                    }
                    // connection to electricity
                    else if (shape.getEntityReference().getEntityType().equals("connection to electricity")) {
                        shape.setFill(Color.rgb(186, 1, 29, 0.6));
                        shape.setStroke(Color.rgb(186, 1, 29, 0.7));
                    }
                    // bushes
                    else if (shape.getEntityReference().getEntityType().equals("bushes")) {
                        shape.setFill(Color.rgb(122, 186, 1, 0.6));
                        shape.setStroke(Color.rgb(122, 186, 1, 0.7));
                    }
                    // trees
                    else if (shape.getEntityReference().getEntityType().equals("trees")) {
                        shape.setFill(Color.rgb(1, 186, 26, 0.6));
                        shape.setStroke(Color.rgb(1, 186, 26, 0.7));
                    }

                    this.addShapeToMapAndSetListeners(shape);
                }
            }
            // overground path type objects
            for (ImprovedPath shape : entity.toShapes().paths){
                if (shape.getEntityReference().getLayer().equals("overground")) {
                    // power lines
                    if (shape.getEntityReference().getEntityType().equals("power lines")) {
                        shape.setStroke(Color.rgb(186, 1, 29, 0.6));
                    }
                    // path
                    else if (shape.getEntityReference().getEntityType().equals("path")) {
                        shape.setStroke(Color.rgb(80, 80, 80, 0.6));
                        shape.setStrokeWidth(10.0);
                    }

                    this.addShapeToMapAndSetListeners(shape);
                }
            }
        }
    }

}
