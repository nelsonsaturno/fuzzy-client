package fuzzy.translator;

import fuzzy.database.Connector;
import fuzzy.operations.DropFuzzyDomainOperation;
import fuzzy.helpers.Helper;
import fuzzy.operations.AlterFuzzyDomainOperation;
import fuzzy.operations.CreateFuzzyDomainFromColumnOperation;
import fuzzy.operations.CreateFuzzyDomainOperation;
import fuzzy.operations.Operation;
import fuzzy.operations.RemoveFuzzyColumnsOperation;
import fuzzy.operations.CreateFuzzyType2DomainOperation;
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
        CreateTableType2Translator translator = new CreateTableType2Translator(connector, operations);
        translator.translate(createTable);
    }
    

    @Override
    public void visit(AlterTable alterTable) throws Exception {
        throw new UnsupportedOperationException("Alter table is not supported yet.");
    }


    @Override
    public void visit(Select select) throws Exception {
        SelectType2Translator translator = new SelectType2Translator(connector);
        SelectBody selectBody = select.getSelectBody();
        //selectBody.accept(translator);
    }


    @Override
    public void visit(CreateFuzzyDomain createFuzzyDomain) throws Exception {
        // Nada, el otro translator es encargado de traducir esto.
    }


    @Override
    public void visit(AlterFuzzyDomain alterFuzzyDomain) throws Exception {
        // Nada, el otro translator es encargado de traducir esto.
    }


    @Override
    public void visit(CreateFuzzyType2Domain fuzzyDomain) throws Exception {
        String name = fuzzyDomain.getName();
        String type = fuzzyDomain.getType();
        CreateFuzzyType2DomainOperation op = new CreateFuzzyType2DomainOperation(connector, name, type);
        String lower_bound = fuzzyDomain.getLowerBound();
        String upper_bound = fuzzyDomain.getUpperBound();
        op.setBounds(lower_bound, upper_bound);
        operations.add(op);
    }


    @Override
    public void visit(Delete delete) throws Exception {
    }


    @Override
    public void visit(Drop drop) throws Exception {
        /*
        Si es un fuzzy domain, ver si es fuzzy tipo 2 y borrarlo.
        Y dropear las tablas en cascade?
        */
    }


    @Override
    public void visit(Insert insert) throws Exception {
        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(connector);
        insert.getItemsList().accept(translator);
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

}
