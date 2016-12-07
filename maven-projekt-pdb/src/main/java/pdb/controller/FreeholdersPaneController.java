/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import pdb.model.freeholder.Freeholder;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import pdb.model.time.TimeModel;

import pdb.model.freeholder.FreeholderModel;
import pdb.model.spatial.Estate;
import pdb.model.time.TableViewItem;

/**
 * FXML Controller class
 *
 * @author gulan
 */
public class FreeholdersPaneController implements Initializable {

    public MainController mainController;
    
    @FXML
    public TableView<Freeholder> freeholdersTable;
    
    @FXML
    public TableView<Estate> estatesTable;
    
    @FXML
    public DatePicker dateBirth;
    
    @FXML
    public TextField name;
    
    @FXML
    public TextField surname;
    
    public ObservableList<Freeholder> freeholders;
    
    @FXML 
    public AnchorPane detailPanel;
     
    public Freeholder selectedFreeholder;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.freeholders = FXCollections.observableArrayList();
        this.detailPanel.setVisible(false);
        this.selectedFreeholder = null;
    }    
    
    @FXML
    public void addFreeholderClick(ActionEvent event) throws SQLException
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = this.dateBirth.getValue();
        String pickedDate = formatter.format(date);
        FreeholderModel freeholdersModel = new FreeholderModel();
        freeholdersModel.createNewFreeholder(this.name.getText(), this.surname.getText(), pickedDate);
        
        freeholdersModel.getFreeHoldersFromDatabase();
        this.freeholdersTable.setItems(freeholdersModel.getListAllFreeHolders());
    }
    
    @FXML
    public void tableClick(MouseEvent event) throws SQLException
    {
        Freeholder person = this.freeholdersTable.getSelectionModel().getSelectedItem();
        if(person != null)
        {
            this.selectedFreeholder = person;
            this.detailPanel.setVisible(true);
            
            TableColumn firstNameCol = new TableColumn("Id");
            firstNameCol.setMinWidth(80);
            firstNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("id"));
            
            TableColumn fourthNameCol = new TableColumn("Name");
            fourthNameCol.setMinWidth(80);
            fourthNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("name"));
            
            TableColumn secondNameCol = new TableColumn("From");
            secondNameCol.setMinWidth(80);
            secondNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("from"));
            
            TableColumn thirdNameCol = new TableColumn("To");
            thirdNameCol.setMinWidth(80);
            thirdNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("to"));
            
            this.estatesTable.getColumns().clear();
            this.estatesTable.getColumns().addAll(firstNameCol);
            this.estatesTable.getColumns().addAll(fourthNameCol);
            this.estatesTable.getColumns().addAll(secondNameCol);
            this.estatesTable.getColumns().addAll(thirdNameCol);
            this.estatesTable.setItems(person.ownedEstates());
        }
    }
    
    @FXML
    public void backClick(ActionEvent event)
    {
        this.detailPanel.setVisible(false);
    }
    
    public void initList() throws SQLException
    {
        FreeholderModel freeholdersModel = new FreeholderModel();
        freeholdersModel.getFreeHoldersFromDatabase();
        
        TableColumn firstNameCol = new TableColumn("Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("name"));
        
        TableColumn secondNameCol = new TableColumn("BirthDate");
        secondNameCol.setMinWidth(200);
        secondNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("birthDateP"));
        
        this.freeholdersTable.getColumns().clear();
        this.freeholdersTable.getColumns().addAll(firstNameCol);
        this.freeholdersTable.getColumns().addAll(secondNameCol);
        this.freeholdersTable.setItems(freeholdersModel.getListAllFreeHolders());
    }
    
    @FXML
    public void tableHistoryOfEstatesForFreeholderClick(MouseEvent event)
    {
        Estate estate = (Estate) this.estatesTable.getSelectionModel().getSelectedItem();
        if(estate != null)
        {
                SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy");
                TimeModel timeModel = new TimeModel();
                ArrayList<PreparedStatement> sqlQueriesToGetObjectInHistotory = 
                timeModel.createSqlQueriesToGetObjectInHistotory
                (
                    sdf.format(estate.validFrom),
                    sdf.format(estate.validTo),
                    estate.id,
                    "estate"
                );
                
                this.mainController.mapPaneController.clearMap();
                this.mainController.mapPaneController.initializeSpatialEntitiesModel();
                this.mainController.dateOfCurrentlyShowedDatabaseSnapshot = sdf.format(estate.validFrom);
                this.mainController.mapPaneController.loadEntities(sqlQueriesToGetObjectInHistotory.get(0));
                this.mainController.mapPaneController.loadEstates(sqlQueriesToGetObjectInHistotory.get(1));
                this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
        }
    }
    
    public void injectMainController(MainController mainController)
    {
        this.mainController = mainController;
    }
    
    
}
