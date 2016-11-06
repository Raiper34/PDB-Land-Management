/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

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

}
