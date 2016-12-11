/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import pdb.model.time.TimeModel;
import pdb.model.spatial.SpatialEntity;
import pdb.model.spatial.SpatialModel;

/**
 * FXML Controller class
 * Spatial Pane Controller
 * @author jan
 */
public class SpatialPaneController implements Initializable {
    
    /**
     *
     */
    @FXML
    public AnchorPane spatialAnchorPane;
    
    @FXML
    private Label lengthOrPerimeter;
    
    @FXML
    private Label area;
    
    // -------------
    
    @FXML
    private Button calculateDistance;
    
    @FXML
    private Label labelDistance;
    
    // -------------
    
    @FXML
    private TextField distanceTextField;
    
    @FXML
    private Button buttonShowObjectsToCertainDistance;
    
    @FXML
    private ChoiceBox choiceBoxShowObjectsToCertainDistance;
    
    // -------------
    
    @FXML
    private ChoiceBox choiceBoxShowEstatesWhich;
    
    @FXML
    private Button buttonShowEstatesWhich;
    
    @FXML
    private CheckBox checkBoxWaterConnection;
    
    @FXML
    private CheckBox checkBoxConnectionToElectricity;
    
    @FXML
    private CheckBox checkBoxConnectionToGas;
    
    @FXML
    private CheckBox checkBoxHouse;
    
    @FXML 
    private CheckBox checkBoxWaterArea;
    
    // -----
    
    @FXML
    private Button buttonShowEstatesOverWhich;
    
    @FXML
    private ChoiceBox choiceBoxShowEstatesOverWhich;
    
    @FXML
    private CheckBox checkBoxWaterPipe;
    
    @FXML
    private CheckBox checkBoxPowerLine;
    
    @FXML
    private CheckBox checkBoxGasPipe;
    
    @FXML
    private CheckBox checkBoxPath;
    
    private SpatialModel spatialModel;
    
    /**
     * MainController instance
     */
    public MainController mainController;
    
    private String internalState = "DEFAULT";
    
    private SpatialEntity previousSelectedSpatialEntity;

