package it.cnr.iit.ck.sensing.controllers;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ck.sensing.model.ApplicationData;

public class GooglePlayStoreController {

    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?hl=en&id=";
    private static GooglePlayStoreController instance;

    private GooglePlayStoreController(){}

    public static GooglePlayStoreController getInstance(){
        if(instance == null) instance = new GooglePlayStoreController();

        return instance;
    }

    public void getAppCategory(Context context, ApplicationsDataListener listener, String... packages){
        new FetchCategoryTask(context, listener).execute(packages);
    }

    public interface ApplicationsDataListener{
        void onApplicationsAvailable(List<ApplicationData> applications);
    }

    private class FetchCategoryTask extends AsyncTask<String, Void, List<ApplicationData>> {

        private Context context;
        private ApplicationsDataListener listener;

        FetchCategoryTask(Context context, ApplicationsDataListener listener){
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected List<ApplicationData> doInBackground(String... packages) {

            List<ApplicationData> applications = new ArrayList<>();

            for(String packageName : packages){

                String category = PreferencesController.getAppCategory(context, packageName);

                if(category == null)
                    category = getCategory(GOOGLE_URL + packageName);

                PreferencesController.saveAppCategory(context, packageName, category);
                applications.add(new ApplicationData(packageName, category));
            }

            return applications;
        }

        @Override
        protected void onPostExecute(List<ApplicationData> applications) {
            super.onPostExecute(applications);
            listener.onApplicationsAvailable(applications);
        }

        private String getCategory(String query_url) {

            String category = "";

            try {
                Document document = Jsoup.connect(query_url).get();
                Element element = document.select("[itemprop=genre]").first();
                if(element != null) category = element.text();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return category;
        }
    }
}
