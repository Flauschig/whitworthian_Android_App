package mayhem.whitworthian_v2.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends Activity {

    private ArrayList<article> app_Articles;
    private ArrayList<article> search_Articles;
    private article_Selection[] article_Data;
    private ListView search_List;
    int indices[];
    private article_Selection_Adapter adapter;
    private TextView no_Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        // get the action bar
        ActionBar actionBar = getActionBar();

        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        search_Articles = new ArrayList<article>();
        search_List = (ListView) findViewById(R.id.search_list);
        no_Search = (TextView) findViewById(R.id.no_search_info);

        handleIntent(getIntent());


        //Sets up an event handler which waits for an article to be clicked on,
        // then loads the appropriate view
        search_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                load_Article_View(position);
                refresh_View();
            }
        });
    }

    /* On Click, loads the appropriate article */
    public void load_Article_View(int position) {
        app_Articles.get(indices[position]).set_Viewed(true);
        article_Data[position].set_Viewed(true);
        Intent article_View = new Intent(this, ArticleViewActivity.class);
        article_View.putExtra("my_Genre", search_Articles.get(position).get_Genre());
        article_View.putExtra("my_Article", search_Articles.get(position));
        startActivityForResult(article_View, 1);
    }

    /*Handles user input of top action bar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // when the back button is clicked, return to the genre list
                Intent data = new Intent();
                data.putParcelableArrayListExtra("my_Articles", app_Articles);
                setResult(RESULT_OK, data);
                finish();
                return true;
            case mayhem.whitworthian_v2.app.R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
    }
    }


    /* Checks to see if an article fits search criteria.  Returns true if it does. */
    private boolean fits_Search(article this_Article, String query) {
        //Check title
        if (this_Article.get_Title().toLowerCase().contains(query.toLowerCase())) {
            return true;
        }
        return false;
    }

    /* Fills article_Selection array and prepares it for adaption */
    private void fill_Data() {
        article this_Article = new article();
        for(int i = 0; i < search_Articles.size(); i++) {
            this_Article = search_Articles.get(i);
            article_Data[i] = new article_Selection();
            article_Data[i].set_Icon(this_Article.get_image_ID());
            article_Data[i].set_Viewed(this_Article.get_Viewed());
            article_Data[i].set_Desc(this_Article.get_Desc());
            article_Data[i].set_ID(this_Article.get_Article_ID());
            article_Data[i].set_Title(this_Article.get_Title());
        }

    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        app_Articles = intent.getParcelableArrayListExtra("my_Articles");

        setTitle("Search for: " + query);

        for (int i = 0; i < app_Articles.size(); i++) {
            if (fits_Search(app_Articles.get(i), query)) {
                search_Articles.add(app_Articles.get(i));
            }
        }

        //TODO: DISPLAY NO RESULT MESSAGE
        if (search_Articles.size() == 0) {
            return;
        }
        else {
            no_Search.setVisibility(View.INVISIBLE);
        }

        article_Data = new article_Selection[search_Articles.size()];
        indices = new int[search_Articles.size()];

        int count = 0;
        for (int i = 0; i < app_Articles.size(); i++) {
            if (search_Articles.get(count) == app_Articles.get(i)) {
                indices[count++] = i;
                if (count == search_Articles.size()) {
                    break;
                }
            }
        }

        fill_Data();
        adapter = new article_Selection_Adapter(this, article_Data);
        search_List.setAdapter(adapter);

    }

    private void refresh_View() {
        adapter.notifyDataSetChanged();
    }

}
