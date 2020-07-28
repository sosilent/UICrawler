package util.uipath;

import com.google.common.base.Strings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.*;

import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecifiedXpathUtil extends XPathUtil {

    private static String initialActivity;

    public static String getInitialActivity() {
        return initialActivity;
    }

    public static void setInitialActivity(String initialActivity) {
        SpecifiedXpathUtil.initialActivity = initialActivity;
    }

    public static class AdPopoutConfig {
        private String layout_re;
        private String res_id;
        private String class_name;
        private String text;
        private String content_desc;
        private String bounds;

        public String getLayout_re() {
            return layout_re;
        }

        public void setLayout_re(String layout_re) {
            this.layout_re = layout_re;
        }

        public String getRes_id() {
            return res_id;
        }

        public void setRes_id(String res_id) {
            this.res_id = res_id;
        }

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getContent_desc() {
            return content_desc;
        }

        public void setContent_desc(String content_desc) {
            this.content_desc = content_desc;
        }

        public String getBounds() {
            return bounds;
        }

        public void setBounds(String bounds) {
            this.bounds = bounds;
        }
    }

    public static class ActionConfig {
        private String res_id;
        private String class_name;
        private String text;
        private String content_desc;
        private String bounds;
        private String action;
        private String value;

        public String getRes_id() {
            return res_id;
        }

        public void setRes_id(String res_id) {
            this.res_id = res_id;
        }

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getContent_desc() {
            return content_desc;
        }

        public void setContent_desc(String content_desc) {
            this.content_desc = content_desc;
        }

        public String getBounds() {
            return bounds;
        }

        public void setBounds(String bounds) {
            this.bounds = bounds;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class UIPathNode {
        private String activityName;
        private String activityURL;

        private AdPopoutConfig adPopoutConfig;
        private List<ActionConfig> actionConfigList;

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }
        public String getActivityURL() {
            return activityURL;
        }

        public void setActivityURL(String activityURL) {
            this.activityURL = activityURL;
        }

        public AdPopoutConfig getAdPopoutConfig() {
            return adPopoutConfig;
        }

        public void setAdPopoutConfig(AdPopoutConfig adPopoutConfig) {
            this.adPopoutConfig = adPopoutConfig;
        }

        public List<ActionConfig> getActionConfigList() {
            return actionConfigList;
        }

        public void setActionConfigList(List<ActionConfig> actionConfigList) {
            this.actionConfigList = actionConfigList;
        }
    }

    private static boolean isEqual(NodeList left, NodeList right) {
        if (left == null || right == null)
            return false;

        if (left.getLength() != right.getLength())
            return false;

        int iter = 0;

        Map<String, Node> nodes = new HashMap<>();
        while (iter < left.getLength()) {
            Node node = left.item(iter++);
            String nodeXpath = getNodeXpath(node);
            String resId = getResourceIdByNodePath(nodeXpath);
            String text = getTextByNodePath(nodeXpath);

            String id = null;
            if (resId != null)
                id = resId + text != null ? "-" + text : "-";
            else if (text != null)
                id = text;

            if (id != null)
                nodes.put(id, node);
        }

        iter = 0;
        while (iter < right.getLength()) {
            Node node = left.item(iter++);
            String nodeXpath = getNodeXpath(node);
            String resId = getResourceIdByNodePath(nodeXpath);
            String text = getTextByNodePath(nodeXpath);

            String id = null;
            if (resId != null)
                id = resId + text != null ? "-" + text : "-";
            else if (text != null)
                id = text;

            if (id != null)
                return nodes.containsKey(id);
        }
        return true;
    }

    private static String NODE_RESOURCE_ID = "resource-id=\"";
    private static String NODE_TEXT = "text=\"";
    private static String NODE_CONTENT_DESC = "content-desc=\"";

    public static String getResourceIdByNodePath(String path) {
        if (path == null)
            return null;

        int startPos = path.indexOf(NODE_RESOURCE_ID);
        if (startPos >= 0) {
            int endPos = path.indexOf("\"", startPos + NODE_RESOURCE_ID.length());
            if (endPos > 0)
                return path.substring(startPos + NODE_RESOURCE_ID.length(), endPos);
        }
        return null;
    }

    public static String getTextByNodePath(String path) {
        if (path == null)
            return null;

        int startPos = path.indexOf(NODE_TEXT);
        if (startPos >= 0) {
            int endPos = path.indexOf("\"", startPos + NODE_TEXT.length());
            if (endPos > 0)
                return path.substring(startPos + NODE_TEXT.length(), endPos);
        } else {
            startPos = path.indexOf(NODE_CONTENT_DESC);
            if (startPos >= 0) {
                int endPos = path.indexOf("\"", startPos + NODE_CONTENT_DESC.length());
                if (endPos > 0)
                    return path.substring(startPos + NODE_CONTENT_DESC.length(), endPos);
            }
        }
        return null;
    }

    public static long getNodesFromFile(String xml, int pathNodeIndex, List<UIPathNode> uiPathNodeList, long currentDepth) throws Exception {
        log.info("Method: getNodesFromFile");
        log.info("Context: " + Driver.driver.getContextHandles().toString());

        String currentActivity = Driver.getCurrentActivity();
        log.info("++++++++++++++++++ Activity Name : " + currentActivity + "+++++++++++++++++++++++");

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (ConfigUtil.isAutoLoginEnabled()) {
            try {
                if (0 == userLoginCount % userLoginInterval) {
                    log.info("Processing login operation");
                    xml = userLogin(xml);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Fail to log in!");
            }

            userLoginCount++;
        }

        String currentXML = xml;
        if (currentXML == null) {
            log.error("----------!!! failed to get page source and stopped");
            return currentDepth;
        }

        //检查运行时间
        long endTime = System.currentTimeMillis();

        if ((endTime - testStartTime) > (runningTime * 60 * 1000)) {
            log.info("已运行" + (endTime - testStartTime) / 60 / 1000 + "分钟，任务即将结束");
            return currentDepth;
        }

        currentDepth++;
        log.info("------Depth: " + currentDepth);

        //遍历前检查
        // 1.包名是否合法，包名不合法，不会重启
        // 2.检查Depth,超过预定值就返回
        // 3.是否有可遍历的元素,如果没有遍历TabBar,如果无TabBar
        String packageName = getAppName(currentXML);

        //1.检查当前UI的包名是否正确，一定要先查包名！因为其内部控制了stop的值, 包名不合法，-是否应该重启app?--
        if (PackageStatus.VALID != isValidPackageName(packageName, true)) {
            log.info("=====================package: " + packageName + " is invalid, return ....==============================");
            currentXML = Driver.getPageSource();
            log.debug("current page source:\n" + currentXML);

            return currentDepth;
        }

        UIPathNode uiPathNode = uiPathNodeList.get((int) (currentDepth - 1));

        // 2.如果是最后深度，截屏+保存page source
        if (currentDepth == uiPathNodeList.size()) {
            log.info("enter the target ui: depth, " + currentDepth);
            log.debug("----page source-----\n" + currentXML);

            //int index = 1;

            if (!uiPathNode.getActivityName().equalsIgnoreCase(currentActivity)) {
                log.info("======================================== Wrong Activity Name =========================================");
                log.info("Enter the wrong activity:  target activity name: " + uiPathNode.getActivityName() + "; current activity name: " + currentActivity);
                changedActivityMap.put(uiPathNode.getActivityName(), currentActivity);
                log.info("changedActivityMap = " + changedActivityMap);
            }

            currentXML = Driver.getPageSource();
            //check if there is a ad pop out
            //and close the pop out by clicking it
            if (uiPathNode.getAdPopoutConfig() != null) {
                Pattern pattern = Pattern.compile(uiPathNode.getAdPopoutConfig().getLayout_re(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                Matcher matcher = pattern.matcher(currentXML);
                if (matcher.find()) {
                    String resId = uiPathNode.getAdPopoutConfig().getRes_id();
                    String className = uiPathNode.getAdPopoutConfig().getClass_name();
                    String text = uiPathNode.getAdPopoutConfig().getText();

                    MobileElement elem;
                    if (resId != null)
                        elem = Driver.findElemByIdWithoutException(resId);
                    else
                        elem = Driver.findElementByClassAndTextWithoutException(className, text);

                    if (elem != null) {
                        currentXML = clickElement(elem, currentXML);
                    }
                }
            }

            if (uiPathNode.getActionConfigList() != null
                    && uiPathNode.getActionConfigList().size() > 0) {

                for (ActionConfig actionConfig : uiPathNode.getActionConfigList()) {
                    MobileElement element = Driver.findElement(By.id(actionConfig.res_id));
                    if (element != null) {
                        element.setValue(actionConfig.getValue());
                    }
                }
            }

            Driver.snapshotCurStatus(Integer.toString(pathNodeIndex), MetadataUtil.genMetadata(currentActivity));

            String timeStr = pathNodeIndex + "_" + Driver.getCurrentActivity() + "_" + Util.getDatetime();
            Driver.snapshotScreen(Integer.toString(pathNodeIndex), timeStr);
            Driver.snapshotPageSource(Integer.toString(pathNodeIndex), timeStr, Driver.getPageSource());

            Document document = builder.parse(new ByteArrayInputStream(currentXML.getBytes()));
            NodeList nodes = (NodeList) xpath.evaluate(clickXpath, document, XPathConstants.NODESET);

            //screenshot 5 screens at most
            for (int i = 0; i < 5; i++) {
                Driver.swipeVertical(false);

                String pageSource = Driver.getPageSource();
                if (pageSource == null) {
                    log.error("-----failed to get page source and stopped");
                    return currentDepth;
                }

                document = builder.parse(new ByteArrayInputStream(pageSource.getBytes()));
                NodeList newNodes = (NodeList) xpath.evaluate(clickXpath, document, XPathConstants.NODESET);

                if (isEqual(nodes, newNodes))
                    break;

                nodes = newNodes;

                timeStr = Driver.getCurrentActivity() + "." + Util.getDatetime();
                Driver.snapshotScreen(Integer.toString(pathNodeIndex), timeStr);
                Driver.snapshotPageSource(Integer.toString(pathNodeIndex), timeStr, Driver.getPageSource());
            }

            return currentDepth;
        }

        showTabBarElement(currentXML, tabBarXpath);

        for (ActionConfig actionConfig : uiPathNode.getActionConfigList()) {

            MobileElement elem = null;

            String resId = actionConfig.getRes_id();
            String className = actionConfig.getClass_name();
            String text = actionConfig.getText();
            String content_desc = actionConfig.getContent_desc();
            String bounds = actionConfig.getBounds();

            currentXML = Driver.getPageSource();

            if (Strings.isNullOrEmpty(resId) && Strings.isNullOrEmpty(className)) {
                log.error("ui path node is invalid for missing resource id or class name: res id, " + resId + "; class name, " + className);
            } else {
                if (!Strings.isNullOrEmpty(resId)) {
                    List<MobileElement> mobileElements = Driver.findElements(By.id(resId));
                    if (mobileElements.size() == 1)
                        elem = mobileElements.get(0);
                    else {
                        for (MobileElement mobileElement : mobileElements) {
                            if ((text != null && mobileElement.getText().contains(text))
                                    || (content_desc != null && content_desc.equalsIgnoreCase(mobileElement.getAttribute("content-desc")))
                                    || (bounds != null && bounds.equalsIgnoreCase(mobileElement.getAttribute("bounds")))) {
                                elem = mobileElement;
                                break;
                            }
                        }
                    }
                }

                //即使resource id不为null，依然有可能找不到元素
                // 例如：荣耀和p20设备的支付宝首页resource id不一样，p20的配置脚本在荣耀则找不到对应的控件元素
                //换做class，text或content-desc甚至bounds寻找元素
                if (elem == null
                        && !Strings.isNullOrEmpty(className)) {
                    List<MobileElement> mobileElements = Driver.findElements(By.className(className));
                    for (MobileElement mobileElement : mobileElements) {
                        if ((text != null && mobileElement.getText().contains(text))
                                || (content_desc != null && content_desc.equalsIgnoreCase(mobileElement.getAttribute("content-desc")))
                                || (bounds != null && bounds.equalsIgnoreCase(mobileElement.getAttribute("bounds")))
                        ) {
                            elem = mobileElement;
                            break;
                        }
                    }
                }
            }

            if (null == elem) {
                //元素未找到，重新遍历当前页面
                try {
                    Thread.sleep(2000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                log.error("---------Node not found in current UI!!!!!!! Stop.-----------");
                log.info("ui path node:\n"
                        + "res id, " + resId
                        + "; class name, " + className
                        + "; text, " + text);

                log.info("----page source-----\n" + currentXML);

            } else {
                if (actionConfig.getAction() != null
                        && actionConfig.getAction().equalsIgnoreCase("input")) {
                    elem.setValue(actionConfig.getValue());
                } else if (actionConfig.getAction() != null
                        && actionConfig.getAction().equalsIgnoreCase("wait")) {
                    Driver.sleep(10);
                } else {
                    String previousPageStructure = Driver.getPageStructure(xml, clickXpath);
                    log.debug(previousPageStructure);
                    currentXML = clickElement(elem, currentXML);
                    log.debug("------------page source after click-----------\n" + currentXML);

                    String afterPageStructure = Driver.getPageStructure(currentXML, clickXpath);
                    String newActivity = Driver.getCurrentActivity();

                    //点击后进入到了新的页面
                    if (!xml.equals(currentXML) || !currentActivity.equals(newActivity)) {
                        log.info("========================================New Child UI================================");
                        log.info("previous activity: " + currentActivity + "; current activity: " + newActivity);

                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        currentDepth = getNodesFromFile(currentXML, pathNodeIndex, uiPathNodeList, currentDepth);
                        //currentDepth = Long.parseLong(getNodesFromFile(currentXML, currentDepth));
                    } else {
                        log.info("========================================Same UI");
                    }
                    break;
                }
            }
        }

        log.info("-----------------------target depth: " + uiPathNodeList.size() + ", leaving depth, " + currentDepth);
        return currentDepth;
    }
}
