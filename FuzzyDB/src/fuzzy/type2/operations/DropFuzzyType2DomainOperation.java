/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type2.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 *
 * @author bishma-stornelli
 */
public class DropFuzzyType2DomainOperation extends Operation {

    private final String domain;

    public DropFuzzyType2DomainOperation(Connector connector, String domain) {
        super(connector);
        this.domain = domain;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();
        String fullTypeName = schemaName + "." + domain;

        String updateCatalog = "DELETE FROM information_schema_fuzzy.domains2 " +
            "WHERE table_schema = (select current_schema())" + 
            "AND domain_name = '" + domain + "'";

        String funcNameFormat = schemaName + ".__" + domain + "_%s";

        String opClassName = String.format(funcNameFormat, "opclass");
        String dropOpClass = "DROP OPERATOR CLASS IF EXISTS " + opClassName + " USING btree";

        String dropOpFormat = "DROP OPERATOR IF EXISTS %s (" + fullTypeName + "," + fullTypeName +")";
        String dropLowerOp = String.format(dropOpFormat, "<");
        String dropLowerEqOp = String.format(dropOpFormat, "<=");
        String dropEqOp = String.format(dropOpFormat, "=");
        String dropGreaterEqOp = String.format(dropOpFormat, ">=");
        String dropGreaterOp = String.format(dropOpFormat, ">");

        String dropFuncFormat = "DROP FUNCTION IF EXISTS " + funcNameFormat + "(" + fullTypeName + ", " + fullTypeName + ")";
        String dropLowerFunc = String.format(dropFuncFormat, "lower");
        String dropLowerEqFunc = String.format(dropFuncFormat, "lower_eq");
        String dropEqFunc = String.format(dropFuncFormat, "eq");
        String dropGreaterEqFunc = String.format(dropFuncFormat, "greater_eq");
        String dropGreaterFunc = String.format(dropFuncFormat, "greater");
        String dropCmpFunc = String.format(dropFuncFormat, "cmp");

        Savepoint sp = this.beginTransaction();
        try {
            connector.executeRawUpdate(updateCatalog);
            connector.executeRaw(dropOpClass);

            connector.executeRaw(dropCmpFunc);

            connector.executeRaw(dropLowerOp);
            connector.executeRaw(dropLowerEqOp);
            connector.executeRaw(dropEqOp);
            connector.executeRaw(dropGreaterEqOp);
            connector.executeRaw(dropGreaterOp);

            connector.executeRaw(dropLowerFunc);
            connector.executeRaw(dropLowerEqFunc);
            connector.executeRaw(dropEqFunc);
            connector.executeRaw(dropGreaterEqFunc);
            connector.executeRaw(dropGreaterFunc);
            
            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }
    }
    
}
