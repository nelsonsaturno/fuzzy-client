package fuzzy.type2.operations;

import java.sql.SQLException;
/* imports from fuzzy */
import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Printer;
import fuzzy.type2.translator.FuzzyType2ExpTranslator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;

/**
 * Setups a new Type-2 Fuzzy type in the schema. This includes all the queries
 * required for the custom ordering of this type.
 */
public class CreateFuzzyType2ConstantOperation extends Operation {

    private final String name;
    private final String domain;
    private final boolean setSchema;
    private String expression = null;

    /**
     * Creates a new instance.
     *
     * @param connector Connector instance used to interface with the database.
     * @param name name of the new type. Must not contain any periods. It will
     * be used verbatim in the generated SQL queries.
     * @param domain domain name
     * @param setSchema
     * @param expression
     */
    public CreateFuzzyType2ConstantOperation(Connector connector, String name, 
            String domain, boolean setSchema, String expression) {
        super(connector);
        this.name = name;
        this.domain = domain;
        this.setSchema = setSchema;
        if (setSchema) {
            this.expression = expression;
        }
    }

    /**
     * Insert the constant into catalog.
     *
     * @param catalog the catalog which is related to.
     * @throws java.sql.SQLException
     */
    public void update(String catalog) throws SQLException {

        String insertIntoCatalog = "UPDATE information_schema_fuzzy.constants2 "
                + "SET constant_schema = '" + catalog + "', "
                + "fuzzy_type = '" + this.expression + "' "
                + "WHERE constant_schema = 'NULL';";
        this.connector.executeRaw(insertIntoCatalog);
    }

    /**
     * Function that determines if a given domain exists.
     *
     * @return boolean determining if the domain exists.
     * @throws java.sql.SQLException
     */
    public boolean domainExists() throws SQLException {
        String domainExists = "SELECT EXISTS "
                + "(SELECT 1 FROM information_schema_fuzzy.domains2 "
                + "WHERE domain_name = '" + this.domain + "' LIMIT 1);";
        Connector.ExecutionResult queryResult = this.connector.executeRaw(domainExists);
        queryResult.result.next();
        return !queryResult.result.getString(1).equals("f");
    }

    /**
     * Function that determines if a given constant already exists
     *
     * @param catalog the catalog which is related to.
     * @return boolean determining if the constant already exists.
     * @throws java.sql.SQLException
     */
    public boolean constantExists(String catalog) throws SQLException {
        String domainExists = "SELECT EXISTS "
                + "(SELECT 1 FROM information_schema_fuzzy.constants2 "
                + "WHERE constant_name = '" + this.name + "' "
                + "AND domain_name = '" + this.domain + "' "
                + "AND constant_schema = '" + catalog + "' LIMIT 1);";
        Connector.ExecutionResult queryResult = this.connector.executeRaw(domainExists);
        queryResult.result.next();
        return !queryResult.result.getString(1).equals("f");
    }

    @Override
    public void execute() throws SQLException {

        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        /* Get te current schema name */
        String catalog = this.connector.getSchema();
        /* Is the domain does not exists or the constant is already 
         defined is not inserted and throws and exception */
        
        if (!this.setSchema) {
            if (constantExists(catalog)) {
                    throw new SQLException("Constant '" + this.name + "' "
                            + "already exists for schema " + catalog + ", "
                            + "domain " + this.domain + ".", "42000", 3020, null);
            }

            if (!domainExists()) {
                throw new SQLException("Domain name not found for " + catalog + ".", "42000", 3020, null);
            }
        } else {
            update(catalog);
        }
    }
}
