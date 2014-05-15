package mayhem.whitworthian_v2.app;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/** This is the ArticleListActivity.
 *  Includes the following functionality:
 *  -Receives articles under-the-hood from splash page and/or genre list
 *  -Displays articles to the user
 *  -Responds to user input by either opening an article, returning to genre page, or searching.
 *
 *  Contains the following class variables:
 *      num_Articles    -the number of articles to display
 *      indices         -An integer array containing the indices of articles to display
 *      article_List    -A ListView object which corresponds to the ListView in the activity
 *      my_Genre        -The genre of the articles displayed
 *      my_Image        -The image corresponding to that genre.
 *      my_Instance     -A boolean determining whether or not this is the root top news list.
 *      app_Articles    -ArrayList containing all article data
 *      article_Data    -Adapted relevant article data in a format that the app can handle.
 *      adapter         -An adapter which puts article_Data into the view.
 */
public class ArticleListActivity extends ActionBarActivity {
    private int num_Articles;
    private int[] indices;
    private ListView article_List;
    private String my_Genre;
    private int my_Image;
    private boolean my_Instance;
    private ArrayList<Article> app_Articles;
    private ArticleSelection article_Data[];
    private ArticleSelectionAdapter adapter;

    /** OVERRIDEN ACTIVITY FUNCTIONS
     * onCreate()
     * onOptionsItemSelected()
     */

