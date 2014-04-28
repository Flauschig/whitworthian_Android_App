package mayhem.whitworthian_v2.app;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class handles the RSS feed, and sorts its data.  Contains the following:
 * this_Article:        An article object which fills with the info of the current article
 * article_List:        An ArrayList of articles; where all articles are stored from this feed
 * counter:             An integer which assigns IDs to the articles for tracking purposes
 * articlesAdded:       How many articles are in article_List
 * ARTICLES_LIMIT:      Total number of articles allowed to be pulled from the feed
 * chars:               A buffer which reads in characters from the RSS feed.
 */
public class Rss_Handler extends DefaultHandler {
    private article this_Article = new article();
    private ArrayList<article> article_List = new ArrayList<article>();
    private int counter= 0;
    private int articlesAdded = 0;
    private static final int ARTICLES_LIMIT = 20;
    StringBuffer chars = new StringBuffer();

    /*Constructor - Must be fed an ID base to keep track of different ID numbers */
    public Rss_Handler(int id_base){counter = id_base;}

    /* This method is called every time an XML start element ("<") is found.
     * Reset the value of chars, as we're only interested in what's inside the node.
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        chars = new StringBuffer();
    }

    /*
     * This method is called every time an XML end element (">") is found
     * here we check what element is being closed, if it is a relevant leaf node (Title, Body, etc.)
     * we keep the data inside and store that in our current this_Article
     *
     * If it's the close of an "item" node, then we know that it's time to add this_Article to the
     * ListArray of articles
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //Store the title, body (encoded) and genre (a category element) of the article
        if (localName.equalsIgnoreCase("title")){
            this_Article.set_Article_Title(chars.toString());
        } else if (localName.equalsIgnoreCase("encoded")){
            this_Article.set_Article_Body(chars.toString());
        } else if (localName.equalsIgnoreCase("category")) {
            String local = chars.toString();
            if (local.contains("Sports")) {
                this_Article.set_Article_Genre("Sports");
            } else if (local.contains("News")) {
                this_Article.set_Article_Genre("News");
            } else if (local.contains("Opinions")) {
                this_Article.set_Article_Genre("Opinions");
            } else if (local.contains("Arts & Culture")) {
                this_Article.set_Article_Genre("Arts & Culture");
            }
        } else if (localName.equalsIgnoreCase("description")) {
            this_Article.set_Article_Desc(chars.toString());
        } else if (localName.equalsIgnoreCase("guid")) {
            String ID = chars.toString();
            if (ID.contains(".com/?p=")) {
                String[] parts = ID.split("=");
                //TODO: Add a try/catch in case the URL format is changed.
                this_Article.set_Article_ID(Integer.parseInt(parts[1]));
            }
        }


        // If this is the end of the article (end of an item and not the top item of the feed), then
        // store the article.
        if (localName.equalsIgnoreCase("item") && !(this_Article.get_Title().equals("The Whitworthian"))) {
            if(this_Article.get_Article_ID() == -1)
            { this_Article.set_Article_ID(counter++); } //If not found, set article ID

            this_Article.set_Article_Has_Image(false);  //Currently no articles have images.

            //add article and reset local article.
            article_List.add(this_Article);
            this_Article = new article();

            // Check if we've stored too many articles--if we have, back out.
            articlesAdded++;
            if (articlesAdded >= ARTICLES_LIMIT)
            {
                throw new SAXException();
            }
        }
    }


    /* This method is called when characters are found in between XML markers.
     * We don't know exactly when this is called (if it's at end nodes, etc., so we just store
     * characters and deal with them in endElement
     */
    public void characters(char ch[], int start, int length) {
        chars.append(new String(ch, start, length));
    }

    /* Accessors */
    public ArrayList<article> getArticleList() {return article_List;}
}