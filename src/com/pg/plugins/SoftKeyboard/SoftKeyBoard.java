package com.pg.plugins.SoftKeyboard;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;

import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyBoard extends CordovaPlugin {

    public SoftKeyBoard() {
    }

    public void showKeyBoard() {
    	webView.requestFocus();
        InputMethodManager mgr = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(webView, InputMethodManager.SHOW_FORCED);
        
        //mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
       // mgr.sh
       // ((InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(webView, 0);
       
    }
    
    public void hideKeyBoard() {
        InputMethodManager mgr = (InputMethodManager) cordova.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(webView.getWindowToken(), 0);
    }
    
    public boolean isKeyBoardShowing() {
        
     int heightDiff = webView.getRootView().getHeight() - webView.getHeight();
     return (100 < heightDiff); // if more than 100 pixels, its probably a keyboard...
    }

    @Override
public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
if (action.equals("show")) {
            this.showKeyBoard();
            callbackContext.success("done");
            return true;
            //new PluginResult(PluginResult.Status.OK);
}
        else if (action.equals("hide")) {
            this.hideKeyBoard();
            callbackContext.success();
            return true;
            //new PluginResult(PluginResult.Status.OK);

        }
        else if (action.equals("isShowing")) {	
            callbackContext.success(Boolean.toString(this.isKeyBoardShowing()));
            return true;
            //new PluginResult(PluginResult.Status.OK);

        }
else {
return false;
    //new PluginResult(PluginResult.Status.ERROR);

}
}
}