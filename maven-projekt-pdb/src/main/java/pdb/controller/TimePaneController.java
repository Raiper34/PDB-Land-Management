/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pdb.model.multimedial.Photo;
import pdb.model.DatabaseModel;
import pdb.model.TimeModel;



/**
 * 
 * @author jan
 */
public class TimePaneController implements Initializable {
    
    @FXML
    public ComboBox comboBox; 
    
    @FXML
    public AnchorPane timeAnchorPane;
    
    @FXML
    private DatePicker datePicker;

    @FXML
    void datePickerOnAction(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        LocalDate date = datePicker.getValue();
        String pickedDate = formatter.format(date);
        if (pickedDate != null) {
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.mapPaneController.loadEntities(pickedDate);
            this.mainController.mapPaneController.loadEstates(pickedDate);
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
            this.mainController.selectedSpatialEntity = null;
       }
    }
    
    public MainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
                address = t1;    
                this.mainController.mapPaneController.clearMap();
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
            }    
        });*/
    }
    
    @FXML
    void comboBoxOnAction(ActionEvent event) {
       // comboBox.getSelectionModel().getSelectedItem();
       String pickedDate = (String) comboBox.getSelectionModel().getSelectedItem();
       if (pickedDate != null) {
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.mapPaneController.loadEntities(pickedDate);
            this.mainController.mapPaneController.loadEstates(pickedDate);
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
            this.mainController.selectedSpatialEntity = null;
       }
    }
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) {
        
    }
    
    // method called when the controller is focused (user clicked on apropiate menu item)
    public void resetState() {
        reloadComboBox();
    }
    
    public void reloadComboBox() {
        TimeModel timeModel = new TimeModel();
        List<String> listOfDateWhenSomethingSpatialObjectChanges = timeModel.getListOfDateWhenSomethingSpatialObjectChanges(
                this.mainController.undergroundCheckbox.isSelected(), 
                this.mainController.groundCheckbox.isSelected(), 
                this.mainController.overgroundCheckbox.isSelected());
        comboBox.getItems().clear();
        comboBox.getItems().addAll(listOfDateWhenSomethingSpatialObjectChanges);
    }

}

