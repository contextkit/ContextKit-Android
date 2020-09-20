package it.cnr.iit.ckdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import it.cnr.iit.ck.CKManager;
import it.cnr.iit.ck.ContextKit;

public class SensingActivity extends AppCompatActivity {

    private ContextKit contextKit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensing_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(CKManager.RUNNING) ((Button)findViewById(R.id.button2)).setText("STOP READING");
        else ((Button)findViewById(R.id.button2)).setText("START NEW READING");
    }

    private void startASK(){

        contextKit= new ContextKit(this, getResources().getString(R.string.ck_conf));
        try {
            contextKit.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopASK(){

        if(contextKit != null) contextKit.stop();
    }

    public void onControlClicked(View view){

        if(!CKManager.RUNNING){
            startASK();
            ((Button)view).setText("STOP READING");

        }else{
            stopASK();
            ((Button)view).setText("START NEW READING");
        }
    }
}
