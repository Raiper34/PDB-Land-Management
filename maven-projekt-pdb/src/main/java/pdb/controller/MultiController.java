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
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author archie
 */
public class MultiController implements Initializable {
    @FXML
    public AnchorPane multiAnchorPane;
       
    public MainController mainController;
    
    @FXML
    private MainController fXMLController;
    
    public void addParent(MainController c1) {
        this.mainController = c1;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
}
