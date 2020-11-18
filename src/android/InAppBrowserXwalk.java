package com.ebwebview.plugin;

import org.apache.cordova.*;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkNavigationHistory;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.TextureView;
import android.view.ViewGroup;

import android.content.Context;
import android.app.Activity;
import android.webkit.ValueCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

public class InAppBrowserXwalk extends CordovaPlugin {

    
	
	private BrowserDialog[] d;
    	private XWalkView[] brw;
	private CallbackContext callbackContext;
	private CallbackContext callbackContext2;
	private int index=-1;
	
	public void init(final JSONArray data) throws JSONException {
		
		d=new BrowserDialog[6];
		brw= new XWalkView[6];
		Log.d("janp", "INIT DONE");
		index=1;
    }
	
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
		if (index==-1) this.init(data);
        if(action.equals("open")) {
            this.callbackContext = callbackContext;
            this.openBrowser(data);
        } else if(action.equals("load")) {
            //this.callbackContext = callbackContext;
            this.loadUrl(data);
        } else if(action.equals("close")) {
            this.closeBrowser(data);
        } else if(action.equals("show")) {
            this.showBrowser(data);
        } else if(action.equals("hide")) {
            this.hideBrowser(data);
        } else if(action.equals("setPosition")) {
            this.setPosition(data);
        } else if(action.equals("setSize")) {
            this.setSize(data);
		} else if(action.equals("goBack")) {
            this.goBack(data);
		} else if(action.equals("hasHistory")) {
            this.hasHistory(data, callbackContext);
        } else if(action.equals("getScreenshot")) {
            this.getScreenshot(data, callbackContext);
        } else if (action.equals("injectScriptCode")) {
            String jsWrapper = null;
            if (data.getBoolean(2)) {       // Called with callback
				jsWrapper = "JSON.stringify([eval(%s)])";
            }
            injectDeferredObject(data.getInt(0),data.getString(1), jsWrapper,callbackContext);     // JavaScript String
        }  else if (action.equals("injectScriptFile")) {
            String jsWrapper;
            jsWrapper = "(function(d) { var c = d.createElement('script'); c.src = %s; d.body.appendChild(c); })(document)";
            injectDeferredObject(data.getInt(0),data.getString(1), jsWrapper,callbackContext);
        } else if (action.equals("injectStyleCode")) {
            String jsWrapper;
             jsWrapper = "(function(d) { var c = d.createElement('style'); c.innerHTML = %s; d.body.appendChild(c); })(document)";
            injectDeferredObject(data.getInt(0),data.getString(1), jsWrapper,callbackContext);
        }  else if (action.equals("injectStyleFile")) {
            String jsWrapper;
            jsWrapper = "(function(d) { var c = d.createElement('link'); c.rel='stylesheet'; c.type='text/css'; c.href = %s; d.head.appendChild(c); })(document)";
            injectDeferredObject(data.getInt(0),data.getString(1), jsWrapper,callbackContext);
        }
        return true;
    }
	
	
	class MyXWalkView extends XWalkView {
		public MyXWalkView(Context a, Activity b) {
			super(a, b);
		}
	}
    class MyResourceClient extends XWalkResourceClient {
		private int i;
		public void setIndex(int i){
			this.i=i;
		}
           MyResourceClient(XWalkView view) {
               super(view);
           }

           @Override
           public void onLoadStarted (XWalkView view, String url) {
               try {
                   JSONObject obj = new JSONObject();
                   obj.put("type", "loadstart");
                   obj.put("url", url);
				   obj.put("n", String.valueOf(this.i));
                   PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                   result.setKeepCallback(true);
                   callbackContext.sendPluginResult(result);
               } catch (JSONException ex) {}
           }

           @Override
           public void onLoadFinished (XWalkView view, String url) {
               try {
                   JSONObject obj = new JSONObject();
                   obj.put("type", "loadstop");
                   obj.put("url", url);
				   obj.put("n", String.valueOf(this.i));
                   PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                   result.setKeepCallback(true);
                   callbackContext.sendPluginResult(result);
               } catch (JSONException ex) {}
           }

            public void onLoadError (XWalkView view, String url) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "loaderror");
                    obj.put("url", url);
					obj.put("n", String.valueOf(this.i));
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                } catch (JSONException ex) {}
            }
    }
	
	
	

    private void openBrowser(final JSONArray data) throws JSONException {
	final int i=data.getInt(0);
        final String url = data.getString(1);
	
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Create Dialogs.
		if(brw[i] == null) {
                    	d[i] = new BrowserDialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
                    	brw[i] = new MyXWalkView(cordova.getActivity(), cordova.getActivity());
			//brw[i] = new WebView(cordova.getActivity());
					
                    	XWalkCookieManager mCookieManager = new XWalkCookieManager();
                    	mCookieManager.setAcceptCookie(true);
                    	mCookieManager.setAcceptFileSchemeCookies(true);
			MyResourceClient mrc=new MyResourceClient(brw[i]);
			mrc.setIndex(i);
                    	brw[i].setResourceClient(mrc);
						
                }else{
			//brw[i].loadUrl(url);
			brw[i].load(url,"");
			return;
		}
                //brw[i].loadUrl(url);
		brw[i].load(url,"");
                int left=0, top=0, width=0, height=0;
                float alpha=1f;
                if(data != null && data.length() > 1) {
                    try {
                        // Options will be like : left=0,top=150,width=320,height=200
                        String paramString = data.getString(1);
                        String[] params = paramString.split(",");

                        for (int i = 1; i < params.length; i++) {
                            String[] keyValue = params[i].split("=");

                            String paramKey = keyValue[0];
                            String paramVal = keyValue[1];

                            if (paramKey.compareTo("left") == 0) {
                                left = Integer.parseInt(paramVal);
                            } else if (paramKey.compareTo("top") == 0) {
                                top = Integer.parseInt(paramVal);
                            } else if (paramKey.compareTo("width") == 0) {
                                width = Integer.parseInt(paramVal);
                            } else if (paramKey.compareTo("height") == 0) {
                                height = Integer.parseInt(paramVal);
                            } else if (paramKey.compareTo("alpha") == 0) {
                                alpha = Float.parseFloat(paramVal);
                            }
                        }
                    }catch (JSONException ex){
                         String msg = ex.getMessage();
                    }
                }

                d[i].requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window dialogWindow = d[i].getWindow();

                // Set dialog as modaless
                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                dialogWindow.getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

                lp.x = left; // The new position of the X coordinates
                lp.y = top; // The new position of the Y coordinates
                lp.width = width; // Width
                lp.height = height; // Height
                lp.alpha = alpha; // Transparency
                dialogWindow.setAttributes(lp);

                d[i].setCancelable(true);
                d[i].addContentView(brw[i], lp);
                d[i].show();
            }
        });
    }

    public void loadUrl(final JSONArray data) throws JSONException {
	final int i=data.getInt(0);
		
        if(d[i] == null) {
            openBrowser(data);
            return;
        }
        final String url = data.getString(1);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //brw[i].loadUrl(url);
		brw[i].load(url,"");
                d[i].show();
            }
        });
    }

    public void hideBrowser(final JSONArray data) throws JSONException  {
	final int i=data.getInt(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(d[i] != null) {
                    d[i].hide();
                }
            }
        });
    }

    public void showBrowser(final JSONArray data) throws JSONException  {
	final int i=data.getInt(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(d[i] != null) {
                    d[i].show();
                }
            }
        });
    }

    public void closeBrowser(final JSONArray data) throws JSONException  {
	final int i=data.getInt(0);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(d[i] == null)
                    return;
				brw[i].onDestroy();
                d[i].dismiss();
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "exit");
                    PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                } catch (JSONException ex) {}

                brw[i] = null;
                d[i] = null;
            }
        });
    }

    // Set the left and top parameter. Data : LEFT, TOP
    public void setPosition(JSONArray data) throws JSONException {
	final int i=data.getInt(0);
        final int left = data.getInt(1);
        final int top = data.getInt(2);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    if(d[i] == null)
                        return;

                    Window dialogWindow = d[i].getWindow();
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    lp.x = left; // The new position of the X coordinates
                    lp.y = top; // The new position of the Y coordinates
                    dialogWindow.setAttributes(lp);
                    //d[i].setContentView(xWalkWebView, lp);
					d[i].setContentView(brw[i], lp);
                    d[i].show();

            }
        });
    }

    // Set the width and height parameter. Data : WIDTH, HIGHT
    public void setSize(JSONArray data)  throws JSONException {
		final int i=data.getInt(0);
        final int width = data.getInt(1);
        final int height = data.getInt(2);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(d[i] == null)
                    return;
                Window dialogWindow = d[i].getWindow();

                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = width; // The new position of the X coordinates
                lp.height = height; // The new position of the Y coordinates
                dialogWindow.setAttributes(lp);
				//d[i].setContentView(xWalkWebView, lp);
                d[i].setContentView(brw[i], lp);
                d[i].show();
            }
        });
    }


    // Set the width and height parameter. Data : ALPHA
    public void setAlpha(JSONArray data)   throws JSONException {
		final int i=data.getInt(0);
        final float alpha = (float) data.getDouble(1);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(d[i] == null)
                    return;

                Window dialogWindow = d[i].getWindow();

                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.alpha = alpha; // The new position of the X coordinates
                dialogWindow.setAttributes(lp);
				//d[i].setContentView(xWalkWebView, lp);
                d[i].setContentView(brw[i], lp);
                d[i].show();
            }
        });
    }


    private void injectDeferredObject(int n,String source, String jsWrapper,CallbackContext _callbackContext) {
        String scriptToInject;
		final int n2=n;
        if (jsWrapper != null) {
            org.json.JSONArray jsonEsc = new org.json.JSONArray();
            jsonEsc.put(source);
            String jsonRepr = jsonEsc.toString();
            String jsonSourceString = jsonRepr.substring(1, jsonRepr.length()-1);
            scriptToInject = String.format(jsWrapper, jsonSourceString);
        } else {
            scriptToInject = source;
        }
        final String finalScriptToInject = scriptToInject;
		callbackContext2 = _callbackContext;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
					Log.e("ERW", "evaluateJavascript(finalScriptToInject");
                    brw[n2].evaluateJavascript(finalScriptToInject, new ValueCallback<String>() {
						
						@Override
						public void onReceiveValue(String s) {
						String ret="";
							
							JsonReader reader = new JsonReader(new StringReader(s));

							// Must set lenient to parse single values
							reader.setLenient(true);

							try {
								if(reader.peek() != JsonToken.NULL) {
									if(reader.peek() == JsonToken.STRING) {
										String msg = reader.nextString();
										if(msg != null) {
											//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
											ret=ret.concat(msg);
										}
									}
								}
							} catch (IOException e) {
								Log.e("TAG", "injectDifferedObject: IOException", e);
							} finally {
								try {
									reader.close();
								} catch (IOException e) {
									// NOOP
								}
							}
						/**/
						//ret=s;
						if (callbackContext2!=null){
							PluginResult result = new PluginResult(PluginResult.Status.OK, ret);
							result.setKeepCallback(true);
							callbackContext2.sendPluginResult(result);
						}
							
							
							
						}
					});
					
					
					
					
					
					Log.e("ERW", finalScriptToInject);
            }
        });
    }
    // Get the screenshot of the browser dialog.
    // Params : JPEG Quality, and what to do callback.
	// add to config.xml for this to work:
	// <preference name="CrosswalkAnimatable" value="true" />
    public void getScreenshot(JSONArray data, CallbackContext _callbackContext)  throws JSONException {
		final int i=data.getInt(0);
        float fq =  (float) data.getDouble(1);
		final int q=Math.round(fq*100);
        callbackContext2 = _callbackContext;
		
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(d[i] == null)
                	return;

                Window dialogWindow = d[i].getWindow();
                Bitmap bitmap;
				bitmap = getBitmap(brw[i]);
                String base64Image = bitMapToString(bitmap,q);
		
		PluginResult result = new PluginResult(PluginResult.Status.OK, base64Image);
		result.setKeepCallback(true);
		callbackContext2.sendPluginResult(result);
		
            }
        });
    }

	
	
	// helper function for xwalk bitmap
	private Bitmap getBitmap(View ViewXW) {
		Bitmap bitmap = null;
		try {
			TextureView textureView = findXWalkTextureView((ViewGroup)ViewXW);
				if (textureView != null) {
					bitmap = textureView.getBitmap();
					return bitmap;
				}
		} catch(Exception e) {
			 e.printStackTrace();
		}
		Log.e("ERW", "getBitmap ---- bitmap from textureview failed");
		return bitmap;
	}
	// helper function for xwalk bitmap
	private TextureView findXWalkTextureView(ViewGroup group) {
		int childCount = group.getChildCount();
		for(int i=0;i<childCount;i++) {
			View child = group.getChildAt(i);
			if(child instanceof TextureView) {
				String parentClassName = child.getParent().getClass().toString();
				boolean isRightKindOfParent = (parentClassName.contains("XWalk"));
				if(isRightKindOfParent || true) {
					return (TextureView) child;
				}
			} else if(child instanceof ViewGroup) {
				TextureView textureView = findXWalkTextureView((ViewGroup) child);
				if(textureView != null) {
					return textureView;
				}
			}
		}
		return null;
	}
	
	
    // Helper method for storing the bitmap image for JPEG base64.
    private String bitMapToString(Bitmap bitmap, int q) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, q, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            System.gc();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }
	
	
	// Chack if browser has history
    // Params : browser window and what to do callback.
    public void hasHistory(JSONArray data, CallbackContext _callbackContext)  throws JSONException {
		final int i=data.getInt(0);
		callbackContext2 = _callbackContext;
		this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
				String ret="0";
                if (brw[i]==null) {
					PluginResult result = new PluginResult(PluginResult.Status.OK, ret);
					result.setKeepCallback(true);
					callbackContext.sendPluginResult(result);
				}
				
				Boolean res = brw[i].getNavigationHistory().canGoBack();
				if (res) {ret="1";}
				
				PluginResult result = new PluginResult(PluginResult.Status.OK, ret);
				result.setKeepCallback(true);
				callbackContext2.sendPluginResult(result);
            }
        });
		
		
		
    }
	
	
	// goBack in history
    public void goBack(JSONArray data) throws JSONException {
		final int i=data.getInt(0);
		this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (brw[i]==null) return;
		
				if (brw[i].getNavigationHistory().canGoBack())
					brw[i].getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
					}
        });
    }
	
}
