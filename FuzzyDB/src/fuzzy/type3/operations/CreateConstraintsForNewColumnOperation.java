/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class CreateConstraintsForNewColumnOperation extends ColumnOperation {
    
    public CreateConstraintsForNewColumnOperation(Connector connector) {
        super(connector);
    }
    
    public CreateConstraintsForNewColumnOperation(Connector connector,
                                   String schemaName, 
                                   String tableName, 
                                   String columnName) {
        super(connector);
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public void execute() throws SQLException {
        String addForeignKeyConstraint = "ALTER TABLE "
            + getSchemaTableForSQL() + " "
            + "ADD CONSTRAINT " + columnName + "_foreign_key "
            + "FOREIGN KEY (" + columnName + ") "
            + "REFERENCES information_schema_fuzzy.labels (label_id) "
            + "ON UPDATE CASCADE ON DELETE RESTRICT";
        
        connector.executeRawUpdate(addForeignKeyConstraint);
        
        // You cannot query on check constraints
        // ALTER FUZZY DOMAIN DROP LABEL, care must be taken with all columns
        // referencing this fuzzy domain
        /*
        String addCheckConstraint = "ALTER TABLE " 
                + getSchemaTableForSQL() + " "
                + "ADD CONSTRAINT " + columnName + "_valid_labels "
                + "CHECK ("+ columnName + " IN ("
                + "SELECT label_id "
                + "FROM information_schema_fuzzy.labels "
                + "WHERE domain_id = " + getDomainIdForSql() + "))";
        
        connector.executeRawUpdate(addCheckConstraint);
        */
    }
}
