package it.cnr.iit.ck.features;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.cnr.iit.ck.logs.FileLogger;
import it.cnr.iit.ck.sensing.model.Featurable;
import it.cnr.iit.ck.sensing.model.Features;
import it.cnr.iit.ck.sensing.model.SensorData;
import it.cnr.iit.ck.sensing.probes.BaseProbe;
import it.cnr.iit.ck.sensing.probes.ContinuousProbe;

/**
 * The FeaturesExtractor extracts features from the sensed data (probes) that will be used as input
 * of the reasoning layer.
 */
public class FeaturesExtractor extends ContinuousProbe {

    private Features lastFeatures;
    private List<BaseProbe> probes;

    public FeaturesExtractor(List<BaseProbe> probes){
        this.probes = new ArrayList<>(probes);

        List<String> headers = new ArrayList<>();

        for(BaseProbe probe : this.probes)
            headers.add(probe.getFeaturesHeader());

        lastFeatures = new Features(StringUtils.join(headers, FileLogger.SEP));
    }

    @Override
    public void init() {}

    @Override
    public void onFirstRun() {}

    @Override
    public void exec() {

        List<Double> features = new ArrayList<>();
        for(BaseProbe probe : probes)
            features.addAll(probe.getLastData().getFeatures());

        lastFeatures.update(features);

        logOnFile(lastFeatures);
    }

    @Override
    public void onStop() {}

    @Override
    public void parseConfiguration(JsonObject configuration) {}

    @Override
    public SensorData getLastData() {
        return null;
    }

    @Override
    public String getFeaturesHeader() {
        return null;
    }
}
