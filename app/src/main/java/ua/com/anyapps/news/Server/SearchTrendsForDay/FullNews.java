package ua.com.anyapps.news.Server.SearchTrendsForDay;

public class FullNews
{
    private String art_text;

    public String getArt_text ()
    {
        return art_text;
    }

    public void setArt_text (String art_text)
    {
        this.art_text = art_text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [art_text = "+art_text+"]";
    }
}
