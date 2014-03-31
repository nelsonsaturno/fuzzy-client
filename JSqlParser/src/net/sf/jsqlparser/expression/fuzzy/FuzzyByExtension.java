package net.sf.jsqlparser.expression.fuzzy;

import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class FuzzyByExtension implements Expression {

    private class Element {
        public Double possibility;
        public Expression expression;

        public Element(Double possibility, Expression expression) {
            this.possibility = possibility;
            this.expression = expression;
        }
    }

    private ArrayList<Element> elements;

    public FuzzyByExtension(Double possibility, Expression expression) {
        this.elements = new ArrayList();
        this.elements.add(new Element(possibility, expression));
    }

    public void addPossibility(Double possibility, Expression expression) {
        this.elements.add(new Element(possibility, expression));
    }

    public void accept(ExpressionVisitor expressionVisitor) throws Exception {
        expressionVisitor.visit(this);
    }
}
    
