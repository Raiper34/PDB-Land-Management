/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.spatial;

/**
 * SDO_GTYPE is DL0T 
 * @author raiper34
 */
public class SdoGtype {
            
    //D
    public static final int ONE_DIMENSION = 2;
    public static final int TWO_DIMENSION = 3;
    public static final int THREE_DIMENSION = 4;
    
    //L
    public static final int LRS0 = 0;
    public static final int LRS3 = 3;
    public static final int LRS4 = 4;
    
    //T
    public static final int UNKOWN_GEOMETRY = 0;
    public static final int POINT = 1;
    public static final int LINESTRING = 2;
    public static final int POLYGON = 3;
    public static final int COLLECTION = 4;
    public static final int MULTIPOINT = 5;
    public static final int MULTILINESTRING = 6;
    public static final int MULTIPOLYGON = 7;
    
    /**
     * 
     * @param geometry
     * @return 
     */
    public static String make(int geometry)
    {
        return "200" + Integer.toString(geometry);
    }
    
    /**
     * 
     * @param geometry
     * @param dimension
     * @return 
     */
    public static String make(int geometry, int dimension)
    {
        return Integer.toString(dimension) + "00" + Integer.toString(geometry);
    }
    
    /**
     * 
     * @param geometry
     * @param dimension
     * @param lengthDimension
     * @return 
     */
    public static String make(int geometry, int dimension, int lengthDimension)
    {
        return Integer.toString(dimension) + Integer.toString(lengthDimension) + "0" + Integer.toString(geometry);
    }
}
