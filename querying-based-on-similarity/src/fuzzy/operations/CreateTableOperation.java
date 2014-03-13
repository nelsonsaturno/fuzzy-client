/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.database.Connector;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

/**
 *
 * @author bishma-stornelli
 */
public class CreateTableOperation extends Operation {

    private String createTableSQL;
    private String schemaName;
    private String tableName;
    private List<String> columnNames;
    private List<String> domainNames;

    public CreateTableOperation(Connector connector, String createTableSQL, String schemaName, String tableName) {
        super(connector);
        this.createTableSQL = createTableSQL;
        this.schemaName = schemaName == null ? "(select database())" : "'" + schemaName + "'";
        this.tableName = tableName;
    }   
    
    @Override
    public void execute() throws SQLException {
        Savepoint sp = null;
        try {
            sp = this.beginTransaction();
            
            // GET domain ids
            
            // Check that all domains exists
            
            
            // CREATE TABLE
            int updateCount = connector.fastUpdate(createTableSQL);

            // INSERT in columns
            StringBuffer insertSql = new StringBuffer("INSERT INTO information_schema_fuzzy.columns "
                    + "VALUES (?,?,?,?)");
            
            for (int i = 0 ; i < this.columnNames.size() ; ++i) {
                insertSql.append(", (?,?,?,?)");
            }
            PreparedStatement insertStatement = this.connector.getConnection().prepareStatement(insertSql.toString());
            
            for (int i = 0 ; i < this.columnNames.size() ; ++i) {
                //insertStatement.se
            }
            
            // CREATE FOREIGN KEYS

            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }
    }
    
    public void addFuzzyColumn(String columnName, String domainName) {
        this.columnNames.add(columnName);
        this.domainNames.add(domainName);
    }
    
}
