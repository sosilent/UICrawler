package util.uipath;

import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.ConfigUtil;
import util.Driver;
import util.PackageStatus;
import util.XPathUtil;

import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.util.List;

public class SpecifiedXpathUtil extends XPathUtil {

    public static class UIPathNode {
        private String activityName;
        private String resourceId;
        private String text;

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static String getNodesFromFile(String xml, int pathNodeIndex, List<UIPathNode> uiPathNodeList, long currentDepth) throws Exception{
        log.info("Method: getNodesFromFile");

        log.info("Context: " + Driver.driver.getContextHandles().toString());

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

        //检查运行时间
        long endTime = System.currentTimeMillis();

        if((endTime - testStartTime) > ( runningTime * 60 * 1000)) {
            log.info("已运行" + (endTime - testStartTime)/60/1000 + "分钟，任务即将结束");
            stop = true;
            return xml;
        }

        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        NodeList nodes = (NodeList) xpath.evaluate(clickXpath, document, XPathConstants.NODESET);

        log.info(String.valueOf("UI nodes length : " + nodes.getLength()));

        int length = nodes.getLength();

        String previousPageStructure = Driver.getPageStructure(xml,clickXpath);
        String afterPageStructure = previousPageStructure;
        String currentXML = xml;

        //检查stop为true时快速退出
        if(stop){
            log.info("-----stop=true, Fast exit");
            return currentXML;
        }

        //遍历前检查
        // 1.包名是否合法，包名不合法，不会重启
        // 2.检查Depth,超过预定值就返回
        // 3.是否有可遍历的元素,如果没有遍历TabBar,如果无TabBar
        String packageName = getAppName(currentXML);

        if (packageName.equals("com.tencent.mm") || packageName.equals("com.tencent.xin")){
            if(currentXML.contains("附近的小程序")){
                log.info("已遍历完小程序，跳转到了小程序主页面，遍历停止");
                stop = true;

                return currentXML;
            }
        }

        String currentActivity = Driver.getCurrentActivity();
        log.info("++++++++++++++++++ Activity Name : " + currentActivity +"+++++++++++++++++++++++");

        currentDepth++;
        log.info("------Depth: " + currentDepth);

        UIPathNode uiPathNode = uiPathNodeList.get((int) (currentDepth-1));

        //1.检查当前UI的包名是否正确，一定要先查包名！因为其内部控制了stop的值, 包名不合法，-是否应该重启app?--
        if(PackageStatus.VALID != isValidPackageName(packageName,true)){
            log.info("=====================package: "+ packageName + " is invalid, return ....==============================");
            currentXML = Driver.getPageSource();
            return currentXML;
        }

        // 2.检查Depth,超过预定值就返回
        if(currentDepth == uiPathNodeList.size()){
            stop = true;
            log.info("enter the target ui: depth, " + currentDepth);
            //Driver.pressBack(repoStep);
            if (uiPathNode.getActivityName().equalsIgnoreCase(currentActivity))
                Driver.takeScreenShotWithSubDir(Integer.toString(pathNodeIndex));

            currentXML = Driver.getPageSource();
            return currentXML;
        }

        log.info("node length is " + length);

        //3.检查页面内有没有可以遍历的元素, 跳过循环，然后查找是否有TabBar元素，此处不可用return，不然tabBar不会被遍历
        if(length == 0){
            log.error("========!!!!!!!No UI node found in current page!!!!! Begin to find tab bar element... =====");
        }

        showTabBarElement(currentXML, tabBarXpath);

        //遍历UI内的Node元素
        int iter = 0;
        while(iter++ <length && !stop){
            log.info("Element index is : " + iter);

            Node tmpNode = nodes.item(iter);
            log.info("node name: " + tmpNode.getNodeName());

            String nodeXpath = getNodeXpath(tmpNode);
            log.info("nodeXpath: " + nodeXpath);

            if(nodeXpath == null){
                log.error("Null nodeXpath , continue.");
                continue;
            }

            //Comment this if not in test mode
            //nodeXpath = showNodes(currentXML,nodeXpath);

            //判断当前元素是否点击过
            if(set.add(nodeXpath)){

                MobileElement elem = Driver.findElementWithoutException(By.xpath(nodeXpath));

                if(null == elem){
                    //元素未找到，重新遍历当前页面
                    xpathNotFoundElementList.add(nodeXpath);
                    log.info("---------Node not found in current UI!!!!!!! Stop current iteration.-----------" );
                    continue;
                }

                if (uiPathNode.getResourceId() == null)
                    continue;
                else if (uiPathNode.getText() == null && nodeXpath.indexOf(uiPathNode.getResourceId()) < 0)
                    continue;
                else if (uiPathNode.getText() != null
                        && (nodeXpath.indexOf(uiPathNode.getResourceId()) < 0 || nodeXpath.indexOf(uiPathNode.getText()) < 0))
                    continue;

                currentXML = clickElement(elem,currentXML);
                afterPageStructure = Driver.getPageStructure(currentXML,clickXpath);

                //点击后进入到了新的页面
                if(!stop && !isSamePage(previousPageStructure,afterPageStructure)) {
                    log.info("========================================New Child UI================================");

                    //遍历子UI前 先检查包名是否合法
                    packageName = getAppName(currentXML);

                    if(PackageStatus.VALID != isValidPackageName(packageName)){
                        currentXML = Driver.getPageSource();
                        afterPageStructure = Driver.getPageStructure(currentXML,clickXpath);
                        break;
                    }

                    //遍历子UI
                    getNodesFromFile(currentXML, pathNodeIndex, uiPathNodeList, currentDepth);

                    //子页面返回后检查
                    // 1.包名是否合法
                    // 2.stop是否在子页面中设置为了true

                    //在子页面中发现了包名变化， 停止当前遍历
                    if(stop){
                        break;
                    }

                    //从子UI返回后，检查包名
                    currentXML = Driver.getPageSource();
                    packageName = getAppName(currentXML);
                    if(PackageStatus.VALID != isValidPackageName(packageName,false)){
                        break;
                    }

                    //判断从子页面返回后，父页面是否发生了变化
                    afterPageStructure = Driver.getPageStructure(currentXML,clickXpath);

                    if(isSamePage(previousPageStructure,afterPageStructure)){
                        log.info("Parent page stay the same after returning from child page");
                        //页面未变化， 继续遍历
                    }else{
                        log.info("Parent page changed after returning from child page");
                        //从子页面返回后 父页面发了生变化 停止遍历当前页面
                        break;
                    }
                }else{
                    log.info("========================================Same UI");
                }
            }else {
                //元素已经点击过
                log.info("---existed--- " + nodeXpath + "\n");
            }
        }

        log.info("\n\n\n!!!!!!!!!!!!!Done!!!!!!!!!!!!!!! stop: " + stop +"\n\n\n");

        //currentDepth --;

        //包名发生变化退出遍历
        if(stop){
            log.info("\n\n\nPackage name changed: "+ packageName + " set stop to true and return!!!!!!\n\n\n");
            return currentXML;
        }

        log.info("node length after while is " + iter);
        log.info("========================================Complete iterating current UI with following elements: ");
        //log.info( "\n\n\n" + previousPageStructure + "\n\n\n");
        log.info("depth before return is " + currentDepth);
        return currentXML;
    }
}
