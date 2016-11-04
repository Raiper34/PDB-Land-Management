/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import pdb.model.TimeModel;
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
    
    private SpatialModel spatialModel;
    
    public MainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.spatialModel = new SpatialModel();
    }
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            lengthOrPerimeter.textProperty().setValue(String.format("%.2f", spatialModel.getLengthOrPerimeter(this.mainController.selectedSpatialEntity)) + "m");
            area.textProperty().setValue(String.format("%.2f", spatialModel.getArea(this.mainController.selectedSpatialEntity)) + "\u33A1");
        }
    }
    
    // method called when the controller is focused (user clicked on apropiate menu item)
    public void resetState() {
        if (this.mainController.selectedSpatialEntity != null) {
            lengthOrPerimeter.textProperty().setValue(String.format("%.2f", spatialModel.getLengthOrPerimeter(this.mainController.selectedSpatialEntity)) + "m");
            area.textProperty().setValue(String.format("%.2f", spatialModel.getArea(this.mainController.selectedSpatialEntity)) + "\u33A1");
        }
    }
 //\u33A1
}

