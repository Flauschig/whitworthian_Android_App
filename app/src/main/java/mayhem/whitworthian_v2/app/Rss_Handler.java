package mayhem.whitworthian_v2.app;
import android.content.Context;
import android.content.res.Resources;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;
import android.view.Display;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class handles the RSS feed, and sorts its data.  Contains the following:
 */
public class Rss_Handler{
    private ArrayList<article> my_Articles;
    private article current_Article;
    private Context ctxt;
    private ArrayList<String> categories;

    public Rss_Handler(Context ctxt) {
        my_Articles = new ArrayList<article>();
        this.ctxt = ctxt;
    }


    public void parse(InputStream is) throws IOException, SAXException {
        RootElement rss = new RootElement("rss");
        Element channel = rss.requireChild("channel");
        Element item = channel.requireChild("item");
        item.setElementListener(new ElementListener() {
            public void end() {
                onItem();
            }
            public void start(Attributes attributes) {
                current_Article = new article();
                categories = new ArrayList<String>();
            }
        });
        item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                current_Article.set_Article_Title(body);
            }
        });
        item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                current_Article.set_Article_Desc(body);
            }
        });
        item.getChild("guid").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                if (body.contains(".com/?p=")) {
                    String[] parts = body.split("=");
                    //TODO: Add a try/catch in case the URL format is changed.
                    current_Article.set_Article_ID(Integer.parseInt(parts[1]));
                }
            }
        });
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
        item.getChild("http://purl.org/rss/1.0/modules/content/", "encoded").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                current_Article.set_Article_Body(body);
            }
        });
        item.getChild("http://search.yahoo.com/mrss/", "content").setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                if (check_Image(attributes.getValue("", "url"))) {
                    current_Article.set_image_URL(attributes.getValue("", "url"));
                } else {
                    current_Article.set_image_URL(null);
                }
            }
        });
        item.getChild("http://search.yahoo.com/mrss/", "thumbnail").setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                String thumb_String = attributes.getValue("", "url");
                if (thumb_String.contains((".png"))) {
                    thumb_String = thumb_String.replace(".png", "-186x186.png");
                }
                else if (thumb_String.contains(".jpg")) {
                    thumb_String = thumb_String.replace(".jpg", "-186x186.jpg");
                }
                current_Article.set_Thumb_URL(thumb_String);

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

    public void onItem() {
        current_Article.set_Categories(categories);
        my_Articles.add(current_Article);
        return;
    }
    public ArrayList<article> getArticleList() {return my_Articles;}
}