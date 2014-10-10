/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type3.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Memory;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 *
 * @author hector
 */
public class InsertTranslator2 extends Translator {
    
    public InsertTranslator2(Connector connector){
        super(connector);
    }
    
    public void translate(Insert insert) {
        String schemaName;
        try {
            schemaName = Helper.getSchemaName(connector);
        } catch (SQLException ex) {
            Logger.debug(InsertTranslator2.class.getName() + ": " + "Error getting schema name");
            return;
        }
        
        String tableName = insert.getTable().getName();
        List columns = insert.getColumns();
        List values = ( (ExpressionList) insert.getItemsList() ).getExpressions();
        int size = values.size();
                        
        if (columns == null) {
            
            HashSet<String> allColumns;
            
            try {
                allColumns = Memory.getColumns(connector, schemaName, tableName);
            } catch (SQLException ex) {
                Logger.debug(InsertTranslator2.class.getName() + ": " + "Error getting all columns");
                return;
            }
                
            if ( size != allColumns.size() ) {
                Logger.debug(InsertTranslator2.class.getName() + ": " + "Columns size and Values size are differents");
                return;
            }

            int i = 0;
            Integer labelId;
            StringValue fuzzyLabel;
            String domainName;
            for (String column : allColumns) {
                
                boolean isFuzzy;
                
                try {
                    isFuzzy = Memory.isFuzzyColumn(connector, schemaName, tableName, column);
                } catch (SQLException ex) {
                    Logger.debug(InsertTranslator2.class.getName() + ": " + "Error querying if column is fuzzy");
                    return;
                }
                
                if ( isFuzzy ) {
                    
                    try {
                        domainName = Helper.getDomainNameForColumn(connector, insert.getTable(), column);
                    } catch (SQLException ex) {
                        Logger.debug(InsertTranslator2.class.getName() + ": " + "Error getting domain name");
                        return;
                    }
                
                    if ( values.get(i) instanceof StringValue ) {
                        fuzzyLabel = (StringValue) values.get(i);
                        
                        try {
                            labelId = getFuzzyLabelId(schemaName, domainName, fuzzyLabel.getValue());
                        } catch (SQLException ex) {
                            Logger.debug(InsertTranslator2.class.getName() + ": " + "Error getting label id");
                            return;
                        }
                        
                        fuzzyLabel.setValue(labelId.toString());
                    }
                }
                
                i++;
            }
        }
    }
}
