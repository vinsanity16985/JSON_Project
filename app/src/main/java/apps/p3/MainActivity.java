package apps.p3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/*!!!!!!!!!!!!!!!!!!!!!!!!
Vincent Noca $ Josh Cohen
 */

public class MainActivity extends AppCompatActivity {

    private JSONTask jsonTask;
    private DownloadTask downloadTask;
    private SharedPreferences myPrefs;
    private ArrayList<Pet> myPets;
    Spinner spinner;
    private ImageView backgroundIMG;
    SharedPreferences.OnSharedPreferenceChangeListener optionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create the toolbar and set it as the main actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spinner = (Spinner)findViewById(R.id.spinner);
        myPets = new ArrayList<Pet>();
        myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        backgroundIMG = (ImageView)findViewById(R.id.background);

        //Tests the device's connection to a network
        if(!isConnected()){
            //Alerts the user if the device is not connected to a network
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
            adBuilder.setTitle(R.string.alertdialog_title);
            adBuilder.setMessage(R.string.alertdialog_message);
            adBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            adBuilder.show();
        }
        else{
            //Otherwise starts a thread
            jsonTask = new JSONTask(this);
            jsonTask.execute(myPrefs.getString("Settings Option", ""));
        }

        //Preference listener which is called whenever a change to the preferences is made by the user
        optionListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String url = myPrefs.getString(key, "");
                jsonTask = new JSONTask(MainActivity.this);
                jsonTask.execute(url);
            }
        };
        myPrefs.registerOnSharedPreferenceChangeListener(optionListener);
    }

    /**
     * Will populate the menu with menu items if there are any
     * @param: Menu menu, The menu that will be populated with menu items
     * @return: boolean true, Indicates that the menu was populated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if they are present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Determines which menu item was clicked by the user and calls necessary function to handle the click
     * @param: MenuItem item, The menu item that is clicked by the user
     * @return: boolean true, Indicates that a menu item was clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Determines if the users device is connected to a network or not
     * @return: boolean, indicates if the network is connected or not
     */
    public boolean isConnected(){
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            status = true;
        }

        return status;
    }

    /*
    Will fill the spinner with data from the list it is passed
    @param: ArrayList<Pet> pets, holds a pet object for each pet obtained from the site
     */
    public void populateSpinner(ArrayList<Pet> pets){
        ArrayList<String> petNames = new ArrayList<String>();
        for(Pet p : pets){
            petNames.add(p.getName());
        }
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, petNames);
        spinner.setAdapter(adapter);

        //A spinner listener that will call a thread to download the image for the selected pet in the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String imageURL = myPrefs.getString("Settings Option", "") + myPets.get(position).getPicture();
                downloadTask = new DownloadTask(MainActivity.this);
                downloadTask.execute(imageURL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*
    Called in JSONTask to transfer the ArrayList<Pet> from the thread to the mainActivity
    @param: ArrayList<Pet> pets, ArrayList taken from JSONTask
     */
    public void setMyPets(ArrayList<Pet> pets){
        this.myPets = pets;
        populateSpinner(myPets);
    }

    /*
    Takes in a bitmap and changes the background image to that bitmap
    @param: Bitmap bm, The bitmap that will be mapped to the background image
     */
    public void setImage(Bitmap bm){
        backgroundIMG.setImageBitmap(bm);
        backgroundIMG.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
