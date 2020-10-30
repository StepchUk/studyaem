package loc.stalex.studyaem.core.services;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component(service = QueryBuilderSearch.class)
public class QueryBuilderSearch implements QuerySearchType {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder builder;

    @Override
    public String executeQuery(String searchPath, String searchText) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);

            Map<String, String> map = new HashMap<>();

            map.put("path", searchPath);
            map.put("type", "dam:Asset");
            map.put("nodename", "*.pdf");
            map.put("fulltext", "%" + searchText + "%");
            map.put("p.limit", "-1");

            Query query = builder.createQuery(PredicateGroup.create(map), session);

            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                stringBuilder.append("Name: ").append(hit.getTitle()).append(" ")
                        .append("Path: ").append(hit.getPath()).append("<br />");
            }
        } catch (Exception e) {
            log.debug("QueryBuilderSearch: {}", e.getMessage());
        }

        return stringBuilder.toString().isEmpty() ? "Nothing was found" : stringBuilder.toString();
    }
}
