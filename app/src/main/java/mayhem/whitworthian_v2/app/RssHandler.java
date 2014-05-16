package mayhem.whitworthian_v2.app;
import android.content.Context;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class handles the RSS feed, and sorts its data.
 *
 * Contains the following:
 *
 * my_Articles:         An arraylist of all articles fetched from RSS feed
 * ids:                 An arraylist of the ids of all fetched articles
 * current_Article:     Temporary data for the article being fetched.
 * ctxt:                The application context, used for accessing strings.xml resources
 * categories:          An arraylist of strings of RSS feed categories for the current article.
 * badID:               Used in the case of an article's ID being a duplicate
 */
public class RssHandler {
    private ArrayList<Article> my_Articles;
    private ArrayList<Integer> ids;
    private Article current_Article;
    private Context ctxt;
    private ArrayList<String> categories;
    private int badID = -1337;

    /* Constructor */
    public RssHandler(Context ctxt) {
        my_Articles = new ArrayList<Article>();
        ids = new ArrayList<Integer>();
        this.ctxt = ctxt;
    }


    /* Parses data from the RSS feed */
    public void parse(InputStream is) throws IOException, SAXException {
        RootElement rss = new RootElement("rss");
        Element channel = rss.requireChild("channel");
        Element item = channel.requireChild("item");
        item.setElementListener(new ElementListener() {
            public void end() {
                onItem();
            }
            public void start(Attributes attributes) {
                current_Article = new Article();
                categories = new ArrayList<String>();
            }
        });
        //Retrieves the title
        item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                current_Article.set_Article_Title(body);
            }
        });
        //Retrieves the description
        item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                current_Article.set_Article_Desc(body);
            }
        });
        //Retrieves the ID
        item.getChild("guid").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                if (body.contains(".com/?p=")) {
                    String[] parts = body.split("=");
                    int temp = Integer.parseInt(parts[1]);
                    if (ids.contains(temp)) {
                        current_Article.set_Article_ID(badID);
                    }else {
                        //TODO: Add a try/catch in case the URL format is changed.
                        current_Article.set_Article_ID(temp);
                        ids.add(temp);
                    }
                }
            }
        });
        //Retrieves categories/genre
        item.getChild("category").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                categories.add(body);
                if (body.contains("Sports")) {
                    current_Article.set_Article_Genre("Sports");
                } else if (body.contains("News")) {
                    current_Article.set_Article_Genre("News");
                } else if (body.contains("Opinions")) {
                    current_Article.set_Article_Genre("Opinions");
                } else if (body.contains("Arts & Culture")) {
                    current_Article.set_Article_Genre("Arts & Culture");
                }
            }
        });
        //Retrieves the article body
        item.getChild("http://purl.org/rss/1.0/modules/content/", "encoded").
                setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                current_Article.set_Article_Body(clean_Article_Body(body));
            }
        });
        //Retrieves the image url for the article
        item.getChild("http://search.yahoo.com/mrss/", "content").
                setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                if (check_Image(attributes.getValue("", "url"))) {
                    current_Article.set_image_URL(format_Image(attributes.getValue("", "url")));
                } else {
                    current_Article.set_image_URL(null);
                }
            }
        });
        //Retrieves the thumbnail url for the article
        item.getChild("http://search.yahoo.com/mrss/", "thumbnail").
                setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                String thumb_String = attributes.getValue("", "url");
                if (thumb_String.contains((".png"))) {
                    thumb_String = remove_Old_Dims(thumb_String, "png")
                            .replace(".png", "-186x186.png");
                }
                else if (thumb_String.contains(".jpg")) {
                    thumb_String = remove_Old_Dims(thumb_String, "jpg")
                            .replace(".jpg", "-186x186.jpg");
                }
                current_Article.set_Thumb_URL(format_Image(thumb_String));

            }
        });
        Xml.parse(is, Xml.Encoding.UTF_8, rss.getContentHandler());
    }

    /*Sees if the image url is one of the generic images for a given section */
    public boolean check_Image(String url) {
        try {
            if (url.contains(ctxt.getString(R.string.ac_img_url))) {
                return false;
            } else if (url.contains(ctxt.getString(R.string.news_img_url))) {
                return false;
            } else if (url.contains(ctxt.getString(R.string.opinions_img_url))) {
                return false;
            } else if (url.contains(ctxt.getString(R.string.sports_img_url))) {
                return false;
            }
            return true;
        } catch (NullPointerException bad) {
            bad.printStackTrace();
        }
        return true;
    }

    /*If a thumbnail file has dimensions on it, strip those dimensions.  We need to make it
      186x186 and we do so by adding those dims to the string.
      Double dimension specification = bad.
     */
    private String remove_Old_Dims(String url, String fileType) {
        //Currently, we're splitting the url on every "-".  If the segment contains the file type
        //(.png or .jpg), and that file type hax an x in the 3rd, 4th, or 5th character, then
        //we can assume the chunk takes the form dim1xdim2.jpg.  We strip dim1xdim2.
        //TODO: Do this with regex rather than this.  It's more reliable.

        String[] parts = url.split("-");
        boolean hadDims = false;
        if (parts[parts.length-1].contains(fileType)) {
            String current = parts[parts.length-1];
            if (current.charAt(2) == 'x' || current.charAt(3) == 'x' || current.charAt(4) == 'x' ||
                    current.charAt(5) == 'x') {
                parts[parts.length-1] = "." + fileType;
                hadDims = true;
            }
        }
        url = "";
        for (int i = 0; i < parts.length; i++) {
            url += parts[i];
            if(hadDims && (i == parts.length - 2)) {continue;}
            if (i != parts.length - 1) {url += "-";}
        }
        return url;
    }

    /* Applies justification to all text in the article's body*/
    private String clean_Article_Body(String body) {
            return "<body style=\"text-align:justify;\"> " + body + " </body>";
    }

    /*Surrounds image urls in appropriate html so they can be accessed from webviews. */
    private String format_Image(String image_URL) {
        if (image_URL == null) {
            return null;
        }
        return  "<body style=\"margin: 0; padding: 0\">" +
                "<img src=" + image_URL + " width=\"100%\" />" +
                "</body>";
    }

    //mark all current articles in my_Articles as top news.
    public void mark_Top() {
        for (int i = 0; i < my_Articles.size(); i++) {
            my_Articles.get(i).set_Article_Is_Top(true);
        }
    }

    //Once an item is completed in the RSS feed, this adds the article to the arraylist.
    public void onItem() {
        if (current_Article.get_Article_ID() != badID) {
            current_Article.set_Categories(categories);
            my_Articles.add(current_Article);
        }
        return;
    }
    public ArrayList<Article> getArticleList() {return my_Articles;}
}