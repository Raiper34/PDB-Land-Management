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
 
    public TableViewItem(String validFrom, String validTo, int id) {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.id = id;
    }
 
    public String getValidFrom() {
        return this.validFrom;
    }
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }
        
    public String getValidTo() {
        return this.validTo;
    }
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
    
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
