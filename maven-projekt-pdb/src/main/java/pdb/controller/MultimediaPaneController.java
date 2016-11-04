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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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



/**
 * 
 * @author raiper34
 */
public class MultimediaPaneController implements Initializable {
    
    @FXML
    public Button selectImageButton; 
    
    @FXML
    public Button deleteImageButton; 
    
    @FXML
    public AnchorPane imageLayout;
    
    public MainController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }
    
    @FXML
    public void selectImageClick(ActionEvent event) throws SQLException, IOException
    {
        final Stage stage = (Stage) this.selectImageButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));                 
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png")
        );
        File imageFile = fileChooser.showOpenDialog(stage);
        Photo photoModel = new Photo();
        if(imageFile != null)
        {
            int id = this.mainController.selectedSpatialEntity.id;
            photoModel.insertPhotoFromFile(imageFile.getAbsolutePath(), id);
        }
        this.setImageById(this.mainController.selectedSpatialEntity.id);
    }
    
    @FXML
    public void deleteImageClick(ActionEvent event) throws SQLException, IOException
    {
        Photo photoModel = new Photo();
        photoModel.deleteEstatePhoto(this.mainController.selectedSpatialEntity.id);
        this.setImageById(this.mainController.selectedSpatialEntity.id);
    }
    
    public void setImageById(int id) throws SQLException, IOException
    {
        this.imageLayout.getChildren().clear();
        Photo photoModel = new Photo();
        Image img = photoModel.getPhotoFromDatabase(photoModel.estatesPhotoId(id));
        if(img != null)
        {
            ImageView imgView = new ImageView(img);
            imgView.fitWidthProperty().bind(this.imageLayout.widthProperty());
            imgView.fitHeightProperty().bind(this.imageLayout.heightProperty());
            this.imageLayout.getChildren().add(imgView);
            this.deleteImageButton.setDisable(false);
        }
        else
        {
            this.deleteImageButton.setDisable(true);
        }
    }
    
    public void injectMainController(MainController mainController)
    {
        this.mainController = mainController;
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException 
    {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED)
        {
            System.out.println("MULTIMEDIA PANE");
            this.setImageById(this.mainController.selectedSpatialEntity.id);
        }
    }

}
