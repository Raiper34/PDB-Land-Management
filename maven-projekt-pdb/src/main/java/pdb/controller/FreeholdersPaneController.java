/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import pdb.model.freeholder.FreeholderModel;
import pdb.model.spatial.Estate;

/**
 * FXML Controller class
 *
 * @author gulan
 */
public class FreeholdersPaneController implements Initializable {

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
            
            TableColumn firstNameCol = new TableColumn("Estate");
            firstNameCol.setMinWidth(300);
            firstNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("info"));
            this.estatesTable.getColumns().clear();
            this.estatesTable.getColumns().addAll(firstNameCol);
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
        firstNameCol.setMinWidth(300);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("name"));
        this.freeholdersTable.getColumns().clear();
        this.freeholdersTable.getColumns().addAll(firstNameCol);
        this.freeholdersTable.setItems(freeholdersModel.getListAllFreeHolders());
    }
    
}
