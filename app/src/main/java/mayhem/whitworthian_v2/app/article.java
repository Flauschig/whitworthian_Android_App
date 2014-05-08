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
    private boolean has_Thumb;
    private int image_ID;
    private String image_URL;
    private String thumb_URL;



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
        out.writeByte((byte) (is_Top ? 1 : 0));
        out.writeByte((byte) (has_Image ? 1 : 0));
        out.writeByte((byte) (viewed ? 1 : 0));
        out.writeByte((byte) (has_Thumb ? 1 : 0));
        out.writeInt(image_ID);
        out.writeString(image_URL);
        out.writeString(thumb_URL);

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
        has_Thumb = in.readByte() != 0;
        image_ID = in.readInt();
        image_URL = in.readString();
        thumb_URL = in.readString();
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
        this.has_Thumb = false;
        this.viewed = false;
        this.image_ID = 0;
        this.image_URL = null;
        this.thumb_URL = null;
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
    public boolean get_Has_Thumb() { return has_Thumb; }
    public boolean get_Viewed() {return viewed;}
    public int get_image_ID(){
        return image_ID;
    }
    public String get_image_URL(){
        return image_URL;
    }
    public String get_Thumb_URL() {return thumb_URL; }

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
    public void set_Viewed(boolean viewed) {this.viewed = viewed;}
    public void set_image_URL(String image_URL)
    {
        this.image_URL = image_URL;
        if (this.image_URL == null) {
            this.has_Image = false;
        }
        else {
            this.has_Image = true;
        }
    }
    public void set_Thumb_URL(String thumb_URL)
    {
        this.thumb_URL = thumb_URL;
        if (this.thumb_URL == null) {
            this.has_Thumb = false;
        }
        else {
            this.has_Thumb = true;
        }
    }
}
