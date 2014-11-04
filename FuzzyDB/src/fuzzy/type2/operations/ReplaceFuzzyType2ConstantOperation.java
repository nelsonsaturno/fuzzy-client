/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type2.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Printer;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.expression.fuzzy.FuzzyByExtension;
import net.sf.jsqlparser.expression.fuzzy.FuzzyTrapezoid;

/**
 *
 * @author smaf
 */
public class ReplaceFuzzyType2ConstantOperation extends Operation {

    private final Table table;
    private final List columns;
    private final List expressions;

    public ReplaceFuzzyType2ConstantOperation(Connector connector, Table table,
            List columns, List expressions) {
        super(connector);
        this.table = table;
        this.columns = columns;
        this.expressions = expressions;
    }

    /**
     * Replaces a given constant if it exists, otherwise exception is raised.
     *
     * @param schemaName schema where the query is executed.
     * @param tableName table where the constant wants to be replaced.
     * @param attributeName attribute tied to the constant.
     * @param expression expression that might be replaced
     * @return
     * @throws SQLException
     */
    public Expression replaceConstantIfExists(String schemaName, String tableName,
            String attributeName, Expression expression) throws SQLException {
        /* First check tha the column s of fuzzy type */
        String attributeIsFuzzy = "SELECT name, table_name "
                + "FROM information_schema_fuzzy.columns2 "
                + "WHERE table_schema = '" + schemaName + "' "
                + "AND table_name = '" + tableName + "' "
                + "AND name = '" + attributeName + "';";
        Connector.ExecutionResult queryResult = this.connector.executeRaw(attributeIsFuzzy);
        if (queryResult.result.next()) {
            /* If the expression is a string then its a type2 constant*/
            if ("string".equals(expression.getExpressionType())) {
                String getConstantValue = "SELECT name, table_name, constant_name, value, fuzzy_type "
                        + "FROM information_schema_fuzzy.columns2, information_schema_fuzzy.constants2 "
                        + "WHERE table_schema = '" + schemaName + "' "
                        + "AND table_name='" + tableName + "' "
                        + "AND name = '" + attributeName + "' "
                        + "AND constant_schema = table_schema "
                        + "AND constant_name = " + expression.toString() + ";";
                queryResult = this.connector.executeRaw(getConstantValue);
                if (queryResult.result.next()) {
                    Expression returnExpression;
                    String value = queryResult.result.getString(4);
                    /* Parse the expression */
                    String possibilities = value.substring(value.indexOf("{") + 1, value.indexOf("}"));
                    value = value.substring(value.indexOf("}") + 1, value.length());
                    String values = value.substring(value.indexOf("{") + 1, value.indexOf("}"));
                    if ("fuzzyextension".equals(queryResult.result.getString(5))) {
                        /* Fuzzy by extension */
                        returnExpression = parseExtensionPossibilities(possibilities, values);
                    } else {
                        /* Fuzzy by trapezoid */
                        returnExpression = parseTrapezoidPossibilities(values);
                    }
                    return returnExpression;
                } else {
                    /* Raise exception if the constant does no exist */
                    throw new SQLException("Constant " + expression.toString() + " does not exist.", "42000", 3020, null);
                }
            }
        }
        return null;
    }

    /**
     * Casts possibilities to Trapezoid Expression
     *
     * @param possibilitiesString string of possibilities
     * @return Expression
     */
    public static Expression parseTrapezoidPossibilities(String possibilitiesString) {
        String[] possibilitiesToParse = possibilitiesString.split(",");
        Expression[] trapezoidValues = new Expression[4];
        for (int i = 0; i < possibilitiesToParse.length; i++) {
            if ("NULL".equalsIgnoreCase(possibilitiesToParse[i])) {
                    trapezoidValues[i] = new NullValue();
            } else {
                trapezoidValues[i] = new DoubleValue(possibilitiesToParse[i]);
            }
        }
        FuzzyTrapezoid f = new FuzzyTrapezoid(trapezoidValues[0], trapezoidValues[1],
                trapezoidValues[2], trapezoidValues[3]);
        return new FuzzyTrapezoid(trapezoidValues[0], trapezoidValues[1],
                trapezoidValues[2], trapezoidValues[3]);
    }

    /**
     * Casts possibilities to Extension Expression
     *
     * @param possibilitiesString string of possibilities
     * @param valuesString string of values
     * @return Expression
     */
    public static Expression parseExtensionPossibilities(String possibilitiesString, String valuesString) {
        String[] possibilitiesToParse = possibilitiesString.split(",");
        String[] valuesToParse = valuesString.split(",");
        FuzzyByExtension extension = null;
        for (int i = 0; i < valuesToParse.length; i++) {
            if (i == 0) {
                extension = new FuzzyByExtension(Double.parseDouble(possibilitiesToParse[i]), new DoubleValue(valuesToParse[i]));
            } else {
                extension.addPossibility(Double.parseDouble(possibilitiesToParse[i]), new DoubleValue(valuesToParse[i]));
            }
        }
        return extension;
    }

    public void iterateSelectedColumns(String schemaName) throws SQLException {
        Iterator iterator = this.columns.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            String attribute = iterator.next().toString();
            /* Replace the expression if needed. */
            Expression expression = replaceConstantIfExists(schemaName, this.table.getName(),
                    attribute, (Expression) this.expressions.get(counter));
            if (expression != null) {
                this.expressions.set(counter, expression);
            }
            counter++;
        }
    }

    public void iterateColumns(String schemaName) throws SQLException {
        String getColumns = "SELECT column_name "
                + "FROM information_schema.columns "
                + "WHERE table_schema = '" + schemaName + "' "
                + "AND table_name = '" + this.table.getName() + "'";
        Connector.ExecutionResult queryResult = this.connector.executeRaw(getColumns);
        String column;
        int counter = 0;
        while (queryResult.result.next()) {
            column = queryResult.result.getString(1);
            Expression expression = replaceConstantIfExists(schemaName, this.table.getName(),
                    column, (Expression) this.expressions.get(counter));
            if (expression != null) {
                this.expressions.set(counter, expression);
            }
            counter++;
        }
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getSchema().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getSchema();

        /* If the columns are listed */
        if (this.columns != null) {
            iterateSelectedColumns(schemaName);
        } else {
            iterateColumns(schemaName);
        }
    }
}
