/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.type5.operations;

import fuzzy.common.operations.Operation;
import fuzzy.database.Connector;
import java.sql.SQLException;

/**
 *
 * @author hector
 */
public class CreateFuzzyDomainOperation extends Operation {

    String domainName;
    Integer type3DomainId;

    public CreateFuzzyDomainOperation(Connector connector, 
            String domainName, Integer type3DomainId) {
        
        super(connector);
        this.domainName = domainName;
        this.type3DomainId = type3DomainId;
    }

    @Override
    public void execute() throws SQLException {
        String catalog = this.connector.getSchema();
        
        String insertDomainCatalog = "INSERT INTO information_schema_fuzzy.domains "
                                   + "VALUES (DEFAULT, "               // domain_id
                                   + "'" + catalog + "' ,"             // table_schema
                                   + "'" + this.domainName + "' ,"     // domain_name
                                   + " 5 ,"                            // domain_type
                                   + " " + this.type3DomainId + ");";  // type3_domain_id
    
        
        /*
        * CREATE TYPE test_schema.test AS (
        *    odd real[], 
        *    value integer[],
        * )
        */
        String fullTypeName = catalog + "." + domainName;
        String createType = "CREATE TYPE " + fullTypeName + " AS ("
                           + "odd real[], "
                           + "value TEXT ARRAY)";
        
        this.connector.executeRaw(insertDomainCatalog);
        this.connector.executeRaw(createType);
    }
}