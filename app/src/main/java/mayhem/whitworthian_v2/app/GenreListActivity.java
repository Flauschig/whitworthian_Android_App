package mayhem.whitworthian_v2.app;

import android.app.SearchManager;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/** This is the GenreListActivity.
 *  Includes the following functionality:
 *  -Receives articles under-the-hood from article list
 *  -Displays genres to the user
 *  -Responds to user input by opening an article list page or searching.
 *
 *  Contains the following class variables:
 *      NUM_GENRES:         The number of genres to display --> HARDCODED CONSTANT
 *      genres:             A string array of all possible genres
 *      genre_List:         A ListView item, where the genres are listed.
 *      app_Articles        ArrayList containing all article data
 */
public class GenreListActivity extends ActionBarActivity {
    final int NUM_GENRES = 5;
    private String[] genres = new String[NUM_GENRES];
    private ListView genre_List;
    private ArrayList<Article> app_Articles;


    /** OVERRIDEN ACTIVITY FUNCTIONS
     * onCreate()
     * onCreateOptionsMenu()
     * onOptionsItemSelected()
     * startActivity()
     * handleIntent()
     * onActivityResult()
     */

    /* Creates the activity, sets the title string, and gets the data for all articles
     * If there was a search invoked, handles the search */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        //Receives the application ArrayList of articles
        get_Article_Data();

        //If there was a search, it's handled here.  Search happens weirdly.  In order to ensure
        // search can return information (i.e. which articles were viewed), we first have to create
        // a new instance of GenreListActivity with the search intent, which then calls a search
        // activity for results.
        handle_Intent(getIntent());

        //The genre list view is always titled "The Whitworthian"
        try{
            setTitle(getResources().getString(R.string.app_name));
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0020"),
                    Toast.LENGTH_LONG).show();
        }

    }

    /* Set up the search button*/
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.genre_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*Handles Search button clicks */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Stuff gets weird here.  When search is called, we actually briefly open another instance
      of GenreListActivity, so we have to be sure to preserve app_Articles upon doing so.
      The reason for these shenanigans is to later use startActivityForResult() in handle_Intent()
      so that the search can return which articles are viewed in the search view.
     */
    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            try{
                intent.putParcelableArrayListExtra("my_Articles", app_Articles);
                super.startActivity(intent);
                finish();
                return;
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0026"),
                        Toast.LENGTH_LONG).show();
            }
        }
        super.startActivity(intent);
    }

    /* Handles returning data from article list or from search view */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //RESULT CODE 1 = ARTICLE LIST ACTIVITY
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle goodies = data.getExtras();
                try{
                    this.app_Articles = goodies.getParcelableArrayList("my_Articles");
                }
                catch(Exception bad){Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0024"),
                        Toast.LENGTH_LONG).show();
                }
            }
        }
        //RESULT CODE 2 = SEARCH RESULTS ACTIVITY
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Bundle goodies = data.getExtras();
                try{
                    this.app_Articles = goodies.getParcelableArrayList("my_Articles");
                }
                catch(Exception bad){
                    Toast.makeText(getApplicationContext(),
                            String.format("A non-fatal error occurred! \nCode: 6d617968656d-0025"),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /** HANDLE USER INPUT
     * handle_Intent() function: Starts a SearchResultsActivity when search is requested.
     * genre_Item_Click() function: Opens up an ArticleListActivity of appropriate genre.
     */

    /*When we re-open the genre list with a search activity, it hits this code, and then
     * opens search FOR RESULT so that we can get back which articles the user clicks on. */
    private void handle_Intent(Intent intent) {
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            try{
            String query = intent.getStringExtra(SearchManager.QUERY);
            // manually launch the real search activity
            final Intent searchIntent = new Intent(getApplicationContext(),
                    SearchResultsActivity.class);
            // add query to the Intent Extras
            searchIntent.putExtra(SearchManager.QUERY, query);
            searchIntent.putParcelableArrayListExtra("my_Articles", app_Articles);
            startActivityForResult(searchIntent, 2);
            } catch(Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0019"),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /* Controls the behavior of the application when a genre is clicked.  Takes the name of the
     * genre, and opens a new article list of that genre */
    public void genre_Item_Click(String new_Genre) {
        try {
            Intent article_List = new Intent(this, ArticleListActivity.class);
            article_List.putExtra("this_Genre", new_Genre);
            article_List.putParcelableArrayListExtra("my_Articles", app_Articles);
            article_List.putExtra("first_Instance", false);
            startActivityForResult(article_List, 1);
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0017"),
                    Toast.LENGTH_LONG).show();
        }
    }


    /** FILL LOCAL DATA
     * fill_Genre_String() function: fills genres from strings.xml
     * get_Genre_List() function: finds ListView element
     * set_Genre_List_Adapter() function: Adapts genres to appropriate ListView
     * get_Article_Data() function: retrieves article data from intent
     */

    /* Fills the genres array from data in strings.xml */
    protected void fill_Genre_String() {
        try{
            genres = getResources().getStringArray(R.array.news_Genres);
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0023"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* Gets the ID of the ListView which to displays the genres */
    protected ListView get_Genre_List(View V) {
        if (genre_List == null) {
            try {
                genre_List = (ListView) V.findViewById(R.id.genre_List_View);
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0022"),
                        Toast.LENGTH_LONG).show();
            }
        }
        return genre_List;
    }

    //Sets the list adapter for the genre list.  Displays all text in genre string
    protected void set_Genre_List_Adapter(View V) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genres);
            get_Genre_List(V).setAdapter(adapter);
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0021"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* Locally unpacks and tracks the article data */
    protected void get_Article_Data() {
        Bundle goodies = getIntent().getExtras();
        try{
            this.app_Articles = goodies.getParcelableArrayList("my_Articles");
        }
        catch(Exception bad){
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0016"),
                    Toast.LENGTH_LONG).show();
            this.app_Articles = new ArrayList<Article>();
        }
    }


    /**PLACEHOLDERFRAGMENT: contains all elements viewed and interacted with by user.
     */
    public class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_genre_list,
                    container, false);

            //Fills the genre view fragment
            fill_Genre_String();
            set_Genre_List_Adapter(rootView);

            //Sets up the genre list to wait for user input & respond to it.
            //id & position refer to the number on the list selected
            genre_List.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected_Genre = genres[position];
                    genre_Item_Click(selected_Genre);
                }
            });

            return rootView;
        }
    }


}
