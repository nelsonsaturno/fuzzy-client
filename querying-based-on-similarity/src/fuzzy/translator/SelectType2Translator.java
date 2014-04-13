package fuzzy.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import static fuzzy.translator.TableRef.ParentType.JOIN;
import static fuzzy.translator.TableRef.ParentType.PLAIN_SELECT;
import static fuzzy.translator.TableRef.ParentType.SUB_JOIN;
import static fuzzy.translator.TableRef.TableType.SUB_SELECT;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;


public class SelectType2Translator implements SelectVisitor {

    protected Connector connector;
    private boolean mainselect;


    public SelectType2Translator(Connector connector) {
        this.connector = connector;
        this.mainselect = false;
    }

    public SelectType2Translator(Connector connector, boolean mainselect) {
        this.connector = connector;
        this.mainselect = mainselect;
    }

    @Override
    public void visit(PlainSelect plainSelect) throws Exception {
        TableRefList tableRefSet = new TableRefList(connector, plainSelect);
        FuzzyColumnSet fuzzyColumnSet = new FuzzyColumnSet(connector, tableRefSet, plainSelect, 2);
        tableRefSet.debugDump();
        fuzzyColumnSet.debugDump();

        FuzzyType2ExpTranslator translator = new FuzzyType2ExpTranslator(this.connector, this.mainselect, fuzzyColumnSet);

        for (SelectItem item : (List<SelectItem>) plainSelect.getSelectItems()) {
            item.accept(translator);
        }

        translator.setMainselect(false);
        Expression where = plainSelect.getWhere();
        if (null != where) {
            where.accept(translator);
        }
    }

    @Override
    public void visit(Union union) throws Exception {
        for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext();) {
            PlainSelect plainSelect = (PlainSelect) iter.next();
            plainSelect.accept(this);
        }
    }

}
