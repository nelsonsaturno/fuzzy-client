package fuzzy.operations;

import fuzzy.database.Connector;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Savepoint;


public class AddFuzzyType2ColumnOperation extends Operation {

    private String schemaName;
    private String tableName;
    private String columnName;
    private Integer domainId;

    public AddFuzzyType2ColumnOperation(Connector connector,
        String schemaName, String tableName, String columnName, Integer domainId) {
        super(connector);
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.domainId = domainId;
    }

    @Override
    public void execute() throws SQLException {
        if (this.connector.getCatalog().equals("")) {
            throw new SQLException("No database selected");
        }
        String catalog = this.connector.getCatalog();

        /*
        * Suponiendo que se agregó la siguiente columna:
        * CREATE TABLE test_schema.test_table (
        *    ...
        *    col1 fuzzy_domain
        *    ...
        * )
        * El id de fuzzy_domain es 666
        * Y es sobre Integer con rango del 1 al 100
        */

        /*
        * INSERT INTO information_schema_fuzzy.columns2
        * VALUES ('test_schema', 'test_table', 'col1', 666)
        */
        String insert_column = "INSERT INTO information_schema_fuzzy.columns2 "
                             + "VALUES ("
                             + "'" + this.schemaName + "', "
                             + "'" + this.tableName + "', "
                             + "'" + this.columnName + "', "
                             + domainId
                             + ")";

        // Se agregaron las restricciones de integridad via ALTER TABLE porque
        // no era posible hacerlo modificando el AST de la consulta original.
        // pues JSqlParser no lo modeló. Y no vale la pena ponerse a implementarlo
        // en el parser.

        /*
        * ALTER TABLE test_schema.test_table 
        * ADD CONSTRAINT col1_possibility_range
        * CHECK (0.0 <= ALL (col1.odd) AND 1.0 >= ALL (col1.odd))
        */
        String check_possibility = "ALTER TABLE " + this.schemaName + "." + this.tableName
                                 + "ADD CONSTRAINT " + this.columnName + "_possibility_range"
                                 + "CHECK (" 
                                 + "0.0 <= ALL (" + this.columnName + ".odd) AND "
                                 + "1.0 >= ALL (" + this.columnName + ".odd))";

    
        /*
        * ALTER TABLE test_schema.test_table
        * ADD CONSTRAINT col1_matching_lengths
        * CHECK (array_length(col1.odd, 1) == array_length(col1.value, 1))
        */
        String check_matching_lengths = "ALTER TABLE " + this.schemaName + "." + this.tableName
                                      + "ADD CONSTRAINT " + this.columnName + "_matching_lengths"
                                      + "CHECK (" 
                                      + "array_length(" + this.columnName + ".odd, 1) = "
                                      + "array_length(" + this.columnName + ".value, 1))";

        // Buscar el dominio a ver si fue definido sobre un rango
        // En cuyo caso también hay que agregar un CHECK para validarlo.
        String find_domain_range = "SELECT start, finish "
                                 + "FROM information_schema_fuzzy.domains2 "
                                 + "WHERE id = " + this.domainId + " "
                                 + "LIMIT 1";
        ResultSet rs = this.connector.fastQuery(find_domain_range);
        rs.next();
        String lower_bound = rs.getString("start");
        String upper_bound = rs.getString("finish");

        String check_value;
        if (null != lower_bound && null != upper_bound) {
            /*
            * ALTER TABLE test_schema.test_table ADD CONSTRAINT col1_value_range
            * CHECK (1 <= ALL (col1.value) AND 100 >= ALL (col1.value))
            */
            check_value = "ALTER TABLE " + this.schemaName + "." + this.tableName
                               + "ADD CONSTRAINT " + this.columnName + "_value_range"
                               + "CHECK (" 
                               + lower_bound + " <= ALL (" + this.columnName + ".value) AND "
                               + upper_bound + " >= ALL (" + this.columnName + ".value))";
        } else {
            // No sé que pasa si mando a JDBC una consulta que sea el string vacío
            // Y no quiero pner una guardia más abajo en el try {} para hacer o no
            // el check_value, así que mando SELECT 0. Total no hace nada.
            // Si, es un palo de escoba.
            check_value = "SELECT 0";
        }

        Savepoint sp = this.beginTransaction();
        try {
            this.connector.fast(insert_column);
            this.connector.fast(check_possibility);
            this.connector.fast(check_matching_lengths);
            this.connector.fast(check_value);
            this.commitTransaction();
        } catch (SQLException e) {
            this.rollback(sp);
            throw e;
        }

    }

}