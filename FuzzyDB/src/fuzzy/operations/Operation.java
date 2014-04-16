/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 *
 * @author bishma-stornelli
 */
public abstract class Operation {

    protected Connector connector;

    Operation(Connector connector) {
        this.connector = connector;
    }

    /**
     * Executes the operation
     * 
     * @throws SQLException 
     */
    public abstract void execute() throws SQLException;

    /**
     * Start a transaction in the current connection. If there's no connection
     * a new connection will be opened.
     * 
     * @return a Savepoint to rollback the transaction if needed.
     */
    protected Savepoint beginTransaction() throws SQLException {
        this.connector.getConnection().setAutoCommit(false);
        return this.connector.getConnection().setSavepoint();
    }

    /**
     * Commit the current transaction.
     */
    protected void commitTransaction() throws SQLException {
        this.connector.getConnection().commit();
    }
    
    protected void rollback(Savepoint sp) throws SQLException {
        this.connector.getConnection().rollback(sp);
    }
}
