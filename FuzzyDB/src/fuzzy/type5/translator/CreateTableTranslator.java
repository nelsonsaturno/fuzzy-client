/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.type3.translator.Translator;
import fuzzy.type5.operations.addFuzzyTypeColumnOperation;
import java.sql.SQLException;
import java.util.List;
import net.sf.jsqlparser.statement.table.ColumnDefinition;
import net.sf.jsqlparser.statement.table.CreateTable;

/**
 *
 * @author hector
 */
public class CreateTableTranslator extends Translator {

    public CreateTableTranslator(Connector connector, 
            List<Operation> operations) {
        
        super(connector, operations);
    }
    
    public void translate(CreateTable createTable) throws SQLException {
        List columns = createTable.getColumnDefinitions();
        
        if ( columns == null ) {
            throw new SQLException("No column definitions");
        }
        
        String schemaName = Helper.getSchemaName(connector, createTable.getTable());
        String tableName = createTable.getTable().getName();
        
        String columnName, columnTypeName;
        ColumnDefinition columnDefinition;
        Integer domainId;
        for (int i = 0; i < columns.size(); i++ ) {
            columnDefinition = (ColumnDefinition) columns.get(i);
            columnName = columnDefinition.getColumnName();
            columnTypeName = columnDefinition.getColDataType().getDataType();
                        
            domainId = getFuzzyDomainId(schemaName, columnTypeName, "5");
            
            // El dominio es tipo 5
            if ( domainId != null ) {
                operations.add(new addFuzzyTypeColumnOperation(connector, 
                        schemaName, tableName, columnName, domainId));
            }
        }
    }
}
