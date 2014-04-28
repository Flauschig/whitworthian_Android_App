package mayhem.whitworthian_v2.app;

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
    private ArrayList<article> app_Articles;


    /* Creates the activity, sets the title string, and gets the data for all articles */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        //The genre list view is always titled "The Whitworthian"
        setTitle(getResources().getString(R.string.app_name));
        get_Article_Data();
    }

    /* After OnCreate, OnCreateOptionsMenu is called under-the-hood Here the search view
    * is initialized, and click-handling of the genre list is set up.*/
    //TODO: Make search work
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.genre_list, menu);
        /* ATTEMPS AT MAKING SEARCH WORK
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);

        */
        return true;
    }

    /*Handles all input for the top action bar. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) { //Settings
            return true;
        }
        else if (id == R.id.action_search) { //Search
            //TODO: Make Search Work
            onSearchRequested();
        }
        return super.onOptionsItemSelected(item);
    }

    /* Fills  the genres array from data in strings.xml */
    protected void fill_Genre_String() {
        genres = getResources().getStringArray(R.array.news_Genres);
    }

    /* Gets the ID of the ListView which to displays the genres */
    protected ListView get_Genre_List(View V) {
        if (genre_List == null) {
            genre_List = (ListView) V.findViewById(R.id.genre_List_View);
        }
        return genre_List;
    }

    //Sets the list adapter for the genre list.  Displays all text in genre string
    protected void set_Genre_List_Adapter(View V) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genres);
        get_Genre_List(V).setAdapter(adapter);
    }

    /* Controls the behavior of the application when a genre is clicked.  Takes the name of the
     * genre, and opens a new article list of that genre */
    public void genre_Item_Click(String new_Genre) {
        Intent article_List = new Intent(this, ArticleListActivity.class);
        article_List.putExtra("this_Genre", new_Genre);
        article_List.putParcelableArrayListExtra("my_Articles", app_Articles);
        article_List.putExtra("first_Instance", false);
        startActivityForResult(article_List, 1);
    }

    /* Locally unpacks and tracks the article data */
    protected void get_Article_Data() {
        Bundle goodies = getIntent().getExtras();
        try{
            this.app_Articles = goodies.getParcelableArrayList("my_Articles");
        }
        catch(NullPointerException bad){
            this.app_Articles = new ArrayList<article>();
        }
    }

    /* Handles returning data from article List */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle goodies = data.getExtras();
                try{
                    this.app_Articles = goodies.getParcelableArrayList("my_Articles");
                }
                catch(NullPointerException bad){
                    //TODO: Something better here.
                    this.app_Articles = new ArrayList<article>();
                }
            }
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
            View rootView = inflater.inflate(R.layout.fragment_genre_list,
                    container, false);

            //Fills the genre view fragment
            fill_Genre_String();
            get_Genre_List(rootView);
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
