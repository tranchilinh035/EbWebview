/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

// special patch to correctly work on Ripple emulator (CB-9760)
// if (window.parent && !!window.parent.ripple) { // https://gist.github.com/triceam/4658021
//     module.exports = window.open.bind(window); // fallback to default window.open behaviour
//     return;
// }

var exec = require('cordova/exec');
var channel = require('cordova/channel');
var modulemapper = require('cordova/modulemapper');
var urlutil = require('cordova/urlutil');

function EbWebview() {
   this.channels = {
        'loadstart': channel.create('loadstart'),
        'loadstop' : channel.create('loadstop'),
        'loaderror' : channel.create('loaderror'),
        'exit' : channel.create('exit')
   };
}

EbWebview.prototype = {
    _eventHandler: function (event) {
        if (event && (event.type in this.channels)) {
            this.channels[event.type].fire(event);
        }
    },
	close: function (n,eventname) {
        exec(null, null, "EbWebview", "close", [n]);
    },
    load: function (n,url) {
      exec(null, null, "EbWebview", "load", [n,url]);
    },
    show: function (n,eventname) {
      exec(null, null, "EbWebview", "show", [n]);
    },
    hide: function (n,eventname) {
      exec(null, null, "EbWebview", "hide", [n]);
    },
    setPosition: function (n,left, top) {
      exec(null, null, "EbWebview", "setPosition", [n,left, top]);
    },
    setSize: function (n,width, height) {
      exec(null, null, "EbWebview", "setSize", [n,width, height]);
    },
	hasHistory: function (n,callback) {
      exec(callback, callback, "EbWebview", "hasHistory", [n]);
    },
	goBack: function (n) {
      exec(null, null, "EbWebview", "goBack", [n]);
    },
    getScreenshot: function (n,quality, callback) {
      exec(callback, callback, "EbWebview", "getScreenshot", [n,quality]);
    },
    addEventListener: function (n,eventname,f) {
        if (eventname in this.channels) {
            this.channels[eventname].subscribe(f);
        }
    },
    removeEventListener: function(n,eventname, f) {
        if (eventname in this.channels) {
            this.channels[eventname].unsubscribe(f);
        }
    },

    executeScript: function(n,injectDetails, cb) {
        if (injectDetails.code) {
            exec(cb, null, "EbWebview", "injectScriptCode", [n,injectDetails.code, !!cb]);
        } else if (injectDetails.file) {
            exec(cb, null, "EbWebview", "injectScriptFile", [n,injectDetails.file, !!cb]);
        } else {
            throw new Error('executeScript requires exactly one of code or file to be specified');
        }
    },

    insertCSS: function(n,injectDetails, cb) {
        if (injectDetails.code) {
            exec(cb, null, "EbWebview", "injectStyleCode", [n,injectDetails.code, !!cb]);
        } else if (injectDetails.file) {
            exec(cb, null, "EbWebview", "injectStyleFile", [n,injectDetails.file, !!cb]);
        } else {
            throw new Error('insertCSS requires exactly one of code or file to be specified');
        }
    }
};


exports.getCookie = function (n, cb) {
    exec(cb, null, 'EbWebview', 'getCookie', [n]);
}

exports.saveDataStore = function (n) {
    exec(null, null, "EbWebview", "saveDataStore", [n]);
}

exports.hide = function (n,eventname) {
    exec(null, null, "EbWebview", "hide", [n]);
};

exports.executeScript = function(n,injectDetails, cb) {
    if (injectDetails.code) {
        exec(cb, null, "EbWebview", "injectScriptCode", [n,injectDetails.code, !!cb]);
    } else if (injectDetails.file) {
        exec(cb, null, "EbWebview", "injectScriptFile", [n,injectDetails.file, !!cb]);
    } else {
        throw new Error('executeScript requires exactly one of code or file to be specified');
    }
};

exports.open = function(n,strUrl, strWindowFeatures, callbacks) {
    // Don't catch calls that write to existing frames (e.g. named iframes).
    // if (window.frames && window.frames[strWindowName]) {
    //     var origOpenFunc = modulemapper.getOriginalSymbol(window, 'open');
    //     return origOpenFunc.apply(window, arguments);
    // }

    strUrl = urlutil.makeAbsolute(strUrl);
    var iab = new EbWebview();

    callbacks = callbacks || {};
    for (var callbackName in callbacks) {
        iab.addEventListener(n,callbackName, callbacks[callbackName]);
    }

    var cb = function(eventname) {
       iab._eventHandler(eventname);
    };

    strWindowFeatures = strWindowFeatures || "";

    exec(cb, cb, "EbWebview", "open", [n,strUrl, strWindowFeatures]);
    return iab;
};
