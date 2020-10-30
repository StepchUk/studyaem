package loc.stalex.studyaem.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

public class QueryManagerSearch implements QuerySearchType {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public String executeQuery(String searchPath, String searchText) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String sqlStatement = "select * from [dam:Asset] as a where ISDESCENDANTNODE(a, '" + searchPath + "') and contains(*, '%" + searchText + "%') and name() like '%.pdf'";
//            String sqlStatement = "select * from [dam:Asset] where contains(*, '%" + searchText + "%') and name() like '%.pdf'";

            Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");

            QueryResult execute = query.execute();

            NodeIterator nodes = execute.getNodes();

            while (nodes.hasNext()) {
                Node node = nodes.nextNode();
                stringBuilder.append("Name: ").append(node.getName()).append(" ")
                        .append("Path: ").append(node.getPath()).append("<br />");
            }
        } catch(Exception e) {
            log.debug("QueryManagerSearch: {}", e.getMessage());
        }

        return stringBuilder.toString().isEmpty() ? "Nothing was found" : stringBuilder.toString();
    }
}
