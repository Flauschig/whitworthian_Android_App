package mayhem.whitworthian_v2.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** This is the SplashActivity.
 *  Includes the following functionality:
 *  -Retrieves article data from RSS feeds
 *  -Opens a Top News Article List
 *
 *  Contains the following class variables:
 *      app_Articles:       ArrayList containing all article data
 *      NUM_GENRES:         The total number of genres -- HARDCODED
 *      urls:               An array of URLs from which to obtain data through RSS
 *      alert:              A dialog that tells the user something went bad
 *      my_Progress_Bar:    The scroll wheel that tells the user that load is occuring
 *      my_Progress_Text:   Gives user idea that progress is being made on loading data
 *      locked:             Ensures that only one loading thread can exist at a time.
 */
public class SplashActivity extends ActionBarActivity {
    private ArrayList<Article> app_Articles;
    private final int NUM_GENRES = 5;
    private final URL urls[] = new URL[NUM_GENRES];
    private ProgressBar my_Progress_Bar;
    private TextView my_Progress_Text;
    private boolean locked;


    /* Creates the layout, fills the urls array, and fetches all data from thewhitworthian.com */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        locked = false;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        app_Articles = null;
        fill_URLs(); // fill url array
    }

    /* Inflates options menu without functionality */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    /*Fills the URL string with all appropriate feeds */
    private void fill_URLs() {
        try{
            String[] url_String = getResources().getStringArray(R.array.news_urls);
            for(int i = 0; i < url_String.length; i++) {
                urls[i] = new URL(url_String[i]);
            }
        }
        catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0037"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /*Opens up a background AsyncTask which fetches all of the data from the website */
    private class FetchArticlesTask extends AsyncTask<URL, Integer, ArrayList<Article>> {
        /*doInBackground is where the action happens, connection is made here, and data is
         * collected.
         */
        //TODO: Fix crash on loss of internet connectivity.
        //TODO: Try to make the data collection and storing cleaner/more efficient
        @Override
        protected ArrayList<Article> doInBackground(URL... urls) {
            RssHandler new_Parser = new RssHandler(getApplicationContext());
            ArrayList<Article> arrays[] = new ArrayList[NUM_GENRES];
            for (int i = 0; i < NUM_GENRES; i++) { // loop through all feeds
                try {
                    if (is_Network_Connected()) {
                        //Setup for connection
                        InputStream input = urls[i].openStream();
                        new_Parser.parse(input);
                        new_Parser.getArticleList(); //store the data.
                        publishProgress(new Integer[]{i+1});
                        if (i == 0) {
                            new_Parser.mark_Top();
                        }
                    } else {
                        return null;
                    }
                } catch (Exception bad) {
                    Toast.makeText(getApplicationContext(),
                            String.format("Failed to retrieve articles! \nCode: 6d617968656d-0038"),
                            Toast.LENGTH_LONG).show();
                }
            }
            publishProgress(new Integer[]{NUM_GENRES+1});

            return new_Parser.getArticleList();
        }

        /*Check to see if connected to a network*/
        private boolean is_Network_Connected() {
            final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
        }




        /* Updates load text on splash page */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            switch(progress[0]) {
                case 0: update_Progress(getResources().getString(R.string.get_top));
                    break;
                case 1: update_Progress(getResources().getString(R.string.get_news));
                    break;
                case 2: update_Progress(getResources().getString(R.string.get_sports));
                    break;
                case 3: update_Progress(getResources().getString(R.string.get_opinion));
                    break;
                case 4: update_Progress(getResources().getString(R.string.get_ac));
                    break;
                case 5: update_Progress(getResources().getString(R.string.cleaning_data));
                    break;
            }
        }

        /* After articles are gathered, this opens up the Top News article list*/
        @Override
        protected void onPostExecute(ArrayList<Article> result) {
            super.onPostExecute(result);

            if(result==null) {
                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(getApplicationContext(), "Internet Connection Failed.", Toast.LENGTH_SHORT).show();
                        update_Progress(getResources().getString(R.string.connection_fail));
                        hide_Progress();
                    }
                });
                locked = false;
                return;
            }

            try{
                app_Articles = result;
                read_File(getResources().getString(R.string.article_file));
                Intent article_List = new Intent(SplashActivity.this, ArticleListActivity.class);
                article_List.putExtra("this_Genre", "Top News");
                article_List.putParcelableArrayListExtra("my_Articles", app_Articles);
                article_List.putExtra("first_Instance", true);
                startActivity(article_List);

                // close this activity
                finish();
            } catch(Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occured! \nCode: 6d617968656d-0040"),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /*Handles item menu click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                try {
                    if (!(locked)) {
                        locked = true;
                        my_Progress_Bar.setVisibility(View.VISIBLE);
                        update_Progress(getResources().getString(R.string.load_text));
                        new FetchArticlesTask().execute(this.urls);
                    }
                    return true;
                } catch(Exception bad) {
                    Toast.makeText(getApplicationContext(),
                            String.format("A non-fatal error occured! \nCode: 6d617968656d-0041"),
                            Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Initialize the progress bar & text */
    public void init_Progress_Bar(View view) {
        try{
            my_Progress_Bar = (ProgressBar) view.findViewById(R.id.news_Load_Bar);
            my_Progress_Text = (TextView) view.findViewById(R.id.progress_Text);
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occured! \nCode: 6d617968656d-0042"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /*Hide progress bar */
    public void hide_Progress() {
        my_Progress_Bar.setVisibility(View.INVISIBLE);
    }

    /*Update text in progress textview */
    public void update_Progress(String update){
        if (my_Progress_Text != null) {
            my_Progress_Text.setText(update);
        }
    }

    private void read_File(String file_Name){

        //TODO: Add error code

        // If the article has previously been viewed, then set viewed
        try {
            // Set up the buffer for the input
            byte[] buffer;
            // Set up the file input stream
            File file = new File(getFilesDir()+File.separator+file_Name);
            if (file.exists()) {

                //FileInputStream fis = new FileInputStream(file);
                FileReader file_Reader = new FileReader(getFilesDir()+File.separator+file.getName());
                BufferedReader buffer_Reader = new BufferedReader(file_Reader);
                String line = buffer_Reader.readLine();
                /*temp="";
                while((c = fis.read()) != -1){
                    temp = temp + Character.toString((char)c);
                }
                fis.close(); */

                // Split the String on @, and feed the article IDs into a String List
                String[] articles_array = line.split("@");
                List<String> articles = new ArrayList();
                int spill_Over = 60;

                // If there are over 60 more saved articles than the articles currently in the feed,
                // delete the first half and overwrite the file
                if(articles_array.length > app_Articles.size() + spill_Over){
                    String tempStr = "";
                    // Copy as many articles as are in the article list
                    for(int i = spill_Over; i < articles_array.length; i++){
                        tempStr += articles_array[i];
                        tempStr += "@";
                        articles.add(articles_array[i]);
                    }

                    // Overwrite "ArticlesViewed"
                    // Write the string to the file "ArticlesViewed"
                    file.delete();
                    file.createNewFile();
                    FileWriter file_Writer = new FileWriter(getFilesDir()+File.separator+file.getName(), true);
                    BufferedWriter buffer_Writer = new BufferedWriter(file_Writer);
                    buffer_Writer.write(tempStr);
                    buffer_Writer.close();
                } else {
                    for(int i = 0; i < articles_array.length; i++){
                        articles.add(articles_array[i]);
                    }
                }


                // Check each article ID in articles, and if it matches
                // article_Data[j].get_ID(), then it has already been viewed
                // Set article_Data[j].set_Viewed to true.
                for(int i = 0; i < articles.size(); i++){
                    for(int j = 0; j < app_Articles.size(); j++){
                        if(Integer.toString(app_Articles.get(j).get_Article_ID()).
                                equals(articles.get(i))){
                            app_Articles.get(j).set_Viewed(true);
                        }
                    }
                }

            }

            // Catch exceptions
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), String.format("Creating file to save articles viewed...", e.toString()), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), String.format("Error! %s", e.toString()), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash, container, false);

            init_Progress_Bar(rootView); //Initialize progress bar
            if (!(locked)) {
                locked = true;
                new FetchArticlesTask().execute(urls); // fetch data
            }

            return rootView;
        }
    }

}
