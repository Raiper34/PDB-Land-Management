/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import pdb.model.spatial.SpatialEntitiesModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import pdb.model.spatial.Shapes;
import pdb.model.spatial.SpatialEntity;

/**
 * FXML Controller class
 * Add Entity Pane Controller
 * @author mmarus
 */
public class AddEntityPaneController implements Initializable {

    @FXML
    public AnchorPane addEntityAnchorPane;

    @FXML
    public ToggleGroup toggleNewObject;

    public MainController mainController;

    @FXML
    private MainController fXMLController;

    private String typeOfNewSpatialEntity;
    private String shapeOfNewSpatialEntity;
    private String layerOfNewSpatialEntity;
    private List<Line> newLines;
    private List<Circle> newPoints;
    private Shapes newShapes;
    private Rectangle newRectangle;
    private Circle newCircle;
    private Polygon newPolygon;
    private SpatialEntity newSpatialEntity;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newPoints = new ArrayList<>();
        newLines = new ArrayList<>();
        newShapes = new Shapes();

        typeOfNewSpatialEntity = "house";
        shapeOfNewSpatialEntity = "rectangle";
        layerOfNewSpatialEntity = "overground";
        //Toggle the shape and type of the entity
        toggleNewObject.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton checkedButton = (RadioButton) t1.getToggleGroup().getSelectedToggle();
                String[] parts = checkedButton.getId().split("-");
                typeOfNewSpatialEntity = parts[0];
                shapeOfNewSpatialEntity = parts[1];
                layerOfNewSpatialEntity = parts[2];
                deleteNewEntity();
            }
        });
    }

    /*
    * Creates point if left mouse button clicked
    * @param InputEvent event
    */
    public void addPointOnLeftMouseClick(InputEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            //Get the x and y of the click and create there a new circle
            MouseEvent mouseEvent = (MouseEvent) event;

            /*The ending point of multiline*/
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {

                return;
            }
            addPoint(mouseEvent);
        }
    }

    /*
    * Creates new point and appends it to list of newPoints and draw it on map
    * @param MouseEvent event
    */
    public void addPoint(MouseEvent event) {
        Circle point = new Circle(event.getX(), event.getY(), 2.0f, Paint.valueOf("Black"));
        newPoints.add(point);
        mainController.mapPaneController.mapa.getChildren().add(point);
    }

    /* removePoint from map and from list of newPoints
    * @param int index
    */
    public void removePoint(int index) {
        mainController.mapPaneController.removeShapeFromMap(newPoints.get(index));
        newPoints.remove(index);
    }
    
    /* addLine to list of newLines and draw it on map
    * 
    */
    public void addLine() {
        if (newPoints != null && newPoints.size() > 1) {
            Line newLine = new Line(
                    newPoints.get(newPoints.size() - 1).getCenterX(),
                    newPoints.get(newPoints.size() - 1).getCenterY(),
                    newPoints.get(newPoints.size() - 2).getCenterX(),
                    newPoints.get(newPoints.size() - 2).getCenterY()
            );
            newLines.add(newLine);
            mainController.mapPaneController.mapa.getChildren().add(newLine);
        }
    }

    /* addRectangle to newRectangle and draw it on map
    */
    public void addRectangle() {
        double width = Math.abs(newPoints.get(0).getCenterX() - newPoints.get(1).getCenterX());
        double height = Math.abs(newPoints.get(0).getCenterY() - newPoints.get(1).getCenterY());
        double x = Math.min(newPoints.get(0).getCenterX(), newPoints.get(1).getCenterX());
        double y = Math.min(newPoints.get(0).getCenterY(), newPoints.get(1).getCenterY());

        newRectangle = new Rectangle(x, y, width, height);
        mainController.mapPaneController.mapa.getChildren().add(newRectangle);
    }

    /* Draw a rectangle on map after mouse pressed and dragged.
    * @param InputEvent event
    */
    public void addRectangleEventHandler(InputEvent event) {
        MouseEvent mouseEvent = (MouseEvent) event;

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if (newRectangle != null) {
                mainController.mapPaneController.removeShapeFromMap(newRectangle);
                newRectangle = null;
            }
            //Create starting point
            addPoint(mouseEvent);
            //Create ending point
            addPoint(mouseEvent);
            addRectangle();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (mouseEvent.getX() > 650 || mouseEvent.getY() > 650) {
                return;
            }
            //remove old point from map and list
            removePoint(1);
            //remove newRectangle from map and clear it
            mainController.mapPaneController.removeShapeFromMap(newRectangle);
            //create new point
            addPoint(mouseEvent);
            addRectangle();
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            //Remove the points
            removePoint(1);
            removePoint(0);
            newPoints.clear();
        }
    }

    
    /*
    * Add circle to newPoints and draw it on map
    */
    public void addCircle() {
        double radius = calculateRadius();

        if (newPoints.get(0).getCenterX() - radius < 0
                || newPoints.get(0).getCenterY() - radius < 0
                || newPoints.get(0).getCenterX() + radius > 650
                || newPoints.get(0).getCenterY() + radius > 650) {
            return;
        }

        newCircle = new Circle(newPoints.get(0).getCenterX(), newPoints.get(0).getCenterY(), radius);
        mainController.mapPaneController.mapa.getChildren().add(newCircle);
    }
    
    /*
    * calculateRadius from start to end of mouse drag event
    */
    private double calculateRadius() {
        Point2D start = new Point2D(newPoints.get(0).getCenterX(), newPoints.get(0).getCenterY());
        Point2D end = new Point2D(newPoints.get(1).getCenterX(), newPoints.get(1).getCenterY());
        return start.distance(end);
    }

    /* Create circle and draw it on map on mouse press and drag
    * @param InputEvent event
    */
    public void addCircleEventHandler(InputEvent event) {
        MouseEvent mouseEvent = (MouseEvent) event;

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if (newCircle != null) {
                mainController.mapPaneController.removeShapeFromMap(newCircle);
                newCircle = null;
            }
            //Create starting point
            addPoint(mouseEvent);
            //Create ending point
            addPoint(mouseEvent);
            addCircle();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

            //remove old point from map and list
            removePoint(1);
            //remove newRectangle from map and clear it
            mainController.mapPaneController.removeShapeFromMap(newCircle);
            //create new point
            addPoint(mouseEvent);
            addCircle();
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            //Remove the points
            removePoint(1);
            removePoint(0);
            newPoints.clear();
        }
    }

    /* Create polygon from points and draw it on map after mouse click
    * @param InputEvent event
    */
    private void addPolygonEventHandler(InputEvent event) {
        MouseEvent mouseEvent = (MouseEvent) event;
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (newPoints.isEmpty()) {
                addPointOnLeftMouseClick(event);
                newPolygon = new Polygon(mouseEvent.getX(), mouseEvent.getY());
            } else {
                addPointOnLeftMouseClick(event);
                mainController.mapPaneController.removeShapeFromMap(newPolygon);
                newPolygon.getPoints().add(mouseEvent.getX());
                newPolygon.getPoints().add(mouseEvent.getY());
                System.out.println(newPolygon.getPoints().toString());
                mainController.mapPaneController.mapa.getChildren().add(newPolygon);
            }
        }
    }

    /* Call the relevant event handler based on a shape to be inserted (selected in GUI)
    * @param InputEvent event
    */
    public void addNewSpatialEntity(InputEvent event) {
        switch (shapeOfNewSpatialEntity) {
            case "point":
                if (newPoints.isEmpty()) {
                    addPointOnLeftMouseClick(event);
                }
                return;
            case "multipoint":
                addPointOnLeftMouseClick(event);
                break;
            case "line":
                addLineToPath(event);
                break;
            case "multiline":
                addLineToPaths(event);
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

    /* createJGeometry from gained shapes from mouse events
    * @return JGeometry
     */
    public JGeometry createJGeometry() {
        switch (shapeOfNewSpatialEntity) {
            case "point":
            case "multipoint":
                return SpatialEntity.createJGeometryFromShapes(newPoints, "points");
            case "line":
            case "multiline":
                return SpatialEntity.createJGeometryFromShapes(newShapes);
            case "rectangle":
                return SpatialEntity.createJGeometryFromShapes(newRectangle);
            case "polygon":
                return SpatialEntity.createJGeometryFromShapes(newPolygon);
            case "circle":
                return SpatialEntity.createJGeometryFromShapes(newCircle);
            default:
                System.err.println("There is no such shape of new spatial entity: " + shapeOfNewSpatialEntity);
                return null;
        }
    }

    /* saveNewSpatialEntity prepare and populate new spatial entity and then save it to DB
     */
    public void saveNewSpatialEntity() {
        SpatialEntitiesModel spatialEntitiesModel = mainController.mapPaneController.spatialEntitiesModel;
        SpatialEntity newSpatialEntity = null;
        //Create the new entity and jgeometry from the points I have got
        JGeometry geometry = createJGeometry();
        if (geometry == null) {
            System.out.println("Trying to save empty spatial entity");
            return;
        }

        Calendar calToday = Calendar.getInstance();
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);
        Date validFromDate = calToday.getTime();

        Date validToDate = new GregorianCalendar(2116, 9, 27).getTime();
        if (typeOfNewSpatialEntity.equals("estate")) {
            //getLastID of estate
            int id = spatialEntitiesModel.getNewIdForEstate();
            newSpatialEntity = new Estate(
                    id,
                    typeOfNewSpatialEntity + id,
                    typeOfNewSpatialEntity + id,
                    geometry,
                    validFromDate,
                    validToDate,
                    null
            );

            mainController.mapPaneController.addSpatialEntityToMap((Estate) newSpatialEntity);
            spatialEntitiesModel.saveSpatialEntityToDB((Estate) newSpatialEntity);
        } else {
            int id = spatialEntitiesModel.getNewIdForEntity();
            newSpatialEntity = new Entity(
                    id,
                    typeOfNewSpatialEntity + id,
                    typeOfNewSpatialEntity + id,
                    geometry,
                    validFromDate,
                    validToDate,
                    typeOfNewSpatialEntity,
                    layerOfNewSpatialEntity
            );

            mainController.mapPaneController.addSpatialEntityToMap((Entity) newSpatialEntity);
            spatialEntitiesModel.saveSpatialEntityToDB((Entity) newSpatialEntity);
        }

        deleteNewEntity();
    }

     /* deleteNewEntity from map - clears the temporary objects not yet inserted to DB
     */
    public void deleteNewEntity() {
        //Remove all the new shapes from map
        for (Circle shape : newPoints) {
            mainController.mapPaneController.removeShapeFromMap(shape);
        }
        for (Line shape : newLines) {
            mainController.mapPaneController.removeShapeFromMap(shape);
        }
        mainController.mapPaneController.removeShapesFromMap(newShapes);
        mainController.mapPaneController.removeShapeFromMap(newRectangle);
        mainController.mapPaneController.removeShapeFromMap(newCircle);
        mainController.mapPaneController.removeShapeFromMap(newPolygon);
        newShapes = new Shapes();
        newRectangle = null;
        newCircle = null;
        newPolygon = null;
        newLines.clear();
        newPoints.clear();
    }

    /*
    * @param MainController mainController
     */
    public void addParent(MainController mainController) {
        this.mainController = mainController;
    }

    /*
    * @param InputEvent event
     */
    public void handleInputEventForMap(InputEvent event) {
        this.addNewSpatialEntity(event);
    }
    
    
    /* Insert new path to list of improved paths in Shapes class
    * @param InputEvent event
     */
    private void addLineToPaths(InputEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            MouseEvent mouseEvent = (MouseEvent) event;

            /*The ending point of multiline*/
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (!newShapes.paths.isEmpty() && newShapes.getLastPath().getElements().isEmpty()) {
                    return;
                }
                newShapes.addNewPath((Entity) newSpatialEntity, 0);
            } else {
                addPoint(mouseEvent);
                if (newShapes.paths.isEmpty()) {
                    newShapes.addNewPath((Entity) newSpatialEntity, 0);
                }
                mainController.mapPaneController.mapa.getChildren().remove(newShapes.getLastPath());
                newShapes.addElementToLastPath(mouseEvent.getX(), mouseEvent.getY());
                mainController.mapPaneController.mapa.getChildren().add(newShapes.getLastPath());
            }
        }
    }

    /* Insert new path to list of improved paths in Shapes class, but ignore the right click
    * in order to create only 1 paths.
    * @param InputEvent event
     */
    private void addLineToPath(InputEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (((MouseEvent) event).getButton() == MouseButton.SECONDARY) {
                return;
            } else {
                addLineToPaths(event);
            }
        }
    }
}
