package mayhem.whitworthian_v2.app;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * This class adapts an article_Selection to the ListView in fragment_article_list by using
 * the article_list_item_row layout.
 *
 * contains these elements:
 *  context - the current app context
 *  layoutResourceID - the ID of article_list_item_row
 *
 */

public class article_Selection_Adapter extends ArrayAdapter<article_Selection> {
    private Context context;
    private int layout_Resource_ID;
    private article_Selection data[] = null;


    /* Constructor */
    public article_Selection_Adapter(Context context, article_Selection[] data) {
        super(context, R.layout.article_list_item_row, data);
        this.layout_Resource_ID = R.layout.article_list_item_row;
        this.context = context;
        this.data = data;
    }

    /* Fills article data into the appropriate ListView */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Inflates the list
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(this.layout_Resource_ID, parent, false);


        //Fills the list with data
        article_Selection_Holder holder = new article_Selection_Holder();
        try{
            holder.img_Icon = (ImageView) rowView.findViewById(R.id.article_Img_Icon);
            holder.txt_Title = (TextView) rowView.findViewById(R.id.article_Title);
            holder.txt_Desc = (TextView) rowView.findViewById(R.id.article_Desc);
            holder.img_URL = (WebView) rowView.findViewById(R.id.thumbnail);

            holder.txt_Title.setText(data[position].get_Title());
            holder.img_Icon.setImageResource(data[position].get_Icon());
            holder.txt_Desc.setText(trim_Desc(data[position].get_Desc()));

            if(data[position].get_icon_URL() == null) {
                holder.img_URL.setVisibility(View.GONE);
            } else {
                final String mimeType = "text/html";
                final String encoding = "UTF-8";
                holder.img_URL.loadDataWithBaseURL("", data[position].get_icon_URL(),
                        mimeType, encoding, "");
                holder.img_URL.setBackgroundColor(Color.argb(1, 0, 0, 0));
                holder.img_Icon.setVisibility(View.INVISIBLE);
            }


            //If it's viewed, make it look different
            if (data[position].get_Viewed()) {
                holder.txt_Title.setTextAppearance(this.context, R.style.old_title);
                holder.txt_Desc.setTextAppearance(this.context, R.style.old_desc);
                rowView.setBackgroundColor(Color.parseColor(context.getString(R.string.clicked_grey)));
            }
            else {
                holder.txt_Title.setTextAppearance(this.context, R.style.new_title);
            }
        }
        catch(NullPointerException bad) {
            bad.printStackTrace();
        }
        return rowView;
    }

    /* Clean ellipse and dash tags in description */
    private String trim_Desc(String desc) {
        desc = desc.replace("&#8211;", "-");
        desc = desc.replace(" [&#038;hellip", "...");
        desc = desc.replace("&#8217;", "'");
        desc = desc.substring(0, 100) + "...";
        return desc;
    }

    /* A data structure which holds the layout resources being filled. */
    static class article_Selection_Holder {
        private ImageView img_Icon = null;
        private TextView txt_Title = null;
        private TextView txt_Desc = null;
        private WebView img_URL = null;
    }
}