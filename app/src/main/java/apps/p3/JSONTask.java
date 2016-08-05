package apps.p3;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JSONTask extends AsyncTask<String, Void, ArrayList<Pet>>{

    private final static int TIMEOUT = 1000;
    private final static int READ_THIS_AMOUNT = 8096;
    private MainActivity activity;
    private ArrayList<Pet> pets = new ArrayList<Pet>();

    public JSONTask(MainActivity activity){
        attach(activity);
    }

    void detach(){
        this.activity = null;
    }

    void attach(MainActivity activity){
        if(activity == null){
            throw new IllegalArgumentException("Activity cannot be null");
        }
        this.activity = activity;
    }

    /*
    Called upon execution of the thread
    It will connect to a given website, obtain the JSON data and then put it in an ArrayList<Pet>
    @param: String... params, website(s) to connect to
    @return: ArrayList<Pet>, The list obtained from JSON data
     */
    protected ArrayList<Pet> doInBackground(String... params){
        JSONParse parser = new JSONParse();
        String url = params[0]+activity.getString(R.string.image_ext);
        String json;

        try {
            URL Url = new URL(url);

            // this does no network IO
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();

            // can further configure connection before getting data
            // cannot do this after connected
            connection.setRequestMethod("GET");
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            // this opens a connection, then sends GET & headers

            // wrap in finally so that stream bis is sure to close
            // and we disconnect the HttpURLConnection
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()), READ_THIS_AMOUNT);
            try {
                connection.connect();

                // lets see what we got make sure its one of
                // the 200 codes (there can be 100 of them
                // http_status / 100 != 2 does integer div any 200 code will = 2
                int statusCode = connection.getResponseCode();
                if (statusCode / 100 != 2) {
                    //Log.e(TAG, "Error-connection.getResponseCode returned "
                    //        + Integer.toString(statusCode));
                    return null;
                }

                //in = new BufferedReader(new InputStreamReader(connection.getInputStream()), READ_THIS_AMOUNT);

                // the following buffer will grow as needed
                String myData;
                StringBuffer sb = new StringBuffer();

                while ((myData = in.readLine()) != null) {
                    sb.append(myData);
                }

                //json = sb.toString();
                pets = parser.getPets(sb.toString());

                return pets;
            } finally {
                // close resource no matter what exception occurs
                in.close();
                connection.disconnect();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }

    /*
    Called after doInBackground is complete
    Will transfer the ArrayList<Pet> obtained in doInBackground to the MainActivity
    @param: ArrayList<Pet> result, ArrayList to be transferred
     */
    @Override
    protected void onPostExecute(ArrayList<Pet> result) {
        if(activity != null){
            if(result != null){
                //If not null transfer the list
                this.activity.setMyPets(result);
            }
            else{
                //Otherwise make the spinner invisible and give the user an error message
                this.activity.spinner.setVisibility(View.INVISIBLE);
                Toast.makeText(this.activity, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
