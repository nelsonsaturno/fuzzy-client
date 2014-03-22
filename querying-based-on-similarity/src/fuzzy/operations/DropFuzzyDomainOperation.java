/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.database.Connector;
import fuzzy.helpers.Logger;
import fuzzy.translator.Translator;
import java.sql.SQLException;

/**
 *
 * @author bishma-stornelli
 */
public class DropFuzzyDomainOperation extends Operation {

    private final String domain;

    public DropFuzzyDomainOperation(Connector connector, String domain) {
        super(connector);
        this.domain = domain;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getCatalog().equals("")) {
            throw new SQLException("No database selected");
        }
        String schemaName = this.connector.getCatalog();

        Logger.debug("Starting DROP FUZZY DOMAIN " + domain + " operation");
        String sql = "DELETE FROM information_schema_fuzzy.domains " +
            "WHERE table_schema = '"+schemaName+"'" + 
            "AND domain_name = '" + domain + "'";
        
        int rows = connector.fastUpdate(sql);
        if (rows == 0) {
            String c = connector.getCatalog();
            if (c == null || c.isEmpty()) {
                Logger.debug("No database selected");
                throw Translator.ER_NO_DB_ERROR;
            }
            Logger.debug("Unknown domain '" + domain + "'");
            throw Translator.FR_UNKNOWN_DOMAIN(domain);
        }
    }
    
}
