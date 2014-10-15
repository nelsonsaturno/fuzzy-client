package fuzzy.helpers;

import fuzzy.database.Connector;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.jsqlparser.schema.Table;

/**
 *
 * @author bishma-stornelli
 */
public class Helper  {
    

    public static String getSchemaName(Connector c, Table table) throws SQLException {
        return table.getSchemaName() != null ? table.getSchemaName() : getSchemaName(c);
    }
    
    public static String getSchemaName(Connector c) throws SQLException {
        return c.getSchema();
    }

    public static String getDomainNameForColumn(Connector c,
                           Table table, String columnName) throws SQLException {
        String schemaName = Helper.getSchemaName(c, table);
        String tableName = table.getName();
        String sql = "SELECT domain_name "
                + "FROM information_schema_fuzzy.domains AS D JOIN "
                + "information_schema_fuzzy.columns AS C ON (D.domain_id = C.domain_id) "
                + "WHERE C.table_schema = '" + schemaName + "' "
                + "AND C.table_name = '" + tableName + "' "
                + "AND C.column_name = '" + columnName +"'";
        
        Logger.debug("Looking for domain name with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        SQLException e = null;
        try {
            if (rs.first()) {
                return rs.getString("domain_name");
            }
        } catch (SQLException ex) {
            e = ex;
        }
        throw new SQLException("Domain name not found for " + schemaName + "." + tableName + "." + columnName, "42000", 3020, e);
    }
    /*
     * Funcion que dado un nombre de dominio (domainName) y la tabla
     * a la que pertenece (table), retorna el id del dominio tipo3
     * sobre el cual esta basado el dominio.
     * 
     * Solo retorna valores validos con dominios de tipo5 ya que estos
     * son los unicos para los cuales el atributo 'type3_domain_id' de
     * la tabla 'domains' es diferente de NULL.
     */
    public static Integer getType3DomainIdRelated(Connector c,
                           Table table, Integer domainId) throws SQLException {
        String sql = "SELECT type3_domain_id "
                + "FROM information_schema_fuzzy.domains AS D "
                + "WHERE D.domain_id = " + domainId;
        
        Logger.debug("Looking for type3domainId id with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        SQLException e = null;
        try {
            if (rs.first()) {
                return rs.getInt("type3_domain_id");
            }
        } catch (SQLException ex) {
            e = ex;
        }
        throw new SQLException("Error getting Type3DomainId related to domain with id " + domainId , "42000", 3020, e);
    }
    
}
