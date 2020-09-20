package it.cnr.iit.ck.sensing.model;

import java.util.List;

public interface SensorData extends Loggable{

    List<Double> getFeatures();
}
