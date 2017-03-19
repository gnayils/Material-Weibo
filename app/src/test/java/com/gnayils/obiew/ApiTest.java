package com.gnayils.obiew;

import com.gnayils.obiew.util.URLParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Gnayils on 12/03/2017.
 */

@Ignore
public class ApiTest {


    public void testJsonParser() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/Gnayils/GitHubRepositories/obiew/app/src/testJsonParser/res/comment_timeline.json"))));

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

    @Test
    public void testUrlDecode() throws MalformedURLException, UnsupportedEncodingException {
        URL url = new URL("https://api.weibo.com/oauth2/authorize?client_id=2388828892&scope=friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog&amp;redirect_uri=http://baidu.com&display=mobile&forcelogin=true");
        Map<String, List<String>> maps = URLParser.decode(url);
        System.out.println(maps.get("scope"));
    }
}
