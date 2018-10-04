package com.neusoft.features.search;

import com.neusoft.features.exception.CustomRuntimeException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;


/**
 * SearchSupport.java
 *
 * @author <A>cheng.yy@neusoft.com</A>
 * @date May 11, 2015
 * @Copyright: © 2001-2015 东软集团股份有限公司
 */
@SuppressWarnings("deprecation")
public class SearchHelper implements SearchSupport {

    private Client searchClient;
    private String highlightCSS = "span class='match',span";

    public SearchHelper(SearchClientFactory searchClientFactory) throws Exception {
        this.searchClient = searchClientFactory.getClient();
    }

    public void setHighlightCSS(String highlightCSS) {
        this.highlightCSS = highlightCSS;
    }

    @Override
    public IndexResponse indexDocument(String indexName, String typeName, Map<String, Object> docMap) {
        IndexResponse response = this.searchClient.prepareIndex(indexName, typeName).setSource(docMap).get();
        return response;
    }


    @Override
    public GetResponse getDocument(String ineexName, String typeName, String id) {
        GetResponse response = this.searchClient.prepareGet("twitter", "tweet", "1").setOperationThreaded(false).get();
        return response;
    }

    @Override
    public DeleteResponse deleteIndex(String indexName) {
        DeleteResponse response = this.searchClient.prepareDelete().setIndex(indexName).get();
        return response;
    }


