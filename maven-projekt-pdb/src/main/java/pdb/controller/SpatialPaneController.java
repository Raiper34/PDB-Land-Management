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
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import pdb.model.TimeModel;

/**
 * 
 * @author jan
 */
public class SpatialPaneController implements Initializable {
    
    
    @FXML
    public AnchorPane spatialAnchorPane;
    
    public MainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) {
        
    }
    
    // method called when the controller is focused (user clicked on apropiate menu item)
    public void resetState() {
    }

}

