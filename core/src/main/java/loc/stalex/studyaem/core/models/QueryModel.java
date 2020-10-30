package loc.stalex.studyaem.core.models;

import loc.stalex.studyaem.core.services.QueryBuilderSearch;
import loc.stalex.studyaem.core.services.QuerySearchType;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class QueryModel {

    @Inject
    private QueryBuilderSearch querySearchType;

    @Inject
    @Optional
    private String searchPath;

    @Inject
    @Optional
    private String searchText;

    @Inject
    @Optional
    private String queryType;

    private String searchResult;

    @PostConstruct
    protected void init() {
        if (searchPath == null || searchText == null) {
            searchResult = "Nothing was found";
        } else {
            searchResult = querySearchType.executeQuery(searchPath, searchText);
        }
    }

    public String getSearchPath() {
        return searchPath;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getSearchResult() {
        return searchResult;
    }
}
