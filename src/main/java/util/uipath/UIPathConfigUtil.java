package util.uipath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Ma Yi on 2017/4/27.
 */
public class UIPathConfigUtil {
    public static Logger log = LoggerFactory.getLogger(UIPathConfigUtil.class);

    private static String UI_PATH = "UI_PATH";

    private static String ACTIVITY_NAME = "activity_name";
    private static String ELEMENT_ID = "resource_id";
    private static String TEXT = "text";
    private static String CLASS = "class";

    private static String AD = "ad";
    private static String LAYOUT_RE = "layout_re";
    private static String CLICKABLE_WIDGET = "clickable_widget";

    private static UIPathConfigUtil configUtil;
    private static Map<Integer, List<SpecifiedXpathUtil.UIPathNode>> uiPath;

    public static UIPathConfigUtil initialize(String file){
        log.info("Method: initialize");

        Map<String,Object> configItems = new HashMap<>();
        try {
            log.info("Reading config file " + file);

            InputStream input = new FileInputStream(new File(file));
            Yaml yaml = new Yaml();
            configUtil = new UIPathConfigUtil();

            Map<String, Object> map = yaml.load(input);

            Map<Integer, Object> pathMap = (Map<Integer, Object>) map.get(UI_PATH);

            uiPath = new HashMap<>();
            Set<Integer> keys = pathMap.keySet();

            for (int indexNo : keys) {
                List<Map<String, String>> nodeMapList = (List<Map<String, String>>) pathMap.get(indexNo);

                List<SpecifiedXpathUtil.UIPathNode> nodes = new ArrayList<>();
                for (Map nodeMap : nodeMapList) {
                    SpecifiedXpathUtil.UIPathNode node = new SpecifiedXpathUtil.UIPathNode();
                    node.setActivityName((String) nodeMap.get(ACTIVITY_NAME));
                    node.setResourceId((String) nodeMap.get(ELEMENT_ID));
                    node.setText((String) nodeMap.get(TEXT));
                    node.setClassName((String) nodeMap.get(CLASS));

                    if (nodeMap.containsKey(AD)) {
                        SpecifiedXpathUtil.AdPopoutConfig adPopoutConfig = new SpecifiedXpathUtil.AdPopoutConfig();
                        Map<String, Object> reMap = (Map<String, Object>) nodeMap.get(AD);
                        adPopoutConfig.setLayout_re((String) reMap.get(LAYOUT_RE));

                        Map<String, String> widgetMap = (Map<String, String>) reMap.get(CLICKABLE_WIDGET);
                        if (widgetMap != null) {
                            adPopoutConfig.setWidget_res_id(widgetMap.get(ELEMENT_ID));
                            adPopoutConfig.setWidget_text(widgetMap.get(TEXT));
                            adPopoutConfig.setWidget_class(widgetMap.get(CLASS));
                        }

                        node.setAdPopoutConfig(adPopoutConfig);
                    }

                    nodes.add(node);
                }
                uiPath.put(indexNo, nodes);
            }

            return configUtil;

        }catch (Exception e){
            log.error("!!!!!!Fail to read config file");
            e.printStackTrace();
        }

        return null;
    }

    public static Map<Integer, List<SpecifiedXpathUtil.UIPathNode>> getUIPath() {
        return uiPath;
    }
}
