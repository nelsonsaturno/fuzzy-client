package net.sf.jsqlparser.statement.fuzzy.constant;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 * A "CREATE FUZZY DOMAIN" statement
 */
public class CreateFuzzyConstant implements Statement {

    private String name;
    private String domain;
    private String value;


//    public CreateFuzzyConstant(String name, String domain,
//            String value) {
//        this.name = name;
//        this.domain = domain;
//        this.value = value;
//    }
//
//    public CreateFuzzyConstant(String name, Column column) {
//        this.name = name;
////        this.column = column;
//    }

    @Override
    public void accept(StatementVisitor statementVisitor) throws Exception {
        statementVisitor.visit(this);
    }

    /**
     * The name of the fuzzy domain to be created
     * @return 
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * A list of {@link Expression}s of this fuzzy domain. Only strings not
     * validated by the parser
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        String sql = "CREATE FUZZY CONSTANT " + name + " " + domain + ":= " + value + ";";
//        if (similarityList.getExpressions().size() > 0) {
//            sql += " SIMILARITY {" + similarityList.toString(false) + "}";
//        }
        return sql;
    }

//    public boolean isFromColumn() {
//        return null != column;
//    }
//
//    public Column getFromColumn() {
//        return this.column;
//    }
}