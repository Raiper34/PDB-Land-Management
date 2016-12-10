/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import javafx.util.Callback;
import oracle.spatial.geometry.JGeometry;
import pdb.model.SpatialEntitiesModel;
import pdb.model.entityModification.EntityModificationModel;
import pdb.model.freeholder.Freeholder;
import pdb.model.freeholder.FreeholderModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import pdb.model.SpatialEntitiesModel;

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
    private ComboBox comboboxFreeholders;
    
    @FXML
    private Button buttonSave;
    
    @FXML
    public TextArea descriptionArea;
    
    @FXML
    public TextField nameField;
    
    @FXML
    public DatePicker pickerFrom;
    
    @FXML
    public DatePicker pickerTo;
    
    @FXML
    public ToggleGroup editation;
    
    @FXML 
    public DatePicker datePickerDeleteFrom;
    
    @FXML
    public DatePicker datePickerDeleteTo;
    
    @FXML 
    private Button buttonDeleteObjInInterval;
    
    String editationMode = "Move";
    
    Point2D start = null;
    Point2D end = null;
    JGeometry originalGeometry;
    
    private EntityModificationModel entityModificationModel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //drawTest();
        //System.out.println("Hello from EntityModificationPane");
        this.entityModificationModel = new EntityModificationModel();
        editation.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton checkedButton = (RadioButton) t1.getToggleGroup().getSelectedToggle();
                editationMode = checkedButton.getText();
            }
        });
    }
    
    /*
    * @param MainController mainController
    */
    public void addParent(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void saveClick(ActionEvent event) throws SQLException
    {
       this.mainController.originalSelectedSpatialEntityGeometry = this.mainController.selectedSpatialEntity.geometry;
       SpatialEntitiesModel spatialEntitiesModel = mainController.mapPaneController.spatialEntitiesModel;
       
       spatialEntitiesModel.updateSpatialEntity(this.mainController.selectedSpatialEntity, this.mainController.selectedSpatialEntity);

       //Get model
       this.entityModificationModel.deleteObjectInInterval(this.mainController.selectedSpatialEntity, this.pickerFrom.getValue(), this.pickerTo.getValue());
       SpatialEntitiesModel entityModel = new SpatialEntitiesModel();
       
       //Set dates
       LocalDate localDate = this.pickerFrom.getValue();
       Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
       this.mainController.selectedSpatialEntity.validFrom = Date.from(instant);
       localDate = this.pickerTo.getValue();
       instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
       this.mainController.selectedSpatialEntity.validTo = Date.from(instant);
       //Set fdescription and name
       this.mainController.selectedSpatialEntity.name = this.nameField.getText();
       this.mainController.selectedSpatialEntity.description = this.descriptionArea.getText();
       
       if(this.mainController.selectedSpatialEntity instanceof Estate)
       {
           //Set freeholder
           Freeholder freeholder = (Freeholder) this.comboboxFreeholders.getSelectionModel().getSelectedItem();
           if(freeholder != null)
           {
                ((Estate)this.mainController.selectedSpatialEntity).setFreeholder(freeholder);
           }
           entityModel.saveSpatialEntityToDB((Estate)this.mainController.selectedSpatialEntity);
       }
       else
       {
           entityModel.saveSpatialEntityToDB((Entity)this.mainController.selectedSpatialEntity);
       }
       //Update
       this.updateMapWithModifications();
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException 
    {
        
        //this.mainController.selectedSpatialEntity;
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED)
        {
            this.actualizePaneContent();
        }
    }
    
    
    
    
    public void handleInputEventForMap(InputEvent t){
        switch (editationMode) {
            case "Move":
                doMove(t);
                break;
            case "Resize":
                doResize(t, editationMode);
                break;
            case "Rotate":
                doResize(t, editationMode);
                break;
        }
        
    }
    
    public void doMove(InputEvent t){
        if(t.getEventType() == MouseEvent.MOUSE_PRESSED && this.mainController.selectedSpatialEntity != null){
            if( ! pressedOnSelectedObject(t))
                return;
            start = new Point2D(((MouseEvent) t).getX(), ((MouseEvent) t).getY());
            originalGeometry = this.mainController.selectedSpatialEntity.geometry;
        } else if(t.getEventType() == MouseEvent.MOUSE_DRAGGED){
            if (start == null || this.mainController.selectedSpatialEntity == null) {
                return;
            }
            
            end = new Point2D(((MouseEvent) t).getX(), ((MouseEvent) t).getY());
            Point2D translation = end.subtract(start);
            JGeometry translated = null;
            try {
                translated = originalGeometry.affineTransforms(true, translation.getX(), translation.getY(),
                    0, false, null, 0, 0, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, null, null);
                if(! isGeometryInMap(translated)){
                    return;
                }
                this.mainController.selectedSpatialEntity.geometry = translated;
                this.mainController.mapPaneController.drawSpatialEntities();
            } catch (Exception ex) {
                Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if (t.getEventType() == MouseEvent.MOUSE_RELEASED) {
            start = null;
            originalGeometry = null;
        }
    }
    
    private void doResize(InputEvent t, String operation) {
        if(t.getEventType() == MouseEvent.MOUSE_CLICKED && this.mainController.selectedSpatialEntity != null){
            if( ! pressedOnSelectedObject(t))
                return;
            start = new Point2D(((MouseEvent) t).getX(), ((MouseEvent) t).getY());
        }
            
        if(t.getEventType() == ScrollEvent.SCROLL && this.mainController.selectedSpatialEntity != null){
            originalGeometry = this.mainController.selectedSpatialEntity.geometry;
            try {
                JGeometry point;
                if(start != null){
                    point = new JGeometry(start.getX(), start.getY(), 0);
                } else {
                    point = new JGeometry(originalGeometry.getFirstPoint()[0], originalGeometry.getFirstPoint()[1], 0);
                }
                double scale = ((ScrollEvent) t).getDeltaY() / 1000;
                JGeometry translated = null;
                
                if(operation.equals("Resize")){
                    translated = originalGeometry.affineTransforms(false, 0, 0, 0,
                        true, point, 1+scale, 1+scale, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, null, null);
                } else {
                    if(originalGeometry.isCircle())
                        return;
                    translated = originalGeometry.affineTransforms(false, 0, 0, 0, 
                        false, null, 0, 0, 0,
                        true, point, null, 1*scale, -1, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, null, null);
                }
                
                if(! isGeometryInMap(translated) || isGeometryTooSmall(translated)){
                    return;
                }
                this.mainController.selectedSpatialEntity.geometry = translated;
                this.mainController.mapPaneController.drawSpatialEntities();
            } catch (Exception ex) {
                Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
            originalGeometry = null;
        } 
    }
    
    public boolean isGeometryTooSmall(JGeometry translated){
        double x = translated.getFirstPoint()[0];
        double y = translated.getFirstPoint()[1];
        double radius = 15;
        JGeometry minimalGeom = new JGeometry(3, 0, new int[]{1, 1003, 1},
            new double[]{x-radius,y-radius, x+radius,y-radius, x+radius,y+radius, x-radius,y+radius, x-radius,y-radius}
        );
        try {
            if( translated.isInside(minimalGeom, 0, "FALSE"))
                return true;
        } catch (Exception ex) {
            Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isGeometryInMap(JGeometry translated){
        JGeometry map = new JGeometry(3, 0, new int[]{1, 1003, 1},
            new double[]{0,0, 650,0, 650,650, 0,650, 0,0}
        );
        try {
            if(translated.isInside(map, 0, "FALSE"))
                return true;
        } catch (Exception ex) {
            Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean pressedOnSelectedObject(InputEvent t) {
        JGeometry point = new JGeometry(((MouseEvent) t).getX(), ((MouseEvent) t).getY(), 0);
        try {
            if(point.anyInteract(this.mainController.selectedSpatialEntity.geometry, 10, "FALSE"))
                return true;
        } catch (Exception ex) {
            Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void resetState() throws SQLException {
        
        if (this.mainController.selectedSpatialEntity != null) {
            this.actualizePaneContent();
        }
        else {
            buttonDeleteObjInInterval.setDisable(true);
            this.pickerFrom.setValue(null);
            this.datePickerDeleteFrom.setValue(null);
            this.pickerTo.setValue(null);
            this.datePickerDeleteTo.setValue(null);
            this.comboboxFreeholders.getSelectionModel().clearSelection();
            
            this.nameField.setText("");
            this.descriptionArea.setText("");
        }
    }
    
    private void actualizePaneContent() throws SQLException  {
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
            
            //SetDefault Values
            this.nameField.setText(this.mainController.selectedSpatialEntity.name);
            this.descriptionArea.setText(this.mainController.selectedSpatialEntity.description);
            int index = 0;
            boolean found = false;
            //this.mainController.selectedSpatialEntity.toShapes(spatialEntity, editationMode)

            if(this.mainController.selectedSpatialEntity instanceof Estate) {
                int currentFreeholderId = ((Estate)this.mainController.selectedSpatialEntity).freeholder.id;
                for(Freeholder freeholder : freehodlers) {
                    if(freeholder.id == currentFreeholderId)
                     {
                        found = true;
                        break;
                     }
                    index++;
                }
                if(found)
                {
                    this.comboboxFreeholders.getSelectionModel().select(index);
                }
            }
            
            Instant instant = Instant.ofEpochMilli(this.mainController.selectedSpatialEntity.validFrom.getTime());
            this.pickerFrom.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
            this.datePickerDeleteFrom.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
            instant = Instant.ofEpochMilli(this.mainController.selectedSpatialEntity.validTo.getTime());
            this.pickerTo.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
            this.datePickerDeleteTo.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
            this.buttonDeleteObjInInterval.setDisable(false);
    }
    
    @FXML
    void buttonDeleteObjInIntervalClicked(MouseEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");        

        this.entityModificationModel.deleteObjectInInterval(this.mainController.selectedSpatialEntity, this.datePickerDeleteFrom.getValue(), this.datePickerDeleteTo.getValue());
        this.updateMapWithModifications();
    }
    
    void updateMapWithModifications()
    {
        this.mainController.mapPaneController.clearMap();
        this.mainController.mapPaneController.initializeSpatialEntitiesModel();
        this.mainController.mapPaneController.loadEntities(this.mainController.dateOfCurrentlyShowedDatabaseSnapshot);
        this.mainController.mapPaneController.loadEstates(this.mainController.dateOfCurrentlyShowedDatabaseSnapshot);
        this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected()); 
    }
}
