/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.time;


/**
 *
 * @author jan
 */
public class TableViewItem {
    private String validFrom;
    private String validTo;
    private int id;
    private String spatialEntityType; // 'estate' or 'related spatial entity'
 
    /**
     *
     * @param validFrom
     * @param validTo
     * @param id
     * @param spatialEntityType
     */
    public TableViewItem(String validFrom, String validTo, int id, String spatialEntityType) {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.id = id;
        this.spatialEntityType = spatialEntityType;
    }
 
    /**
     *
     * @return
     */
    public String getValidFrom() {
        return this.validFrom;
    }

    /**
     *
     * @param validFrom
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }
        
    /**
     *
     * @return
     */
    public String getValidTo() {
        return this.validTo;
    }

    /**
     *
     * @param validTo
     */
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
    
    /**
     *
     * @return
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *
     * @return
     */
    public String getSpatialEntityType() {
        return this.spatialEntityType;
    }

    /**
     *
     * @param spatialEntityType
     */
    public void setSpatialEntityType(String spatialEntityType) {
        this.spatialEntityType = spatialEntityType;
    }
}
