package mayhem.whitworthian_v2.app;

import android.support.v7.app.ActionBarActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/** This is the SearchResultsActivity.
 *  Includes the following functionality:
 *  -Receives articles under-the-hood from genre list
 *  -Sees if any articles titles or categories match a search query string
 *  -Displays articles which fit search criteria in a list.
 *  -Responds to user input by opening an article view page.
 *
 *  Contains the following class variables:
 *      app_Articles        ArrayList containing all article data
 *      search_Articles     ArrayList containing all data for articles which fit search criteria
 *      article_Data        ArticleSelection array which is used to display articles in the list
 *      adapter             ArticleSelectionAdapter which adapts article_Data to the view
 *      indices             Integer array containing the index of search articles in app_Articles
 *      search_List         The ListView where search results appear
 *      no_Search           A TextView which informs the user if there are no search results
 */
public class SearchResultsActivity extends ActionBarActivity {
    private ArrayList<Article> app_Articles;
    private ArrayList<Article> search_Articles;
    private ArticleSelection[] article_Data;
    private ArticleSelectionAdapter adapter;
    int indices[];
    private ListView search_List;
    private TextView no_Search;

    /** OVERRIDEN ACTIVITY FUNCTIONS
     * onCreate()
     * onOptionsItemSelected()
     */

