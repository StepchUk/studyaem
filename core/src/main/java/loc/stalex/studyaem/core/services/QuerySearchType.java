package loc.stalex.studyaem.core.services;

public interface QuerySearchType {

    String queryManagerSearch(String searchPath, String searchText);

    String queryBuilderSearch(String searchPath, String searchText);
}
