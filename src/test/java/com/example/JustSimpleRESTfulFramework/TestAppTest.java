package com.example.JustSimpleRESTfulFramework;

import com.alibaba.fastjson.JSON;
import com.example.JustSimpleRESTfulFramework.model.TestRequestBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class TestAppTest {
    ExecutorService executorService;

    @BeforeAll
    void setUp() {
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> new RESTApplication().bootstrap(TestApp.class));
    }

    @Test
    void should_get_NOT_FOUND_when_url_of_request_is_not_matched() {
        given()
                .get("/not-match-path")
                .then()
                .statusCode(404)
                .body(Matchers.containsString("Not Found"));
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_GET() {
        given()
                .get("/test")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("test_get"));
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_POST() {
        given()
                .post("/test")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("test_post"));
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_with_sub_resource() {
        given()
                .get("/test/sub-resource/get")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("test_sub_resource"));
    }

    @Test
    void should_get_string_query_param_when_url_of_request_is_matched_and_with_query_param() {
        given()
                .get("/test/string-query-param?name=hello")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("hello"));
    }

    @Test
    void should_get_integer_query_param_when_url_of_request_is_matched_and_with_query_param() {
        given()
                .get("/test/integer-query-param?count=10")
                .then()
                .statusCode(200)
                .body(Matchers.equalTo("10"));
    }

    @Test
    void should_get_array_query_param_when_url_of_request_is_matched_and_with_query_param() {
        given()
                .get("/test/array-query-param?tags=1&tags=abc&tags=hello")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("1"), Matchers.containsString("abc"), Matchers.containsString("hello"));
    }

    @Test
    void should_get_list_query_param_when_url_of_request_is_matched_and_with_query_param() {
        given()
                .get("/test/list-query-param?tags=1&tags=abc&tags=hello")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("1"), Matchers.containsString("abc"), Matchers.containsString("hello"));
    }

    @Test
    void should_get_path_param_when_url_of_request_is_matched_and_with_path_param() {
        given()
                .get("/test/path-param/123?id=456")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("123"));
    }

    @Test
    void should_get_request_body_when_url_of_request_is_matched_and_with_request_body() {
        TestRequestBody body = new TestRequestBody("123", "test", 10.5);

        given()
                .body(JSON.toJSONString(body))
                .post("/test/request-body")
                .then()
                .statusCode(200)
                .body("id", Matchers.is("123"))
                .body("name", Matchers.is("test"))
                .body("age", Matchers.is(10.5F));
    }

    @AfterAll
    void cleanUp() {
        executorService.shutdown();
    }
}
