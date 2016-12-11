/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.time;


/**
 * Class represents table view item - record in table of history of selected object in Time Pane
 * @author jan
 */
public class TableViewItem {
    private String validFrom;
    private String validTo;
    private int id;
    private String spatialEntityType; // 'estate' or 'related spatial entity'
 
    /**
     * Constructor initialize internal attributes
     * @param validFrom valid_from attribute in db
     * @param validTo valid_to attribute in db
     * @param id id attribute in db
     * @param spatialEntityType possible values: "related spatial entity" or "estate"
     */
    public TableViewItem(String validFrom, String validTo, int id, String spatialEntityType) {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.id = id;
        this.spatialEntityType = spatialEntityType;
    }
 
    /**
     * Return validFrom attribute 
     * @return validFrom attribute 
     */
    public String getValidFrom() {
        return this.validFrom;
    }

    /**
     * Set validFrom attribute 
     * @param validFrom
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }
        
    /**
     * Return validTo attribute 
     * @return validTo attribute 
     */
    public String getValidTo() {
        return this.validTo;
    }

    /**
     * Set validTo attribute 
     * @param validTo
     */
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
    
    /**
     * Return id attribute
     * @return id attribute 
     */
    public int getId() {
        return this.id;
    }

    /**
     * Set id attribute 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Return spatialEntityType attribute 
     * @return spatialEntityType attribute 
     */
    public String getSpatialEntityType() {
        return this.spatialEntityType;
    }

    /**
     * Set spatialEntityType attribute 
     * @param spatialEntityType
     */
    public void setSpatialEntityType(String spatialEntityType) {
        this.spatialEntityType = spatialEntityType;
    }
}
