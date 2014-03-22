/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.database;

import fuzzy.helpers.Logger;
import fuzzy.helpers.Printer;
import fuzzy.operations.Operation;
import fuzzy.translator.StatementTranslator;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.TokenMgrError;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.fuzzy.domain.AlterFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyDomain;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

/**
 *
 * @author andras
 */
public class Connector {


    /**
     * MySQL primitive data types, used to avoid querying for a type on
     * suspicion that could be fuzzy
     */
    private static final String [] DATA_TYPES = {"TINYINT​", "BOOLEAN​", "SMALLINT​",
        "MEDIUMINT​", "INT​", "INTEGER​", "BIGINT​", "DECIMAL​", "DEC, NUMERIC, FIXED​",
        "FLOAT​", "DOUBLE​", "DOUBLE PRECISION​", "BIT​", "CHAR​", "VARCHAR​", "BINARY​",
        "CHAR BYTE​", "VARBINARY​", "TINYBLOB​", "BLOB​", "BLOB DATA TYPE​", "MEDIUMBLOB​",
        "LONGBLOB​", "TINYTEXT​", "TEXT​", "MEDIUMTEXT​", "LONGTEXT​", "ENUM​", "SET", "DATE​",
        "TIME​", "DATETIME​", "TIMESTAMP​", "YEAR​", "POINT", "LINESTRING", "POLYGON",
        "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION", "GEOMETRY"};
    
    /**
     * Check if @dataType is a native data type of the dbms
     * 
     * @param dataType the dataType to be tested
     * @return true if it's a native data type of the dbms
     */
    public static boolean isNativeDataType(String dataType) {
        return Arrays.asList(DATA_TYPES).contains(dataType.toLowerCase());
    }

    private Connection connection;
    private enum TYPE {UPDATE, QUERY, ANY};
    private ResultSet resultSet;
    private Integer updateCount;

    private String catalog; // Temp hack for migrating to PostgreSQL
    

    
    /**
     * Driver module used by java.sql
     */
    private static final String driver = "org.postgresql.Driver"; //"com.mysql.jdbc.Driver";

    
    /**
     * Driver protocol to open connection with database
     */
    private static final String driverProtocol = "jdbc:postgresql"; //"jdbc:mysql";


    public Connector() 
        throws SQLException {
        setup("127.0.0.1", "fuzzy", "fuzzy", "");//"localhost", "root", "", "");
    }

    public Connector(String host, String username, String password, String databaseName) 
        throws SQLException {
        setup(host, username, password, databaseName);
    }

