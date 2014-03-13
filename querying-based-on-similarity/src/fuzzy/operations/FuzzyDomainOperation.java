/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.operations;

import fuzzy.database.Connector;
import fuzzy.ddl.Domain;
import fuzzy.ddl.Label;
import fuzzy.ddl.Relation;
import fuzzy.ddl.Similarity;
import fuzzy.helpers.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bishma-stornelli
 */
abstract public class FuzzyDomainOperation extends Operation {
    
    protected Relation<String> relation;
    
    FuzzyDomainOperation(Connector connector) {
        super(connector);
    }

    public void addLabel(String label) throws Exception {
        relation.addLabel(label);
    }

    public void addSimilarity(String label1, String label2, double value) throws Exception {
        relation.addSimilarity(label1, label2, value);
    }

    public void dropLabel(String label) throws Exception {
        relation.dropLabel(label);
    }

    public void dropSimilarity(String label1, String label2) throws Exception {
        relation.dropSimilarity(label1, label2);
    }

    public void calculate() throws Exception {
        relation.calculate();
    }

    @Override
    public void execute() throws SQLException {
        Domain domain = relation.getDomain();
        if (0 == domain.getId()) {
            String sql = "INSERT INTO information_schema_fuzzy.domains "
                            + "VALUES (null, (SELECT database()), '"//TODO escape
                            + domain.getName() + "')";
            domain.setId(connector.fastInsert(sql));
        }
        List<String> labelsToCreate = new ArrayList<String>();
        List<String> labelsToDelete = new ArrayList<String>();
        for (Label<String> l : (Iterable<Label<String>>)relation.getLabels()) {
            if (l.isToBeCreated()) {
                labelsToCreate.add(l.getName());
                String sql = "INSERT INTO information_schema_fuzzy.labels "
                                + "VALUES (null, "
                                + l.getDomain().getId() +",'"
                                + l.getName() + "')";//TODO escapar
                l.setId(connector.fastInsert(sql));
            } else if (l.isToBeDropped()) {
                labelsToDelete.add(l.getName());
                String sql = "DELETE FROM information_schema_fuzzy.labels "
                                + "WHERE label_id = " + l.getId();
                Logger.debug(sql);
                connector.fastUpdate(sql);
            }
        }
        if (labelsToCreate.size() > 0){
            String sql = "INSERT INTO information_schema_fuzzy.labels "
                    + "VALUES ";
            for (int i = 0 ; i < labelsToCreate.size() ; ++i) {
                String label = labelsToCreate.get(i);
                sql += "(null, " + domain.getId() + ", '" + label + "')";
                if (i != labelsToCreate.size() - 1) {
                    sql += ",";
                }
            }
        }
        
        for (Similarity<String> s : (Iterable<Similarity<String>>)
                                                   relation.getSimilarities()) {
            if (s.isToBeCreated()) {
                String sql = "INSERT INTO information_schema_fuzzy.similarities "
                                + "VALUES ("
                                + s.getLabel1().getId() +","
                                + s.getLabel2().getId() + ","
                                + s.getValue() + ",b'"
                                + s.getDerivated() + "')";
                connector.fastUpdate(sql);
            } else if (s.isToBeAltered()) {
                String sql = "UPDATE information_schema_fuzzy.similarities "
                                + "SET derivated=" + s.getDerivated()
                                + " WHERE label1_id = " + s.getLabel1().getId()
                                + " AND label2_id = " + s.getLabel2().getId();
                connector.fastUpdate(sql);
            } else if (s.isToBeDropped()) {
                String sql = "DELETE FROM information_schema_fuzzy.similarities "
                                + "WHERE label1_id = " + s.getLabel1().getId()
                                + " AND label2_id = " + s.getLabel2().getId();
                connector.fastUpdate(sql);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final FuzzyDomainOperation other = (FuzzyDomainOperation) obj;
        if ((this.relation == null) ? (other.relation != null) : !this.relation.equals(other.relation)) {
            return false;
        }
        return true;
    }
    
    
}
