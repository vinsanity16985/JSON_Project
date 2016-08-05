package apps.p3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, Bitmap> {

    private static final int DEFAULTBUFFERSIZE = 50;
    private static final int NODATA = -1;

    private MainActivity activity;
    private String url;

    public DownloadTask(MainActivity activity){
        attach(activity);
    }

    void attach(MainActivity activity){
        if(activity == null){
            throw new IllegalArgumentException(activity.getString(R.string.illegal_arg_exception));
        }
        this.activity = activity;
    }

    /*
    Connects to a given website and obtains a bitmap for the image being downloaded
    @param: String... params, Website(s) to connect to
    @return: Bitmap, Bitmap obtained from the site
     */
    @Override
    protected Bitmap doInBackground(String... params) {

        // site we want to connect to
        String url = params[0];

        // note streams are left willy-nilly here because it declutters the
        // example
        try {
            URL url1 = new URL(url);

            // this does no network IO
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

            // can further configure connection before getting data
            // cannot do this after connected
            // connection.setRequestMethod("GET");
            connection.setReadTimeout(500);
            connection.setConnectTimeout(500);

            // this opens a connection, then sends GET & headers
            connection.connect();

            // lets see what we got make sure its one of
            // the 200 codes (there can be 100 of them
            // http_status / 100 != 2 does integer div any 200 code will = 2
            int statusCode = connection.getResponseCode();
            if (statusCode / 100 != 2) {
                return null;
            }

            // get our streams, a more concise implementation is
            // BufferedInputStream bis = new
            // BufferedInputStream(connection.getInputStream());
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            // the following buffer will grow as needed
            ByteArrayOutputStream baf = new ByteArrayOutputStream(DEFAULTBUFFERSIZE);
            //ByteArrayBuffer baf = new ByteArrayBuffer(DEFAULTBUFFERSIZE);
            int current = 0;

            // wrap in finally so that stream bis is sure to close
            try {
                while ((current = bis.read()) != NODATA) {
                    baf.write((byte) current);
                }

                // convert to a bitmap
                byte[] imageData = baf.toByteArray();
                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            } finally {
                // close resource no matter what exception occurs
                bis.close();
            }
        } catch (IOException exc) {
            return null;
        }
    }

    /*
    Takes in a Bitmap obtained from doInBackground and sets the background ImageView to it
    @param: Bitmap result, Bitmap obtained from doInBackground
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            activity.setImage(result);
        }
    }
}
