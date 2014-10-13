/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.translator;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.helpers.Logger;
import fuzzy.type3.translator.Translator;
import fuzzy.type5.operations.CreateFuzzyType5DomainOperation;
import java.sql.SQLException;
import java.util.List;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.fuzzy.domain.AlterFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyDomain;
import net.sf.jsqlparser.statement.fuzzy.domain.CreateFuzzyType2Domain;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.table.AlterTable;
import net.sf.jsqlparser.statement.table.CreateTable;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

/**
 *
 * @author hector
 */
public class StatementType5Translator extends Translator implements StatementVisitor {

    public StatementType5Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    @Override
    public void visit(Select select) throws Exception { }

    @Override
    public void visit(Delete delete) throws Exception { }

    @Override
    public void visit(Update update) throws Exception { }

    @Override
    public void visit(Insert insert) throws Exception { }

    @Override
    public void visit(Replace replace) throws Exception { }

    @Override
    public void visit(Drop drop) throws Exception { }

    @Override
    public void visit(Truncate truncate) throws Exception { }

    @Override
    public void visit(CreateTable createTable) throws Exception {
        CreateTableTranslator translator = new CreateTableTranslator(connector, operations);
        translator.translate(createTable);
    }

    @Override
    public void visit(AlterTable alterTable) throws Exception { }

    @Override
    public void visit(CreateFuzzyDomain fuzzyDomain) throws Exception {
        // Instruccion corresponde a tipo 3
    }

    @Override
    public void visit(CreateFuzzyType2Domain fuzzyDomain) throws Exception {
        String type3DomainName = fuzzyDomain.getType().toLowerCase();
        
        // Instruccion corresponde a tipo 2
        if ( Connector.isNativeDataType(type3DomainName) ) {
            return;
        }
        
        String schemaName;
        
        try {
            schemaName = Helper.getSchemaName(connector);
        } catch (SQLException ex) {
            Logger.debug(StatementType5Translator.class.getName() + ": " + "Error getting schema name");
            throw new SQLException("Error getting schema name");
        }
        
        if ( schemaName == null || schemaName.equals("") ) {
            Logger.debug(StatementType5Translator.class.getName() + ": " + "Error getting schema name");
            throw new SQLException("Error getting schema name");
        }
        
        Integer type3DomainId = getFuzzyDomainId(schemaName, type3DomainName);
        
        // domainName es un tipo Nativo o no esta definido como tipo 3
        if ( type3DomainId == null ) {
            Logger.debug(StatementType5Translator.class.getName() + ": " + "type3 Domain not found");
            throw new SQLException("type3 Domain not found");
        }
        
        CreateFuzzyType5DomainOperation operation = 
                new CreateFuzzyType5DomainOperation(connector, 
                        fuzzyDomain.getName(), 
                        type3DomainId);
        
        operations.add(operation);
        this.ignoreAST = true;
    }

    @Override
    public void visit(AlterFuzzyDomain alterFuzzyDomain) throws Exception { }
}
