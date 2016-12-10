package pdb.controller;

import pdb.model.DatabaseModel;
import pdb.model.DatabaseAccessInfo;
import pdb.controller.MainController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * FXML Controller class
 * Database settings
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
    
    @FXML
    private Label errorLabel;
    
    private DatabaseAccessInfo dbInfo;
    
    private MainController mainController;

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
        this.errorLabel.setText("");
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        String host = this.hostInput.getText();
        String port = this.portInput.getText();
        String serviceName = this.serviceNameInput.getText();
        
        DatabaseModel database = DatabaseModel.getInstance();
        if(database.isConnected())
        {
            database.disconnectDatabase();
        }
        if(database.connectDatabase(host, port, serviceName, username, password))
        {
            this.dbInfo.saveAccessInfo(username, password, host, port, serviceName); 
            this.mainController.makeModalInvisible();
            this.mainController.databaseInitialized();
        }
        else
        {
            this.errorLabel.setText("Could not connect, maybe wrong access data!");
        }
        
    }
    
    /**
     * Inject main controller to this controller
     * @param mainController 
     */
    public void injectMainController(MainController mainController)
    {
        this.mainController = mainController;
    }
    
    
}
