package com.gnayils.obiew;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Gnayils on 12/03/2017.
 */

@Ignore
public class ApiTest {

    @Test
    public void test() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/Gnayils/GitHubRepositories/obiew/app/src/test/res/comment_timeline.json"))));

        JsonParser parser = new JsonParser();
        JsonElement rootJson = parser.parse(bufferedReader);
        JsonObject commentsJson = rootJson.getAsJsonObject();
        JsonArray commentJsonArray = commentsJson.getAsJsonArray("comments");
        for(int i=0; i<commentJsonArray.size(); i++) {
            JsonObject commentJson = commentJsonArray.get(i).getAsJsonObject();
            commentJson.remove("user");
            commentJson.remove("status");
        }
        System.out.println(rootJson.toString());
    }
}
