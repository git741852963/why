package com.neusoft.features.search;

import com.neusoft.features.exception.CustomRuntimeException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SearchProvider.java
 *
 * @author <A>zhang.zhj@neusoft.com</A>
 */
public interface SearchSupport {


    /**
     * 根据Index和Type索引文档
     *
     * @param indexName 索引名
     * @param typeName  类型名
     * @param docMap    文档信息
     * @return
     */
    IndexResponse indexDocument(String indexName, String typeName, Map<String, Object> docMap);


    /**
     * 根据ID取得文档数据
     *
     * @param ineexName 索引名
     * @param typeName  类型名
     * @param id        文档ID
     * @return
     */
    GetResponse getDocument(String ineexName, String typeName, String id);


    /**
     * 删除索引
     *
     * @param indexName
     * @return
     */
    DeleteResponse deleteIndex(String indexName);

    /**
     * 批量索引数据
     *
     * @param indexName
     * @param documentMap
     * @return
     * @throws CustomRuntimeException
     */
    boolean bulkIndexDocuments(String indexName, HashMap<String, Object[]> documentMap) throws CustomRuntimeException;

    /**
     * 搜索（支持json版本）
     *
     * @param indexNames  索引名称
     * @param queryString 搜索字符串
     * @param from        从第几条记录开始（必须大于等于0）
     * @param offset      一共显示多少条记录（必须大于0）
     * @param sortField   排序字段名称
     * @param sortType    排序方式（asc，desc）
     * @return
     */
    List<Map<String, Object>> simpleSearch(String[] indexNames, byte[] queryString, int from, int offset, @Nullable String sortField, @Nullable String sortType) throws CustomRuntimeException;


    /**
     * 搜索
     *
     * @param indexNames       索引名称
     * @param searchContentMap 搜索内容HashMap
     * @param filterContentMap 过滤内容HashMap
     * @param from             从第几条记录开始（必须大于等于0）
     * @param offset           一共显示多少条记录（必须大于0）
     * @param sortField        排序字段名称
     * @param sortType         排序方式（asc，desc）
     * @return
     */
    List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, @Nullable HashMap<String, Object[]> filterContentMap, int from, int offset, @Nullable String sortField, @Nullable String sortType) throws CustomRuntimeException;

    /**
     * 搜索
     *
     * @param indexNames       索引名称
     * @param searchContentMap 搜索内容HashMap
     * @param searchLogic      搜索条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
     * @param filterContentMap 过滤内容HashMap
     * @param filterLogic      过滤条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
     * @param from             从第几条记录开始（必须大于等于0）
     * @param offset           一共显示多少条记录（必须大于0）
     * @param sortField        排序字段名称
     *                         （asc，desc）
     * @return
     */
    List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, SearchOption.SearchLogic searchLogic, @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchOption.SearchLogic filterLogic, int from, int offset, @Nullable String sortField, @Nullable String sortType) throws CustomRuntimeException;

    /**
     * 搜索（复杂）
     *
     * @param indexNames             索引名称
     * @param mustSearchContentMap   must内容HashMap
     * @param shouldSearchContentMap should内容HashMap
     * @param from                   从第几条记录开始（必须大于等于0）
     * @param offset                 一共显示多少条记录（必须大于0）
     * @param sortField              排序字段名称
     * @param sortType               排序方式（asc，desc）
     * @return
     */
    List<Map<String, Object>> complexSearch(String[] indexNames, @Nullable HashMap<String, Object[]> mustSearchContentMap, @Nullable HashMap<String, Object[]> shouldSearchContentMap, int from, int offset, @Nullable String sortField, @Nullable String sortType) throws CustomRuntimeException;

    /**
     * 获得推荐列表
     *
     * @param indexNames 索引名称
     * @param fieldName  字段名称
     * @param value      值
     * @param count      数量
     * @return
     */
    List<String> getSuggest(String[] indexNames, String fieldName, String value, int count) throws CustomRuntimeException;


