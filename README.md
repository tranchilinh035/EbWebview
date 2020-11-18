
# cordova-plugin-multi-webview
embeded webview for cordova app

This plugin is different with inappbrowser, You can load html page or external urls in the embeded webview, and control it directly.
It uses Crosswalk if available
Opens up to 6 inappBrowsers on screen

### Background
This plugin was originally commissioned by [ZeGenie Inc] [ze].

### Install
    cordova plugin add https://github.com/janpekarguru/cordova-plugin-multi-webview
### Remove
    cordova plugin remove cordova-plugin-ebwebview

Add this plugin so iOS will use WkWebView with this plugin
https://github.com/apache/cordova-plugin-wkwebview-engine

### Method
  - cordova.EbWebview.open(n,url, param,callbacks)
```sh    
app.webview = cordova.EbWebview.open(0,encodeURI('http://webkam.com'), 'left=0,top=0,width=320,height=200',{loadstop:ldstop});
```
n=browser number [0..5]
  - load(n,url)
  - show(n), hide(n)
  - setPosition(n,left, top)
  - setSize(n,width, height)
  - addEventListener(n,eventName, callback)
  - removeEventListenenr(n,eventName, callback)
  - hasHistory: (n,callback) 
  - goBack: (n)
  - getScreenshot:  (n,quality, callback)
  - executeScript: (n,injectDetails, cb)
  - insertCSS: (n,injectDetails, cb)
     
Add to config.xml for getScreenshot to work:
```sh 
<preference name="CrosswalkAnimatable" value="true" />	
```




   
   [ze]: <http://www.zegenie.com>
