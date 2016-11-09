/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import pdb.model.TimeModel;
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
    private Button calculateDistance;

    @FXML
    private Label area;
    
    @FXML
    private Label labelDistance;
    
    @FXML
    private TextField distanceTextField;
    
    @FXML
    private Button buttonShowObjectsToCertainDistance;
    
    @FXML
    private ChoiceBox choiceBoxShowObjectsToCertainDistance;
    
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

            ArrayList<String> sqlQueriesToGetObjectsInSpecifiedDistance = this.spatialModel.createSqlQueriesToGetObjectsInSpecifiedDistance(
                    this.mainController.selectedSpatialEntity,
                    choiceBoxShowObjectsToCertainDistance.getValue().toString(), 
                    distance);
        }
        catch(NumberFormatException e) {
            System.err.println("NumberFormatException: " + e.getMessage());
        }
    }
 //\u33A1
}

