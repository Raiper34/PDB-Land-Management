/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import javafx.util.Callback;
import pdb.model.freeholder.Freeholder;
import pdb.model.freeholder.FreeholderModel;

/**
 *
 * @author archie
 */
public class EntityModificationPaneController implements Initializable {
    
    public MainController mainController;
    
    @FXML
    private MainController fXMLController;
    
    @FXML
    public AnchorPane entityModificationAnchorPane;
    
    @FXML 
    private Button buttonDeleteObjInInterval;
    
    @FXML
    private ComboBox comboboxFreeholders;
    
    @FXML
    private Button buttonSave;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //drawTest();
        System.out.println("Hello from EntityModificationPane");
    }
    
    /*
    * @param MainController mainController
    */
    public void addParent(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void saveClick(ActionEvent event) throws SQLException
    {
       Freeholder test = (Freeholder) this.comboboxFreeholders.getSelectionModel().getSelectedItem();
       if(test != null)
       {
        System.out.println(test.id);
       }
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException 
    {
        //this.mainController.selectedSpatialEntity;
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED)
        {
            FreeholderModel freeholdersModel = new FreeholderModel();
            freeholdersModel.getFreeHoldersFromDatabase();
            ObservableList<Freeholder> freehodlers = freeholdersModel.getListAllFreeHolders();
            this.comboboxFreeholders.setItems(freehodlers);

            this.comboboxFreeholders.setCellFactory(new Callback<ListView<Freeholder>,ListCell<Freeholder>>(){
                 @Override
                 public ListCell<Freeholder> call(ListView<Freeholder> l){
                     return new ListCell<Freeholder>(){
                         @Override
                         protected void updateItem(Freeholder item, boolean empty) {
                             super.updateItem(item, empty);
                             if (item == null || empty) {
                                 setGraphic(null);
                             } else {
                                 setText(item.first_name + " " +item.surname);
                             }
                         }
                     } ;
                 }
             });
        }
    }

}
