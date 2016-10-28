/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.projekt;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * FXML Controller class
 *
 * @author gulan
 */
public class DatabaseSettingsController implements Initializable {
    
    @FXML
    private TextField usernameInput;
    
    @FXML
    private PasswordField passwordInput; 
            
    @FXML
    private TextField hostInput;
            
    @FXML
    private TextField portInput;
            
    @FXML
    private TextField serviceNameInput;
    
    private DatabaseAccessInfo dbInfo;
    
    private FXMLController mainController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.dbInfo = new DatabaseAccessInfo();
        this.dbInfo.loadAccessInfoToAttributes();
        this.usernameInput.setText(this.dbInfo.username);
        this.passwordInput.setText(this.dbInfo.password);
        this.hostInput.setText(this.dbInfo.host);
        this.portInput.setText(this.dbInfo.port);
        this.serviceNameInput.setText(this.dbInfo.serviceName);
    }

    /**
     * Event that fires after connect button click
     * @param event 
     */
    @FXML
    private void connectButtonClick(ActionEvent event)
    {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        String host = this.hostInput.getText();
        String port = this.portInput.getText();
        String serviceName = this.serviceNameInput.getText();
        String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
        
        DatabaseModel database = DatabaseModel.getInstance();
        database.connectDatabase(url, username, password);
        
        this.dbInfo.saveAccessInfo(username, password, host, port, serviceName); 
        this.mainController.makeModalInvisible();
    }
    
    /**
     * Inject main controller to this controller
     * @param mainController 
     */
    public void injectMainController(FXMLController mainController)
    {
        this.mainController = mainController;
    }
    
    
}
