package it.cnr.iit.ckdemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.cnr.iit.ck.reasoning.Test;

public class CRActivityDemo extends AppCompatActivity {

    private Context context;

    private TextView runningExpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.context_recognition_activity);

        runningExpTV = findViewById(R.id.running_exp_tv);

        this.context = this;
    }

    public void onStartFullExperimentClicked(View view){

        ExecutorService schTaskEx = Executors.newFixedThreadPool(1);
        schTaskEx.execute(new FullExperimentRunnable(5));
    }


    class FullExperimentRunnable implements Runnable{

        int run;

        public FullExperimentRunnable(int run){
            this.run = run;
        }

        public void run(){
            for(int i=0; i<this.run; i++){
                runningExpTV.setText("Running exp: " + (i+1) + "/" + this.run);

                Test test = new Test(context);

                double avgTime = test.fullExperiment();
                test.close();

                Log.d("CR", "Test " + i + "Average time: " + avgTime + "ms");
                System.gc();
            }
        }
    }
}