    /* Sets up view and enables back button */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        search_Articles = new ArrayList<Article>();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(getIntent())).commit();
        }

        // Enable Back navigation on Action Bar icon
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0027"),
                    Toast.LENGTH_LONG).show();
        }


    }

    /*Handles back button presses */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try{
                    // when the back button is clicked, return to the genre list
                    Intent data = new Intent();
                    data.putParcelableArrayListExtra("my_Articles", app_Articles);
                    setResult(RESULT_OK, data);
                    finish();
                } catch(Exception bad) {
                    Toast.makeText(getApplicationContext(),
                            String.format("A non-fatal error occurred! \nCode: 6d617968656d-0035"),
                            Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }
                return true;
            case mayhem.whitworthian_v2.app.R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** SEARCH HANDLING METHOD
     * handle_Intent() function: overarching search handling function
     * fits_Search() function: Checks to see if the article fits the query
     * fill_Data() function: Fill the data which fits the search results into variables for
     *                        list adaptation.
     * no_Results() function: If there are no search results, tailors display accordingly.
     */

    /* Handles the Intent of the data by searching, tailoring the data to ListView, and
       displaying the data in the ListView */
    private void handle_Intent(Intent intent) {
        String query = null;

        //get the search query from intent
        try{
            query = intent.getStringExtra(SearchManager.QUERY);
            app_Articles = intent.getParcelableArrayListExtra("my_Articles");
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0029"),
                    Toast.LENGTH_LONG).show();
            no_Results(true);
            return;
        }

        //Fill action bar title
        setTitle("Search for: " + query);

        //Sort out the articles that fit the search
        for (int i = 0; i < app_Articles.size(); i++) {
            if (fits_Search(app_Articles.get(i), query)) {
                search_Articles.add(app_Articles.get(i));
            }
        }

        //Sets display according to number of results
        if (no_Results(search_Articles.size() == 0)) {
            return;
        }

        //Tailors data for list formatting.
        try {
            article_Data = new ArticleSelection[search_Articles.size()];
            indices = new int[search_Articles.size()];

            int count = 0;
            for (int i = 0; i < app_Articles.size(); i++) {
                if (search_Articles.get(count) == app_Articles.get(i)) {
                    indices[count] = i;
                    fill_Data(count++);
                    if (count == search_Articles.size()) {
                        break;
                    }
                }
            }
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0032"),
                    Toast.LENGTH_LONG).show();
        }

        //Adapts formatted data to the list.
        try {
            adapter = new ArticleSelectionAdapter(this, article_Data);
            search_List.setAdapter(adapter);
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0033"),
                    Toast.LENGTH_LONG).show();
        }

    }

    /* Checks to see if an article fits search criteria.  Returns true if it does. */
    private boolean fits_Search(Article this_Article, String query) {
        //Check title
        if (this_Article.get_Title().toLowerCase().contains(query.toLowerCase())) {
            return true;
        }
        for (int i = 0; i < this_Article.get_Categories().length; i++) {
            if (this_Article.get_Categories()[i].toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /* Fills ArticleSelection array and prepares it for adaption */
    private void fill_Data(int i) {
        Article this_Article = search_Articles.get(i);
        article_Data[i] = new ArticleSelection();
        article_Data[i].set_Viewed(this_Article.get_Viewed());
        article_Data[i].set_Desc(this_Article.get_Desc());
        article_Data[i].set_ID(this_Article.get_Article_ID());
        article_Data[i].set_Title(this_Article.get_Title());
        article_Data[i].set_Icon(this_Article.get_image_ID());

        if (app_Articles.get(i).get_Has_Thumb()) {
            article_Data[i].set_icon_URL(this_Article.get_Thumb_URL());
        }


    }

    /* If there are no results, this sets the screen to display the no results image/text.  If there
       are results, it ensures that these elements are not displayed. */
    private boolean no_Results(boolean none) {
        if (none) {
            try{
                getActionBar().setIcon(R.drawable.bad_search);
                return none;
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0030"),
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            try{
                getActionBar().setIcon(R.drawable.search_button);
                no_Search.setVisibility(View.INVISIBLE);
                return none;
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0031"),
                        Toast.LENGTH_LONG).show();
            }
        }
        return none;
    }


    /** HANDLE USER INPUT
     * load_Article_View() function: loads ArticleViewActivity of selected article
     * refresh_View() function: refreshes the local list when an item is clicked to show
     *                            it as viewed.
     */

    /* On Click, loads the appropriate article & saves view status to file if necessary*/
    public void load_Article_View(int position) {
        try{
            if(!article_Data[position].get_Viewed()){
                // If it hasn't been viewed, save this file to the data file.
                try{
                    //Make a file object for the appropriate file
                    File file = new File(getFilesDir()+File.separator+
                            getResources().getString(R.string.article_file));

                    //Make the file if it doesn't exist
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    //Write out this ID, delimted with "@"
                    String write_String = String.valueOf(app_Articles.get(indices[position]).
                            get_Article_ID()) + "@";
                    FileWriter file_Writer = new FileWriter(getFilesDir()+File.separator+file.getName(), true);
                    BufferedWriter buffer_Writer = new BufferedWriter(file_Writer);
                    buffer_Writer.write(write_String);
                    buffer_Writer.close();
                }
                catch (IOException e){
                    Toast.makeText(getApplicationContext(), String.format("Error! %s", e.toString()), Toast.LENGTH_LONG).show();
                }
            }

            //Open up an ArticleviewActivity
            app_Articles.get(indices[position]).set_Viewed(true);
            article_Data[position].set_Viewed(true);
            Intent article_View = new Intent(this, ArticleViewActivity.class);
            article_View.putExtra("my_Genre", search_Articles.get(position).get_Genre());
            article_View.putExtra("my_Article", search_Articles.get(position));
            startActivityForResult(article_View, 1);
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0036"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* Refreshes the ListView upon click */
    private void refresh_View() {
        try{
            adapter.notifyDataSetChanged();
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0034"),
                    Toast.LENGTH_LONG).show();}
    }


    /** PLACEHOLDERFRAGMENT.
     *  This is the actual view on which the application focuses while a SearchResultsActivity
     *  is active.     */
    public class PlaceholderFragment extends Fragment {
        private Intent thisIntent;

        //Get the intent from the outside class
        public PlaceholderFragment(Intent thisIntent) {
            this.thisIntent = thisIntent;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search_results,
                    container, false);


            //Set up variables of outer class, ensure that all views are looking at the right view.
            try{
                search_List = (ListView) rootView.findViewById(R.id.search_list);
                no_Search = (TextView) rootView.findViewById(R.id.no_search_info);
                if (search_List == null || no_Search == null) {
                    throw new Exception();
                }
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0028"),
                        Toast.LENGTH_LONG).show();
            }


            //Handles the intent, searches through data, sets up list
            handle_Intent(thisIntent);


            //Sets up an event handler which waits for an article to be clicked on,
            // then loads the appropriate view
            search_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    load_Article_View(position);
                    refresh_View();
                }
            });
            return rootView;
        }
    }
}
