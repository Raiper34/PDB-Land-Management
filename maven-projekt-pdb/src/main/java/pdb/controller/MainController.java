/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import oracle.spatial.geometry.JGeometry;
import pdb.model.DatabaseModel;
import pdb.model.DatabaseModel;
import pdb.model.DatabaseTest;



/**
 * 
 * @author raiper34
 */
public class MainController implements Initializable {

    @FXML
    private CheckBox checkBox;

    @FXML
    private Label label;

    @FXML
    private Canvas canvas;
    
    
    @FXML
    private AnchorPane mapa;

    private GraphicsContext gc;
    
    @FXML
    private Button wer;

    private int clickedCount = 2;
    
    @FXML
    private AnchorPane mapPane;
    
    @FXML
    public AnchorPane databaseSettingsModal;
    
    @FXML 
    public MapPaneController mapPaneController;
    
    @FXML 
    public DatabaseSettingsController databaseSettingsController;

    @FXML
    void checked(ActionEvent event) throws SQLException {
        DatabaseTest databaseTest = new DatabaseTest();
        JGeometry geom = databaseTest.databaseOperation();

        if (clickedCount % 2 != 0) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            label.setText("Klikni, pro vykresleni obdelniku z databaze!");
        } else {
            if (geom.isRectangle()) {
                double x1 = geom.getOrdinatesArray()[0];
                double y1 = geom.getOrdinatesArray()[1];

                double x2 = geom.getOrdinatesArray()[2];
                double y2 = geom.getOrdinatesArray()[3];

                gc.setFill(Color.AQUA);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(10);

                //gc.fill();
                gc.strokeRect(x1, y2, x2 - x1, y2 - y1);
                gc.fillRect(x1, y2, x2 - x1, y2 - y1);
            }

            label.setText("Obdelnik z databaze vykreslen!");
        }
        clickedCount++;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.injects();
    }
    
    /**
     * Inject this main controller to all his "children"
     */
    private void injects()
    {
        this.databaseSettingsController.injectMainController(this);
        this.mapPaneController.addParent(this);
    }
    
    /**
     * Make Database settings modal invisible
     */
    public void makeModalInvisible()
    {
        this.databaseSettingsModal.setVisible(false);
    }
    
    @FXML
    public void initializeClick(ActionEvent event)
    {
        DatabaseModel db = DatabaseModel.getInstance();
        db.initializeDatabase();
    }
    
    @FXML
    public void closeClick(ActionEvent event)
    {
        final Stage stage = (Stage) this.mapPane.getScene().getWindow();
        stage.close();
    }
    
    public void databaseInitialized()
    {
        this.mapPaneController.initializeSpatialEntitiesModel();
        this.mapPaneController.loadEntities();
        this.mapPaneController.loadEstates();
        this.mapPaneController.drawSpatialEntities();
    }

}
