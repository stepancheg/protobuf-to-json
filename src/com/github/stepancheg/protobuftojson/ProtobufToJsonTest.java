package com.github.stepancheg.protobuftojson;

import com.github.stepancheg.protobuftojson.test.TestData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Stepan Koltsov
 */
public class ProtobufToJsonTest {

    @Test
    public void simple() {
        TestData.City.Builder city = TestData.City.newBuilder();
        city.setName("London");

        {
            TestData.Citizen.Builder citizen = city.addCitizensBuilder();
            citizen.setName("John");
            citizen.setAge(30);
        }
        {
            TestData.Citizen.Builder citizen = city.addCitizensBuilder();
            citizen.setName("Paul");
            citizen.setAge(40);
        }

        String jsonString = ProtobufToJson.protobufToJsonString(city.build());
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonString);

        JsonObject expected = new JsonObject();
        expected.addProperty("name", "London");
        JsonArray citizens = new JsonArray();
        {
            JsonObject citizen = new JsonObject();
            citizen.addProperty("name", "John");
            citizen.addProperty("age", 30);
            citizens.add(citizen);
        }
        {
            JsonObject citizen = new JsonObject();
            citizen.addProperty("name", "Paul");
            citizen.addProperty("age", 40);
            citizens.add(citizen);
        }

        expected.add("citizens", citizens);

        Assert.assertEquals(expected, jsonObject);
    }

}
