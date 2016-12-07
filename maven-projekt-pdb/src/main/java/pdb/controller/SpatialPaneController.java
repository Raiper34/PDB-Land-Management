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
 * 
 * @author jan
 */
public class SpatialPaneController implements Initializable {
    
    
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
    
    public MainController mainController;
    
    private String internalState = "DEFAULT";
    
    private SpatialEntity previousSelectedSpatialEntity;

    /**
     * Initializes the controller class.
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
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
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
    
    // method called when the controller is focused (user clicked on apropiate menu item)
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
    
    @FXML
    void calculateDistanceButtonClicked(MouseEvent event) {
        this.internalState = "READY TO MEASURE DISTANCE";
        this.calculateDistance.setDisable(true);
        this.calculateDistance.textProperty().setValue("Click to other geometry to calculate distance");
        this.previousSelectedSpatialEntity = this.mainController.selectedSpatialEntity;
    }
    
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

