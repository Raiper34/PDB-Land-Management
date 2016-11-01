/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import pdb.model.SpatialEntitiesModel;

/**
 *
 * @author archie
 */
public class AddEntityPaneController implements Initializable {
    
    @FXML
    public AnchorPane addEntityAnchorPane;
    
    @FXML 
    public ToggleGroup toggleNewObject;
       
    public MainController mainController;
    
    @FXML
    private MainController fXMLController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //drawTest();
        System.out.println("Hello from addentity");
//        toggleNewObject.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
//            @Override
//            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1){
//                System.out.println(t.getToggleGroup().getUserData().toString());
//                RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
//                System.out.println("Selected Radio Button - "+chk.getText());
//            }
//        });
    }
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    @FXML
    public void toggleNewObjectAction(ActionEvent action)
    {
      System.out.println("Toggled: " + toggleNewObject.getSelectedToggle().getUserData().toString());
    }
    
    
    public void drawTest(){
        Rectangle r = new Rectangle();
        r.setX(50);
        r.setY(50);
        r.setWidth(200);
        r.setHeight(100);
        r.setArcWidth(20);
        r.setArcHeight(20);
                 
        Rectangle r1 = new Rectangle();

        r.setX(50);
        r.setY(50);
        r.setWidth(200);
        r.setHeight(100);
        r.setArcWidth(20);
        r.setArcHeight(20);
        r.fillProperty();
        addEntityAnchorPane.getChildren().add(r);
        addEntityAnchorPane.getChildren().add(r1);
    }
    
}
