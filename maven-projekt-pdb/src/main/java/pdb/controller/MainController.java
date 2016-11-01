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
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
    private CheckBox checkBox;

    @FXML
    private Label label;

    @FXML
    private Canvas canvas;
    
    
    @FXML
    private AnchorPane mapa;

    private GraphicsContext gc;
    
    private String currentState = "DEFAULT"; // default state
    
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
        switch (this.currentState) {
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
        }
    }
    
    @FXML
    public void groundCheckboxClick(ActionEvent event)
    {
        this.mapPaneController.clearMap();
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
    }
    
    void mapClickedEventHandler(MouseEvent event) {
        
        //Get the x and y of the click and create there a new circle
        this.addEntityPaneController.addNewSpatialEntity(event);      
    }
    
    public void setCurrentState(String state) {
        this.currentState = state;
    }
    
    @FXML
    public void disconnectClick(ActionEvent event)
    {
        this.mapPaneController.clearMemoryAndMap();
        this.databaseSettingsModal.setVisible(true);

    }
}
