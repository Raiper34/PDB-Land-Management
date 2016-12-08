/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pdb.model.multimedial.Photo;
import pdb.model.DatabaseModel;
import pdb.model.freeholder.Freeholder;
import pdb.model.freeholder.FreeholderModel;
import pdb.model.time.TimeModel;
import pdb.model.time.TableViewItem;



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
    private TableColumn columnValidFrom;
    
    @FXML 
    private TableColumn columnValidTo;
    
    @FXML
    private TableView tableHistoryOfSelectedObject;
    
    @FXML
    private TableView freeholdersHistoryTable;
    
    @FXML
    private Label selectedDateBoldLabel;
    
    @FXML
    private Slider slider;
    
    private List<String> listOfDateWhenSomethingSpatialObjectChanges = new ArrayList<>();

    @FXML
    void datePickerOnAction(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        LocalDate date = datePicker.getValue();
        String pickedDate = formatter.format(date);
        if (pickedDate != null) {
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.dateOfCurrentlyShowedDatabaseSnapshot = pickedDate;
            this.mainController.mapPaneController.loadEntities(pickedDate);
            this.mainController.mapPaneController.loadEstates(pickedDate);
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
            this.mainController.selectedSpatialEntity = null;
       }
    }
    
    public MainController mainController;
    
    private TimeModel timeModel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.timeModel = new TimeModel();
        
        this.columnValidFrom.setCellValueFactory(new PropertyValueFactory<TableViewItem, String>("validFrom"));
        this.columnValidTo.setCellValueFactory(new PropertyValueFactory<TableViewItem, String>("validTo"));
        
        slider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                String selectedDate = listOfDateWhenSomethingSpatialObjectChanges.get((int) slider.getValue());
                selectedDateBoldLabel.setText(selectedDate);
                mainController.mapPaneController.clearMap();
                mainController.mapPaneController.initializeSpatialEntitiesModel();
                mainController.dateOfCurrentlyShowedDatabaseSnapshot = selectedDate;
                mainController.mapPaneController.loadEntities(selectedDate);
                mainController.mapPaneController.loadEstates(selectedDate);
                mainController.mapPaneController.drawSpatialEntities(mainController.undergroundCheckbox.isSelected(),mainController.groundCheckbox.isSelected(), mainController.overgroundCheckbox.isSelected());
                mainController.selectedSpatialEntity = null;
            }
        });
    }
        
    @FXML
    public void tableHistoryOfSelectedObjectClick(MouseEvent event)
    {
        TableViewItem tableviewItem = (TableViewItem) this.tableHistoryOfSelectedObject.getSelectionModel().getSelectedItem();
        if(tableviewItem != null)
        {
                ArrayList<PreparedStatement> sqlQueriesToGetObjectInHistotory = 
                this.timeModel.createSqlQueriesToGetObjectInHistotory
                (
                    tableviewItem.getValidFrom(),
                    tableviewItem.getValidTo(), 
                    tableviewItem.getId(),
                    tableviewItem.getSpatialEntityType()
                );
                
                this.mainController.mapPaneController.clearMap();
                this.mainController.mapPaneController.initializeSpatialEntitiesModel();
                this.mainController.dateOfCurrentlyShowedDatabaseSnapshot = tableviewItem.getValidFrom();
                this.mainController.mapPaneController.loadEntities(sqlQueriesToGetObjectInHistotory.get(0));
                this.mainController.mapPaneController.loadEstates(sqlQueriesToGetObjectInHistotory.get(1));
                this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
        }
    }
        
        /* final ObservableList<Person> data =
        FXCollections.observableArrayList(
          new Person("Jacob", "Smith", "jacob.smith@example.com"),
          new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
          new Person("Ethan", "Williams", "ethan.williams@example.com"),
          new Person("Emma", "Jones", "emma.jones@example.com"),
          new Person("Michael", "Brown", "michael.brown@example.com")
        );
        tableHistoryOfSelectedObject.setItems(data);*/
        /*comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
                address = t1;    
                this.mainController.mapPaneController.clearMap();
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
            }    
        });*/
    
    
    @FXML
    void comboBoxOnAction(ActionEvent event) {
       // comboBox.getSelectionModel().getSelectedItem();
       String pickedDate = (String) comboBox.getSelectionModel().getSelectedItem();
       if (pickedDate != null) {
            this.mainController.mapPaneController.clearMap();
            this.mainController.mapPaneController.initializeSpatialEntitiesModel();
            this.mainController.dateOfCurrentlyShowedDatabaseSnapshot = pickedDate;
            this.mainController.mapPaneController.loadEntities(pickedDate);
            this.mainController.mapPaneController.loadEstates(pickedDate);
            this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
            this.mainController.selectedSpatialEntity = null;
       }
    }
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            ObservableList<TableViewItem> data = this.timeModel.getHistoryOfObjecWithSpecifiedId(this.mainController.selectedSpatialEntity);

            tableHistoryOfSelectedObject.setItems(data);
            
            this.initFreeholdersHistory();
            
            
            
            /*this.lengthOrPerimeter.textProperty().setValue(String.format("%.2f", spatialModel.getLengthOrPerimeter(this.mainController.selectedSpatialEntity)) + "m");
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
            }*/
        }
    }
    
    public void initFreeholdersHistory() throws SQLException
    {
        FreeholderModel freeholdersModel = new FreeholderModel();
        ObservableList<Freeholder> estatesFreeholders = freeholdersModel.getEstatesFreeholdersFromDatabase(this.mainController.selectedSpatialEntity.id);
        
        TableColumn firstNameCol = new TableColumn("Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("name"));
        
        TableColumn secondNameCol = new TableColumn("From");
        secondNameCol.setMinWidth(100);
        secondNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("wasFreeholderOfEstateFrom"));
        
        TableColumn thirdNameCol = new TableColumn("To");
        thirdNameCol.setMinWidth(100);
        thirdNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("wasFreeholderOfEstateTo"));
        
        
        this.freeholdersHistoryTable.getColumns().clear();
        this.freeholdersHistoryTable.getColumns().addAll(firstNameCol);
        this.freeholdersHistoryTable.getColumns().addAll(secondNameCol);
        this.freeholdersHistoryTable.getColumns().addAll(thirdNameCol);
        this.freeholdersHistoryTable.setItems(estatesFreeholders);
    }
    
    // method called when the controller is focused (user clicked on apropiate menu item)
    public void resetState() {
        reloadComboBox();
        /*
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
            this.buttonDeleteObjInInterval.setDisable(true);
            
        }
        this.internalState = "DEFAULT";
        this.calculateDistance.textProperty().setValue("Calculate distance to other geometry");
        */
    }
    
    public void reloadComboBox() {
        listOfDateWhenSomethingSpatialObjectChanges = this.timeModel.getListOfDateWhenSomethingSpatialObjectChanges(
                this.mainController.undergroundCheckbox.isSelected(), 
                this.mainController.groundCheckbox.isSelected(), 
                this.mainController.overgroundCheckbox.isSelected());
                //

        comboBox.getItems().clear();

        comboBox.getItems().addAll(listOfDateWhenSomethingSpatialObjectChanges);

        
        slider.setMin(0);
        slider.setMax(listOfDateWhenSomethingSpatialObjectChanges.size() - 1);
        //slider.setValue(listOfDateWhenSomethingSpatialObjectChanges.size() - 1);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        //slider.setSnapToTicks(true);
        slider.setBlockIncrement(1);
        slider.setMinorTickCount(0);
        slider.setMajorTickUnit(1);

        selectedDateBoldLabel.setText(listOfDateWhenSomethingSpatialObjectChanges.get((int) slider.getValue()));

    }

}

