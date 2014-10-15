/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.database.Connector;
import java.util.Iterator;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import fuzzy.common.translator.FuzzyColumnSet;
import fuzzy.common.translator.TableRefList;


public class SelectTranslator implements SelectVisitor {

    protected Connector connector;
    private boolean mainselect;


    public SelectTranslator(Connector connector) {
        this.connector = connector;
        this.mainselect = false;
    }

    public SelectTranslator(Connector connector, boolean mainselect) {
        this.connector = connector;
        this.mainselect = mainselect;
    }

    @Override
    public void visit(PlainSelect plainSelect) throws Exception {
        TableRefList tableRefSet = new TableRefList(connector, plainSelect);
        FuzzyColumnSet fuzzyColumnSet = new FuzzyColumnSet(connector, tableRefSet, plainSelect, 5);

        FuzzyExpTranslator translator = new FuzzyExpTranslator(this.connector, this.mainselect, fuzzyColumnSet);

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
