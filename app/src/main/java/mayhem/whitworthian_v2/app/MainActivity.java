package mayhem.whitworthian_v2.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/** This is the MainActivity.
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
 */
public class MainActivity extends ActionBarActivity {
    private ArrayList<article> app_Articles;
    private final int NUM_GENRES = 5;
    private final URL urls[] = new URL[NUM_GENRES];
    private ProgressBar my_Progress_Bar;
    private TextView my_Progress_Text;


    /* Creates the layout, fills the urls array, and fetches all data from thewhitworthian.com */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        fill_URLs(); // fill url array

        app_Articles = null;
    }

    /* Inflates options menu without functionality */
    //TODO: Add a refresh if the program loses internet connection, or something of the sort
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    /*Fills the URL string with all appropriate feeds */
    private void fill_URLs() {
        try{
            urls[0] = new URL("http://www.thewhitworthian.com/feed/");
            urls[1] = new URL("http://www.thewhitworthian.com/category/news/feed/");
            urls[2] = new URL("http://www.thewhitworthian.com/category/sports/feed/");
            urls[3] = new URL("http://www.thewhitworthian.com/category/opinions/feed/");
            urls[4] = new URL("http://www.thewhitworthian.com/category/arts-and-culture/feed/");
        }
        catch (MalformedURLException bad1) {
            bad1.printStackTrace();
        }
    }

    /*Opens up a background AsyncTask which fetches all of the data from the website */
    private class FetchArticlesTask extends AsyncTask<URL, Integer, ArrayList<article>> {
        /*doInBackground is where the action happens, connection is made here, and data is
         * collected.
         */
        //TODO: Fix crash on loss of internet connectivity.
        //TODO: Try to make the data collection and storing cleaner/more efficient
        @Override
        protected ArrayList<article> doInBackground(URL... urls) {
            ArrayList<article> arrays[] = new ArrayList[NUM_GENRES];
            for (int i = 0; i < NUM_GENRES; i++) { // loop through all feeds
                try {
                    if (is_Network_Connected()) {
                        //Setup for connection
                        Rss_Handler new_Parser = new Rss_Handler(getApplicationContext());
                        InputStream input = urls[i].openStream();
                        new_Parser.parse(input);

                        arrays[i] = new_Parser.getArticleList(); //store the data.
                        publishProgress(new Integer[]{i});
                    } else {
                        return null;
                    }

                } catch (IOException bad) {
                    bad.printStackTrace();
                    break;
                } catch (SAXException bad) {
                    bad.printStackTrace();
                    break;
                } catch (Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        return null;
                    }

                }

            }
            publishProgress(new Integer[]{NUM_GENRES+1});

            return clean_Article_Bodies(combine_Arrays(arrays)); // Combines the array list
        }

        /*Check to see if connected to a network*/
        private boolean is_Network_Connected() {
            final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
        }


        /* Applies justification to all text in the article's body*/
        private ArrayList<article> clean_Article_Bodies(ArrayList<article> all_Articles) {
            for(int j = 0; j < all_Articles.size(); j++)
            {
                //justify text
                String body = all_Articles.get(j).get_Body();
                body = "<body style=\"text-align:justify;\"> " + body + " </body>";
                all_Articles.get(j).set_Article_Body(body);
            }
            return all_Articles;
        }

        /*Surrounds image urls in appropriate html */
        private String format_Image(String image_URL) {
            if (image_URL == null) {
                return null;
            }
            return  "<body style=\"margin: 0; padding: 0\">" +
                    "<img src=" + image_URL + " width=\"100%\" />" +
                    "</body>";
        }



        /*Combine an array of ArrayLists of articles into one ArrayList of articles. */
        private ArrayList<article> combine_Arrays (ArrayList<article>[] arrays) {
            boolean accept = true; //Only accept articles that aren't in the list already
            ArrayList<article> all_articles = new ArrayList<article>();

            for(int i = 0; i < NUM_GENRES; i++) { //loop through genres
                for(int j = 0; j < arrays[i].size(); j++) { //loop through articles in this genre
                    for(int k = 0; k < all_articles.size(); k++) { //loop through stored articles
                        //Don't accept articles we already have
                        if (all_articles.get(k).get_Title().equals(arrays[i].get(j).get_Title()))
                        { accept = false;}
                    }
                    //Mark top news articles as top news
                    if (i == 0) { arrays[i].get(j).set_Article_Is_Top(true); }
                    else { arrays[i].get(j).set_Article_Is_Top(false); }

                    //format URLs of images
                    arrays[i].get(j).set_image_URL(format_Image(arrays[i].get(j).get_image_URL()));
                    arrays[i].get(j).set_Thumb_URL(format_Image(arrays[i].get(j).get_Thumb_URL()));

                    //Add articles we're accepting to the array
                    if (accept) { all_articles.add(arrays[i].get(j)); }
                    else { accept = true; }
                }
            }
            return all_articles;
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
        protected void onPostExecute(ArrayList<article> result) {
            super.onPostExecute(result);

            if(result==null) {
                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(getApplicationContext(), "Internet Connection Failed.", Toast.LENGTH_SHORT).show();
                        update_Progress(getResources().getString(R.string.connection_fail));
                        hide_Progress();
                    }
                });
                return;
            }

            app_Articles = result;
            Intent article_List = new Intent(MainActivity.this, ArticleListActivity.class);
            article_List.putExtra("this_Genre", "Top News");
            article_List.putParcelableArrayListExtra("my_Articles", app_Articles);
            article_List.putExtra("first_Instance", true);
            startActivity(article_List);

            // close this activity
            finish();
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
                my_Progress_Bar.setVisibility(View.VISIBLE);
                update_Progress(getResources().getString(R.string.load_text));
                new FetchArticlesTask().execute(this.urls);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Initialize the progress bar & text */
    public void init_Progress_Bar(View view) {
        my_Progress_Bar = (ProgressBar) view.findViewById(R.id.news_Load_Bar);
        my_Progress_Text = (TextView) view.findViewById(R.id.progress_Text);
    }

    /*Hide progress bar */
    public void hide_Progress() {
        my_Progress_Bar.setVisibility(View.INVISIBLE);
    }

    /*Update text in progress textview */
    public void update_Progress(String update){
        my_Progress_Text.setText(update);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            init_Progress_Bar(rootView); //Initialize progress bar
            new FetchArticlesTask().execute(urls); // fetch data

            return rootView;
        }
    }

}
