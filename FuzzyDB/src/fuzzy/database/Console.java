package fuzzy.database;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a singleton class that serves to query the database.
 * To use it call the method initialize with the arguments passed to the DBMS
 * and then ask the instance by calling the getInstance() method.
 */
public class Console {

    @Parameter(names = "-u")
    private static String username = "fuzzy";//"root";
    @Parameter(names = "-p", password = true, description = "Enter password")
    private static String password = "fuzzy";//"";
    @Parameter(names = "-h")
    private static String host = "127.0.0.1";//"localhost";
    private static String databaseName = null;

    
    /**
     * Parameters sent by console
     */
    @Parameter
    private static List<String> additionalParameters = new ArrayList<String>();


    private static Console instance = null;


    protected Console() {
        // Exists only to defeat instantiation.
    }
    
    /** Initialize all parameters required to start the connection with the 
     * database and test it.
     * 
     * @param args an array of parameters console-style with options: -u <user>
     * -p (ask password) -h <host> <database>
     * @return true if everything is ok.
     */
    public static Connector getConnector(String[] args) throws SQLException{
        instance = new Console();
        new JCommander(instance, args);
        if (additionalParameters.size() > 0) {
            databaseName = additionalParameters.get(0);
        }
        // TODO load metadata script to avoid user to run it manually
        return new Connector(host, username, password, databaseName);
        
    }
}
