/*
 *   Copyright (c) 2017. Mattia Campana, mattia.campana@iit.cnr.it, Franca Delmastro, franca.delmastro@gmail.com
 *
 *   This file is part of ContextKit.
 *
 *   ContextKit (CK) is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ContextKit (CK) is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ContextKit (CK).  If not, see <http://www.gnu.org/licenses/>.
 */

package it.cnr.iit.ck.commons;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SensorSamples {

    private DescriptiveStatistics[] dimensions;

    public SensorSamples(int nDimensions, int maxElements){

        dimensions = new DescriptiveStatistics[nDimensions];
        for(int i=0; i<nDimensions; i++) dimensions[i] = new DescriptiveStatistics(maxElements);
    }

    public void newSample(float[] values){

        for(int i=0; i<dimensions.length; i++)
                dimensions[i].addValue(values[i]);
    }

    public int getNumberOfSamples(){
        return dimensions[0].getValues().length;
    }

    public double[] getStatistics() {

        double stats[] = null;

        for(DescriptiveStatistics dim : dimensions){

            double[] dimStats;

            if(dim.getN() == 0) {
                dimStats = new double[14];
                for(int i=0; i<14; i++) dimStats[i] = -1;

            }else {
                dimStats = new double[]{
                        dim.getMin(),
                        dim.getMax(),
                        dim.getMean(),
                        dim.getQuadraticMean(),
                        dim.getPercentile(25),
                        dim.getPercentile(50),
                        dim.getPercentile(75),
                        dim.getPercentile(100),
                        dim.getVariance(),
                        dim.getPopulationVariance(),
                        dim.getSkewness(),
                        dim.getSumsq(),
                        dim.getStandardDeviation(),
                        dim.getKurtosis()
                };
            }

            if(stats == null) stats = dimStats;
            else stats = ArrayUtils.addAll(stats, dimStats);

        }

        return stats;
    }

    public void reset(){
        for(DescriptiveStatistics ds : dimensions) ds.clear();
    }
}
