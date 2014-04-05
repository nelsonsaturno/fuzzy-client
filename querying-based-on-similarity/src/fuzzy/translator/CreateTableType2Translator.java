package fuzzy.translator;

import fuzzy.database.Connector;
import fuzzy.helpers.Helper;
import fuzzy.operations.AddFuzzyColumnOperation;
import fuzzy.operations.Operation;
import java.sql.SQLException;
import java.util.Iterator;

import java.util.List;
import net.sf.jsqlparser.statement.table.ColumnDefinition;
import net.sf.jsqlparser.statement.table.CreateTable;

/*
        Ver qué columnas son de difusas tipo 2.
        Agregarlas a la tabla de columnas difusas.
        Cambiarle el tipo al tipo difuso que le toca según el dominio.
        Agregarle los CHECKs del dominio difuso, si los tiene
 */
public class CreateTableType2Translator extends Translator {

    CreateTableType2Translator(Connector connector, List<Operation> operations) {
        super(connector, operations);
    }

    public void translate(CreateTable createTable)
        throws SQLException {
        String schemaName = Helper.getSchemaName(connector, createTable.getTable());
        String tableName = createTable.getTable().getName();
        if (createTable.getColumnDefinitions() != null) {
            for (Iterator iter = createTable.getColumnDefinitions().iterator(); iter.hasNext();) {
                ColumnDefinition columnDefinition = (ColumnDefinition) iter.next();
                String columnName = columnDefinition.getColumnName();
                String dataType = columnDefinition.getColDataType().getDataType();
                Integer domainId = null;
                if ((domainId = getFuzzyDomainId(schemaName, dataType)) != null) {
                    columnDefinition.getColDataType().setDataType("INTEGER");
                    // I got to translate the default value too, because it must references
                    // the label id and not the label
                    List<String> columnSpecs = columnDefinition.getColumnSpecStrings();
                    for (int i = 0; columnSpecs != null && i < columnSpecs.size() - 1; ++i) {
                        if (columnSpecs.get(i).equalsIgnoreCase("DEFAULT")) {
                            // NEXT ITEM SHOULD BE THE DEFAULT VALUE SELECTED
                            String defaultValue = columnSpecs.get(i + 1);
                            // It should have ' or " to enclose the value so we'll remove it
                            defaultValue = defaultValue.substring(1, defaultValue.length() - 1);

                            Integer defaultValueId = getFuzzyLabelId(schemaName, dataType, defaultValue);
                            // Now, defaultValueId is what we want as default value
                            columnSpecs.set(i + 1, defaultValueId.toString());
                            // And since columnSpecs should have only 0 or 1 DEFAULT keys
                            break;
                        }
                    }
                    // Add index from this column to fuzzy domains
                    // createTable.getColumnDefinitions().add(buildIndexForFuzzyColumn(schemaName, dataType));

                    // Queue a query to insert this definition in the metadata
                    operations.add(new AddFuzzyColumnOperation(connector, schemaName, tableName, columnName, domainId));
                }
            }
        }
    }

    /** It supposes to build an index to add a foreign key to the fuzzy domain
     * but Foreign key is not supported by the parser. so....
     * 
     * @param schemaName
     * @param domainName
     * @return 
     */
    private Object buildIndexForFuzzyColumn(String schemaName, String domainName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
