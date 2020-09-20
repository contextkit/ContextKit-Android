package it.cnr.iit.ck.sensing.model;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;
import it.cnr.iit.ck.logs.FileLogger;

public class WiFiAp implements SensorData{

    private static final List<String> LOG_HEADER = Arrays.asList("ssid", "bssid", "signal_level",
            "dbm_level","connected");
    public static final List<String> FEATURES_HEADER = Arrays.asList("wifi_connected");

    private static final int MAX_RSSI_LEVELS = 4;

    private String SSID, BSSID;
    private Integer signalLevel, dbmLevel;
    private Boolean connected;

    public WiFiAp(){}

    public WiFiAp(ScanResult scanResult, String connectedBSSID){

        this.connected = scanResult.BSSID.equals(connectedBSSID);
        this.SSID = scanResult.SSID;
        this.BSSID = scanResult.BSSID;
        this.signalLevel = WifiManager.calculateSignalLevel(scanResult.level, MAX_RSSI_LEVELS);
        this.dbmLevel = scanResult.level;
    }

    public boolean isConnected(){return this.connected;}

    @Override
    public String getDataToLog() {
        return SSID + FileLogger.SEP + BSSID + FileLogger.SEP + signalLevel + FileLogger.SEP +
                dbmLevel + FileLogger.SEP + connected;
    }

    @Override
    public String getLogHeader() {
        return StringUtils.join(LOG_HEADER, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() {
        return new ArrayList<>(Collections.singleton(FeaturesUtils.binarize(connected)));
    }
}
