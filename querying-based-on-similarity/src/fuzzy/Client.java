package fuzzy;

import fuzzy.database.Connector;
import fuzzy.database.Console;
import fuzzy.helpers.Printer;
import fuzzy.helpers.Reader;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Main class of the project for command line execution
 */
public class Client {

    private static Connector connector;

    /** java FuzzyClient [-u username] [-p] [-h host] [databasename]
     * 
     * Codes of exit:
     * - 1 error testing connection
     * - 10 error obtaining catalog name
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            connector = Console.getConnector(args);            
        } catch (SQLException e) {
            Printer.printSQLErrors(e);
            System.exit(20);
        }

        Reader keybrd = new Reader();
        String userInput;

        while (true) {
            String catalogName = null;
            try {
                catalogName = connector.getCatalog();        
            } catch (SQLException e) {
                Printer.printSQLErrors(e);
            }
            if (catalogName == null) {// There was an error in connection
                System.exit(10);
            }

            if (catalogName.isEmpty()) {
                catalogName = "(none)";
            }

            userInput = keybrd.nextLine("FuzzyDB [" + catalogName + "]> ");

            // Parche para que no explote si cierro el input con EOF, tipo
            // cuando haces Ctrl+D
            if (null == userInput) {
                Printer.println("");
                Printer.printlnInWhite("Bye");
                System.exit(0);
            }

            // José Alberto: Odio cuando alguien clava una regex así y no explica que diablos hace
            Pattern p = Pattern.compile("(?s)\\s*((?:'(?:\\\\.|[^\\\\']|''|)*'|/\\*.*?\\*/|(?:--|#)[^\r\n]*|[^\\\\'])*?)(?:;|$)");
            Matcher m = p.matcher(userInput);
            while (m.find()) {
                String sentence = m.group(1);
                if ("".equals(sentence)) {
                    continue;
                }

                if (proccessAdministrationCommand(sentence)) {
                    continue;
                }

                try {
                    connector.execute(sentence);
                } catch (SQLException e) {
                    Printer.printSQLErrors(e);
                    continue;
                }
                if (null != connector.getResultSet()) {
                    Printer.printResultSet(connector.getResultSet());
                } else if (-1 != connector.getUpdateCount()) {
                    Printer.printRowsUpdated(connector.getUpdateCount());
                }
            }
        }
    }

    /**
     * In case an administrative sentence is specified, execute it and
     * indicate (returning) that no further actions are required.
     * 
     * @param sentence Sentence that could be administrative
     * @return if the sentence specified is administrative
     */
    private static boolean proccessAdministrationCommand(String sentence) {
        String[] words = sentence.split(" ");
        if (words.length > 0 && words[0].equalsIgnoreCase("use")) {
            if (words.length == 1) {
                // Missing argument
                Printer.println("ERROR: USE must be followed by a database name");
            } else {
                String catalogName = words[1].replaceAll(";$", "");
                try {
                    connector.setCatalog(catalogName);
                } catch (SQLException e) {
                    Printer.printSQLErrors(e);
                }
            }
            return true;
        } else if (words.length > 0 && words[0].equalsIgnoreCase("quit")) {
            Printer.printlnInWhite("Bye");
            System.exit(0);
        }
        return false;
    }
}
