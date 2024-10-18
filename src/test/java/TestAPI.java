import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class TestAPI {
    private static Map<String, List<String>> breeds;
    private String baseUrl = "https://dog.ceo/api/breed/";
    public void getMapOfDogs(Response response) {
        Map<String, List<String>> mapOfDogs = response.jsonPath().getMap("message");
//        System.out.println(mapOfDogs);
        breeds = mapOfDogs;
    }

    @Test
    public void getAndValidateMapDogsApiAll(){
        String url = "https://dog.ceo/api/breeds/list/all";
        getWithSuccessHavingMessageAndStatus(url);
        Response response = RestAssured.get(url).andReturn();
        getMapOfDogs(response);
    }
    @Test
    public void listOfAllBreeds() {
        when().get("https://dog.ceo/api/breeds/list/all")
                .then()
                .log().everything()
                .statusCode(200).body("$", hasKey("message")).and().body("$", hasKey("status")).and().body("status", equalTo("success"));
    }
    @Test
    public void randomImageApiTest(){
        String url = "https://dog.ceo/api/breeds/image/random";
        getWithSuccessHavingMessageAndStatus(url);
    }
    public void getWithSuccessHavingMessageAndStatus(String url) {
        RestAssured.get(url).then().statusCode(200).assertThat()
                .body("$", hasKey("message")).and().body("$", hasKey("status"))
                .and().body("status", equalTo("success"))
                .log().everything();
    }
    public static Response doGetRequest(String endpoint) {
        return null;
    }
    @Test
    public void testEachBreedAndSubBreed() {
        String url = "https://dog.ceo/api/breeds/list/all";
        getWithSuccessHavingMessageAndStatus(url);
        Response response = RestAssured.get(url).andReturn();
        getMapOfDogs(response);
        for (String breed : breeds.keySet()) {
            getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/list", breed));
            List<String> subBreeds = breeds.get(breed);
            if (subBreeds.isEmpty()) {
                getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/images", breed));
            } else {
                for (String subBreed : subBreeds) {
                    getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/%s/images", breed, subBreed));
                    getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/%s/images/random", breed, subBreed));
                    for(int i = 0; i < 10; i++){
                        getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/%s/images/random/%d", breed, subBreed, i));
                    }

                }
            }
        }
    }
    @Test
    public void testEachBreed(){
        String url = "https://dog.ceo/api/breeds/list/all";
        getWithSuccessHavingMessageAndStatus(url);
        Response response = RestAssured.get(url).andReturn();
        getMapOfDogs(response);
        for (String breed : breeds.keySet()) {
            getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/list", breed));
            getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/images", breed));
            getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/images/random", breed));
            for(int i = 0; i < 10; i++){
                getWithSuccessHavingMessageAndStatus(String.format("https://dog.ceo/api/breed/%s/images/random/%d", breed, i));
            }
        }
    }
}
