package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.fuzzy.constant.CreateFuzzyConstant;
import net.sf.jsqlparser.statement.fuzzy.domain.OrderedDomain;

/**
 * A class to de-parse (that is, transform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class CreateFuzzyConstantDeParser {

    protected StringBuffer buffer;


    public CreateFuzzyConstantDeParser(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public void deParse(CreateFuzzyConstant createFuzzyConstant) {
        buffer.append("CREATE FUZZY CONSTANT ")
              .append(createFuzzyConstant.getName())
              .append(" ")
              .append(createFuzzyConstant.getDomain())
              .append(" := ")
              .append(createFuzzyConstant.getValue());
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }
}
