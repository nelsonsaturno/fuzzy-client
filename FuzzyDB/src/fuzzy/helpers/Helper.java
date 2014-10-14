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
    
    public static String getColumnIdForColumn(Connector c,
                           Table table, String columnName) throws SQLException {
        String schemaName = Helper.getSchemaName(c, table);
        String tableName = table.getName();
        String sql = "SELECT column_id "
                + "FROM information_schema_fuzzy.columns AS C "
                + "WHERE C.column_name = '" + columnName + "' AND "
                + "C.table_schema = '" + schemaName + "' AND "
                + "C.table_name = '" + tableName + "'";
        
        Logger.debug("Looking for column id with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        SQLException e = null;
        try {
            if (rs.first()) {
                return rs.getString("column_id");
            }
        } catch (SQLException ex) {
            e = ex;
        }
        throw new SQLException("ColumnId not found for " + schemaName + "." + tableName + "." + columnName, "42000", 3020, e);
    }
    
    public static String getNewRowId(Connector c,
                           Table table) throws SQLException {
        String schemaName = Helper.getSchemaName(c, table);
        String tableName = table.getName();
        String sql = "SELECT COUNT(_fuzzy_row_id)"
                + " FROM " + schemaName + "." + tableName;
        
        Logger.debug("Getting All row from user table with query:\n" + sql);
        
        ResultSet rs = c.executeRawQuery(sql);
        SQLException e = null;
        try {
            if (rs.first()) {
                return String.valueOf(rs.getInt(1) + 1);
            }
        } catch (SQLException ex) {
            e = ex;
        }
        throw new SQLException("Error getting New Row Id " + schemaName + "." + tableName, "42000", 3020, e);
    }

    
}
