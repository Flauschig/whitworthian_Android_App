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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/** This is the ArticleViewActivity.
 *  Includes the following functionality:
 *  -Receives a single article under-the-hood
 *  -Displays article's image, title, and body to the user in a scrollable format
 *  -Responds to user input by returning to article list page or by searching.
 *  -Allows user to change font size of article body.
 *
 *  Contains the following class variables:
 *  my_Genre:           The genre of the current article -- For action bar purposes
 *  my_Article:         Article to be displayed in this view
 *  my_Image_URL:       A string containing the article's image's URL, if available.
 *  font_size:          An integer which determines if article body font is small (100) medium (150)
 *                          or large (200)
 */
public class ArticleViewActivity extends ActionBarActivity {
    private String my_Genre;
    private Article my_Article;
    private View fragment_View;
    public static int font_Size = 100;


    /** OVERRIDEN ACTIVITY FUNCTIONS
     * onCreate()
     * onCreateOptionsMenu()
     * onOptionsItemSelected()
     */


    /* Create activity and fragment, set up local data */
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
        setup_Action_Bar_Appearance(goodies);
        get_Article_Data(goodies);
    }

    /*After OnCreate, OnCreateOptionsMenu creates the font size option button */
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
                return true;
            case R.id.action_font_size:
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                FontSizeDialogFragment dialog = new FontSizeDialogFragment();
                dialog.show(fm, "my dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** SET UP THE DISPLAY
     * get_Article_Data() function: receives the article to display.
     * setup_Action_Bar_Appearance() function: sets display title & back button image
     * set_Imageview_Banner() function: sets the top banner image.
     * set_Webview_Body() function: fills the text of the WebView which contains the article body.
     * set_Webview_Image() function: fills in the WebView which contains the article image
     * set_Textview_Title() functon: sets the text of the title.
     */

    /* Receives the article to display from the intent bundle */
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
    }

    /* Puts parent article list's genre and genre's icon on the action bar. */
    private void setup_Action_Bar_Appearance(Bundle goodies){
        //Try to get the genre from intent; if all else fails, set it as top news
        try{
            my_Genre = goodies.getString("my_Genre");
        }catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0009"),
                    Toast.LENGTH_LONG).show();
            my_Genre = getResources().getString(R.string.news);
        }
        try{
            //Set up action bar title as "The Whitworthian" or the genre.
            if (my_Genre.equals(getResources().getString(R.string.top)))
                setTitle(R.string.app_name);
            else
                setTitle(my_Genre);

            //Set up action bar image
            if (my_Genre.equals(getResources().getString(R.string.news))) {
                getActionBar().setIcon(R.drawable.news_box);
            } else if (my_Genre.equals(getResources().getString(R.string.sports))) {
                getActionBar().setIcon(R.drawable.sports_box);
            } else if (my_Genre.equals(getResources().getString(R.string.arts_culture))) {
                getActionBar().setIcon(R.drawable.ac_box);
            } else if (my_Genre.equals(getResources().getString(R.string.opinions))) {
                getActionBar().setIcon(R.drawable.opinions_box);
            } else {
                getActionBar().setIcon(R.drawable.ic_launcher);
            }
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0010"),
                    Toast.LENGTH_LONG).show();
            my_Genre = getResources().getString(R.string.top);
        }
    }

    /*Sets the article banner Image from my_Article.get_image_ID() */
    private void set_Imageview_Banner(View rootView) {
        ImageView image_Box = null;
        try {
            image_Box = (ImageView) rootView.findViewById(R.id.article_banner);
            if (my_Article.get_image_ID() == R.drawable.news_box) {
                image_Box.setImageResource(R.drawable.news_bar);
            } else if (my_Article.get_image_ID() == R.drawable.opinions_box) {
                image_Box.setImageResource(R.drawable.opinions_bar);
            } else if (my_Article.get_image_ID() == R.drawable.ac_box) {
                image_Box.setImageResource(R.drawable.ac_bar);
            } else if (my_Article.get_image_ID() == R.drawable.sports_box) {
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

    /* Sets the data in the article body's WebView */
    public void set_Webview_Body(View rootView, String mimeType, String encoding) {
        WebSettings ws = null;
        try {
            //Get the view & the font settings
            final WebView body_Text = (WebView) rootView.findViewById(R.id.article_content);
            ws = body_Text.getSettings();
            //Load the data
            body_Text.loadDataWithBaseURL("", my_Article.get_Body(), mimeType, encoding, "");

            //Refresh the WebView with the current font settings with a 0.3 second delay
            //Note, this is as good of a fix as there is to the WebView's current sizing issues,
            // it seems.  And this isn't even always a fix.
            ws.setTextZoom(font_Size);
            body_Text.postDelayed(new Runnable() {
                @Override
                public void run() {
                    body_Text.scrollTo(0, 0);
                }
            }, 300);

            //Makes webview background NEARLY transparent.  Keeps it from flickering.
            body_Text.setBackgroundColor(Color.argb(1, 0, 0, 0));
            //Scales in-article images to fit screen width
            body_Text.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("Failed to Load Article Body! \nCode: 6d617968656d-0013"),
                    Toast.LENGTH_LONG).show();

            //Make the Webview disappear, if possible.
            final WebView body_Text = (WebView) rootView.findViewById(R.id.article_content);
            if (body_Text != null) {
                body_Text.setVisibility(View.GONE);
            }
        }
    }

    /* Sets the data in the article image's WebView */
    public void set_Webview_Image(View rootView, String mimeType, String encoding) {
        WebView image = null;
        //Fill the Image WebView, or make it disappear if bad things happen.
        try {
            image = (WebView) rootView.findViewById(R.id.article_image);
            if (my_Article.get_Has_Image()) {
                //Load the URL for the image
                image.loadDataWithBaseURL("", my_Article.get_image_URL(), mimeType, encoding, "");
                //Make the background ALMOST transparent, so there's no flickering
                image.setBackgroundColor(Color.argb(1, 0, 0, 0));
                //Make the image fit in the screen
                image.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
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
    }

    /* Sets the title of the article */
    public void set_Textview_Title(View rootView) {
        TextView title_Text = null;
        try{
            title_Text = (TextView) rootView.findViewById(R.id.article_title);
            title_Text.setText(my_Article.get_Title());
        } catch (Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("Failed to Load Title! \nCode: 6d617968656d-0012"),
                    Toast.LENGTH_LONG).show();
            if (title_Text != null) {
                title_Text.setText("Oops!  The title exploded...");
            }
        }
    }


    /** FONT SETTING FUNCTIONS
     * save_Font_Setting() function: saves the selected font size to preferences file
     * read_Font_Setting() function: reads the selected font size from preferences file
     */

    /* Saves font size setting to file */
    public void save_Font_Setting() {
        File file = new File(getFilesDir()+File.separator+
                getResources().getString(R.string.preferences_file));
        try {
            //If the file exists
            if (file.exists()) {
                //Read in all preferences
                FileReader file_Reader = new FileReader(getFilesDir()+File.separator+file.getName());
                BufferedReader buffer_Reader = new BufferedReader(file_Reader);
                String line = buffer_Reader.readLine();
                buffer_Reader.close();

                //Split apart preferences
                String preferences[] = line.split("@");
                line = "";

                //Search for font size in preferences, and rewrite the preference
                for (String pref: preferences) {
                    if (pref.contains(getResources().getString(R.string.fontsize_preference))) {
                        line += getResources().getString(R.string.fontsize_preference) + "=" +
                                Integer.toString(font_Size) + "@";

                    } else{ //If this preference isn't font size, remember it as is
                        line += pref + "@";
                    }
                }

                //Empty File
                PrintWriter writer = new PrintWriter(file);
                writer.print("");
                writer.close();

                //Rewrite preferences to file
                FileWriter file_Writer = new FileWriter(getFilesDir()+File.separator+file.getName(), true);
                BufferedWriter buffer_Writer = new BufferedWriter(file_Writer);
                buffer_Writer.write(line);
                buffer_Writer.close();

            } else {
                //If there's no preference file, make it and put the font size preference in it.
                file.createNewFile();
                String preference_String = getResources().getString(R.string.fontsize_preference) +
                        "=" + Integer.toString(font_Size) +  "@"; //String delimiter
                FileWriter file_Writer = new FileWriter(getFilesDir()+File.separator+file.getName(), true);
                BufferedWriter buffer_Writer = new BufferedWriter(file_Writer);
                buffer_Writer.write(preference_String);
                buffer_Writer.close();
            }
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0043"),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* Reads font size setting from file */
    public void read_Font_Setting() {
        File file = new File(getFilesDir()+File.separator+
                getResources().getString(R.string.preferences_file));
        try {
            if (file.exists()) {
                //If the file exists, read in preferences
                FileReader file_Reader = new FileReader(getFilesDir()+File.separator+file.getName());
                BufferedReader buffer_Reader = new BufferedReader(file_Reader);
                String line = buffer_Reader.readLine();
                buffer_Reader.close();

                //Split apart preferences
                String preferences[] = line.split("@");
                //Look for font size preference, and set the local variable
                for (String pref: preferences) {
                    if (pref.contains(getResources().getString(R.string.fontsize_preference))) {
                        font_Size = Integer.parseInt(pref.substring(pref.lastIndexOf("=")+1));
                    }
                }
            } else {
                //Default font size.
                font_Size = 100;
            }
        } catch(Exception bad) {
            Toast.makeText(getApplicationContext(),
                    String.format("A non-fatal error occurred! \nCode: 6d617968656d-0044"),
                    Toast.LENGTH_LONG).show();
            font_Size = 100;
        }
    }



    /** PLACEHOLDERFRAGMENT:
     * A placeholder fragment which is the focus of the ArticleviewActivity.  All data presentation
     * functions are called through this fragment's onCreateView activity.
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

            //Initialize Variables -- including font size
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            read_Font_Setting();

            //Set the Banner Image
            set_Imageview_Banner(rootView);

            //Set the image, if it exists
            set_Webview_Image(rootView, mimeType, encoding);

            //Set the Title, if it can be found
            set_Textview_Title(rootView);

            //Set the Body
            set_Webview_Body(rootView, mimeType, encoding);



                return rootView;
        }
    }


    /** FONTSIZEDIALOGFRAGMENT:
     * A dialog fragment that will allow the user to select a font size
     */
    public class FontSizeDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pick_font)
                    .setItems(R.array.font_sizes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final String mimeType = "text/html";
                            final String encoding = "UTF-8";
                            //Set the font size based on the user choice, and save to file.
                            switch(which){
                                case 0: // Small
                                    font_Size = 100;
                                    save_Font_Setting();
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                                case 1: // Medium
                                    font_Size = 150;
                                    save_Font_Setting();
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                                case 2: // Large
                                    font_Size = 200;
                                    save_Font_Setting();
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                                default:// Default is small
                                    font_Size = 100;
                                    save_Font_Setting();
                                    set_Webview_Body(fragment_View, mimeType, encoding);
                                    break;
                            }
                        }
                    });
            return builder.create();
        }
    }

}
