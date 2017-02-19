package com.gnayils.obiew;

import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void computeResultIsCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        assertTrue(3 == ((int)Math.ceil(8 / 3d)));
        assertTrue(2 == ((int)Math.ceil(6 / 3d)));
        assertTrue(2 == ((int)Math.ceil(5 / 3d)));
        assertTrue(1 == ((int)Math.ceil(1 / 3d)));
    }


    @Test
    public void parseStringToDateWithoutException() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        System.out.println(dateFormat.parse("Sat Feb 04 13:48:00 +0800 2017").getTime());

    }

    @Test
    public void regularExpression() {
        String string = "回复@沙漏篇篇:#西游伏魔片#。//@沙漏篇篇:吴亦凡有什么#好作品#？http://t.cn/Rx38cDf //@司马平邦：他需要更多好的作品支撑，https://www.baidu.com/asdkjl?swe=23sdf 但是@吴亦凡 明显落后他";

        //Matcher mentionMatcher = Pattern.compile("@[^\\x01-\\x2f\\x3a-\\x40\\x5b-\\x5e\\x60\\x7b-\\xbf\\xf7-\\xf8]+").matcher(string);
        Matcher mentionMatcher = Pattern.compile("@[\\w\\u4e00-\\u9fa5]+").matcher(string);
        while(mentionMatcher.find()) {
            System.out.println(mentionMatcher.start() + ", " + mentionMatcher.end() + ", " + mentionMatcher.group());
        }

        Matcher topicMatcher = Pattern.compile("#[^#]+#").matcher(string);
        while(topicMatcher.find()) {
            System.out.println(topicMatcher.start() + ", " + topicMatcher.end() + ", " + topicMatcher.group());
        }

        Matcher httpMatcher = Pattern.compile("([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)[tT].[cC][nN]/[\\w]+").matcher(string);
        while(httpMatcher.find()) {
            System.out.println(httpMatcher.start() + ", " + httpMatcher.end() + ", " + httpMatcher.group());
        }
    }

    @Test
    public void useMatcherReplace() {
        String string = "[带着微博去旅行][广告][哆啦A梦微笑][哆啦A梦汗][哆啦A梦花心][哆啦A梦吃惊]→_→[微笑][可爱][太开心][鼓掌][嘻嘻][哈哈][笑cry][挤眼][馋嘴][白眼][黑线][挖鼻][哼][怒][抓狂][委屈][可怜][失望][悲伤][泪][害羞][污][爱你][亲亲][色][舔屏][钱][doge][喵喵][二哈][酷][坏笑][阴险][偷笑][思考][疑问][晕][傻眼][衰][骷髅][嘘][闭嘴][汗][吃惊][感冒][生病][吐][拜拜][鄙视][左哼哼][右哼哼][怒骂][打脸][顶][哈欠][困][睡][互粉][抱抱][摊手][心][伤心][鲜花][男孩儿][女孩儿][握手][作揖][赞][耶][good][弱][NO][ok][haha][来][拳头][加油][熊猫][兔子][猪头][草泥马][奥特曼][太阳][月亮][浮云][下雨][沙尘暴][微风][飞机][照相机][话筒][音乐][给力][威武][蜡烛][围观][干杯][蛋糕][礼物][喜][钟][肥皂][绿丝带][围脖][浪][羞嗒嗒][好爱哦][偷乐][赞啊][笑哈哈][好喜欢][求关注][噢耶]";
        Matcher matcher = Pattern.compile("\\[\\S+?\\]").matcher(string);
        while(matcher.find()) {
            System.out.println("<item>" + matcher.group() + "</item>");
        }
    }


   @Test
   public void getStaticField() throws NoSuchFieldException, IllegalAccessException {
       Field staticField = Static.class.getDeclaredField("resId");
       System.out.println(staticField.get(null));
   }

    @Test
    public void printASCIIChar() {
        for (char i = 0; i < 255; i++) {
            System.out.println(i + "");
        }
    }

    public static class Static {

        public static final int resId = 1234567;

    }

    @Test
    public void operand() {
        long id = 4071426909192703L;
        System.out.println(id);
        System.out.println(5^2);
        System.out.println(Integer.toBinaryString(5));
        System.out.println(Integer.toBinaryString(7));
        System.out.println((int)(id ^ (id >>> 32)));
    }
    
}