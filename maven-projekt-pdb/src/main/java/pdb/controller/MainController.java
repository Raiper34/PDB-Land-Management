/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import pdb.model.spatial.Estate;
import pdb.model.spatial.ImprovedCircle;
import pdb.model.spatial.ImprovedPath;
import pdb.model.spatial.ImprovedPolygon;
import pdb.model.spatial.SpatialEntity;



/**
 * 
 * @author gulan
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
    
    // showed object on map in this date, Date si in format "dd. MM. yyyy"

    /**
     *
     */
    public String dateOfCurrentlyShowedDatabaseSnapshot;
    
    @FXML
    private AnchorPane mapa;

    private GraphicsContext gc;
    
    private String currentTitledPane = "DEFAULT"; // default 

    private int clickedCount = 2;
    
    @FXML
    private AnchorPane mapPane;
    
    /**
     *
     */
    @FXML
    public AnchorPane databaseSettingsModal;
    
    /**
     *
     */
    @FXML 
    public MapPaneController mapPaneController;
    
    /**
     *
     */
    @FXML 
    public AddEntityPaneController addEntityPaneController;
    
    /**
     *
     */
    @FXML 
    public EntityModificationPaneController entityModificationPaneController;
    
    /**
     *
     */
    @FXML 
    public DatabaseSettingsController databaseSettingsController;
    
    /**
     *
     */
    @FXML 
    public FreeholdersPaneController freeholdersPaneController;
    
    /**
     *
     */
    @FXML 
    public TimePaneController timePaneController;
    
    /**
     *
     */
    @FXML 
    public SpatialPaneController spatialPaneController;
    
    /**
     *
     */
    @FXML
    public CheckBox undergroundCheckbox;
    
    /**
     *
     */
    @FXML
    public CheckBox groundCheckbox;
    
    /**
     *
     */
    @FXML
    public CheckBox overgroundCheckbox;
    
    /**
     *
     */
    @FXML
    public MultimediaPaneController multimediaPaneController;

    /**
     *
     */
    public SpatialEntity selectedSpatialEntity = null; 

    /**
     *
     */
    public JGeometry originalSelectedSpatialEntityGeometry = null; 

    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
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
                    {
                        try {
                            entityModificationPaneController.resetState();
                        } catch (SQLException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
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
                        spatialPaneController.resetState();
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
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        LocalDate date = LocalDate.now();
        this.dateOfCurrentlyShowedDatabaseSnapshot = formatter.format(date);
    }
    
    /**
     * Inject this main controller to all his "children"
     */
    private void injects()
    {
        this.databaseSettingsController.injectMainController(this);
        this.multimediaPaneController.injectMainController(this);
        this.mapPaneController.addParent(this);
        this.addEntityPaneController.addParent(this);
        this.entityModificationPaneController.addParent(this);
        this.timePaneController.addParent(this);
        this.spatialPaneController.addParent(this);
        this.freeholdersPaneController.injectMainController(this);
    }
    
    /**
     * Make Database settings modal invisible
     */
    public void makeModalInvisible()
    {
        this.databaseSettingsModal.setVisible(false);
    }
    
    /**
     *
     * @param event
     */
    @FXML
    public void initializeClick(ActionEvent event)
    {
        DatabaseModel db = DatabaseModel.getInstance();
        db.initializeDatabase();
        this.mapPaneController.clearMemoryAndMap();
        this.databaseInitialized();
    }
    
    /**
     *
     * @param event
     */
    @FXML
    public void closeClick(ActionEvent event)
    {
        final Stage stage = (Stage) this.mapPane.getScene().getWindow();
        stage.close();
    }
    
    /**
     *
     */
    public void databaseInitialized()
    {
        this.mapPaneController.initializeSpatialEntitiesModel();
        this.mapPaneController.loadEntities();
        this.mapPaneController.loadEstates();
        this.mapPaneController.drawSpatialEntities();
        try {
            this.freeholdersPaneController.initList();
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     * @param t
     * @param shape
     * @throws SQLException
     * @throws IOException
     */
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (shape instanceof ImprovedPolygon) {
                ImprovedPolygon improvedShape = (ImprovedPolygon) shape;
                if (improvedShape.isEstate()) {
                    this.multimediaPaneController.unlock();
                    setSelectedSpatialEntity((SpatialEntity) improvedShape.getEstateReference() );
                }
                else {
                    this.multimediaPaneController.lock();
                    setSelectedSpatialEntity((SpatialEntity) improvedShape.getEntityReference() );
                }
            } else if ( shape instanceof ImprovedCircle) {
                this.multimediaPaneController.lock();
                ImprovedCircle improvedShape = (ImprovedCircle) shape;
                setSelectedSpatialEntity((SpatialEntity) improvedShape.getEntityReference() );
            } else if ( shape instanceof ImprovedPath) {
                this.multimediaPaneController.lock();
                ImprovedPath improvedShape = (ImprovedPath) shape;
                setSelectedSpatialEntity((SpatialEntity) improvedShape.getEntityReference() );
            }
            else
            {
                this.multimediaPaneController.lock();
            }
        }
        
        //setSelectedSpatialEntity();
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
                entityModificationPaneController.handleInputEventForShape(t, shape);
                break;
            case "Multimedia":
                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon improvedShape = (ImprovedPolygon) shape;
                    if (improvedShape.isEstate()) {
                        multimediaPaneController.handleInputEventForShape(t, shape);
                    }
                }
                break;
            case "Time":
                timePaneController.handleInputEventForShape(t, shape);
                break;
            case "Spatial":
                spatialPaneController.handleInputEventForShape(t, shape);
                break;
            case "Freeholders":
                // freeHoldersPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
        
        }
    }
    
    /**
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    public void groundCheckboxClick(ActionEvent event) throws SQLException
    {
        setSelectedSpatialEntity(null);
        this.mapPaneController.clearMap();
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
        this.timePaneController.reloadComboBox();
        this.spatialPaneController.resetState(); // must be after this.selectedSpatialEntity = null
        this.entityModificationPaneController.resetState();
    }
    
    /**
     *
     * @param event
     */
    public void handleInputEventForMap(InputEvent event) {
        switch (this.currentTitledPane) {
            case "DEFAULT":
                break;
            case "Add entity":
                addEntityPaneController.handleInputEventForMap(event);
                break;
            case "Entity modification":
                entityModificationPaneController.handleInputEventForMap(event);
                break;
            case "Multimedia":
                //multimediaPaneController.handleInputEventForMap(InputEvent event);
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
    
    /**
     *
     * @param state
     */
    public void setCurrentTitledPane(String state) {
        this.currentTitledPane = state;
    }
    
    /**
     *
     * @param event
     */
    @FXML
    public void disconnectClick(ActionEvent event)
    {
        this.mapPaneController.clearMemoryAndMap();
        this.databaseSettingsModal.setVisible(true);

    }
    
    /**
     *
     * @param event
     */
    @FXML
    public void showAllObjectsInSelectedTimeContext(ActionEvent event) {
        this.mapPaneController.clearMap();
        this.mapPaneController.initializeSpatialEntitiesModel();
        this.mapPaneController.loadEntities(this.dateOfCurrentlyShowedDatabaseSnapshot);
        this.mapPaneController.loadEstates(this.dateOfCurrentlyShowedDatabaseSnapshot);
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
    }
    
    /**
     *
     * @param spatialEntity
     */
    public void setSelectedSpatialEntity(SpatialEntity spatialEntity) {
        
        if(selectedSpatialEntity != null && originalSelectedSpatialEntityGeometry != null){
            if(spatialEntity != null && spatialEntity.id == selectedSpatialEntity.id)
                return;
            selectedSpatialEntity.geometry = originalSelectedSpatialEntityGeometry;
        }
        if(spatialEntity != null)
            originalSelectedSpatialEntityGeometry = (JGeometry) spatialEntity.geometry.clone();
        else
            originalSelectedSpatialEntityGeometry = null;
        selectedSpatialEntity = spatialEntity;
    }
}
