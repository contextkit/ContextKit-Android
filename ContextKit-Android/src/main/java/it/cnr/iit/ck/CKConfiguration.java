package it.cnr.iit.ck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CKConfiguration implements Serializable {

    public static final String PROBE_LOCATION = "LocationProbe";

    private String logBaseDirectory;
    private List<ProbeConf> probes = new ArrayList<>();

    public CKConfiguration(String logBaseDirectory){
        this.logBaseDirectory = logBaseDirectory;
    }

    public void addProbe(ProbeConf probe){this.probes.add(probe);}
    public List<ProbeConf> getProbes(){return this.probes;}
    public String getLogBaseDirectory(){return this.logBaseDirectory;}

    public static class ProbeConf implements Serializable{

        public String probeClass, logFile;
        public int startDelay, interval;

        public ProbeConf(String probeClass, String logFile, int startDelay, int interval){
            this.probeClass = probeClass;
            this.logFile = logFile;
            this.startDelay = startDelay;
            this.interval = interval;
        }
    }
}
