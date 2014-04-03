package net.sf.jsqlparser.statement.fuzzy.domain;

public class BoundedOrderedDomain extends OrderedDomain {
    
    public static enum Type {
        CHAR, INTEGER, REAL
    }

    private Type type;
    private String lowerBound;
    private String upperBound;

    public BoundedOrderedDomain(Type type, String lowerBound, String upperBound) {
        this.type = type;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String toString() {
        return this.lowerBound + ".." + this.upperBound;
    }

}