/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.helpers;

import java.util.TreeMap;

/**
 *
 * @author Jose
 */

/*
 * Centralize the error messages 
 */
public class Error {
    
    private static TreeMap<String, String> messages;
    private static final String str[][] ={
            {"getSchemaT5", "Error getting schema name"},
            {"getDomainT3", "Type3 Domain not found"},
            {"getAllCols","Error getting all columns"},
            {"colValSiz","Columns size and Values size are differents"},
            {"fuzzyColQuery","Error querying if column is fuzzy"},
            {"notImplemented", "Operation is not suported yet."},
            {"noCol", "No column definitions"}
        };
    
    
    private static void fill(){
        for (String[] str1 : str) {
            messages.put(str1[0], str1[1]);
        }
    }
    
    /*
     * Get the Error string using the key 
     */
    public static String getError(String key){
        if(messages.isEmpty()){
            Error.fill();
        }
        return messages.get(key);
    }
    
    
}