//    /**
//     * 批量删除数据，危险
//     *
//     * @param indexName
//     *            索引名称
//     * @param contentMap
//     *            删除数据
//     * @return
//     */
//    boolean bulkDeleteDocuments(String indexName,
//                           HashMap<String, Object[]> contentMap) throws CustomRuntimeException;
//
//
//    /**
//     * 批量更新数据，先删除，再插入，需要传递新数据的完整数据
//     *
//     * @param indexName
//     *            索引名称
//     * @param oldContentMap
//     *            旧数据
//     * @param newContentMap
//     *            新数据
//     * @return
//     */
//    boolean bulkUpdateData(String indexName,
//                           HashMap<String, Object[]> oldContentMap,
//                           HashMap<String, Object[]> newContentMap) throws CustomRuntimeException;
//
//    /**
//     * 批量更新数据，先删除，再插入，只需要传递新数据的差异键值
//     *
//     * @param indexName
//     *            索引名称
//     * @param oldContentMap
//     *            旧数据
//     * @param newContentMap
//     *            新数据
//     * @return
//     */
//    boolean autoBulkUpdateData(String indexName,
//                               HashMap<String, Object[]> oldContentMap,
//                               HashMap<String, Object[]> newContentMap) throws CustomRuntimeException;
//
//    /**
//     * 获得搜索结果总数（支持json版本）
//     *
//     * @param indexNames
//     *            索引名称
//     * @param queryString
//     *            所搜字符串
//     * @return
//     */
//    long getCount(String[] indexNames, byte[] queryString)
//            throws CustomRuntimeException;
//
//    /**
//     * 获得搜索结果总数
//     *
//     *
//     * @param indexNames
//     *            索引名称
//     * @param searchContentMap
//     *            搜索内容HashMap
//     * @param filterContentMap
//     *            过滤内容HashMap
//     * @return
//     */
//    long getCount(String[] indexNames,
//                  HashMap<String, Object[]> searchContentMap,
//                  @Nullable HashMap<String, Object[]> filterContentMap)
//            throws CustomRuntimeException;
//
//    /**
//     * 获得搜索结果总数
//     *
//     * @param indexNames
//     *            索引名称
//     * @param searchContentMap
//     *            搜索内容HashMap
//     * @param searchLogic
//     *            搜索条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
//     * @param filterContentMap
//     *            过滤内容HashMap
//     * @param filterLogic
//     *            过滤条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
//     * @return
//     */
//    long getCount(String[] indexNames,
//                  HashMap<String, Object[]> searchContentMap,
//                  SearchLogic searchLogic,
//                  @Nullable HashMap<String, Object[]> filterContentMap,
//                  @Nullable SearchLogic filterLogic) throws CustomRuntimeException;
//
//    /**
//     * 获得搜索结果总数（复杂）
//     *
//     * @param indexNames
//     *            索引名称
//     * @param mustSearchContentMap
//     *            must内容HashMap
//     * @param shouldSearchContentMap
//     *            should内容HashMap
//     * @return
//     */
//    long getComplexCount(String[] indexNames,
//                         @Nullable HashMap<String, Object[]> mustSearchContentMap,
//                         @Nullable HashMap<String, Object[]> shouldSearchContentMap)
//            throws CustomRuntimeException;
//

//
//    /**
//     * 搜索（去掉排序参数的简化版本）
//     *
//     * @param indexNames
//     *            索引名称
//     * @param searchContentMap
//     *            搜索内容HashMap
//     * @param filterContentMap
//     *            过滤内容HashMap
//     * @param from
//     *            从第几条记录开始（必须大于等于0）
//     * @param offset
//     *            一共显示多少条记录（必须大于0）
//     * @return
//     */
//    List<Map<String, Object>> simpleSearch(String[] indexNames,
//                                           HashMap<String, Object[]> searchContentMap,
//                                           @Nullable HashMap<String, Object[]> filterContentMap, int from,
//                                           int offset) throws CustomRuntimeException;
//

//    /**
//     * 分组统计
//     *
//     * @param indexName
//     *            索引名称
//     * @param mustSearchContentMap
//     *            must内容HashMap
//     * @param shouldSearchContentMap
//     *            should内容HashMap
//     * @param groupFields
//     *            分组字段
//     * @return
//     */
//    Map<String, String> group(String[] indexName,
//                              @Nullable HashMap<String, Object[]> mustSearchContentMap,
//                              @Nullable HashMap<String, Object[]> shouldSearchContentMap,
//                              String[] groupFields) throws CustomRuntimeException;
//
//    /**
//     *
//     * @param indexNames
//     * @param mustSearchContentMap
//     * @param shouldSearchContentMap
//     * @return
//     * @throws CustomRuntimeException
//     */
//    Map<String, Long> aggregation(String[] indexNames,
//                                  HashMap<String, Object[]> mustSearchContentMap,
//                                  HashMap<String, Object[]> shouldSearchContentMap, String field)
//            throws CustomRuntimeException;
}