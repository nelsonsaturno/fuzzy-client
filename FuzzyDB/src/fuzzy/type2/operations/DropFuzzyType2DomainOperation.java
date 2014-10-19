/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type2.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import fuzzy.type3.translator.Translator;
import java.sql.SQLException;

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

        String opClassName = String.format(funcNameFormat, "centroid_class");
        String dropOpClass = "DROP OPERATOR CLASS IF EXISTS " + opClassName + " USING btree";

        String dropOpFormat = "DROP OPERATOR IF EXISTS %s (" + fullTypeName + "," + fullTypeName +")";
        String dropLowerOp = String.format(dropOpFormat, "&@<");
        String dropLowerEqOp = String.format(dropOpFormat, "&@<=");
        String dropEqOp = String.format(dropOpFormat, "&@=");
        String dropGreaterEqOp = String.format(dropOpFormat, "&@>=");
        String dropGreaterOp = String.format(dropOpFormat, "&@>");

        String dropFuncFormat = "DROP FUNCTION IF EXISTS " + funcNameFormat + "(" + fullTypeName + ", " + fullTypeName + ")";
        String dropLowerFunc = String.format(dropFuncFormat, "centroid_lower");
        String dropLowerEqFunc = String.format(dropFuncFormat, "centroid_lower_eq");
        String dropEqFunc = String.format(dropFuncFormat, "centroid_eq");
        String dropGreaterEqFunc = String.format(dropFuncFormat, "centroid_greater_eq");
        String dropGreaterFunc = String.format(dropFuncFormat, "centroid_greater");
        String dropCmpFunc = String.format(dropFuncFormat, "centroid_cmp");

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
       
        opClassName = String.format(funcNameFormat, "choquet_class");
        dropOpClass = "DROP OPERATOR CLASS IF EXISTS " + opClassName + " USING btree";

        dropOpFormat = "DROP OPERATOR IF EXISTS %s (" + fullTypeName + "," + fullTypeName +")";
        dropLowerOp = String.format(dropOpFormat, "&#<");
        dropLowerEqOp = String.format(dropOpFormat, "&#<=");
        dropEqOp = String.format(dropOpFormat, "&#=");
        dropGreaterEqOp = String.format(dropOpFormat, "&#>=");
        dropGreaterOp = String.format(dropOpFormat, "&#>");

        dropFuncFormat = "DROP FUNCTION IF EXISTS " + funcNameFormat + "(" + fullTypeName + ", " + fullTypeName + ")";
        dropLowerFunc = String.format(dropFuncFormat, "choquet_lower");
        dropLowerEqFunc = String.format(dropFuncFormat, "choquet_lower_eq");
        dropEqFunc = String.format(dropFuncFormat, "choquet_eq");
        dropGreaterEqFunc = String.format(dropFuncFormat, "choquet_greater_eq");
        dropGreaterFunc = String.format(dropFuncFormat, "choquet_greater");
        dropCmpFunc = String.format(dropFuncFormat, "choquet_cmp");

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
        
        opClassName = String.format(funcNameFormat, "sugeno_class");
        dropOpClass = "DROP OPERATOR CLASS IF EXISTS " + opClassName + " USING btree";

        dropOpFormat = "DROP OPERATOR IF EXISTS %s (" + fullTypeName + "," + fullTypeName +")";
        dropLowerOp = String.format(dropOpFormat, "&%<");
        dropLowerEqOp = String.format(dropOpFormat, "&%<=");
        dropEqOp = String.format(dropOpFormat, "&%=");
        dropGreaterEqOp = String.format(dropOpFormat, "&%>=");
        dropGreaterOp = String.format(dropOpFormat, "&%>");

        dropFuncFormat = "DROP FUNCTION IF EXISTS " + funcNameFormat + "(" + fullTypeName + ", " + fullTypeName + ")";
        dropLowerFunc = String.format(dropFuncFormat, "sugeno_lower");
        dropLowerEqFunc = String.format(dropFuncFormat, "sugeno_lower_eq");
        dropEqFunc = String.format(dropFuncFormat, "sugeno_eq");
        dropGreaterEqFunc = String.format(dropFuncFormat, "sugeno_greater_eq");
        dropGreaterFunc = String.format(dropFuncFormat, "sugeno_greater");
        dropCmpFunc = String.format(dropFuncFormat, "sugeno_cmp");

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
    }
    
}
