package it.cnr.iit.ck.sensing.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;

public class RunningApplications implements SensorData {

    private List<ApplicationData> applications = new ArrayList<>();
    private String[] categories;

    public RunningApplications(String[] categories){this.categories = categories;}

    public RunningApplications(List<ApplicationData> applications){
        this.applications = applications;
    }

    public void setCategories(String[] categories){
        this.categories = categories;
    }

    @Override
    public List<Double> getFeatures() {

        List<String> appsCategories = new ArrayList<>();
        for(ApplicationData app : applications) appsCategories.add(app.category);

        return FeaturesUtils.oneHotVector(appsCategories, (List<String>)Arrays.asList(categories));
    }

    // Not used
    @Override
    public String getDataToLog() { return null;}

    @Override
    public String getLogHeader() { return null;}
}
