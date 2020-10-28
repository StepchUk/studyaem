package loc.stalex.studyaem.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=QueryManager testing",
        "sling.servlet.paths=/services/studyaem/querymanager",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.resourceTypes=sling/servlet/default"
})
public class QueryManagerTestServlet extends SlingAllMethodsServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private QueryBuilder builder;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                          SlingHttpServletResponse response) throws ServletException, IOException {

        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);

            String searchTerm = "industry leadership";

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String sqlStatement = "select * from [dam:Asset] where contains(*, '%" + searchTerm + "%') and name() like '%.pdf'";

            javax.jcr.query.Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");

            QueryResult execute = query.execute();

            NodeIterator nodes = execute.getNodes();

            JSONArray optionsArray = new JSONArray();

            while (nodes.hasNext()) {
                JSONObject eachOption = new JSONObject();
                Node node = nodes.nextNode();
                eachOption.put("name", node.getName());
                eachOption.put("path", node.getPath());
                optionsArray.put(eachOption);
            }

            JSONObject finalJsonResponse = new JSONObject();

            finalJsonResponse.put("result", optionsArray.length() != 0 ? optionsArray : "empty");

            response.getWriter().println(finalJsonResponse.toString());
        } catch(Exception e) {
            log.debug("QueryManagerTest: {}", e.getMessage());
        }
//        try {
//            Session session = request.getResourceResolver().adaptTo(Session.class);
//
//            String searchTerm = "industry leadership";
//
//            Map<String, String> map = new HashMap<>();
//
//            map.put("path", "/content/dam");
//            map.put("type", "dam:Asset");
//            map.put("nodename", "*.pdf");
//            map.put("fulltext", "%" + searchTerm + "%");
//            map.put("p.limit", "-1");
//
//            Query query = builder.createQuery(PredicateGroup.create(map), session);
//
//            SearchResult result = query.getResult();
//
//            JSONArray optionsArray = new JSONArray();
//
//            // iterating over the results
//            for (Hit hit : result.getHits()) {
//                JSONObject eachOption = new JSONObject();
//                eachOption.put("name", hit.getTitle());
//                eachOption.put("path", hit.getPath());
//                optionsArray.put(eachOption);
//            }
//
//            JSONObject finalJsonResponse = new JSONObject();
//
//            finalJsonResponse.put("result", optionsArray.length() != 0 ? optionsArray : "empty");
//
//            response.getWriter().println(finalJsonResponse.toString());
//        } catch (JSONException | IOException | RepositoryException e) {
//            log.debug("QueryManagerTest: {}", e.getMessage());
//        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException, IOException {

        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);

            String searchPath = request.getParameter("searchpath");
            String searchTerm = request.getParameter("searchterm");

            Map<String, String> map = new HashMap<>();

            map.put("path", searchPath);
            map.put("type", "dam:Asset");
            map.put("nodename", "*.pdf");
            map.put("fulltext", "%" + searchTerm + "%");
            map.put("p.limit", "-1");

            Query query = builder.createQuery(PredicateGroup.create(map), session);

            SearchResult result = query.getResult();

            JSONArray optionsArray = new JSONArray();

            for (Hit hit : result.getHits()) {
                JSONObject eachOption = new JSONObject();
                eachOption.put("name", hit.getTitle());
                eachOption.put("path", hit.getPath());
                optionsArray.put(eachOption);
            }

            JSONObject finalJsonResponse = new JSONObject();

            finalJsonResponse.put("result", optionsArray.length() != 0 ? optionsArray : "empty");

            response.getWriter().println(finalJsonResponse.toString());
        } catch (JSONException | IOException | RepositoryException e) {
            log.debug("QueryManagerTest: {}", e.getMessage());
        }
    }
}