    /* Creates the layout, sets up the appearance of the action bar, and creates a Placeholder
     * Fragment, which displays relevant articles as a pretty list */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mayhem.whitworthian_v2.app.R.layout.activity_article_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //Sets up the action bar and the genre of the article list.  Makes article data usable
        Bundle goodies = getIntent().getExtras();
        setup_Action_Bar_Appearance(goodies);
        get_Article_Data(goodies);
    }

    /* Handles user input of back button */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button pressed
                // Go back to the genre list activity on back button click.
                if(my_Instance){
                    // If this is the root Top News view, then create the genre list
                    Intent myIntent = new Intent(this, GenreListActivity.class);
                    myIntent.putParcelableArrayListExtra("my_Articles", app_Articles);
                    try {
                        startActivity(myIntent);
                    } catch (Exception bad) {
                        Toast.makeText(getApplicationContext(),
                             String.format("A non-fatal error occurred! \nCode: 6d617968656d-0005"),
                             Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    // If this is any other list view, then return back to the genre list with
                    // an OK message.
                    try{
                        Intent data = new Intent();
                        data.putParcelableArrayListExtra("my_Articles", app_Articles);
                        setResult(RESULT_OK, data);
                        finish();
                    } catch (Exception bad) {
                        Toast.makeText(getApplicationContext(),
                                String.format("A non-fatal error occurred! " +
                                        "\nCode: 6d617968656d-0046"),
                                Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** VARIABLE/VIEW INITIALIZATION FUNCTIONS
     * get_Article_Data() function for filling local app_Articles and my_Instance variables
     * setup_Acton_Bar_Appearance() function for filling genre variable & making action bar
     *          pretty and page-specific.  Also enables back button.
     * fill_Article_Local_Data() function makes data from app_Articles more easily usable by
     *          sorting out only those articles of the genre of the current list.
     * set_List_Info() function fills in data for an ArticleSelection from the specified article
     *          in app_Articles
     * get_Article_List() function returns a ListView object which references the article list
     * set_Article_List_Adapter() function prepares data to be put into the list
     * adapt_List() function actually adapts data and displays it in the list
     */

    /* Retrieves article data from Intent Extras.  Also determines retrieves whether or not
     * this is the root Top News instance */
    protected void get_Article_Data(Bundle goodies) {
        try{
            this.app_Articles = goodies.getParcelableArrayList("my_Articles");
        }
        catch(Exception bad){
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0003"),
                    Toast.LENGTH_LONG).show();
            this.app_Articles = new ArrayList<Article>();
        }
        try{
            this.my_Instance = goodies.getBoolean("first_Instance");
        }
        catch(Exception bad){
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0004"),
                    Toast.LENGTH_LONG).show();
            this.my_Instance = false;
        }
    }

    /* Fills in class genre variable and sets up the action bar's appearance*/
    private void setup_Action_Bar_Appearance(Bundle goodies){

        try{
            my_Genre = goodies.getString("this_Genre");
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0001"),
                    Toast.LENGTH_LONG).show();
            my_Genre = getResources().getString(R.string.news);
        }

        try{
            //Set up action bar Title
            if (my_Genre.equals(getResources().getString(R.string.top)))
                setTitle(R.string.app_name);
            else
                setTitle(my_Genre);

            //Set up action bar image
            if (my_Genre.equals(getResources().getString(R.string.news))) {
                my_Image = R.drawable.news_box;
                getActionBar().setIcon(my_Image);
            } else if (my_Genre.equals(getResources().getString(R.string.sports))) {
                my_Image = R.drawable.sports_box;
                getActionBar().setIcon(my_Image);
            } else if (my_Genre.equals(getResources().getString(R.string.arts_culture))) {
                my_Image = R.drawable.ac_box;
                getActionBar().setIcon(my_Image);
            } else if (my_Genre.equals(getResources().getString(R.string.opinions))) {
                my_Image = R.drawable.opinions_box;
                getActionBar().setIcon(my_Image);
            } else {
                my_Image = R.drawable.ic_launcher;
                getActionBar().setIcon(my_Image);
            }
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0002"),
                    Toast.LENGTH_LONG).show();
            my_Genre = getResources().getString(R.string.top);
        }

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /* Makes data in app_Articles usable by picking out articles specific to this genre.
     * Note that top news articles are tracked with the boolean is_Top() method, whereas
     * all other genres are tracked by their genre strings.
     */
    protected void fill_Article_Local_Data() {
        try{
        //Figure out how many articles there are
        if (!(my_Genre.equals(getResources().getString(R.string.top)))) { //Top News
            for (int i = 0; i < app_Articles.size(); i++) {
                if (app_Articles.get(i).get_Genre().equals(my_Genre))
                { num_Articles++; }
            }
        }
        else { //Other Genres
            for (int i = 0; i < app_Articles.size(); i++) {
                if (app_Articles.get(i).is_Top())
                { num_Articles++; }
            }
        }

        //Initialize arrays to proper article number
        article_Data = new ArticleSelection[num_Articles];
        indices = new int[num_Articles];

        //Go through all articles and pick out the ones that we want to look at.
        //"counter" variable keeps track of how many articles of this genre have been recorded.
        int counter = 0;
        if (!(my_Genre.equals(getResources().getString(R.string.top)))){  //All except Top News
            for (int i = 0; i < app_Articles.size(); i++) {
                if (app_Articles.get(i).get_Genre().equals(my_Genre))
                {   counter = set_List_Info(counter, i); }
            }
        }
        else {
            for (int i = 0; i < app_Articles.size(); i++) { //Top News
                if (app_Articles.get(i).is_Top())
                {   counter = set_List_Info(counter, i); }
            }
        }
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0006"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* Used to fill article_Data and get the ID corresponding to list position. */
    private int set_List_Info(int counter, int id) {
        article_Data[counter] = new ArticleSelection();

        article_Data[counter].set_Title(
                app_Articles.get(id).get_Title());
        article_Data[counter].set_Desc(
                app_Articles.get(id).get_Desc());
        article_Data[counter].set_ID(
                app_Articles.get(id).get_Article_ID());
        article_Data[counter].set_Viewed(
                app_Articles.get(id).get_Viewed());
        article_Data[counter].set_Icon(
                app_Articles.get(id).get_image_ID());

        if (app_Articles.get(id).get_Has_Thumb()) {
            article_Data[counter].set_icon_URL(
                    app_Articles.get(id).get_Thumb_URL());
        }

        indices[counter] = id;
        return ++counter;
    }

    /* Returns this activity's article list ID. */
    protected ListView get_Article_List(View V) {
        if (article_List == null) {
            article_List = (ListView) V.findViewById(mayhem.whitworthian_v2.app.R.id.article_List_View);
        }
        return article_List;
    }

    /* Sets this activity's article list adapter to display Title, image, and description. */
    protected void set_Article_List_Adapter(View V) {
        //Then adapt the list to the proper format with the proper data
        get_Article_List(V);
        if (article_Data == null) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0007"),
                    Toast.LENGTH_LONG).show();
            //TODO: Set "no data" view.  Give user option to refresh.
            return;
        }

        adapt_List(article_Data);
    }

    /* Adapts the article List view to display the appropriate data */
    private void adapt_List(ArticleSelection[] article_Data) {
        adapter = new ArticleSelectionAdapter(this, article_Data);
        article_List.setAdapter(adapter);
    }


    /** USER INPUT RESPONSE
     * load_Article_View() function responds to a user's click on a list item and loads an article.
     */

    /* On Click, loads the appropriate article, saving the viewed status of the article to file
     * if appropriate */
    public void load_Article_View(int position) {
        // If the article hasn't been viewed before, then we need to write its ID to file
        if(!article_Data[position].get_Viewed()){
            try{
                //Open up the file, and check if it exists.  If not, create it.
               File file = new File(getFilesDir()+File.separator+
                        getResources().getString(R.string.article_file));
                if (!file.exists()) {
                    file.createNewFile();
                }

                //Write out this ID to the file with a delimiting "@" at the end.
                String write_String = String.valueOf(app_Articles.get(indices[position]).
                        get_Article_ID()) + "@";
                FileWriter file_Writer = new FileWriter(getFilesDir()+File.separator+file.getName(), true);
                BufferedWriter buffer_Writer = new BufferedWriter(file_Writer);
                buffer_Writer.write(write_String);
                buffer_Writer.close();
            }
            catch (Exception bad){
                Toast.makeText(getApplicationContext(),
                        String.format("A non-fatal error occurred! \nCode: 6d617968656d-0047"),
                        Toast.LENGTH_LONG).show();
            }
        }

        //Create an intent which will open up the correct ArticleViewActivity
        try {
            article_Data[position].set_Viewed(true);
            app_Articles.get(indices[position]).set_Viewed(true);
            Intent article_View = new Intent(this, ArticleViewActivity.class);
            article_View.putExtra("my_Genre", my_Genre);
            article_View.putExtra("my_Article", app_Articles.get(indices[position]));
            startActivityForResult(article_View, 1);
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0008"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /** PLACEHOLDERFRAGMENT:
     *  This is the fragment which the user sees when ArticleListActivity is active.
     *
     *  Here the program defines how to respond to user clicks and here the program refreshes the
     *  list after a click in order to display the "viewed" status of articles.
     */
    public class PlaceholderFragment extends Fragment {
        private View root_View;

        public PlaceholderFragment() {
        }

        /* Initializes Fragment and fills the fragment's layout elements with the proper data.
         * Initializes app response to user clicks on articles. */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            root_View = inflater.inflate(mayhem.whitworthian_v2.app.R.layout.fragment_article_list,
                    container, false);

            //Fills the article list with the appropriate articles
            fill_Article_Local_Data();
            set_Article_List_Adapter(root_View);

            //Sets up an event handler which waits for an article to be clicked on,
            // then loads the appropriate view
            article_List.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    load_Article_View(position);
                    refresh_View();

                }
            });
            return root_View;
        }

        /* Forces the app to redraw the fragment, used when articles are clicked. */
        private void refresh_View() {
            adapter.notifyDataSetChanged();
        }
    }

}
