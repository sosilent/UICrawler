# UICrawler
By Chen Haipei in July 30 2020


## 运行环境设置

```
java -jar target/UICrawler-2.0.jar -f config.yml -y ui.yml -u CLB7N18423003046 -n appium 
-u 指定设备udid
-t 指定appium server的端口（此项为可选项，默认值是4723)
-f 指定yml配置文件 若无此参数 默认为config.yml 
-y UI路径
-n appium 
```

## 配置
```
### 根据待测试App修改配置文件中下列各项的值 [详情见 Config.md](doc/Config.md)
  #### Android
  * ANDROID_PACKAGE
  * ANDROID_MAIN_ACTIVITY
```
```
### 根据待测试App需要捕获的页面修改 ui.yml，  支持元素遍历点击和url跳转两种方式
  21:
    - activity_name: 'com.taobao.browser.BrowserActivity'
      scheme_url: 'https://m.tb.cn/h.VskpZAZ?sm=0fb7c6'
      actions:
        - resource_id: ''
          class: 'android.view.View'
          text: '金币收益'
          action: click
    - activity_name: 'com.taobao.browser.BrowserActivity'
      actions:
        - resource_id: ''
          class: 'android.widget.Button'
          text: '去完成'
          action: click
    - activity_name: 'com.taobao.browser.BrowserActivity'
```
