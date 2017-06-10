package com.gnayils.obiew;

import android.net.Uri;

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
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

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
        for (int i = 0; i < commentJsonArray.size(); i++) {
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

    @Test
    public void testShortURLRegex() {
        String regex = "http:\\/\\/t\\.cn\\/\\w{7}";
        assertTrue(Pattern.matches(regex, "http://t.cn/RaUUb1v"));

        String text = "{\"videoUrls\":[{\"result\":true,\"url_short\":\"http://t.cn/RaUUb1v\",\"url_long\":\"http://video.weibo.com/show?fid=1034:fefc87144b09http://t.cn/RaUUb1vce6b63ac4f41e260c354\",\"type\":39,\"transcode\":0}]}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testGetTopicKeywordFromUri() {
        String uriString = "com.gnayils.obiew.scheme.topic://#视野#";
        int startIndex = uriString.indexOf("#");
        int endIndex = uriString.lastIndexOf("#");
        System.out.println(startIndex + " " + endIndex);
        System.out.println(uriString.substring(startIndex + 1, endIndex));

        Matcher matcher = Pattern.compile("#[^#]+#").matcher(uriString);
        if (matcher.find()) {
            String matched = matcher.group();
            System.out.println(matched.substring(1, matched.length() - 1));
        }
    }

    @Test
    public void testGetMentionNameFromUri() {
        String uriString = "com.gnayils.obiew.scheme.topic://@Vista看天下";
        int startIndex = uriString.indexOf("@");
        System.out.println(uriString.substring(startIndex + 1));

        Matcher matcher = Pattern.compile("@[\\w\\u4e00-\\u9fa5]+").matcher(uriString);
        if (matcher.find()) {
            String matched = matcher.group();
            System.out.println(matched.substring(1));
        }
    }

    @Test
    public void testSubList() {
        System.out.println("(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 78; i++) {
            ints.add(i);
        }

        for (int i : ints) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < ints.size(); i += 13) {
            int fromIndex = i;
            int toIndex = i + 13;
            if (toIndex > ints.size()) {
                toIndex = ints.size();
            }
            List<Integer> subInts = ints.subList(fromIndex, toIndex);

            for (int n : subInts) {
                System.out.print(n + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void testGenerateColor() {
        float step = 255 / 10f;
        for (int i = 1; i < 10; i++) {
            float level = step * i;
            int deep = Math.round(level);
            String value = Integer.toHexString(deep).toUpperCase();
            System.out.println(String.format("<color name=\"while_alpha_%s\">#%sFFFFFF</color>", value, value));
        }
        for (int i = 1; i < 10; i++) {
            float level = step * i;
            int deep = Math.round(level);
            String value = Integer.toHexString(deep).toUpperCase();
            System.out.println(String.format("<color name=\"black_alpha_%s\">#%s000000</color>", value, value));
        }
        for (int i = 1; i < 10; i++) {
            float level = step * i;
            int deep = Math.round(level);
            String value = Integer.toHexString(deep).toUpperCase();
            System.out.println(String.format("<color name=\"accent_alpha_%s\">#%sFE3E80</color>", value, value));
        }

        for (int i = 1; i < 10; i++) {
            float level = step * i;
            int deep = Math.round(level);
            String value = Integer.toHexString(deep).toUpperCase();
            System.out.println(String.format("<color name=\"primary_alpha_%s\">#%s3E50B4</color>", value, value));
        }
    }

    @Test
    public void testNofityAndWait() {

        final List<Double> doubleList = new ArrayList<>();


        for (int i = 0; i < 11; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        doubleList.add(Math.random());
                        try {
                            Thread.sleep((long) (Math.random() * 100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (doubleList) {
                        System.out.println("notify");
                        doubleList.notify();
                    }
                }
            }).start();
            synchronized (doubleList) {
                try {
                    System.out.println("wait");
                    doubleList.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < doubleList.size(); i += 2) {
            int fromIndex = i;
            int toIndex = i + 2;
            if (toIndex > doubleList.size()) {
                toIndex = doubleList.size();
            }
            List<Double> subDoubleList = doubleList.subList(fromIndex, toIndex);
            for (double d : subDoubleList) {
                System.out.print(d + " ");
            }
            System.out.println();
        }
    }
}
