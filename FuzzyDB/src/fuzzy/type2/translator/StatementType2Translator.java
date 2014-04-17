package fuzzy.type2.translator;

import fuzzy.type2.translator.CreateTableType2Translator;
import fuzzy.database.Connector;
import fuzzy.type3.operations.DropFuzzyDomainOperation;
import fuzzy.helpers.Helper;
import fuzzy.type3.operations.AlterFuzzyDomainOperation;
import fuzzy.type3.operations.CreateFuzzyDomainFromColumnOperation;
import fuzzy.type3.operations.CreateFuzzyDomainOperation;
import fuzzy.common.operations.Operation;
import fuzzy.type3.operations.RemoveFuzzyColumnsOperation;
import fuzzy.type2.operations.CreateFuzzyType2DomainOperation;
import fuzzy.type3.translator.Translator;
import java.util.List;
import java.util.ArrayList;
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
        SelectType2Translator translator = new SelectType2Translator(connector, true);
        SelectBody selectBody = select.getSelectBody();
        selectBody.accept(translator);
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

        // Mark this statement to be ignored by the translation execution.
        // This means this statement, when deparsed, won't make sense for the
        // RDBMS.
        this.ignoreAST = true;
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
        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(connector);

        List<Expression> new_exps = new ArrayList<Expression>();
        for (Expression exp : (List<Expression>) update.getExpressions()) {
            translator.setReplacement(null);
            exp.accept(translator);
            Expression replacement = translator.getReplacement();
            new_exps.add(null != replacement ? replacement : exp);
        }
        update.setExpressions(new_exps);
        if (null != update.getWhere()) {
            translator.setReplacement(null);
            update.getWhere().accept(translator);
            if (null != translator.getReplacement()) {
                update.setWhere(translator.getReplacement());
            }
        }
    }

}
