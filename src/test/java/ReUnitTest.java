import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReUnitTest {

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void queryParaParserTest() {
        String xml = "<android.widget.RelativeLayout index=\"1\" package=\"com.eg.android.AlipayGphone\" class=\"android.widget.RelativeLayout\" text=\"\" content-desc=\"全屏广告\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[0,0][1080,2159]\" displayed=\"true\">\n" +
                "          <android.widget.LinearLayout index=\"0\" package=\"com.eg.android.AlipayGphone\" class=\"android.widget.LinearLayout\" text=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[60,270][1020,423]\" displayed=\"true\">\n" +
                "            <android.widget.ImageView index=\"0\" package=\"com.eg.android.AlipayGphone\" class=\"android.widget.ImageView\" text=\"\" content-desc=\"关闭\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[939,270][1020,351]\" displayed=\"true\" />\n" +
                "          </android.widget.LinearLayout>\n" +
                "          <android.widget.ImageView index=\"1\" package=\"com.eg.android.AlipayGphone\" class=\"android.widget.ImageView\" text=\"\" content-desc=\"推荐广告\" resource-id=\"com.alipay.mobile.advertisement:id/standardlayer_contentview\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" long-clickable=\"false\" password=\"false\" scrollable=\"false\" selected=\"false\" bounds=\"[60,423][1020,1821]\" displayed=\"true\" />\n" +
                "        </android.widget.RelativeLayout>";

        String reg = "<android.widget.RelativeLayout .*? content-desc=\"全屏广告\" .*?/android.widget.RelativeLayout>";
        String reg1 = "<android.widget.ImageView .*? content-desc=\"关闭\" .*? />";

        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        System.out.println(xml);
        System.out.println(reg);

        assert (matcher.find());

        int start = matcher.start();
        int end = matcher.end();

        String target = xml.substring(start, end);
        System.out.println(target);

        Pattern pattern1 = Pattern.compile(reg1, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher1 = pattern1.matcher(target);

        assert (matcher1.find());
        System.out.println(target.substring(matcher1.start(), matcher1.end()));
    }
}
