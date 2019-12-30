package ua.com.anyapps.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class SearchTrendsForDayListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay> objects;
    private static final String TAG = "debapp";

    SearchTrendsForDayListAdapter(Context context, List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay> news) {
        this.context = context;
        this.objects = news;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(Context context, List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay> news){
        this.context = context;
        this.objects = news;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // используем созданные, но не используемые view
        View v = view;
        if (v == null) {
            v = lInflater.inflate(R.layout.one_news_row, viewGroup, false);
        }

        ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay newsInfo = getProduct(i);

        ImageView ivArtImage = (ImageView) v.findViewById(R.id.ivArtImage);

        try {
            Picasso.get().load(newsInfo.getArt_image_url()).into(ivArtImage);
        }catch (Exception ex){
            Log.d(TAG, "NOIMAGE: "+newsInfo.getArt_title());
        }

        TextView tvArtTitle = (TextView)v.findViewById(R.id.tvArtTitle);
        TextView tvNewsDate = (TextView)v.findViewById(R.id.tvNewsDate);
        TextView tvQuery = (TextView)v.findViewById(R.id.tvQuery);
        TextView tvArtSnippet = (TextView)v.findViewById(R.id.tvArtSnippet);
        TextView tvArtSource = (TextView)v.findViewById(R.id.tvArtSource);

        tvArtTitle.setText(newsInfo.getArt_title());
        String newDate = "";
        newDate = newsInfo.getNews_period().substring(0, 4) + "/" + newsInfo.getNews_period().substring(4, 6) + "/" + newsInfo.getNews_period().substring(6, 8);

        //newsInfo.getNews_period()
        tvNewsDate.setText(newDate);
        tvQuery.setText(newsInfo.getQuery_title());
        tvArtSnippet.setText(newsInfo.getArt_snippet());
        tvArtSource.setText(newsInfo.getArt_source());

        v.setTag(i);
        tvArtSource.setTag(newsInfo.getNews_url());
        //TextView tvAppLabel = v.findViewById(R.id.tvAppLabel);
        //tvAppLabel.setText(aInfo.applicationLabel);

        //((TextView) view.findViewById(R.id.tvPrice)).setText(p.price + "");
        //((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);

        //CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        // присваиваем чекбоксу обработчик
        //cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        //cbBuy.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        //cbBuy.setChecked(p.box);
        return v;
    }

    ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay getProduct(int position) {
        return ((ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay) getItem(position));
    }
}
