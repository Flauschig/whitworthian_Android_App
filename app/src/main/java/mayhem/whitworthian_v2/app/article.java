package mayhem.whitworthian_v2.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Primary class for handling article data.  Contains the following elements:
 *  article_ID:         used by application to easily distinguish articles
 *  title:              the title of the article
 *  body:               the full body text of the article
 *  desc:               the description of the article
 *  genre:              the genre of the article
 *  is_Top:             a boolean determining whether or not the article is Top News
 *  has_Image:          a boolean determining whether or not the article has an image link
 *  viewed:             a boolean determining whether or not the user has viewed the article
 *  image_ID:           default image ID based on genre
 *  image_URL:          if has_Image is true, the URL of this article's image.
 */
public class article implements Parcelable {
    private int article_ID;
    private String title;
    private String body;
    private String desc;
    private String genre;
    private boolean is_Top;
    private boolean viewed;
    private boolean has_Image;
    private int image_ID;
    private String image_URL;



    /*Part of Parcelable interface.
        If article ever has child classes, this is used to distinguish which type of article
         the parcel is.
     */
    public int describeContents() {
        return 0;
    }

    /*Part of Parcelable interface.
        When parcelled, the program stores the article's information in this order.  It must
         be retrieved in the same order to ensure correctness.
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(article_ID);
        out.writeString(title);
        out.writeString(body);
        out.writeString(desc);
        out.writeString(genre);
        out.writeByte((byte) (is_Top ? 1: 0));
        out.writeByte((byte) (has_Image ? 1 : 0));
        out.writeByte((byte) (viewed ? 1 : 0));
        out.writeInt(image_ID);
        out.writeString(image_URL);

    }

    /*Part of Parcelable interface.
        When unpacking a parcel, the program fills data fields in the order laid out below.
     */
    private article(Parcel in) {
        article_ID = in.readInt();
        title = in.readString();
        body = in.readString();
        desc = in.readString();
        genre = in.readString();
        is_Top = in.readByte() != 0;
        has_Image = in.readByte() != 0;
        viewed = in.readByte() != 0;
        image_ID = in.readInt();
        image_URL = in.readString();
    }

    /*Part of Parcelable interface
        Creates the parcel.
     */
    public static final Parcelable.Creator<article> CREATOR
            = new Parcelable.Creator<article>() {
        public article createFromParcel(Parcel in) {
            return new article(in);
        }

        public article[] newArray(int size) {
            return new article[size];
        }
    };



    /* Default constructor.     */
    public article(){
        this.article_ID = -1;
        this.title = "";
        this.body = "";
        this.desc = "";
        this.genre = "";
        this.is_Top = false;
        this.has_Image = false;
        this.viewed = false;
        this.image_ID = 0;
        this.image_URL = null;
    }

    /* Constructor for articles which DO have image URLs */
    public article(int article_ID, String title, String body, String desc, String genre, boolean is_Top,
                   boolean has_Image, String image_URL){
        this.article_ID = article_ID;
        this.title = title;
        this.body = body;
        this.desc = desc;
        this.genre = genre;
        this.is_Top = is_Top;
        this.has_Image = has_Image;
        this.viewed = false;

        if (this.has_Image)
        {
            this.image_URL = image_URL;
        }
        else{
            this.image_URL = null;
        }

        //Set up default image
        if (this.genre.equals("News")){
            this.image_ID = R.drawable.news_box;
        }
        else if (this.genre.equals("Sports")){
            this.image_ID = R.drawable.sports_box;
        }
        else if (this.genre.equals("Arts & Culture")){
            this.image_ID = R.drawable.ac_box;
        }
        else if (this.genre.equals("Opinions")){
            this.image_ID = R.drawable.opinions_box;
        }
        else{
            this.image_ID = R.drawable.ic_launcher;
        }
    }

    /* Constructor for articles which DO NOT have image URLs */
    public article(int article_ID, String title, String body, String desc, String genre,
                   boolean is_Top, boolean has_Image){
        this.article_ID = article_ID;
        this.title = title;
        this.body = body;
        this.desc = desc;
        this.genre = genre;
        this.is_Top = is_Top;
        this.has_Image = has_Image;
        this.viewed = false;

        if (this.has_Image)
        {
            this.image_URL = image_URL;
        }
        else{
            this.image_URL = null;
        }

        //Set up default image
        if (this.genre.equals("News")){
            this.image_ID = R.drawable.news_box;
        }
        else if (this.genre.equals("Sports")){
            this.image_ID = R.drawable.sports_box;
        }
        else if (this.genre.equals("Arts & Culture")){
            this.image_ID = R.drawable.ac_box;
        }
        else if (this.genre.equals("Opinions")){
            this.image_ID = R.drawable.opinions_box;
        }
        else{
            this.image_ID = R.drawable.whitworthian_w;
        }
    }


    /* Accessors */
    public int get_Article_ID() { return this.article_ID; }
    public String get_Title(){
        return this.title;
    }
    public String get_Body(){
        return this.body;
    }
    public String get_Desc() { return this.desc; }
    public String get_Genre(){
        return this.genre;
    }
    public boolean is_Top(){return is_Top;}
    public boolean get_Has_Image(){
        return has_Image;
    }
    public boolean get_Viewed() {return viewed;}
    public int get_image_ID(){
        return image_ID;
    }
    public String get_image_URL(){
        return image_URL;
    }

    /* Mutators */
    public void set_Article_ID(int id) {this.article_ID = id;}
    public void set_Article_Title(String title) {this.title = title;}
    public void set_Article_Body(String body) {this.body = body;}
    public void set_Article_Desc(String desc) { this.desc = desc; }
    public void set_Article_Genre(String genre) {
        this.genre = genre;
        //Set up default image
        if (this.genre.equals("News")){
            this.image_ID = R.drawable.news_box;
        }
        else if (this.genre.equals("Sports")){
            this.image_ID = R.drawable.sports_box;
        }
        else if (this.genre.equals("Arts & Culture")){
            this.image_ID = R.drawable.ac_box;
        }
        else if (this.genre.equals("Opinions")){
            this.image_ID = R.drawable.opinions_box;
        }
        else{
            this.image_ID = R.drawable.ic_launcher;
        }}
    public void set_Article_Is_Top(boolean is_Top) {this.is_Top = is_Top;}
    public void set_Article_Has_Image(boolean has_Image) {this.has_Image = has_Image;}
    public void set_Viewed(boolean viewed) {this.viewed = viewed;}
    public void set_image_URL(String image_URL) {this.image_URL = image_URL;}
}
