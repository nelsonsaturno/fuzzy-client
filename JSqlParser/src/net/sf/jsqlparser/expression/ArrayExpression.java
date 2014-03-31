package net.sf.jsqlparser.expression;

import java.util.ArrayList;

public class ArrayExpression implements Expression {

    private ArrayList<Expression> elements;

    public ArrayExpression() {
        this.elements = new ArrayList();
    }

    public void addExpression(Expression expression) {
        this.elements.add(expression);
    }

    public void accept(ExpressionVisitor expressionVisitor) throws Exception {
        expressionVisitor.visit(this);
    }

}