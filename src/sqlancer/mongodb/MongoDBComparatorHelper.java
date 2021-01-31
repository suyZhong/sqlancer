package sqlancer.mongodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import sqlancer.IgnoreMeException;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.mongodb.MongoDBProvider.MongoDBGlobalState;
import sqlancer.mongodb.query.MongoDBSelectQuery;

public final class MongoDBComparatorHelper {

    private MongoDBComparatorHelper() {
    }

    public static List<Document> getResultSetAsDocumentList(MongoDBSelectQuery adapter, MongoDBGlobalState state)
            throws Exception {
        ExpectedErrors errors = adapter.getExpectedErrors();
        List<Document> result;
        try {
            adapter.executeAndGet(state);
            result = adapter.getResultSet();
            return result;

        } catch (Exception e) {
            if (e instanceof IgnoreMeException) {
                throw e;
            }

            if (e.getMessage() == null) {
                throw new AssertionError(adapter.getLogString(), e);
            }

            if (errors.errorIsExpected(e.getMessage())) {
                throw new IgnoreMeException();
            }
            throw new AssertionError(adapter.getLogString(), e);
        }
    }

    public static void assumeResultSetsAreEqual(List<Document> resultSet, List<Document> secondResultSet,
            MongoDBSelectQuery originalQuery) {
        if (resultSet.size() != secondResultSet.size()) {
            String assertionMessage = String.format("The Size of the result sets mismatch (%d and %d)!\n%s",
                    resultSet.size(), resultSet.size(), originalQuery.getLogString());
            throw new AssertionError(assertionMessage);
        }

        Set<Document> firstHashSet = new HashSet<>(resultSet);
        Set<Document> secondHashSet = new HashSet<>(secondResultSet);

        if (!firstHashSet.equals(secondHashSet)) {
            Set<Document> firstResultSetMisses = new HashSet<>(firstHashSet);
            firstResultSetMisses.removeAll(secondHashSet);
            Set<Document> secondResultSetMisses = new HashSet<>(secondHashSet);
            secondResultSetMisses.removeAll(firstHashSet);
            StringBuilder firstMisses = new StringBuilder();
            for (Document document : firstResultSetMisses) {
                firstMisses.append(document.toJson()).append(" ");
            }
            StringBuilder secondMisses = new StringBuilder();
            for (Document document : secondResultSetMisses) {
                secondMisses.append(document.toJson()).append(" ");
            }
            String assertMessage = String.format("The Content of the result sets mismatch!\n %s \n %s\n %s",
                    firstMisses.toString(), secondMisses.toString(), originalQuery.getLogString());
            throw new AssertionError(assertMessage);
        }
    }
}
