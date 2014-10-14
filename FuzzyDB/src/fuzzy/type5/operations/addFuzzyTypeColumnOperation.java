/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import java.sql.SQLException;

/**
 *
 * @author hector
 */
public class addFuzzyTypeColumnOperation extends Operation {

    private String schemaName;
    private String tableName;
    private String columnName;
    private Integer domainId;
    
    public addFuzzyTypeColumnOperation(Connector connector,
        String schemaName, String tableName, String columnName, 
        Integer domainId) {
        
        super(connector);

        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.domainId = domainId;
    }

    @Override
    public void execute() throws SQLException {
        
        /*
        * Suponiendo que se agrego la siguiente columna:
        * CREATE TABLE test_table (
        *    ...
        *    col_name fuzzy_domain_type5
        *    ...
        * )
        * El id de fuzzy_domain_type5 es 666
        */

        /*
        * INSERT INTO information_schema_fuzzy.columns5
        * VALUES ('column_id','test_schema', 'test_table', 'col_name', 666)
        */        
        String insertColumnCatalog = "INSERT INTO information_schema_fuzzy.columns "
                                   + "VALUES ("
                                   + "'" + this.schemaName + "' ," // table_schema
                                   + "'" + this.tableName + "' ,"  // table_name
                                   + "'" + this.columnName + "' ," // columnName
                                   + " " + this.domainId + ", "   // domain_id
                                   + "DEFAULT );";           // column_id
        
        this.connector.executeRaw(insertColumnCatalog);
    }
}
