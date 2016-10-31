/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.multimedial;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.ord.im.OrdImage;
import oracle.jdbc.OraclePreparedStatement;
import java.sql.PreparedStatement;
import oracle.jdbc.OracleResultSet;

/**
 *
 * @author archie
 */
public class Photo {

    //The last used ID of the Photo
    public static int lastId = 0;
    public int id;

    public Photo() {
        this.id = lastId++;
    }

    public void insertPhotoFromFile(Connection connection,
            String filename) throws SQLException, IOException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            OrdImage imgProxy = null;

            // insert a new record with an empty ORDImage object
            OraclePreparedStatement pstmtInsert = (OraclePreparedStatement) connection.prepareStatement(
                    "insert into photos(id, photo) values (" + id + ", ordsys.ordimage.init())");

            pstmtInsert.executeUpdate();
            pstmtInsert.close();

            // retrieve the previously created ORDImage object for future updating
            OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) connection.prepareStatement(
                    "select photo from photos where id=" + id + " for update");
            try {
                OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
                try {
                    if (rset.next()) {
                        imgProxy = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
                    }
                } finally {
                    rset.close();
                }
            } finally {
                pstmtSelect.close();
            }
            // load the media data from a file to the ORDImage Java object
            imgProxy.loadDataFromFile(filename);
            imgProxy.setProperties();
            
            OraclePreparedStatement pstmtUpdate1 = (OraclePreparedStatement) connection.prepareStatement(
                    "update photos set photo = ? where id = " + id);
            try {
                pstmtUpdate1.setORAData(1, imgProxy);
                pstmtUpdate1.executeUpdate();
            } finally {
                pstmtUpdate1.close();
            }
            
            PreparedStatement pstmtUpdate2 = connection.prepareStatement(
                    "update photos p set p.photo_si=SI_StillImage(p.photo.getContent()) where id = " + id);
            try {
                pstmtUpdate2.executeUpdate();
            } finally {
                pstmtUpdate2.close();
            }
            
            PreparedStatement pstmtUpdate3 = connection.prepareStatement(
                    "update photos p set"+
                    " p.photo_ac=SI_AverageColor(p.photo_si),"+
                    " p.photo_ch=SI_ColorHistogram(p.photo_si),"+
                    " p.photo_pc=SI_PositionalColor(p.photo_si),"+
                    " p.photo_tx=SI_Texture(p.photo_si) where id = "+id);
            try {
                pstmtUpdate3.executeUpdate();
            } finally {
                pstmtUpdate3.close();
            }
            connection.commit();
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

}
