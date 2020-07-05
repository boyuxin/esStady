package com.yuxin.esapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuxin.esapi.dto.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.spec.OAEPParameterSpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    //创建索引
    @Test
    void testCreaateIndex() throws IOException {
        // 创建索引请求
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("boyuxin_geo");
        // 客户端请求执行  请求之后 获得相应 createIndexResponse
        CreateIndexResponse createIndexResponse =
                restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //创建获取索引
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("boyuxin_index");
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //删除获取索引
    @Test
    void testDeletetIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("boyuxin_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    //测试添加文档
    @Test
    void addDocument() throws IOException {
        //创建对象
        User user1 = new User("柏鱼洗", 10);
        //创建请求
        IndexRequest request = new IndexRequest("boyuxin_index");
        // 规则 put/boyuxin_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(3000));

        //放入请求
        request.source(JSON.toJSONString(user1), XContentType.JSON);

        //客户端 发送请求
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index);
    }

    //获取文档  判断是否存在
    @Test
    void textIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("boyuxin_index", "1");
        //不获取返回的上下文 效率 更高
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //获取文档  获取文档
    @Test
    void textgetExists() throws IOException {
        GetRequest getRequest = new GetRequest("boyuxin_index", "555");
        //不获取返回的上下文 效率 更高
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documentFields);//打印的是全部信息
        System.out.println(documentFields.getSourceAsString());//获取部分信息
    }

    //获取文档  更新文档
    @Test
    void textuodteExists() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("boyuxin_index", "1");
        updateRequest.timeout(TimeValue.timeValueSeconds(300));
        User user = new User();
        user.setAge(5555);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update);
    }

    //获取文档  删除请求
    @Test
    void textdeleteExists() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("boyuxin_index", "1");
        deleteRequest.timeout(TimeValue.timeValueSeconds(300));
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete);

    }

    //获取文档  批量导入
    @Test
    void textinsertAllExists() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1000s");
        ArrayList<User> users = new ArrayList<>();
        for (int i = 100; i < 1000; i++) {
            User user = new User("柏宇鑫" + i, i + 1);
            users.add(user);
            bulkRequest.add(new IndexRequest("boyuxin_index")
                    .id("" + i + 1)
                    .source(JSON.toJSONString(user), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    //查询
    @Test
    void textserchtAllExists() throws IOException {
        SearchRequest searchRequest = new SearchRequest("boyuxin_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //termQuery  精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "2");
        searchSourceBuilder.query(termQueryBuilder);
        /*searchSourceBuilder.from();
        searchSourceBuilder.size();*/
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(search.getHits()));

    }

    @Test
    public void select() {
        SearchRequest searchRequest = new SearchRequest("boyuxin_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    }

    //获取文档  批量导入
    @Test
    void textinsertAllGeo() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1000s");
        ArrayList<MyGeo> users = new ArrayList<>();
        for (int i = 1; i < 500000; i++) {
            Map<String, String> stringStringMap = randomLonLat(121.45049561474607, 121.5013073823242, 31.19115507754109, 31.15355484939741);

            MYPoint geoPoint = new MYPoint(Double.valueOf(stringStringMap.get("W")),Double.valueOf(stringStringMap.get("J")));
            MyGeo myGeo = new MyGeo(CreatName.getChineseName(),geoPoint);
            users.add(myGeo);
            bulkRequest.add(new IndexRequest("my_geo")
                    .source(JSON.toJSONString(myGeo), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk);
    }


    //测试添加文档
    @Test
    void addDocumentGeo() throws IOException {
        //创建对象
        Map<String, String> stringStringMap = randomLonLat(121.45049561474607, 121.5013073823242, 31.19115507754109, 31.15355484939741);
        MYPoint geoPoint = new MYPoint(Double.valueOf(stringStringMap.get("W")),Double.valueOf(stringStringMap.get("J")));
        MyGeo myGeo = new MyGeo(CreatName.getChineseName(),geoPoint);
        //创建请求
        IndexRequest request = new IndexRequest("my_geo");

        //放入请求
        System.out.println(JSON.toJSONString(myGeo));
        request.source(JSON.toJSONString(myGeo), XContentType.JSON);

        //客户端 发送请求
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index);
    }



    @Test
    public void searchGeo() throws IOException {
        SearchRequest searchRequest = new SearchRequest("my_geo");
        SearchSourceBuilder ssb = new SearchSourceBuilder();

        //上海市
        GeoPoint geoPoint = new GeoPoint(31.19115507754109,121.4079235932617);
        //geo距离查询  name=geo字段
        QueryBuilder qb = QueryBuilders
                .geoDistanceQuery("location")
                //距离 9KM
                .distance(5d, DistanceUnit.KILOMETERS)
                //上海市
                .point(geoPoint);
        GeoDistanceSortBuilder sortBuilder = SortBuilders
                .geoDistanceSort("location", geoPoint)
                .order(SortOrder.ASC);
        ssb.sort(sortBuilder);
        ssb.query(qb);
        ssb.from(0);
        ssb.size(100);
        searchRequest.source(ssb);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        for (SearchHit hit : response.getHits().getHits()) {
            MyGeo myGeo = JSON.toJavaObject(JSON.parseObject(hit.getSourceAsString()), MyGeo.class);
            double distance = getDistance(geoPoint.getLat(), geoPoint.getLon(), myGeo.getLocation().getLat(), myGeo.getLocation().getLon());
            System.out.println("相距目标定位  "+distance +"米 ---> "+myGeo);
        }


    }





    public void searchGeoSort() throws IOException {
        SearchRequest searchRequest = new SearchRequest("my_geo");
        SearchSourceBuilder ssb = new SearchSourceBuilder();

        //工体的坐标
        GeoPoint geoPoint = new GeoPoint(39.93367367974064d,116.47845257733152d);

        GeoDistanceSortBuilder sortBuilder = SortBuilders
                .geoDistanceSort("location", geoPoint)
                .order(SortOrder.ASC);

        ssb.sort(sortBuilder);
        searchRequest.source(ssb);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsString());
        }
    }









    public Map<String, String> randomLonLat(double MinLon, double MaxLon, double MinLat, double MaxLat) {
        BigDecimal db = new BigDecimal(Math.random() * (MaxLon - MinLon) + MinLon);
        String lon = db.setScale(14, BigDecimal.ROUND_HALF_UP).toString();// 小数后6位
        db = new BigDecimal(Math.random() * (MaxLat - MinLat) + MinLat);
        String lat = db.setScale(14, BigDecimal.ROUND_HALF_UP).toString();
        Map<String, String> map = new HashMap<>();
        map.put("J", lon);
        map.put("W", lat);
        return map;
    }

    private static final double EARTH_RADIUS = 6371393; // 平均半径,单位：m；不是赤道半径。赤道为6378左右

    /**
     * @描述 反余弦进行计算
     * @参数  [lat1, lng1, lat2, lng2]
     * @返回值  double
     * @创建人  Young
     * @创建时间  2019/3/13 20:31
     **/
    public static double getDistance(Double lat1,Double lng1,Double lat2,Double lng2) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(lng1); // A经弧度
        double radiansAY = Math.toRadians(lat1); // A纬弧度
        double radiansBX = Math.toRadians(lng2); // B经弧度
        double radiansBY = Math.toRadians(lat2); // B纬弧度

        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY);
//        System.out.println("cos = " + cos); // 值域[-1,1]
        double acos = Math.acos(cos); // 反余弦值
//        System.out.println("acos = " + acos); // 值域[0,π]
//        System.out.println("∠AOB = " + Math.toDegrees(acos)); // 球心角 值域[0,180]
        return EARTH_RADIUS * acos; // 最终结果
    }
    public static void main(String[] args) {
        //121.717594,31.12055    121.817629,31.090867
        double distance = getDistance(31.12055, 121.717594,
                31.090867, 121.817629);
        System.out.println("距离" + distance  + "米");
    }







}
