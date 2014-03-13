/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.translator;

import fuzzy.Helper;
import fuzzy.database.Connector.TranslateResult;
import fuzzy.operations.*;
import fuzzy.database.Connector;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

/**
 *
 * @author bishma-stornelli
 */
public class AlterTableChangeTest {
    
    protected static Connector connector;
    protected static Connection connection;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();;
    
    public AlterTableChangeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        connector = new Connector();
        connection = connector.getConnection();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws SQLException {
        Helper.setConnector(connector);
        connection.createStatement().executeUpdate("CREATE DATABASE fuzzy_ddl_test");
        connector.setCatalog("fuzzy_ddl_test");
        connection.createStatement().executeUpdate("CREATE TABLE people ("
                + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(64), "
                + "height DECIMAL UNSIGNED ZEROFILL, "
                + "birthdate DATE, "
                + "comments TEXT)");
        connection.createStatement().executeUpdate("INSERT INTO people(name, height, birthdate) "
                + "VALUES ('Michael Jordan', 1.98, '1963-02-17'),"
                + "('Jennifer Aniston', 1.64, '1969-02-11'),"
                + "('Milla Jovovich', 1.74, '1975-12-17'),"
                + "('Buddah', NULL, NULL),"
                + "(NULL, 2.35, NULL)");
    }
    
    @After
    public void tearDown() throws SQLException {
        connector.setCatalog("information_schema");
        connection.createStatement().executeUpdate("DROP DATABASE fuzzy_ddl_test");
        Helper.cleanSchemaMetaData("fuzzy_ddl_test");      
    }
    
    @Test
    public void createAnOperation() throws Exception{
        TranslateResult translate = connector.translate("ALTER TABLE fuzzy_ddl_test.people CHANGE name names ciudad NOT NULL");
        assertNull(translate.sql);
        assertNotNull(translate.operations);
        assertEquals(1, translate.operations.size());
        assertEquals(ChangeColumnOperation.class, translate.operations.get(0).getClass());
        ChangeColumnOperation o = (ChangeColumnOperation) translate.operations.get(0);
        assertEquals("ciudad", o.getDataType());
        assertEquals("name", o.getOldColumnName());
        assertEquals("names", o.getNewColumnName());
        assertEquals("NOT NULL ", o.getOptions());
        assertEquals("fuzzy_ddl_test", o.getSchemaName());
        assertEquals("people", o.getTableName());
    }
}
