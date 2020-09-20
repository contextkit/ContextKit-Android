package it.cnr.iit.ck.reasoning;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Environment;

import com.opencsv.CSVReader;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private Interpreter tfLite;

    public Test(Context context){

        try {
            tfLite = new Interpreter(loadModelFile(context));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws Exception{
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("context_recognition.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public double fullExperiment(){

        List<Long> times = new ArrayList<>();

        String filePath = "/cr_tests/device_test_full_features.csv";

        try {
            File dataFile = new File(Environment.getExternalStorageDirectory() + filePath);

            CSVReader dataReader = new CSVReader(new FileReader(dataFile.getAbsolutePath()));
            String[] nextLine;

            // Skip the header
            dataReader.readNext();
            while ((nextLine = dataReader.readNext()) != null) {
                long execTime = doInference(nextLine);
                times.add(execTime);
            }

            dataReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        double avg = 0.0;
        for(Long time : times) avg += time;

        return avg;
    }

    private long doInference(String[] csvLine){

        float[] example = new float[csvLine.length];

        for(int i=0; i<csvLine.length; i++)
            example[i] = Float.parseFloat(csvLine[i]);

        float[][] output = new float[1][8];

        long startTime = System.nanoTime();
        tfLite.run(example, output);
        long stopTime = System.nanoTime();

        return (stopTime-startTime)/1000000;
    }

    public void close(){
        tfLite.close();
    }
}
