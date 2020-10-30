package loc.stalex.studyaem.core.services;

import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

@Component(service = QueryManagerSearch.class)
public class QueryManagerSearch implements QuerySearchType {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    SlingRepository repository;

    @Override
    public String executeQuery(String searchPath, String searchText) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Session session = repository.loginService("testwritelistener", null);

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String sqlStatement = "select * from [dam:Asset] as a where ISDESCENDANTNODE(a, '" + searchPath + "') and contains(*, '%" + searchText + "%') and name() like '%.pdf'";

            Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");

            QueryResult execute = query.execute();

            NodeIterator nodes = execute.getNodes();

            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                stringBuilder.append("Name: ").append(node.getName()).append(" ")
                        .append("Path: ").append(node.getPath());
            }
        } catch(Exception e) {
            log.debug("QueryManagerSearch: {}", e.getMessage());
        }

        return stringBuilder.toString().isEmpty() ? "Nothing was found" : stringBuilder.toString();
    }
}
