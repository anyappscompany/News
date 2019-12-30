package ua.com.anyapps.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickShowSearchTrendsForDayListBtn(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, SearchTrendsForDayList.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
        //Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
    }
}
