package fuzzy.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;
import java.sql.Savepoint;


public class CreateFuzzyType2DomainOperation extends Operation {

    private String name;
    private String type;
    private String upper_bound;
    private String lower_bound;

    public CreateFuzzyType2DomainOperation(Connector connector, String name, String type) {
        super(connector);
        this.name = name;
        this.type = type;
        this.upper_bound = null;
        this.lower_bound = null;
    }

    public void setBounds(String lower_bound, String upper_bound) {
        this.upper_bound = upper_bound;
        this.lower_bound = lower_bound;
    }

    @Override
    public void execute() throws SQLException {

        if (this.connector.getCatalog().equals("")) {
            throw new SQLException("No database selected");
        }
        String catalog = this.connector.getCatalog();

        /*
        * Por ejemplo, CREATE FUZZY DOMAIN test AS POSSIBILITY DISTRIBUTION
        * ON 1..100, y el schema actual es 'test_schema', se van a generar las
        * siguientes consultas:
        */


        /*
        * INSERT INTO information_schema_fuzzy.domains2 
        * VALUES (DEFAULT, 'test_schema', 'test', 'INTEGER', '1', '100')
        */
        String insert_domain_catalog = "INSERT INTO information_schema_fuzzy.domains2 "
                                     + "VALUES (DEFAULT, "
                                     + "'" + catalog + "' ,"
                                     + "'" + this.name + "' ,"
                                     + "'" + this.type + "', "
                                     + (null != this.lower_bound ? "'" + this.lower_bound + "'" : "NULL") + ", "
                                     + (null != this.upper_bound ? "'" + this.upper_bound + "'" : "NULL") + ")";
        
        /*
        * CREATE TYPE test_schema.test AS (
        *    odd real[], 
        *    value integer[],
        *    type boolean
        * )
        */
        String create_type = "CREATE TYPE " + catalog + "." + this.name + " AS ("
                           + "odd real[], "
                           + "value " + this.type + " ARRAY,"
                           + "type boolean"
                           + ")";

        /*
        * CREATE OPERATOR > (LEFTARG=test, RIGHTARG=test, PROCEDURE=fuzzy_gt)
        */
        String create_op_gt = "CREATE OPERATOR > ("
                            + "LEFTARG=" + this.name + ", "
                            + "RIGHTARG=" + this.name + ", "
                            + "PROCEDURE=fuzzy_gt"
                            + ")";

        Savepoint sp = this.beginTransaction();
        try {
            this.connector.fast(create_type);
            //this.connector.fast(create_op_gt);
            this.connector.fast(insert_domain_catalog);
            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }
        
    }

}