/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model;

import java.awt.Shape;
import java.lang.Exception;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author jan
 */
public class DatabaseTest {

    public JGeometry databaseOperation() throws SQLException {

        JGeometry jgeom = null;

        try {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:@//berta.fit.vutbr.cz:1526/pdb1");
            /*ods.setUser(System.getProperty("login"));
                        ods.setPassword(System.getProperty("password"));*/
            ods.setUser("XHEREC00");
            ods.setPassword("0oxxz6gs");
            Connection conn = ods.getConnection();

            try {
                //JGeometry jgeom = null;
                Statement stmt = conn.createStatement();
                try {

                    // reading a geometry from database
                    ResultSet rset = stmt.executeQuery(
                            "select geometrie from parcela where cislo = 2");
                    try {
                        if (rset.next()) {
                            // convert the Struct into a JGeometry
                            Struct obj = (Struct) rset.getObject(1);
                            jgeom = JGeometry.loadJS(obj);
                        }
                    } finally {
                        rset.close();
                    }
                } finally {
                    stmt.close();
                }

                // manipulate the geometry via JGeometry
                //System.out.println(jgeom.toStringFull());
                // writing a geometry back to database
                PreparedStatement pstmt = conn.prepareStatement(
                        "update parcela set geometrie=? where cislo = 2");
                try {
                    // convert the JGeometry instance to a Struct
                    Struct obj = JGeometry.storeJS(conn, jgeom);
                    pstmt.setObject(1, obj);
                    pstmt.executeUpdate();
                } finally {
                    pstmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }

        return jgeom;
    }
}
