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
 * @author josegregorio
 */
public class InsertIntoValuesOperation extends Operation {
    
    protected String columnId;
    protected String labelId;
    protected String fuzzy_row_id;
    protected String p_value;
    
    public InsertIntoValuesOperation(Connector connector,
                                   String columnId, 
                                   String labelId, 
                                   String fuzzy_row_id, 
                                   String p_value) {
        super(connector);
        this.columnId = columnId;
        this.labelId = labelId;
        this.fuzzy_row_id = fuzzy_row_id;
        this.p_value = p_value;
    }
    
    @Override
    public void execute() throws SQLException {
        connector.executeRawUpdate(getQuery());
    }
    
    /*
     * INSERT INTO information_schema_fuzzy.values5 
     * VALUES (column_id, label_id, fuzzy_row_id, p_value)
     */
    public String getQuery() {
        return "INSERT INTO information_schema_fuzzy.values5 "
                            + "VALUES ("
                            + columnId +", "
                            + labelId + ", "
                            + fuzzy_row_id + ", "
                            + p_value + ")";
    }
    
}
