package it.cnr.iit.ck.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class FeaturesUtils {

    public static double binarize(boolean feature) {
        return feature ? 1.0 : 0.0;
    }

    /**
     * Return one hot encoding for variable in classes vector
     * @param variable
     * @param classes vector of objects (not primitives) or single values (objects or primitives)
     * @param <T>
     * @return
     */
    public static <T> List<Double> oneHotVector(T variable, List<T> classes) {
        List<Double> oneHotVector = new ArrayList<>(Collections.nCopies(classes.size(), 0.0));
        if(variable != null)
            for (T aClass : classes) {
                oneHotVector.add(binarize(variable.equals(aClass)));
            }
        return oneHotVector;
    }

    public static List<Double> oneHotVector(List<String> variable, List<String> classes) {

        TreeMap<String, Double> cls = new TreeMap<>();
        for(String aClass : classes) cls.put(aClass.toUpperCase(), 0.0);

        if(variable != null)
            for(String aVar : variable)
                if(cls.containsKey(aVar.toUpperCase())) cls.put(aVar.toUpperCase(), 1.0);

        return new ArrayList<>(cls.values());
    }
}
