/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.helpers.Memory;
import fuzzy.type3.translator.Translator;
import fuzzy.type5.operations.InsertIntoValuesOperation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.fuzzy.FuzzyByExtension;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 *
 * @author josegregorio
 */
public class InsertTranslator extends Translator {
    
    public InsertTranslator(Connector connector, List<Operation> operations){
        super(connector, operations);
    }
    
    public void translate(Insert insert) throws SQLException {
        String schemaName;
        
        String tableName = insert.getTable().getName();
        List values = ( (ExpressionList) insert.getItemsList() ).getExpressions();
        List<String> columnNames;
        List columns = insert.getColumns();
        int size = values.size();
        
        try {
            schemaName = Helper.getSchemaName(connector);
        } catch (SQLException ex) {
            Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting schema name");
            return;
        }
        
        if ( columns != null ) {
            columnNames = new ArrayList<String>();
            for (Object column : columns) {
                columnNames.add( ( (Column)column).getColumnName() );
            }
        } else {
            
            HashSet<String> allColumns;
            
            try {
                allColumns = Memory.getColumns(connector, schemaName, tableName);
            } catch (SQLException ex) {
                Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting all columns");
                return;
            }
            
            columnNames = new ArrayList<String>(allColumns);
        }
                
        if ( size != columnNames.size() ) {
            Logger.debug(InsertTranslator.class.getName() + ": " + "Columns size and Values size are differents");
            return;
        }

        int i = 0;
        String labelId;
        FuzzyByExtension fuzzyExt;
        String domainName;
        boolean isFuzzy;
        TreeSet<String> labels = new TreeSet<String>();
        
        for (String column : columnNames) {

            try {
                isFuzzy = Memory.isFuzzyColumn(connector, schemaName, tableName, column);
            } catch (SQLException ex) {
                Logger.debug(InsertTranslator.class.getName() + ": " + "Error querying if column is fuzzy");
                return;
            }

            if ( isFuzzy ) {

                try {
                    domainName = Helper.getDomainNameForColumn(connector, insert.getTable(), column);
                } catch (SQLException ex) {
                    Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting domain name");
                    return;
                }

                if ( values.get(i) instanceof FuzzyByExtension ) {
                    fuzzyExt = (FuzzyByExtension) values.get(i);
                    
                    for (FuzzyByExtension.Element element : fuzzyExt.getPossibilities()) {
                        String elem_possiblity = element.getPossibility().toString();
                        if (element.getExpression() instanceof StringValue) {
                            
                            StringValue sv = (StringValue) element.getExpression();
                            
                            try {
                                
                                labelId = String.valueOf(
                                        getFuzzyLabelId(schemaName, domainName, sv.getValue())
                                );
                                
                                if (!labels.contains(labelId)) {
                                    
                                    labels.add(labelId);
                                    
                                } else {
                                    Logger.debug(InsertTranslator.class.getName() + ": " + "Some labels are repeated");
                                    return;
                                }
                                
                            } catch (SQLException ex) {
                                
                                Logger.debug(InsertTranslator.class.getName() + ": " + "Error getting label id");
                                return;
                                
                            }
                            
                        } else {
                            Logger.debug(InsertTranslator.class.getName() + ": " + "Some values aren't labels");
                            return;
                        }
                    }
                }
            }

            i++;
        }
    }
}