    /**
     * Initializes the controller class.
     * @param url url
     * @param rb resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.spatialModel = new SpatialModel();
        choiceBoxShowObjectsToCertainDistance.setValue("all");
        choiceBoxShowObjectsToCertainDistance.setItems(FXCollections.observableList(Arrays.asList("all", "entities", "estates")));
        
        choiceBoxShowEstatesWhich.setValue("contain");
        choiceBoxShowEstatesWhich.setItems(FXCollections.observableList(Arrays.asList("contain", "not contain")));
        
        choiceBoxShowEstatesOverWhich.setValue("passes through");
        choiceBoxShowEstatesOverWhich.setItems(FXCollections.observableList(Arrays.asList("passes through", "not passes through")));
    }
    
    /**
     * Save instance of Parent controller MainController 
     * @param c1 MainController instance
     */
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    /**
     * Handling input events (which come from MapPaneController and which are redirected here) for shape, especially mouseclick event
     * @param t input event
     * @param shape shape on which event begin
     */
    public void handleInputEventForShape(InputEvent t, Shape shape) {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            this.lengthOrPerimeter.textProperty().setValue(String.format("%.2f", spatialModel.getLengthOrPerimeter(this.mainController.selectedSpatialEntity)) + "m");
            this.area.textProperty().setValue(String.format("%.2f", spatialModel.getArea(this.mainController.selectedSpatialEntity)) + "\u33A1");
            
            switch(this.internalState) {
                case "DEFAULT":
                    this.calculateDistance.setDisable(false);
                    this.buttonShowObjectsToCertainDistance.setDisable(false);
                    break;
                case "READY TO MEASURE DISTANCE":
                    double distance = this.spatialModel.getDistance(this.previousSelectedSpatialEntity, this.mainController.selectedSpatialEntity);
                    this.labelDistance.textProperty().setValue(String.format("%.2f", distance) + "m");
                    this.internalState = "DEFAULT";
                    this.calculateDistance.textProperty().setValue("Calculate distance to other geometry");
                    this.calculateDistance.setDisable(false);
                    break;
            }
        }
    }
    

    /**
     * Method called when the controller is focused (user clicked on apropiate menu item)
     * Usually ensures reinitialization of controller
     */
    public void resetState() {
        if (this.mainController.selectedSpatialEntity != null) {
            this.lengthOrPerimeter.textProperty().setValue(String.format("%.2f", spatialModel.getLengthOrPerimeter(this.mainController.selectedSpatialEntity)) + "m");
            this.area.textProperty().setValue(String.format("%.2f", spatialModel.getArea(this.mainController.selectedSpatialEntity)) + "\u33A1");
            this.calculateDistance.setDisable(false);
            this.buttonShowObjectsToCertainDistance.setDisable(false);
        }
        else {
            this.labelDistance.textProperty().setValue(""); // reset distance information to empty string
            this.lengthOrPerimeter.textProperty().setValue("");
            this.area.textProperty().setValue("");
            this.calculateDistance.setDisable(true);
            this.buttonShowObjectsToCertainDistance.setDisable(true);
            
        }
        this.internalState = "DEFAULT";
        this.calculateDistance.textProperty().setValue("Calculate distance to other geometry");
    }
    
    /**
     * This method is called when user click on button to calculate distance to other geometry
     * @param event mouse event
     */
    @FXML
    void calculateDistanceButtonClicked(MouseEvent event) {
        this.internalState = "READY TO MEASURE DISTANCE";
        this.calculateDistance.setDisable(true);
        this.calculateDistance.textProperty().setValue("Click to other geometry to calculate distance");
        this.previousSelectedSpatialEntity = this.mainController.selectedSpatialEntity;
    }
    
    /**
     * This method is called when user click on button to show obejct which are located in specified distance from selected object
     * @param event mouse event
     */
    @FXML
    void showDistanceButtonClicked(MouseEvent event) {
        try {
            double distance = Double.parseDouble(distanceTextField.textProperty().getValue());

            ArrayList<PreparedStatement> sqlQueriesToGetObjectsInSpecifiedDistance = 
                this.spatialModel.createSqlQueriesToGetObjectsInSpecifiedDistance
                (
                    this.mainController.selectedSpatialEntity,
                    choiceBoxShowObjectsToCertainDistance.getValue().toString(), 
                    distance,
                    this.mainController.dateOfCurrentlyShowedDatabaseSnapshot
                );
            
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.mapPaneController.loadEntities(sqlQueriesToGetObjectsInSpecifiedDistance.get(0));
            this.mainController.mapPaneController.loadEstates(sqlQueriesToGetObjectsInSpecifiedDistance.get(1));
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());

        }
        catch(NumberFormatException e) {
            System.err.println("NumberFormatException: " + e.getMessage());
        }
    }
    
    /**
     * This method is called when user click on button to filter estates which contains or not contains specified objects
     * @param event mouse event
     */
    @FXML
    void buttonShowEstatesWhichClicked(MouseEvent event) {
        
        PreparedStatement sqlQueryToGetEstatesWhichContainOrNotContainSpecifiedObjects = 
            this.spatialModel.createSqlQueryToGetEstatesWhichContainOrNotContainSpecifiedObjects(
                this.checkBoxWaterConnection.isSelected(),
                this.checkBoxConnectionToElectricity.isSelected(),
                this.checkBoxConnectionToGas.isSelected(),
                this.checkBoxHouse.isSelected(),
                this.checkBoxWaterArea.isSelected(),
                choiceBoxShowEstatesWhich.getValue().toString(), 
                this.mainController.dateOfCurrentlyShowedDatabaseSnapshot
            );
            
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.mapPaneController.loadEntities(this.mainController.dateOfCurrentlyShowedDatabaseSnapshot);
            this.mainController.mapPaneController.loadEstates(sqlQueryToGetEstatesWhichContainOrNotContainSpecifiedObjects);
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());

    }
    
    /**
     * This method is called when user click on button to filter estates throught which passes specified objects
     * @param event mouse event
     */
    @FXML
    void buttonShowEstatesOverWhichClicked(MouseEvent event) {
        
        PreparedStatement sqlQueryToGetEstatesOverWhichPassesOrNotPassesThroughSpecifiedObjects = 
            this.spatialModel.createSqlQueryToGetEstatesOverWhichPassesOrNotPassesThroughSpecifiedObjects(                
                this.checkBoxWaterPipe.isSelected(),
                this.checkBoxPowerLine.isSelected(),
                this.checkBoxGasPipe.isSelected(),
                this.checkBoxPath.isSelected(),
                choiceBoxShowEstatesOverWhich.getValue().toString(), 
                this.mainController.dateOfCurrentlyShowedDatabaseSnapshot
            );
            
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.mapPaneController.loadEntities(this.mainController.dateOfCurrentlyShowedDatabaseSnapshot);
            this.mainController.mapPaneController.loadEstates(sqlQueryToGetEstatesOverWhichPassesOrNotPassesThroughSpecifiedObjects);
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());

    }
    
    
 //\u33A1
}

