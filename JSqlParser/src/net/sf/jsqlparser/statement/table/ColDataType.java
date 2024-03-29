package net.sf.jsqlparser.statement.table;

import java.util.List;

import net.sf.jsqlparser.statement.select.PlainSelect;

public class ColDataType {

    private String dataType;
    private List argumentsStringList;

    public List getArgumentsStringList() {
        return argumentsStringList;
    }

    public String getDataType() {
        return dataType;
    }

    public void setArgumentsStringList(List list) {
        argumentsStringList = list;
    }

    public void setDataType(String string) {
        dataType = string;
        if ( string != null )
            dataType = string.toLowerCase();
    }

    public String toString() {
        return dataType + (argumentsStringList!=null?" "+PlainSelect.getStringList(argumentsStringList, true, true):"");
    }
}