package ua.com.anyapps.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.com.anyapps.news.Server.SearchTrendsForDay.FullNews;
import ua.com.anyapps.news.Server.ServerConnect;

public class SearchTrendsForDayList extends AppCompatActivity {

    String locale;
    String countryCode;
    private static final String TAG = "debapp";
    ProgressBar pbLoading;

    ListView lvNews;
    List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay> newsList = new ArrayList<>();
    SearchTrendsForDayListAdapter newsListAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String[] countries;
    String[] countryCodes;
    WebView wv;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trends_for_day_list);

        countries = getResources().getStringArray(R.array.countries);
        countryCodes = getResources().getStringArray(R.array.country_codes);

        pbLoading = (ProgressBar)findViewById(R.id.pbLoading);
        lvNews = (ListView)findViewById(R.id.lvNews);

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        countryCode = sharedPreferences.getString("countryCode", null);

        locale = getResources().getConfiguration().locale.getCountry();

        // если в настройках не сохранеа страна по умолчанию, то взять из телефона
        if(countryCode==null) {
            TelephonyManager tm = (TelephonyManager) SearchTrendsForDayList.this.getSystemService(SearchTrendsForDayList.this.TELEPHONY_SERVICE);
            countryCode = tm.getNetworkCountryIso();

            editor.putString("countryCode", countryCode);
            editor.commit();
        }

        // при клике по новости показать полную версию
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View v;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            v = view;
                wv = new WebView(SearchTrendsForDayList.this);
                alert = new AlertDialog.Builder(SearchTrendsForDayList.this);
                alert.setTitle(newsList.get(Integer.parseInt(v.getTag().toString())).getArt_title());
                //wv.loadUrl("http:\\www.google.com");
                wv.loadDataWithBaseURL("", getResources().getString(R.string.full_news_loading_string), "text/html", "UTF-8", "");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                alert.setView(wv);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                alert.setNeutralButton(">>", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, newsList.get(Integer.parseInt(v.getTag().toString())).getArt_url());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsList.get(Integer.parseInt(v.getTag().toString())).getArt_url()));
                        startActivity(browserIntent);
                    }
                });


                alert.show();

                ServerConnect.getInstance()
                        .getJSONApi()
                        .getfullnews(newsList.get(Integer.parseInt(view.getTag().toString())).getUnique_str())
                        .enqueue(new Callback<ua.com.anyapps.news.Server.SearchTrendsForDay.FullNews>() {
                            @Override
                            public void onResponse(Call<ua.com.anyapps.news.Server.SearchTrendsForDay.FullNews> call, Response<ua.com.anyapps.news.Server.SearchTrendsForDay.FullNews> response) {

                                //Log.d(TAG, "SSSSSSSS " + response.body());
                                if (response.isSuccessful()) {
                                    FullNews fullN = response.body();
                                    Log.d(TAG, "CCCCCCC "+fullN.getArt_text());
                                    wv.loadDataWithBaseURL("", fullN.getArt_text(), "text/html", "UTF-8", "");

                                    //newsListAdapter = new SearchTrendsForDayListAdapter(SearchTrendsForDayList.this, newsList);
                                    //lvNews.setAdapter(newsListAdapter);
                                } else {
                                    Log.e(TAG, "Во время получения полной новости сервер вернул ошибку " + response.code());
                                    //Toast.makeText(MainActivity.this, getResources().getStringArray(R.array.app_errors)[2] + " " + response.code(), Toast.LENGTH_LONG).show();
                                }
                                //hideLoader();
                            }

                            @Override
                            public void onFailure(Call<ua.com.anyapps.news.Server.SearchTrendsForDay.FullNews> call, Throwable t) {
                                hideLoader();
                                Log.e(TAG, "Error - " + t);
                                //Toast.makeText(MainActivity.this, getResources().getStringArray(R.array.app_errors)[3], Toast.LENGTH_LONG).show();
                            }
                        });

                Log.d(TAG, "TAG: " + view.getTag());
                Log.d(TAG, "US: " + newsList.get(Integer.parseInt(view.getTag().toString())).getUnique_str());



            }
        });

        Log.d(TAG, "Loacale: "+locale+" Country code: "+countryCode);

        updateNews(countryCode);
    }

    private void showLoader(){
        pbLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoader(){
        pbLoading.setVisibility(View.GONE);
    }

    private void updateNews(String cCode){

        // установка нового заголовка активити
        ActivityInfo activityInfo = null;
        try {
            activityInfo = getPackageManager().getActivityInfo(
                    getComponentName(), PackageManager.GET_META_DATA);
            String title = activityInfo.loadLabel(getPackageManager())
                    .toString();
            setTitle(title + ": " + cCode.toUpperCase());
            Log.d(TAG, "Title activity: " + title.toUpperCase());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }


        lvNews.setAdapter(null);
        showLoader();
        ServerConnect.getInstance()
                .getJSONApi()
                .getnews(cCode)
                .enqueue(new Callback<List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay>>() {
                    @Override
                    public void onResponse(Call<List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay>> call, Response<List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay>> response) {

                        if (response.isSuccessful()) {
                            newsList = response.body();

                            newsListAdapter = new SearchTrendsForDayListAdapter(SearchTrendsForDayList.this, newsList);
                            lvNews.setAdapter(newsListAdapter);

                            /*if (employeesInfo.getSucess()) {
                                // обработка списка
                                employeesArr = employeesInfo.getData();
                                for(int i = 0; i<employeesArr.length; i++){
                                    Log.d(TAG, "Employe: " + employeesArr[i].getSurname());
                                }
                                if(employeesListAdapter == null){
                                    employeesListAdapter = new EmployeesListAdapter(getApplicationContext(), employeesArr);
                                }
                                if(lvEmployeesList == null){
                                    lvEmployeesList = (ListView) findViewById(R.id.lvEmployeesList);

                                    // выбранный сотрудник Tag - id сотрудника по базе
                                    lvEmployeesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            //Log.d(TAG, "Your favorite : " + view.getTag());
                                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("tags", view.getTag().toString());
                                            b.putString("employeeName", employeesArr[position].getName());
                                            b.putString("employeeSurname", employeesArr[position].getSurname());
                                            intent.putExtras(b);
                                            startActivity(intent);
                                            //finish();
                                        }
                                    });
                                }
                                //lvEmployeesList.setAdapter(employeesListAdapter);
                            }*/


                        } else {
                            Log.e(TAG, "Во время получения списка сотрудников, сервер вернул ошибку " + response.code());
                            //Toast.makeText(MainActivity.this, getResources().getStringArray(R.array.app_errors)[2] + " " + response.code(), Toast.LENGTH_LONG).show();
                        }
                        hideLoader();
                    }

                    @Override
                    public void onFailure(Call<List<ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay>> call, Throwable t) {
                        hideLoader();
                        Log.e(TAG, "Error - " + t);
                        //Toast.makeText(MainActivity.this, getResources().getStringArray(R.array.app_errors)[3], Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for(int i=0; i<countries.length;i++){
            menu.add(countries[i]);
            if(countryCodes[i].equals(countryCode)){
                menu.getItem(i).setChecked(true);
            }
        }

        boolean countryExist = false;

        Log.d(TAG, "Loacale2: "+locale+" Country code2: "+countryCode);

        menu.setGroupCheckable(0, true, true);

        return super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.search_trends_for_day_list_menu, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.isChecked()) {
            item.setChecked(false);
        }
        else {
            item.setChecked(true);
//обновить
            updateNews(item.getTitle().toString().toLowerCase());
            Log.d(TAG, "IID: " + item.getTitle());
            editor.putString("countryCode", item.getTitle().toString().toLowerCase());
            editor.commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
