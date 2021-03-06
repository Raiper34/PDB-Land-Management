package pdb.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import pdb.model.time.TimeModel;

import pdb.model.freeholder.FreeholderModel;
import pdb.model.spatial.Estate;
import pdb.model.time.TableViewItem;

/**
 * FXML Controller class
 * Freeholders Panel controller
 * 
 * @author gulan
 */
public class FreeholdersPaneController implements Initializable {

    /**
     *
     */
    public MainController mainController;
    
    /**
     *
     */
    @FXML
    public TableView<Freeholder> freeholdersTable;
    
    /**
     *
     */
    @FXML
    public TableView<Estate> estatesTable;
    
    /**
     *
     */
    @FXML
    public DatePicker dateBirth;
    
    /**
     *
     */
    @FXML
    public TextField name;
    
    /**
     *
     */
    @FXML
    public TextField surname;
    
    /**
     *
     */
    public ObservableList<Freeholder> freeholders;
    
    /**
     *
     */
    @FXML 
    public AnchorPane detailPanel;
     
    /**
     *
     */
    public Freeholder selectedFreeholder;
    
    /**
     *
     */
    @FXML
    public DatePicker pickerFrom;
    
    /**
     *
     */
    @FXML
    public DatePicker pickerTo;
    
    /**
     *
     */
    @FXML
    public Label labelNumberOfFreeholdersOwnedEstate;
    
    /**
     *
     */
    @FXML
    public Label labelNumberOwnedTimes;
    