    @Override
    public boolean bulkIndexDocuments(String indexName, HashMap<String, Object[]> contentMap) throws CustomRuntimeException {

        try {
            XContentBuilder xContentBuilder = null;
            xContentBuilder = XContentFactory.jsonBuilder().startObject();
            Iterator<Entry<String, Object[]>> iterator = contentMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                String field = entry.getKey();
                Object[] values = entry.getValue();
                String formatValue = formatInsertData(values);
                xContentBuilder = xContentBuilder.field(field, formatValue);
            }
            xContentBuilder = xContentBuilder.endObject();
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            bulkRequest.add(searchClient.prepareIndex(indexName, indexName).setSource(xContentBuilder));
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (!bulkResponse.hasFailures()) {
                return true;
            } else {
                throw new CustomRuntimeException(bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }

    }

    /*
 * 搜索（支持json版本）(non-Javadoc)
 *
 * @see
 * com.mattdamon.core.search.SearchProvider#simpleSearch(java.lang.String[],
 * byte[], int, int, java.lang.String, java.lang.String)
 */
    @Override
    public List<Map<String, Object>> simpleSearch(String[] indexNames, byte[] queryString, int from, int offset, String sortField, String sortType) throws CustomRuntimeException {

        try {
            if (offset <= 0) {
                return null;
            }
            SearchRequestBuilder searchRequestBuilder = searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /* 如果不需要排序 */
            } else {
                /* 如果需要排序 */
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }

            String query = new String(queryString);
            searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return getSearchResult(searchResponse);
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }


    /* 格式化数据 */
    private String formatInsertData(Object[] values) throws CustomRuntimeException {
        if (!checkValue(values)) {
            return "";
        }
//        if (DateUtiler.isDate(values[0])) {
//            return DateUtiler.formatDate(values[0]);
//        }
        String formatValue = values[0].toString();
        for (int index = 1; index < values.length; ++index) {
            formatValue += "," + values[index].toString();
        }
        return formatValue.trim();
    }

    /* 简单的值校验 */
    private boolean checkValue(Object[] values) throws CustomRuntimeException {
        if (values == null) {
            return false;
        } else if (values.length == 0) {
            return false;
        } else if (values[0] == null) {
            return false;
        } else if (values[0].toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /* 获得搜索结果 */
    private List<Map<String, Object>> getSearchResult(SearchResponse searchResponse) throws CustomRuntimeException {
        try {
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (SearchHit searchHit : searchResponse.getHits()) {
                Iterator<Entry<String, Object>> iterator = searchHit.getSource().entrySet().iterator();
                HashMap<String, Object> resultMap = new HashMap<String, Object>();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    resultMap.put(entry.getKey(), entry.getValue());
                }
                Map<String, HighlightField> highlightMap = searchHit.highlightFields();
                Iterator<Entry<String, HighlightField>> highlightIterator = highlightMap.entrySet().iterator();
                while (highlightIterator.hasNext()) {
                    Entry<String, HighlightField> entry = highlightIterator.next();
                    Object[] contents = entry.getValue().fragments();
                    if (contents.length == 1) {
                        resultMap.put(entry.getKey(), contents[0].toString());
                    } else {
//                        logger.systemLog("搜索结果中的高亮结果出现多数据contents.length = "
//                                + contents.length);
                    }
                }
                resultList.add(resultMap);
            }
            return resultList;
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }


    /*
 * 搜索 (non-Javadoc)
 *
 * @see
 * com.mattdamon.core.search.SearchProvider#simpleSearch(java.lang.String[],
 * java.util.HashMap, java.util.HashMap, int, int, java.lang.String,
 * java.lang.String)
 */
    @Override
    public List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset, String sortField, String sortType) throws CustomRuntimeException {

        SearchOption.SearchLogic searchLogic = indexNames.length > 1 ? SearchOption.SearchLogic.should : SearchOption.SearchLogic.must;
        return simpleSearch(indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic, from, offset, sortField, sortType);
    }

    /*
     * 搜索(non-Javadoc)
     *
     * @see
     * com.mattdamon.core.search.SearchProvider#simpleSearch(java.lang.String[],
     * java.util.HashMap, com.mattdamon.core.search.SearchOption.SearchLogic,
     * java.util.HashMap, com.mattdamon.core.search.SearchOption.SearchLogic,
     * int, int, java.lang.String, java.lang.String)
     */
    @Override
    public List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, SearchOption.SearchLogic searchLogic, HashMap<String, Object[]> filterContentMap, SearchOption.SearchLogic filterLogic, int from, int offset, String sortField, String sortType) throws CustomRuntimeException {

        try {
            if (offset <= 0) {
                return null;
            }
            QueryBuilder queryBuilder = null;
            queryBuilder = createQueryBuilder(searchContentMap, searchLogic);
            queryBuilder = createFilterBuilder(filterLogic, queryBuilder, searchContentMap, filterContentMap);
            SearchRequestBuilder searchRequestBuilder = searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT).setQuery(queryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /* 如果不需要排序 */
            } else {
                /* 如果需要排序 */
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = createHighlight(searchRequestBuilder, searchContentMap);

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return getSearchResult(searchResponse);
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }

    /*
     * 搜索（复杂）(non-Javadoc)
     *
     * @see
     * com.mattdamon.core.search.SearchProvider#complexSearch(java.lang.String
     * [], java.util.HashMap, java.util.HashMap, int, int, java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<Map<String, Object>> complexSearch(String[] indexNames, HashMap<String, Object[]> mustSearchContentMap, HashMap<String, Object[]> shouldSearchContentMap, int from, int offset, String sortField, String sortType) throws CustomRuntimeException {
        if (offset <= 0) {
            return null;
        }
        /* 创建must搜索条件 */
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchOption.SearchLogic.must);
        /* 创建should搜索条件 */
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchOption.SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.should(shouldQueryBuilder);
        }
        try {
            SearchRequestBuilder searchRequestBuilder = null;
            searchRequestBuilder = this.searchClient.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT).setQuery(boolQueryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /* 如果不需要排序 */
            } else {
                /* 如果需要排序 */
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, mustSearchContentMap);
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, shouldSearchContentMap);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }

    /*
     * 获得推荐列表(non-Javadoc)
     *
     * @see
     * com.mattdamon.core.search.SearchProvider#getSuggest(java.lang.String[],
     * java.lang.String, java.lang.String, int)
     */
    @Override
    public List<String> getSuggest(String[] indexNames, String fieldName, String value, int count) throws CustomRuntimeException {
        // try {
        // SuggestRequestBuilder suggestRequestBuilder = new
        // SuggestRequestBuilder(
        // this.searchClient);
        // suggestRequestBuilder = suggestRequestBuilder
        // .setIndices(indexNames)..field(fieldName).term(value)
        // .size(count);// .similarity(0.5f);
        // SuggestResponse suggestResponse = suggestRequestBuilder.execute()
        // .actionGet();
        // return suggestResponse.suggestions();
        // } catch (Exception e) {
        //
        // throw new CustomRuntimeException(e);
        // }
        return null;
    }


    /* 创建搜索条件 */
    private QueryBuilder createQueryBuilder(HashMap<String, Object[]> searchContentMap, SearchOption.SearchLogic searchLogic) throws CustomRuntimeException {
        try {
            if (searchContentMap == null || searchContentMap.size() == 0) {
                return null;
            }
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            /* 循环每一个需要搜索的字段和值 */
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                String field = entry.getKey();
                Object[] values = entry.getValue();
                /* 排除非法的搜索值 */
                if (!checkValue(values)) {
                    continue;
                }
                /* 获得搜索类型 */
                SearchOption searchOption = getSearchOption(values);
                QueryBuilder queryBuilder = createSingleFieldQueryBuilder(field, values, searchOption);
                if (queryBuilder != null) {
                    if (searchLogic == SearchOption.SearchLogic.should) {
                        /* should关系，也就是说，在A索引里有或者在B索引里有都可以 */
                        boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                    } else {
                        /* must关系，也就是说，在A索引里有，在B索引里也必须有 */
                        boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
            return boolQueryBuilder;
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }

    /* 获得搜索选项 */
    private SearchOption getSearchOption(Object[] values) throws CustomRuntimeException {
        try {
            for (Object item : values) {
                if (item instanceof SearchOption) {
                    return (SearchOption) item;
                }
            }
            return new SearchOption();
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }

    private QueryBuilder createSingleFieldQueryBuilder(String field, Object[] values, SearchOption searchOption) throws CustomRuntimeException {
        try {
            if (searchOption.getSearchType() == SearchOption.SearchType.range) {
                /* 区间搜索 */
                return createRangeQueryBuilder(field, values);
            }
            // String[] fieldArray =
            // field.split(",");/*暂时不处理多字段[field1,field2,......]搜索情况*/
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (Object valueItem : values) {
                if (valueItem instanceof SearchOption) {
                    continue;
                }
                QueryBuilder queryBuilder = null;
                String formatValue = valueItem.toString().trim().replace("*", "");// 格式化搜索数据
                if (searchOption.getSearchType() == SearchOption.SearchType.term) {
                    queryBuilder = QueryBuilders.termQuery(field, formatValue).boost(searchOption.getBoost());
                } else if (searchOption.getSearchType() == SearchOption.SearchType.querystring) {
                    if (formatValue.length() == 1) {
                        /*
                         * 如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，
                         * 就不需要通配符了
                         */
                        if (!Pattern.matches("[0-9]", formatValue)) {
                            formatValue = "*" + formatValue + "*";
                        }
                    }
//                    QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders
//                            .queryString(formatValue).minimumShouldMatch(
//                                    searchOption.getQueryStringPrecision());
//                    queryBuilder = queryStringQueryBuilder.field(field).boost(
//                            searchOption.getBoost());
                }
                if (searchOption.getSearchLogic() == SearchOption.SearchLogic.should) {
                    boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                } else {
                    boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                }
            }
            return boolQueryBuilder;
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }

    private RangeQueryBuilder createRangeQueryBuilder(String field, Object[] values) throws CustomRuntimeException {

        try {

            if (values.length == 1 || values[1] == null || values[1].toString().trim().isEmpty()) {
                throw new CustomRuntimeException(
//                        ErrorDescription.ERROR_SEARCH_RANGE_QUERY_OCCURRED_0
                );
            }

            boolean timeType = false;
//            if (DateUtiler.isDate(values[0])) {
//                if (DateUtiler.isDate(values[1])) {
//                    timeType = true;
//                }
//            }
            String begin = "", end = "";
            if (timeType) {
                /*
                 * 如果时间类型的区间搜索出现问题，有可能是数据类型导致的：
                 * （1）在监控页面（elasticsearch-head）中进行range搜索，看看什么结果，如果也搜索不出来，则：
                 * （2）请确定mapping中是date类型，格式化格式是yyyy-MM-dd HH:mm:ss
                 * （3）请确定索引里的值是类似2012-01-01 00:00:00的格式
                 * （4）如果是从数据库导出的数据，请确定数据库字段是char或者varchar类型，而不是date类型（此类型可能会有问题）
                 */
//                begin = DateUtiler.formatDate(values[0]);
//                end = DateUtiler.formatDate(values[1]);
            } else {
                begin = values[0].toString();
                end = values[1].toString();
            }
            return QueryBuilders.rangeQuery(field).from(begin).to(end);
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }

    /*
     * 创建过滤条件
     */
    private QueryBuilder createFilterBuilder(SearchOption.SearchLogic searchLogic, QueryBuilder queryBuilder, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) throws Exception {
//        try {
//            Iterator<Entry<String, Object[]>> iterator = searchContentMap
//                    .entrySet().iterator();
//            AndFilterBuilder andFilterBuilder = null;
//            while (iterator.hasNext()) {
//                Entry<String, Object[]> entry = iterator.next();
//                Object[] values = entry.getValue();
//                /* 排除非法的搜索值 */
//                if (!checkValue(values)) {
//                    continue;
//                }
//                SearchOption searchOption = getSearchOption(values);
//                if (searchOption.getDataFilter() == DataFilter.exists) {
//                    /* 被搜索的条件必须有值 */
//                    ExistsFilterBuilder existsFilterBuilder = FilterBuilders
//                            .existsFilter(entry.getKey());
//                    if (andFilterBuilder == null) {
//                        andFilterBuilder = FilterBuilders
//                                .andFilter(existsFilterBuilder);
//                    } else {
//                        andFilterBuilder = andFilterBuilder
//                                .add(existsFilterBuilder);
//                    }
//                }
//            }
//            if (filterContentMap == null || filterContentMap.isEmpty()) {
//                /* 如果没有其它过滤条件，返回 */
//                return QueryBuilders.filteredQuery(queryBuilder,
//                        andFilterBuilder);
//            }
//            /* 构造过滤条件 */
//            QueryFilterBuilder queryFilterBuilder = FilterBuilders
//                    .queryFilter(createQueryBuilder(filterContentMap,
//                            searchLogic));
//            /* 构造not过滤条件，表示搜索结果不包含这些内容，而不是不过滤 */
//            NotFilterBuilder notFilterBuilder = FilterBuilders
//                    .notFilter(queryFilterBuilder);
//            return QueryBuilders.filteredQuery(queryBuilder, FilterBuilders
//                    .andFilter(andFilterBuilder, notFilterBuilder));
//        } catch (Exception e) {
//            throw new CustomRuntimeException(e);
//        }
        return null;
    }

    private SearchRequestBuilder createHighlight(SearchRequestBuilder searchRequestBuilder, HashMap<String, Object[]> searchContentMap) throws CustomRuntimeException {
        Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
        /* 循环每一个需要搜索的字段和值 */
        while (iterator.hasNext()) {
            Entry<String, Object[]> entry = iterator.next();
            String field = entry.getKey();
            Object[] values = entry.getValue();
            /* 排除非法的搜索值 */
            if (!checkValue(values)) {
                continue;
            }
            /* 获得搜索类型 */
            SearchOption searchOption = getSearchOption(values);
            if (searchOption.isHighlight()) {
                /*
                 * http://www.elasticsearch.org/guide/reference/api/search/
                 * highlighting.html
                 *
                 * fragment_size设置成1000，默认值会造成返回的数据被截断
                 */
                searchRequestBuilder = searchRequestBuilder.addHighlightedField(field, 1000).setHighlighterPreTags("<" + highlightCSS.split(",")[0] + ">").setHighlighterPostTags("</" + highlightCSS.split(",")[1] + ">");
            }
        }
        return searchRequestBuilder;
    }


    private SearchResponse searchCountRequest(String[] indexNames, Object queryBuilder) throws CustomRuntimeException {
        try {
            SearchRequestBuilder searchRequestBuilder = searchClient.prepareSearch(indexNames).setSearchType(SearchType.COUNT);
            if (queryBuilder instanceof QueryBuilder) {
                searchRequestBuilder = searchRequestBuilder.setQuery((QueryBuilder) queryBuilder);

            }
            if (queryBuilder instanceof byte[]) {
                String query = new String((byte[]) queryBuilder);
                searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
            }
            return searchRequestBuilder.execute().actionGet();
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
    }


}
