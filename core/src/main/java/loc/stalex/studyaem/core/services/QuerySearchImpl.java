package loc.stalex.studyaem.core.services;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
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
import java.util.HashMap;
import java.util.Map;

@Component(service = QuerySearchType.class)
public class QuerySearchImpl implements QuerySearchType {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    SlingRepository repository;

    @Reference
    private QueryBuilder builder;

    @Override
    public String queryManagerSearch(String searchPath, String searchText) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Session session = getSession();

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String sqlStatement = "select * from [dam:Asset] as a where ISDESCENDANTNODE(a, '" + searchPath + "') and contains(*, '%" + searchText + "%') and name() like '%.pdf'";

            Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");

            QueryResult execute = query.execute();

            NodeIterator nodes = execute.getNodes();

            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                stringBuilder.append("queryManagerSearch ").append("Name: ").append(node.getName()).append(" ")
                        .append("Path: ").append(node.getPath());
            }
        } catch(Exception e) {
            log.debug("QueryManagerSearch: {}", e.getMessage());
        }

        return stringBuilder.toString().isEmpty() ? "Nothing was found" : stringBuilder.toString();
    }

    @Override
    public String queryBuilderSearch(String searchPath, String searchText) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Session session = getSession();

            Map<String, String> map = new HashMap<>();

            map.put("path", searchPath);
            map.put("type", "dam:Asset");
            map.put("nodename", "*.pdf");
            map.put("fulltext", "%" + searchText + "%");
            map.put("p.limit", "-1");

            com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(map), session);

            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                stringBuilder.append("queryBuilderSearch ").append("Name: ").append(hit.getTitle()).append(" ")
                        .append("Path: ").append(hit.getPath());
            }
        } catch (Exception e) {
            log.debug("QueryBuilderSearch: {}", e.getMessage());
        }

        return stringBuilder.toString().isEmpty() ? "Nothing was found" : stringBuilder.toString();
    }

    private Session getSession() throws javax.jcr.RepositoryException {
        return repository.loginService("testwritelistener", null);
    }
}
