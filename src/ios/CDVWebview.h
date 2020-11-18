/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVInvokedUrlCommand.h>
#import <Cordova/CDVScreenOrientationDelegate.h>

#ifdef __CORDOVA_4_0_0
    #import <Cordova/CDVUIWebViewDelegate.h>
#else
    #import <Cordova/CDVWebViewDelegate.h>
#endif

@class CDVEmbeddedWebViewPlug;


@interface CDVEmbeddedWebView : CDVPlugin {
    BOOL _injectedIframeBridge;
}

@property (nonatomic, strong) CDVEmbeddedWebViewPlug* webplug;
@property (nonatomic, strong) NSMutableArray *brw;
@property (nonatomic) int *index;
@property (nonatomic, copy) NSString* callbackId;

@property (nonatomic, copy) NSRegularExpression *callbackIdPattern;

- (void)open:(CDVInvokedUrlCommand*)command;
- (void)load:(CDVInvokedUrlCommand*)command;
- (void)close:(CDVInvokedUrlCommand*)command;

- (void)show:(CDVInvokedUrlCommand*)command;
- (void)hide:(CDVInvokedUrlCommand*)command;
- (void)setPosition:(CDVInvokedUrlCommand*)command;
- (void)setSize:(CDVInvokedUrlCommand*)command;

- (void)getScreenshot:(CDVInvokedUrlCommand*)command;
- (void)hasHistory:(CDVInvokedUrlCommand*)command;
- (void)goBack:(CDVInvokedUrlCommand*)command;

- (void)injectScriptCode:(CDVInvokedUrlCommand*)command;
- (void)injectScriptFile:(CDVInvokedUrlCommand*)command;
- (void)injectStyleCode:(CDVInvokedUrlCommand*)command;
- (void)injectStyleFile:(CDVInvokedUrlCommand*)command;

@end

@interface CDVWebviewOptions : NSObject {}

@property (nonatomic, assign) BOOL location;
@property (nonatomic, assign) BOOL toolbar;
@property (nonatomic, copy) NSString* closebuttoncaption;
@property (nonatomic, copy) NSString* toolbarposition;
@property (nonatomic, assign) BOOL clearcache;
@property (nonatomic, assign) BOOL clearsessioncache;

@property (nonatomic, copy) NSString* presentationstyle;
@property (nonatomic, copy) NSString* transitionstyle;

@property (nonatomic, assign) BOOL enableviewportscale;
@property (nonatomic, assign) BOOL mediaplaybackrequiresuseraction;
@property (nonatomic, assign) BOOL allowinlinemediaplayback;
@property (nonatomic, assign) BOOL keyboarddisplayrequiresuseraction;
@property (nonatomic, assign) BOOL suppressesincrementalrendering;
@property (nonatomic, assign) BOOL hidden;
@property (nonatomic, assign) BOOL disallowoverscroll;

@property (nonatomic, copy) NSNumber* left;
@property (nonatomic, copy) NSNumber* top;
@property (nonatomic, copy) NSNumber* width;
@property (nonatomic, copy) NSNumber* height;

+ (CDVWebviewOptions*)parseOptions:(NSString*)options;

@end

@interface CDVEmbeddedWebViewPlug : UIWebView <UIWebViewDelegate> {
    @private
    NSString* _userAgent;
    NSString* _prevUserAgent;
    NSInteger _userAgentLockToken;
    CDVWebviewOptions *_browserOptions;

#ifdef __CORDOVA_4_0_0
    CDVUIWebViewDelegate* _webViewDelegate;
#else
    CDVWebViewDelegate* _webViewDelegate;
#endif
}

@property (nonatomic, weak) CDVEmbeddedWebView* navigationDelegate;
@property (nonatomic) NSURL* currentURL;

- (void)navigateTo:(NSURL*)url;

- (id)initWithUserAgent:(NSString*)userAgent prevUserAgent:(NSString*)prevUserAgent browserOptions: (CDVWebviewOptions*) browserOptions;

@end
