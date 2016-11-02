/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import oracle.spatial.geometry.JGeometry;
import static oracle.spatial.geometry.JGeometry.GTYPE_POINT;
import pdb.model.DatabaseModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.ImprovedPolygon;



/**
 * 
 * @author raiper34
 */
public class MainController implements Initializable {

    @FXML
    private Accordion accordion;
    
    @FXML
    private CheckBox checkBox;

    @FXML
    private Label label;

    @FXML
    private Canvas canvas;
    
    
    @FXML
    private AnchorPane mapa;

    private GraphicsContext gc;
    
    private String currentTitledPane = "DEFAULT"; // default 
    
    @FXML
    private Button wer;

    private int clickedCount = 2;
    
    @FXML
    private AnchorPane mapPane;
    
    @FXML
    public AnchorPane databaseSettingsModal;
    
    @FXML 
    public MapPaneController mapPaneController;
    
    @FXML 
    public AddEntityPaneController addEntityPaneController;
    
    @FXML 
    public EntityModificationPaneController entityModificationPaneController;
    
    @FXML 
    public DatabaseSettingsController databaseSettingsController;
    
    @FXML 
    public TimePaneController timePaneController;
    
    @FXML 
    public SpatialPaneController spatialPaneController;
    
    @FXML
    public CheckBox undergroundCheckbox;
    
    @FXML
    public CheckBox groundCheckbox;
    
    @FXML
    public CheckBox overgroundCheckbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // detect change of current titledPane 
        accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
            @Override 
            public void changed(ObservableValue<? extends TitledPane> property, final TitledPane oldPane, final TitledPane newPane) {
                if (newPane == null) return;
                switch (newPane.getText()) {
                    case "Add entity":
                        // addEntityPaneController.resetState();
                        currentTitledPane = "Add entity";
                        break;
                    case "Entity modification":
                        // entityModificationPaneController.resetState();
                        currentTitledPane = "Entity modification";
                        break;
                    case "Multimedia":
                        // multimediaPaneController.resetState();
                        currentTitledPane = "Multimedia";
                        break;
                    case "Time":
                        timePaneController.resetState();
                        currentTitledPane = "Time";
                        break;
                    case "Spatial":
                        // spatialPaneController.resetState();
                        currentTitledPane = "Spatial";
                        break;
                    case "Freeholders":
                        // freeHoldersPaneController.resetState();
                        currentTitledPane = "Freeholders";
                        break;
                } 
            }
          });
        this.injects();
    }
    
    /**
     * Inject this main controller to all his "children"
     */
    private void injects()
    {
        this.databaseSettingsController.injectMainController(this);
        this.mapPaneController.addParent(this);
        this.addEntityPaneController.addParent(this);
        this.entityModificationPaneController.addParent(this);
        this.timePaneController.addParent(this);
        this.spatialPaneController.addParent(this);
    }
    
    /**
     * Make Database settings modal invisible
     */
    public void makeModalInvisible()
    {
        this.databaseSettingsModal.setVisible(false);
    }
    
    @FXML
    public void initializeClick(ActionEvent event)
    {
        DatabaseModel db = DatabaseModel.getInstance();
        db.initializeDatabase();
    }
    
    @FXML
    public void closeClick(ActionEvent event)
    {
        final Stage stage = (Stage) this.mapPane.getScene().getWindow();
        stage.close();
    }
    
    public void databaseInitialized()
    {
        this.mapPaneController.initializeSpatialEntitiesModel();
        this.mapPaneController.loadEntities();
        this.mapPaneController.loadEstates();
        this.mapPaneController.drawSpatialEntities();
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) {
        switch (this.currentTitledPane) {
            case "DEFAULT":
                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon polygon = (ImprovedPolygon) shape;
                    if (!polygon.isEstate()) {
                        if (polygon.getEntityReference().getEntityType().equals("house")) {
                            //System.out.println("Event: " + t.getEventType() + " on house");
                        }
                    }
                    else {
                        //System.out.println("Clicked estate");
                    }
                }
                break;
            case "Add entity":
                // addEntityPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
            case "Entity modification":
                // entityModificationPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
            case "Multimedia":
                // multimediaPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
            case "Time":
                timePaneController.handleInputEventForShape(t, shape);
                break;
            case "Spatial":
                // spatialPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
            case "Freeholders":
                // freeHoldersPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
        
        }
    }
    
    @FXML
    public void groundCheckboxClick(ActionEvent event)
    {
        this.mapPaneController.clearMap();
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
        this.timePaneController.reloadComboBox();
    }
    
    public void handleInputEventForMap(InputEvent event) {
        switch (this.currentTitledPane) {
            case "DEFAULT":
                break;
            case "Add entity":
                addEntityPaneController.handleInputEventForMap(event);
                break;
            case "Entity modification":
                // entityModificationPaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Multimedia":
                // multimediaPaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Time":
                // timePaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Spatial":
                // spatialPaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Freeholders":
                // freeHoldersPaneController.handleInputEventForMap(InputEvent event);
                break;
        
        }     
    }
    
    public void setCurrentTitledPane(String state) {
        this.currentTitledPane = state;
    }
    
    @FXML
    public void disconnectClick(ActionEvent event)
    {
        this.mapPaneController.clearMemoryAndMap();
        this.databaseSettingsModal.setVisible(true);

    }
}