    /**
     *
     */
    @FXML
    public Label labelNumberOfEstatesOwnedByFreeholdWithSameName;
            
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.freeholders = FXCollections.observableArrayList();
        this.detailPanel.setVisible(false);
        this.selectedFreeholder = null;
    }    
    
    /**
     * Add freeholder to db, method call after button click
     * @param event
     * @throws SQLException 
     */
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
    
    /**
     * Method call after table click, it erase table of freeholders and show table of freholders estates
     * @param event
     * @throws SQLException 
     */
    @FXML
    public void tableClick(MouseEvent event) throws SQLException
    {
        Freeholder person = this.freeholdersTable.getSelectionModel().getSelectedItem();
        if(person != null)
        {
            this.selectedFreeholder = person;
            this.detailPanel.setVisible(true);
            
            TableColumn firstNameCol = new TableColumn("Id");
            firstNameCol.setMinWidth(80);
            firstNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("id"));
            
            TableColumn fourthNameCol = new TableColumn("Name");
            fourthNameCol.setMinWidth(80);
            fourthNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("name"));
            
            TableColumn secondNameCol = new TableColumn("From");
            secondNameCol.setMinWidth(80);
            secondNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("from"));
            
            TableColumn thirdNameCol = new TableColumn("To");
            thirdNameCol.setMinWidth(80);
            thirdNameCol.setCellValueFactory(
                    new PropertyValueFactory<Estate, String>("to"));
            
            this.estatesTable.getColumns().clear();
            this.estatesTable.getColumns().addAll(firstNameCol);
            this.estatesTable.getColumns().addAll(fourthNameCol);
            this.estatesTable.getColumns().addAll(secondNameCol);
            this.estatesTable.getColumns().addAll(thirdNameCol);
            this.estatesTable.setItems(person.ownedEstates());
        }
    }
    
    /**
     * Go to default page in this panel
     * @param event 
     */
    @FXML
    public void backClick(ActionEvent event)
    {
        this.detailPanel.setVisible(false);
    }
    
    /**
     * Init list of freeholders, it fill table with freehodlders from database
     * @throws SQLException 
     */
    public void initList() throws SQLException
    {
        FreeholderModel freeholdersModel = new FreeholderModel();
        freeholdersModel.getFreeHoldersFromDatabase();
        
        TableColumn firstNameCol = new TableColumn("Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("name"));
        
        TableColumn secondNameCol = new TableColumn("BirthDate");
        secondNameCol.setMinWidth(200);
        secondNameCol.setCellValueFactory(
                new PropertyValueFactory<Freeholder, String>("birthDateP"));
        
        this.freeholdersTable.getColumns().clear();
        this.freeholdersTable.getColumns().addAll(firstNameCol);
        this.freeholdersTable.getColumns().addAll(secondNameCol);
        this.freeholdersTable.setItems(freeholdersModel.getListAllFreeHolders());
    }
    
    /**
     * Show freeholders estate from freeholders estates history on map
     * @param event 
     */
    @FXML
    public void tableHistoryOfEstatesForFreeholderClick(MouseEvent event)
    {
        Estate estate = (Estate) this.estatesTable.getSelectionModel().getSelectedItem();
        if(estate != null)
        {
                SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy");
                TimeModel timeModel = new TimeModel();
                ArrayList<PreparedStatement> sqlQueriesToGetObjectInHistotory = 
                timeModel.createSqlQueriesToGetObjectInHistotory
                (
                    sdf.format(estate.validFrom),
                    sdf.format(estate.validTo),
                    estate.id,
                    "estate"
                );
                
                this.mainController.mapPaneController.clearMap();
                this.mainController.mapPaneController.initializeSpatialEntitiesModel();
                this.mainController.dateOfCurrentlyShowedDatabaseSnapshot = sdf.format(estate.validFrom);
                this.mainController.mapPaneController.loadEntities(sqlQueriesToGetObjectInHistotory.get(0));
                this.mainController.mapPaneController.loadEstates(sqlQueriesToGetObjectInHistotory.get(1));
                this.mainController.mapPaneController.drawSpatialEntities(this.mainController.undergroundCheckbox.isSelected(), this.mainController.groundCheckbox.isSelected(), this.mainController.overgroundCheckbox.isSelected());
        }
    }
    
    /**
     * Inject main controller, we can call main controler methods then, eg. redraw map etc..
     * @param mainController 
     */
    public void injectMainController(MainController mainController)
    {
        this.mainController = mainController;
    }
    
    /**
     * Calculate stats of freeholder, number of owned estates...
     */
    @FXML
    public void calculateStats(){
        System.out.println("Calculating the stats");
        
        if(this.pickerFrom.getValue() != null &&  this.pickerTo.getValue() != null){
            LocalDate localDate = this.pickerFrom.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date pickerFrom = Date.from(instant);

            localDate = this.pickerTo.getValue();
            instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date pickerTo = Date.from(instant);
            
            FreeholderModel freeholderModel = new FreeholderModel();
            int numberOfEstatesOwnedByFreeholdWithSameName = -1;
            numberOfEstatesOwnedByFreeholdWithSameName = freeholderModel.getNumberOfOwnedEstatesOfFreeholdersWithSameFirstName(this.selectedFreeholder, pickerFrom, pickerTo);
            labelNumberOfEstatesOwnedByFreeholdWithSameName.setText("Count of Estates owned by Freeholders with same First Name: " + numberOfEstatesOwnedByFreeholdWithSameName);
            
            int numberOfFreeholdersOwnedEstates = -1;
            int numberOwnedTimes = -1;
            if(this.mainController.selectedSpatialEntity != null &&
                    this.mainController.selectedSpatialEntity instanceof Estate){
                numberOwnedTimes = freeholderModel.getNumberOfOwnedTimesEstateBy(this.selectedFreeholder, (Estate) this.mainController.selectedSpatialEntity ,pickerFrom, pickerTo);
                numberOfFreeholdersOwnedEstates = freeholderModel.getNumberOfFreeholdersOwnedEstateInInterval((Estate) this.mainController.selectedSpatialEntity, pickerFrom, pickerTo);            

                labelNumberOfFreeholdersOwnedEstate.setText("Count of Freeholders owned selected estate: " + numberOfFreeholdersOwnedEstates);
                labelNumberOwnedTimes.setText("Count of times the freeholder owned the selected estate: " + numberOwnedTimes);
            }
            else {
                labelNumberOfFreeholdersOwnedEstate.setText("Please select an estate on a map and click calculate.");
                labelNumberOwnedTimes.setText("Please select an estate on a map and click calculate.");
            }
        } else {
            labelNumberOwnedTimes.setText("You need to pick an interval!");
            labelNumberOfFreeholdersOwnedEstate.setText("You need to pick an interval!");
        }      

        //select
        //zobraz
        
    }
    
    
}
