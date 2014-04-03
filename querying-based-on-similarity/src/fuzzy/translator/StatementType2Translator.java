package fuzzy.translator;

import fuzzy.database.Connector;
import fuzzy.operations.DropFuzzyDomainOperation;
import fuzzy.helpers.Helper;
import fuzzy.operations.AlterFuzzyDomainOperation;
import fuzzy.operations.CreateFuzzyDomainFromColumnOperation;
import fuzzy.operations.CreateFuzzyDomainOperation;
import fuzzy.operations.Operation;
import fuzzy.operations.RemoveFuzzyColumnsOperation;
import java.util.List;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Relation;
import net.sf.jsqlparser.expression.Similarity;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.fuzzy.domain.AlterFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyType2Domain;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.CreateTable;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class StatementType2Translator extends Translator implements StatementVisitor {

    public StatementType2Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }
    

    @Override
    public void visit(CreateTable createTable) throws Exception {
    }
    

    @Override
    public void visit(AlterTable alterTable) throws Exception {
    }


    @Override
    public void visit(Select select) throws Exception {
    }


    @Override
    public void visit(CreateFuzzyDomain createFuzzyDomain) throws Exception {
    }


    @Override
    public void visit(AlterFuzzyDomain alterFuzzyDomain) throws Exception {
    }


    @Override
    public void visit(Delete delete) throws Exception {
    }


    @Override
    public void visit(Drop drop) throws Exception {
    }


    @Override
    public void visit(Insert insert) throws Exception {

    }


    @Override
    public void visit(Replace replace) throws Exception {
    }


    @Override
    public void visit(Truncate truncate) throws Exception {
    }


    @Override
    public void visit(Update update) throws Exception {
    }


    @Override
    public void visit(CreateFuzzyType2Domain fuzzyDomain) throws Exception {
    }
}
