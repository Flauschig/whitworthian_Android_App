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
import java.io.PrintWriter;
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


    /** OVERRIDEN ACTIVITY FUNCTIONS
     * onCreate()
     * onCreateOptionsMenu()
     * onOptionsItemSelected()
     */

    /* Creates the layout, fills the urls array, and creates a Placeholder Fragment, which
       fetches data from thewhitworthian.com */
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

    /* Inflates options menu with refresh button */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu so that users can refresh if no internet connection.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    /*Handles refresh click */
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
                    return super.onOptionsItemSelected(item);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** VARIABLE/VIEW INITIALIZATION FUNCTIONS
     * fill_URLs() function to get url strings
     * init_Progress_Bar() function to find progress wheel/text in view
     */

    /*Fills the URL array with the URLs of the feeds from strings.xml's "news_urls" array*/
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


    /** PROGRESS UPDATE FUNCTIONS
     * hide_Progress() function to hide progress bar
     * update_Progress() function to change progress text
     */

    /*Hide progress bar */
    public void hide_Progress() {
        my_Progress_Bar.setVisibility(View.INVISIBLE);
    }

    /*Update text in progress TextView */
    public void update_Progress(String update){
        if (my_Progress_Text != null) {
            my_Progress_Text.setText(update);
        }
    }


    /** DATA GATHERING:
     *  FetchArticlesTask class for getting data from thewhitworthian.com
     *  read_File() function for figuring out which articles are viewed
     */

    /*Opens up a background AsyncTask which fetches all of the data from the website */
    private class FetchArticlesTask extends AsyncTask<URL, Integer, ArrayList<Article>> {
        /*doInBackground is where the action happens, connection is made here, and data is
         * collected. */
        //TODO: Fix crash on loss of internet connectivity DURING load.
        @Override
        protected ArrayList<Article> doInBackground(URL... urls) {
            // Set up an RssHandler class, which will parse the feed.
            RssHandler new_Parser = new RssHandler(getApplicationContext());

            //Loop through all feed URLs
            for (int i = 0; i < NUM_GENRES; i++) {
                try {
                    //If there's a network connection
                    if (is_Network_Connected()) {
                        //Connect to the RSS stream and get the data
                        InputStream input = urls[i].openStream();
                        new_Parser.parse(input);
                        new_Parser.getArticleList(); //store the data.
                        publishProgress(new Integer[]{i+1});

                        //Mark top news articles as top news
                        if (i == 0) {
                            new_Parser.mark_Top();
                        }
                    } else {
                        //If there's not a connection, return null ->
                        // informs user in later function
                        return null;
                    }
                } catch (Exception bad) {
                    Toast.makeText(getApplicationContext(),
                            String.format("Failed to retrieve articles! \nCode: 6d617968656d-0038"),
                            Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            //Display progress to the user
            publishProgress(new Integer[]{NUM_GENRES+1});

            return new_Parser.getArticleList();
        }

        /*Check to see if connected to a network*/
        private boolean is_Network_Connected() {
            final ConnectivityManager conMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.getState() == NetworkInfo.State.CONNECTED;
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

        /* After articles are gathered, this opens up the Top News article list
        *  If articles weren't gathered, this exits and lets user refresh */
        @Override
        protected void onPostExecute(ArrayList<Article> result) {
            super.onPostExecute(result);

            //If the connection failed, tell the user & unlock load threads
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
                //In case something went wrong, unlock refresh button
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occured! \nCode: 6d617968656d-0040"),
                        Toast.LENGTH_LONG).show();
                locked = false;
            }
        }
    }

    /* Figures out, from file, which articles have been viewed and marks those articles as viewed.
       If the file gets too big, read_File() trims it down, only keeping the most recent articles
       in memory. */
    private void read_File(String file_Name){
        try {
            // Grab the article view file
            File file = new File(getFilesDir()+File.separator+file_Name);

            //If that file exists, then...
            if (file.exists()) {
                //Read in all the data in the file
                FileReader file_Reader = new FileReader(getFilesDir()+File.separator+file.getName());
                BufferedReader buffer_Reader = new BufferedReader(file_Reader);
                String line = buffer_Reader.readLine();

                // Initialize variables, then Split the IDs apart on @
                int spill_Over = 60;
                List<String> articles = new ArrayList();
                String[] articles_array = line.split("@");


                // If there are over 60 more saved articles than the articles currently in the feed,
                // delete the 60.
                if(articles_array.length > app_Articles.size() + spill_Over){
                    String tempStr = "";
                    // Copy ID# 61+, and create a string that will rewrite them to file
                    for(int i = spill_Over; i < articles_array.length; i++){
                        tempStr += articles_array[i];
                        tempStr += "@";
                        articles.add(articles_array[i]);
                    }

                    // Flush "ArticlesViewed.txt"
                    PrintWriter writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();

                    // Rewrite trimmed IDs to file.
                    FileWriter file_Writer = new FileWriter(getFilesDir()+
                            File.separator+file.getName(), true);
                    BufferedWriter buffer_Writer = new BufferedWriter(file_Writer);
                    buffer_Writer.write(tempStr);
                    buffer_Writer.close();
                } else {
                    //If there aren't too many IDs in the file, don't worry about cleaning
                    for(int i = 0; i < articles_array.length; i++){
                        articles.add(articles_array[i]);
                    }
                }

                // Check each article ID from the file.  If it matches
                // any article in app_Articles, then set that matching article as viewed
                for(int i = 0; i < articles.size(); i++){
                    for(int j = 0; j < app_Articles.size(); j++){
                        if(Integer.toString(app_Articles.get(j).get_Article_ID()).
                                equals(articles.get(i))){
                            app_Articles.get(j).set_Viewed(true);
                        }
                    }
                }

            }
        // Catch exceptions, print out error code
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0045"),
                    Toast.LENGTH_LONG).show();
        }

    }



    /** PLACEHOLDERFRAGMENT:
     *  This is the fragment which the user sees when SplashActivity is active.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {}

        /*Creates the view and fetches articles*/
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
