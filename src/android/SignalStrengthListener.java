package com.eyc.listener;

import android.annotation.SuppressLint;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

/**
 * A listener for listening to changes in network connection strength. 
 * This is required for monitoring 2g/3g signal strength
 * Everytime the signal strength changes, the new signal strength (in ASU) is stored in signalStrengthASU data member
 * @author cmahesh
 */

@SuppressLint("NewApi")
public class SignalStrengthListener extends PhoneStateListener {
	/*Static data memeber that stores the value of the latest signal strength (in ASU)*/
	private static int signalStrengthAsu;

	@Override
    public void onSignalStrengthChanged(int asu){
		Log.d("PhoneStateListener", "Signal strength in asu: " + String.valueOf(asu));
		SignalStrengthListener.signalStrengthAsu = asu;
		super.onSignalStrengthChanged(asu);
    }
	
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
	    int signalLevel = getAsuLevel(signalStrength);
		SignalStrengthListener.signalStrengthAsu = (signalLevel == 99? 0: signalLevel); 
	    super.onSignalStrengthsChanged(signalStrength);
	    Log.d("PhoneStateListener", "Signal strength (new): " + String.valueOf( SignalStrengthListener.signalStrengthAsu));
	    
	}
	
	/**
	 * Getter for signal strength value in ASU
	 * @return signal strength value in ASU
	 */
	public int getSignalStrength(){
		return SignalStrengthListener.signalStrengthAsu;				
	}
	
	/**
     * Get the signal level as an asu value between 0..31
     *
     * @hide
     */
    public int getAsuLevel(SignalStrength signalStrength) {
        int asuLevel;
        if (signalStrength.isGsm()) {
        	asuLevel = signalStrength.getGsmSignalStrength();
        } else {
            int cdmaAsuLevel = getCdmaAsuLevel(signalStrength);
            int evdoAsuLevel = getEvdoAsuLevel(signalStrength);
            if (evdoAsuLevel == 0) {
                /* We don't know evdo use, cdma */
                asuLevel = cdmaAsuLevel;
            } else if (cdmaAsuLevel == 0) {
                /* We don't know cdma use, evdo */
                asuLevel = evdoAsuLevel;
            } else {
                /* We know both, use the lowest level */
                asuLevel = cdmaAsuLevel < evdoAsuLevel ? cdmaAsuLevel : evdoAsuLevel;
            }
        }      
        return asuLevel;
    }
	
	 /**
     * Get the cdma signal level as an asu value between 0..31
     *
     * @hide
     */
    public int getCdmaAsuLevel(SignalStrength signalStrength) {
        final int cdmaDbm = signalStrength.getCdmaDbm();
        final int cdmaEcio = signalStrength.getCdmaEcio();
        int cdmaAsuLevel;
        int ecioAsuLevel;
        
        int maxDbm = -75;
        int minDbm = -100;
        if(cdmaDbm >= maxDbm) cdmaAsuLevel = 31;
        else if (cdmaDbm <= minDbm) cdmaAsuLevel = 0;
        else{
        	cdmaAsuLevel = (int)((float)(cdmaDbm - minDbm) * 31 / (maxDbm - minDbm));
        }

        int maxEcio = -90;
        int minEcio = -150;
        if(cdmaEcio >= maxEcio) ecioAsuLevel = 31;
        else if (cdmaEcio <= minEcio) ecioAsuLevel = 0;
        else{
        	ecioAsuLevel = (int)((float)(cdmaEcio - minEcio) * 31 / (maxEcio - minEcio));
        }

        int level = (cdmaAsuLevel < ecioAsuLevel) ? cdmaAsuLevel : ecioAsuLevel;
       
        return level;
    }
    
    /**
     * Get the evdo signal level as an asu value between 0..31
     *
     * @hide
     */
    public int getEvdoAsuLevel(SignalStrength signalStrength) {
        int evdoDbm = signalStrength.getEvdoDbm();
        int evdoSnr = signalStrength.getEvdoSnr();
        int levelEvdoDbm;
        int levelEvdoSnr;

        int maxDbm = -65;
        int minDbm = -105;
        if(evdoDbm >= maxDbm) levelEvdoDbm = 31;
        else if (evdoDbm <= minDbm) levelEvdoDbm = 0;
        else{
        	levelEvdoDbm = (int)((float)(evdoDbm - minDbm) * 31 / (maxDbm - minDbm));
        }
        
        int maxSnr = 7;
        int minSnr = 1;
        if(evdoSnr >= maxSnr) levelEvdoSnr = 31;
        else if (evdoSnr <= minSnr) levelEvdoSnr = 0;
        else{
        	levelEvdoSnr = (int)((float)(evdoSnr - minSnr) * 31 / (maxSnr - minSnr));
        }
       
        int level = (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
     
        return level;
    }

}