    private void setup(String host, String username, String password,
                                      String databaseName) throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            Logger.severe(null, e);
        }
        String url  = driverProtocol + "://" + host + 
                       (databaseName == null ? "" : "/" + databaseName);
        connection = DriverManager.getConnection(url, username, password);
        catalog = "";
    }
    
    /**
     * Static method that returns the instance for the singleton
     * 
     * @return {Connection} connection
     **/    
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Static method that close the connection to the database
     * 
     * @return void
     **/
    
    public void closeConnection() throws SQLException {
        connection.close();
    }

    public String getCatalog() throws SQLException {
        return this.catalog;
        //return connection.getCatalog();
    }


    /**
     * Try to change the catalog of the database
     * 
     * @param catalogName Newer catalog name
     */
    public void setCatalog(String catalogName) throws SQLException {
        this.catalog = catalogName;
        //connection.setCatalog(catalogName);
        // FIXME: Check whatever setCatalog used to do here in MySQL
    }


    public void fast(String sql) throws SQLException {
        Logger.debug("fast: " + sql);
        java.sql.Statement s = this.connection.createStatement();
        s.execute(sql);
        this.resultSet = s.getResultSet();
        this.updateCount = s.getUpdateCount();
    }

    /**
     * Executes a SELECT query and returns the corresponding ResultSet
     * 
     * @param query a SELECT query
     * @return ResultSet result or exception in case it fails
     */
    public ResultSet fastQuery(String sql) throws SQLException {
        Logger.debug("fastQuery: " + sql);
        this.resultSet = connection.createStatement().executeQuery(sql);
        this.updateCount = -1;
        return this.resultSet;
    }
    
    /**
     * Executes an INSERT, UPDATE, etc. query and returns the number of columns
     * updated.
     * 
     * @param query an INSERT, UPDATE, DELETE, etc. query
     * @return The number of updated rows or null if there was a problem during
     * the execution of the query.
     */
    public Integer fastUpdate(String sql) throws SQLException {
        Logger.debug("fastUpdate: " + sql);
        this.updateCount = connection.createStatement().executeUpdate(sql);
        this.resultSet = null;
        return this.updateCount;
    }
    

    public Integer fastInsert(String sql) throws SQLException {
        Logger.debug(sql);
        PreparedStatement statement = connection
                        .prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("No primary key obtained", "42000", 3019);
        }
    }


    public ResultSet executeQuery(String sql) throws SQLException {
        execute(sql);
        return resultSet;
    }


    public Integer executeUpdate(String sql) throws SQLException {
        execute(sql);
        return updateCount;
    }

    
    public TranslateResult translate(String sql) throws SQLException {
        
        // PARSER
        CCJSqlParserManager pa = new CCJSqlParserManager();
        
        net.sf.jsqlparser.statement.Statement s = null;
        try {
            s = pa.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            Throwable c = e.getCause();
            if (c instanceof TokenMgrError) {
                throw new SQLException("JsqlParser.TokenMgrError: " + c.getMessage(), "42000", 3012, c);
            } else if (c instanceof Error) {
                throw new SQLException("JsqlParser.Error: " + c.getMessage(), "42000", 3013, c);
            } else if (c instanceof IOException) {
                throw new SQLException("JsqlParser.IOException: " + c.getMessage(), "42000", 3014, c);
            } else if (c instanceof RuntimeException) {
                Throwable d = c.getCause();
                if (d instanceof UnsupportedEncodingException) {
                    throw new SQLException("JsqlParser.UnsupportedEncodingException: " + c.getMessage(), "42000", 3015, c);
                } else {
                    throw new SQLException("Unknown JsqlParser runtime exception: " + c.getMessage(), "42000", 3016, c);
                }
            } else if (c instanceof ParseException) {
                ParseException d = (ParseException)c;
                int line = d.currentToken.beginLine,
                    column = d.currentToken.beginColumn;
                line = 0 == line ? 0 : line - 1;
                column = 0 == column ? 0 : column - 1;
                String rest = sql.split("\\r?\\n|\\r")[line]
                                  .substring(column);
                throw new SQLException(
                        "You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near '" + rest + "' at line " + (line + 1) + " (JSP)",
                        "42000",
                        1064, c);
            } else {
                throw new SQLException("Unknown JsqlParser exception: " + c.getMessage(), "42000", 3017, c);
            }
        }

        // TRANSLATOR
        List<Operation> operations = new ArrayList<Operation>();
        StatementTranslator st = new StatementTranslator(this, operations);
        try {
            s.accept(st);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Translator exception: " + e.getMessage(),
                                                              "42000", 3018, e);
        }

        String res = null;
        if (s instanceof CreateFuzzyDomain
         || s instanceof AlterFuzzyDomain
            // TODO drop fuzzy domain can be translated into DELETE FROM domain WHERE ...
         || s instanceof Drop && ((Drop)s).getType()
                                          .equalsIgnoreCase("FUZZY DOMAIN")
         || s instanceof AlterTable) {
            // FUZZY DDL are just operations, no query
        } else {
            //DEPARSER
            StringBuffer sb = new StringBuffer();
            StatementDeParser sdp = new StatementDeParser(sb);
            try {
                s.accept(sdp);
            } catch (Exception e) {
                throw new SQLException("Deparser exception: " + e.getMessage(),
                                                              "42000", 3019, e);
            }

            res = sb.toString();
        }

        return new TranslateResult(res, operations);
    }

    public class TranslateResult {
        public String sql;
        public List<Operation> operations;

        TranslateResult(String sql, List<Operation> operations) {
            this.sql = sql;
            this.operations = operations;
        }
    }


    public void execute(String sql) throws SQLException {
        Logger.debug("Executing: " + sql);

        TranslateResult translateResult = translate(sql);
        
        boolean queryOk = true;
        if (null != translateResult.sql) {
            Logger.notice(translateResult.sql);

            // EXECUTE TRANSLATED INPUT
            fast(translateResult.sql);
            queryOk = -1 != updateCount || null != resultSet;
        }
        if (queryOk) {
            for (Operation o : translateResult.operations) {
                try {
                    o.execute();
                } catch (SQLException ex) {
                    Printer.printSQLErrors(ex);
                }
            }
        }
    }


    public ResultSet getResultSet() {
        return resultSet;
    }


    public Integer getUpdateCount() {
        return updateCount;
    }
}
