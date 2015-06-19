package com.eyc.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.eyc.listener.SignalStrengthListener;


/**
 * To retrieve WiFi/2g/3g signal strength (based on conenction type) (in percent)
 * @author cmahesh
 */

@SuppressLint("NewApi")
public class SignalStrength extends CordovaPlugin {
	public CallbackContext connectionCallbackContext;
	/*32 levels are used to be consistent with the range returned by getGSMSignalStrength of SignalStrength object*/    
	private final int levels = 32;
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {		
		this.connectionCallbackContext = callbackContext;
		Activity activity = this.cordova.getActivity();
    	if (action.equals("getSignalStrength")) {
    		/*To check if there is a WiFi connection to measure WiFi signal strength*/
    		ConnectivityManager sockMan = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = sockMan.getActiveNetworkInfo();
            int signalStrength;
            if(info.getTypeName().equalsIgnoreCase("wifi")){
            	WifiSignalStrength wifiSignalStrength = new WifiSignalStrength();
            	signalStrength = wifiSignalStrength.getWifiSignalStrengthPercentage(activity);
            }
            else{            	
        		signalStrength = getNetworkSignalStrengthPercentage(activity);            
            }
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, String.valueOf(signalStrength));
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
    		return true;
    	
    		
    	} else {
    		return false;
    	}
	}
	
	
	/**
	 * 
	 * @param activity
	 * @return Network signal strength in percentage
	 */
	public int getNetworkSignalStrengthPercentage(Activity activity){
		SignalStrengthListener listener = new SignalStrengthListener();
		int signalStrengthASU = listener.getSignalStrength();   
		return (int)(signalStrengthASU * 100 / (levels - 1));
	}
}
