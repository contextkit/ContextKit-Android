package it.cnr.iit.ck.sensing.model;

import it.cnr.iit.ck.logs.FileLogger;

public class ApplicationData implements Loggable{

    private static final String LOG_HEADER = "package,category";

    public String packageName, category;

    public ApplicationData(String packageName, String category){
        this.packageName = packageName;
        this.category = category;
    }

    @Override
    public String getDataToLog() {
        return packageName + FileLogger.SEP + category;
    }

    @Override
    public String getLogHeader() {
        return LOG_HEADER;
    }
}
