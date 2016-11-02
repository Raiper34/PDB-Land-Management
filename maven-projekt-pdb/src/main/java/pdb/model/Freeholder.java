/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author gulan
 */
public class Freeholder 
{

    private final SimpleStringProperty name;
    public String first_name;
    public String surname;
    public String birthDate;
    
    Freeholder(String first_name, String surname, String birthDate)
    {
        this.first_name = first_name;
        this.surname = surname;
        this.birthDate = birthDate;
        name = new SimpleStringProperty(first_name + " " + surname + " (" + birthDate + ")"); 
    }
    
    public String getName() 
    {
        return name.get();
    }
    
    public void setName(String fName) 
    {
        name.set(fName);
    }
}
