/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.projekt;

import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oracle.jdbc.pool.OracleDataSource;
import cz.vutbr.fit.pdb.spatial.QueryComposer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 *
 * @author jan
 */
public class NewFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
        

        /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DatabaseSettings.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Database");
        stage.setScene(new Scene(root1));
        stage.show();*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

        /*QueryComposer test = new QueryComposer();

        Photo fotka1 = new Photo();
        Photo fotka2 = new Photo();
        Photo fotka3 = new Photo();

        try {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:@//berta.fit.vutbr.cz:1526/pdb1");
            ods.setUser(System.getProperty("login"));
            ods.setPassword(System.getProperty("password"));

//            ods.setUser("XHEREC00");
//            ods.setPassword("0oxxz6gs");
            Connection conn = ods.getConnection();

            try {
                fotka1.insertPhotoFromFile(conn, "car1.jpg");
                fotka2.insertPhotoFromFile(conn, "car2.jpg");
                fotka3.insertPhotoFromFile(conn, "car3.jpg");
            } finally {
                conn.close(); // close the connection
            }

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }

        System.out.println(fotka1.id);
        System.out.println(fotka2.id);
        System.out.println(fotka3.id);*/

    }

}
