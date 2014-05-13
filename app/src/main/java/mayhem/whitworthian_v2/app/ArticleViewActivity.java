package mayhem.whitworthian_v2.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** This is the ArticleViewActivity.
 *  Includes the following functionality:
 *  -Receives a single article under-the-hood
 *  -Displays article's image, title, and body to the user in a scrollable format
 *  -Responds to user input by returning to article list page or by searching.
 *
 *  Contains the following class variables:
 *  my_Genre:           The genre of the current article
 *  my_Genre_Image:     The image associated with this article, for action bar customization
 *  my_Article:         Article to be displayed in this view
 *  my_Image_ID:        The ID of the article's genre's banner
 *  my_Image_URL:       A string containing the article's image's URL, if available.
 *  my_Title:           A string containing the title of the article
 *  my_Body:            A spanned string containing the body of the article, likely in HTML
 */
public class ArticleViewActivity extends ActionBarActivity {
    private String my_Genre;
    private int my_Genre_Image;
    private Article my_Article;
    private int my_Image_ID;
    private String my_Image_URL;
    private String my_Title;
    private String my_Body;
    private View fragment_View;
    //private Spanned my_Body;

    /* Create activity and fragment, Sets up local data */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        //Set up local variables
        Bundle goodies = getIntent().getExtras();
        setup_ActionBar_Appearance(goodies);
        get_Article_Data(goodies);

    }


    /*After OnCreate, OnCreateOptionsMenu is called under-the-hood Here the search view
    * is initialized. */
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article_view, menu);
        return true;
    }

    /*Handles user input of top action bar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.action_font_size:
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                FontSizeDialogFragment dialog = new FontSizeDialogFragment();
                dialog.show(fm, "my dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Sets the article banner Image using my_Image_ID */
    private void set_Banner_Image(View V) {
        ImageView image_Box = null;
        try {
            image_Box = (ImageView) V.findViewById(R.id.article_banner);
            if (my_Image_ID == R.drawable.news_box) {
                image_Box.setImageResource(R.drawable.news_bar);
            } else if (my_Image_ID == R.drawable.opinions_box) {
                image_Box.setImageResource(R.drawable.opinions_bar);
            } else if (my_Image_ID == R.drawable.ac_box) {
                image_Box.setImageResource(R.drawable.ac_bar);
            } else if (my_Image_ID == R.drawable.sports_box) {
                image_Box.setImageResource(R.drawable.sports_bar);
            }
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("Failed to Load Banner Image! \nCode: 6d617968656d-0014"),
                    Toast.LENGTH_LONG).show();
            if (image_Box != null) {
                image_Box.setVisibility(View.GONE);
            }
        }


    }

    /* Picks out the proper article from app_Articles and fills in all appropriate data locally */
    protected void get_Article_Data(Bundle goodies) {
        //Pull out important information
        try{
            this.my_Article = goodies.getParcelable("my_Article");
        }
        catch(Exception bad){
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occured! \nCode: 6d617968656d-0015"),
                    Toast.LENGTH_LONG).show();
            my_Article = new Article();
            return;
        }
        //my_Body = Html.fromHtml(my_Article.get_Body());
        my_Body = my_Article.get_Body();
        my_Title = my_Article.get_Title();
        my_Image_URL = my_Article.get_image_URL();
        my_Image_ID = my_Article.get_image_ID();
    }

    /* Puts article genre and genre's icon on the action bar. */
    private void setup_ActionBar_Appearance(Bundle goodies){
        //Try to get the genre, if all else fails, set it as top news
        try{
            my_Genre = goodies.getString("my_Genre");
        }catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0009"),
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
                my_Genre_Image = R.drawable.news_box;
                getActionBar().setIcon(my_Genre_Image);
            } else if (my_Genre.equals(getResources().getString(R.string.sports))) {
                my_Genre_Image = R.drawable.sports_box;
                getActionBar().setIcon(my_Genre_Image);
            } else if (my_Genre.equals(getResources().getString(R.string.arts_culture))) {
                my_Genre_Image = R.drawable.ac_box;
                getActionBar().setIcon(my_Genre_Image);
            } else if (my_Genre.equals(getResources().getString(R.string.opinions))) {
                my_Genre_Image = R.drawable.opinions_box;
                getActionBar().setIcon(my_Genre_Image);
            } else {
                my_Genre_Image = R.drawable.ic_launcher;
                getActionBar().setIcon(my_Genre_Image);
            }
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0010"),
                    Toast.LENGTH_LONG).show();
            my_Genre = getResources().getString(R.string.top);
        }
    }

    public static int font_size = 100;
    /**
     * A dialog fragment that will allow the user to choose a font size
     */
    // TODO: Make the app remember which size you chose for all articles
    public class FontSizeDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pick_font)
                    .setItems(R.array.font_sizes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Get the webview for the content
                            WebView wv = (WebView)findViewById(R.id.article_content);
                            WebSettings ws = wv.getSettings();
                            final String mimeType = "text/html";
                            final String encoding = "UTF-8";


                            switch(which){
                                case 0: // Small
                                    font_size = 100;
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                                case 1: // Medium
                                    font_size = 150;
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                                case 2: // Large
                                    font_size = 200;
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                                default:// Default is small
                                    font_size = 100;
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                            }
                        }
                    });
            return builder.create();
        }
    }

    public void set_Webview_Body(View rootView, String mimeType, String encoding) {
        WebSettings ws = null;
        try {
            final WebView body_Text = (WebView) rootView.findViewById(R.id.article_content);
            ws = body_Text.getSettings();
            body_Text.loadDataWithBaseURL("", my_Body, mimeType, encoding, "");
            ws.setTextZoom(font_size);
            body_Text.postDelayed(new Runnable() {
                @Override
                public void run() {
                    body_Text.scrollTo(0, 0);
                }
            }, 300);
            //Makes webview background NEARLY transparent, not white.
            body_Text.setBackgroundColor(Color.argb(1, 0, 0, 0));
            //Scales in-article images to fit screen width
            body_Text.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("Failed to Load Article Body! \nCode: 6d617968656d-0013"),
                    Toast.LENGTH_LONG).show();
            final WebView body_Text = (WebView) rootView.findViewById(R.id.article_content);
            if (body_Text != null) {
                body_Text.setVisibility(View.GONE);
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        /*Initializes fragment.  Puts data in the proper text fields and sets the image. */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article_view,
                    container, false);

            fragment_View = rootView;

            //Initialize Variables
            WebView image = null;
            TextView title_Text = null;
            final String mimeType = "text/html";
            final String encoding = "UTF-8";

            //Set the image, if it exists
            try {
                image = (WebView) rootView.findViewById(R.id.article_image);
                if (my_Article.get_Has_Image()) {
                    image.loadDataWithBaseURL("", my_Article.get_image_URL(), mimeType, encoding, "");
                    image.setBackgroundColor(Color.argb(1, 0, 0, 0));
                } else {
                    image.setVisibility(View.GONE);
                }
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("Failed to Load Image! \nCode: 6d617968656d-0011"),
                        Toast.LENGTH_LONG).show();
                if (image != null) {
                    image.setVisibility(View.GONE);
                }
            }

                //Set the Title, if it can be found
            try{
                title_Text = (TextView) rootView.findViewById(R.id.article_title);
                title_Text.setText(my_Title);
            } catch (Exception bad) {
                Toast.makeText(getApplicationContext(),
                        String.format("Failed to Load Title! \nCode: 6d617968656d-0012"),
                        Toast.LENGTH_LONG).show();
                if (title_Text != null) {
                    title_Text.setText("Oops!  The title exploded...");
                }
            }

			
            //Set the Body
            set_Webview_Body(rootView, mimeType, encoding);

            //Set the Image
            set_Banner_Image(rootView);


                return rootView;
        }
    }

}
