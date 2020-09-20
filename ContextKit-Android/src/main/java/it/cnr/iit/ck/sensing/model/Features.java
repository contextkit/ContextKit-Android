package it.cnr.iit.ck.sensing.model;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import it.cnr.iit.ck.logs.FileLogger;

public class Features implements Loggable {

    private String logHeader;
    private List<Double> features;

    public Features(String logHeader){
        this.logHeader = logHeader;
    }

    public void update(List<Double> features){
        this.features = features;
    }

    public boolean isComplete(){
        return !features.contains(null);
    }

    @Override
    public String getDataToLog() {
        return StringUtils.join(features, FileLogger.SEP);
    }

    @Override
    public String getLogHeader() {
        return logHeader;
    }
}
