package fuzzy.type2.operations;

import java.sql.SQLException;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;

/**
 * Setups a new Type-2 Fuzzy type in the schema.
 * 
 * This includes all the queries required for the custom ordering of this type.
 *
 */
public class CreateFuzzyType2DomainOperation extends Operation {

    private String name;
    private String type;
    private String upperBound;
    private String lowerBound;

    /**
     * Creates a new instance.
     * @param connector Connector instance used to interface with the database.
     * @param name name of the new type. Must not contain any periods.
     * It will be used verbatim in the generated SQL queries.
     * @param type type of the underlying subtype. Must be a type that will be
     * understood by Postgres, as it will be used verbatim in the generated
     * queries. It can't be a domain type, since Postgres doesn't support arrays
     * of those.
     */
    public CreateFuzzyType2DomainOperation(Connector connector, String name, String type) {
        super(connector);
        this.name = name;
        this.type = type;
        this.upperBound = null;
        this.lowerBound = null;
    }
    
    /**
     * Sets the bounds for the values for this fuzzy type.
     * 
     * <p>The bounds must be a SQL value, as it will be used verbatim in the
     * CHECK constraints that will be generated. Both bounds can be null,
     * or both can have a value. If one is null and the other is not, the
     * behavior is undefined.</p>
     * 
     * <p>The bounds are both inclusive.</p>
     * 
     * @param lowerBound lower bound for the subtype.
     * @param upperBound upper bound for the subtype.
     */
    public void setBounds(String lowerBound, String upperBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    @Override
    public void execute() throws SQLException {

        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String catalog = this.connector.getSchema();

        /*
        * For example, CREATE FUZZY DOMAIN test AS POSSIBILITY DISTRIBUTION
        * ON 1..100, and the current schema is 'test_schema'.
        * The following queries will be generated:
        */


        /*
        * INSERT INTO information_schema_fuzzy.domains2 
        * VALUES (DEFAULT, 'test_schema', 'test', 'INTEGER', '1', '100')
        */
        String insertDomainCatalog = "INSERT INTO information_schema_fuzzy.domains2 "
                                   + "VALUES (DEFAULT, "
                                   + "'" + catalog + "' ,"
                                   + "'" + this.name + "' ,"
                                   + "'" + this.type + "', "
                                   + (null != this.lowerBound ? "'" + this.lowerBound + "'" : "NULL") + ", "
                                   + (null != this.upperBound ? "'" + this.upperBound + "'" : "NULL") + ")";

        String fullTypeName = catalog + "." + this.name;
        
        /*
        * CREATE TYPE test_schema.test AS (
        *    odd real[], 
        *    value integer[],
        *    type boolean
        * )
        */
        String createType = "CREATE TYPE " + fullTypeName + " AS ("
                           + "odd real[], "
                           + "value " + this.type + " ARRAY,"
                           + "type boolean"
                           + ")";

        /*
        * For each operator <, <=, =, >=, >
        * CREATE OR REPLACE FUNCTION test_schema.__test__<opname>(elem1 test_schema.test, elem2 test_schema.test)
        * RETURNS boolean AS $$
        * BEGIN
        * return <actual_fuzzyop>(elem1, elem2);
        * END;
        * $$ LANGUAGE plpgsql;
        */
        String funcNameFormat = catalog + ".__" + this.name + "_%s";
        String lowerFuncName = String.format(funcNameFormat, "centroid_lower");
        String lowerEqFuncName = String.format(funcNameFormat, "centroid_lower_eq");
        String eqFuncName = String.format(funcNameFormat, "centroid_eq");
        String greaterEqFuncName = String.format(funcNameFormat, "centroid_greater_eq");
        String greaterFuncName = String.format(funcNameFormat, "centroid_greater");

        String createFuncFormat = "CREATE OR REPLACE FUNCTION %s(elem1 " + fullTypeName + ", elem2 " + fullTypeName + ") "
                                + "RETURNS boolean AS $$ "
                                + "BEGIN "
                                + "return %s(elem1, elem2); "
                                + "END; "
                                + "$$ LANGUAGE plpgsql;";

        String createLowerFunc = String.format(createFuncFormat, lowerFuncName, "information_schema_fuzzy.fuzzy2_centroid_lower");
        String createLowerEqFunc = String.format(createFuncFormat, lowerEqFuncName, "information_schema_fuzzy.fuzzy2_centroid_lower_eq");
        String createEqFunc = String.format(createFuncFormat, eqFuncName, "information_schema_fuzzy.fuzzy2_centroid_eq");
        String createGreaterEqFunc = String.format(createFuncFormat, greaterEqFuncName, "information_schema_fuzzy.fuzzy2_centroid_greater_eq");
        String createGreaterFunc = String.format(createFuncFormat, greaterFuncName, "information_schema_fuzzy.fuzzy2_centroid_greater");

        /*
        * For each operator <, <=, =, >=, >
        * CREATE OPERATOR <op_symbol> (LEFTARG = test_schema.test, 
        * RIGHTARG = test_schema.test, PROCEDURE = test_schema.__test__<opname>)
        */
        String createOpFormat = "CREATE OPERATOR %s (LEFTARG = " + fullTypeName + ", RIGHTARG = " + fullTypeName + ", PROCEDURE = %s)";
        String createLowerOp = String.format(createOpFormat, "&@<", lowerFuncName);
        String createLowerEqOp = String.format(createOpFormat, "&@<=", lowerEqFuncName);
        String createEqOp = String.format(createOpFormat, "&@=", eqFuncName);
        String createGreaterEqOp = String.format(createOpFormat, "&@>=", greaterEqFuncName);
        String createGreaterOp = String.format(createOpFormat, "&@>", greaterFuncName);

        /*
        * CREATE OR REPLACE FUNCTION test_schema.__test__cmp(comp1 test_schema.test, comp2 test_schema.test)
        * RETURNS integer AS $$
        * BEGIN
        * if comp1 = comp2 then return 0;
        * else if comp1 < comp2 then return -1;
        * else return 1;
        * end if;
        * end if;
        * END;
        * $$ LANGUAGE plpgsql;
        */
        String cmpFuncName = String.format(funcNameFormat, "cmp");
        String createCmpFunc = "CREATE OR REPLACE FUNCTION " + cmpFuncName + "(comp1 " + fullTypeName + ", comp2 " + fullTypeName +") "
                               + "RETURNS integer AS $$ "
                               + "BEGIN "
                               + "if comp1 &@= comp2 then return 0; "
                               + "else if comp1 &@< comp2 then return -1; "
                               + "else return 1; "
                               + "end if; "
                               + "end if; "
                               + "END; "
                               + "$$ LANGUAGE plpgsql;";

        /*
        * CREATE OPERATOR CLASS test_schema.__test__opclass
        * DEFAULT FOR TYPE test_schema.test USING btree AS
        * OPERATOR 1 <,
        * OPERATOR 2 <=,
        * OPERATOR 3 =,
        * OPERATOR 4 >=,
        * OPERATOR 5 >,
        * FUNCTION 1 test_schema.__test__cmp (test_schema.test, test_schema.test);
        */
        String opClassName = String.format(funcNameFormat, "opclass");
        String createOpClass = "CREATE OPERATOR CLASS " + opClassName + " "
                             + "DEFAULT FOR TYPE " + fullTypeName + " USING btree AS "
                             + "OPERATOR 1 &@<, "
                             + "OPERATOR 2 &@<=, "
                             + "OPERATOR 3 &@=, "
                             + "OPERATOR 4 &@>=, "
                             + "OPERATOR 5 &@>, "
                             + "FUNCTION 1 " + cmpFuncName + " (" + fullTypeName + ", " + fullTypeName + ");";


        this.connector.executeRaw(createType);
        this.connector.executeRaw(insertDomainCatalog);
        
        this.connector.executeRaw(createLowerFunc);
        this.connector.executeRaw(createLowerEqFunc);
        this.connector.executeRaw(createEqFunc);
        this.connector.executeRaw(createGreaterEqFunc);
        this.connector.executeRaw(createGreaterFunc);

        this.connector.executeRaw(createLowerOp);
        this.connector.executeRaw(createLowerEqOp);
        this.connector.executeRaw(createEqOp);
        this.connector.executeRaw(createGreaterEqOp);
        this.connector.executeRaw(createGreaterOp);

        this.connector.executeRaw(createCmpFunc);
        this.connector.executeRaw(createOpClass);       
    }
}