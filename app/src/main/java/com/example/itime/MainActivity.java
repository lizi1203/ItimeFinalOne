package com.example.itime;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.IntFunction;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_UPDATE =901 ;
    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList<TimeItem> timeItemList=new ArrayList<>();;
    Button button;
    ListView listView;
    TimeAdapter timeAdapter;
    Bitmap bitmap,bm,bmp;
    DataFileSource dataFileSource;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM  dd,yyyy HH:mm:ss EEE", Locale.ENGLISH);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button=findViewById(R.id.button_new);
        listView=findViewById(R.id.list_view);
        Init();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CreatNewActivity.class);
                startActivityForResult(intent,1);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        timeAdapter = new TimeAdapter(MainActivity.this, R.layout.time_item, timeItemList);
        listView.setAdapter(timeAdapter);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataFileSource.save();
    }

    private void Init(){

        dataFileSource=new DataFileSource(this);
        timeItemList=dataFileSource.load();
        if(timeItemList.size()==0) {
            try {
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.item_new );
                timeItemList.add(
                        new TimeItem("Birthday", sdf.parse("2019-12-03"), "lizi",bmp ));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnedTitle = data.getStringExtra("title");
                    String returnedDescription = data.getStringExtra("description");
                    String returnedDate=data.getStringExtra("date");


                    SimpleDateFormat sdFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    try {
                        date = sdFormat.parse(returnedDate);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            ImageBinder imageBinder = (ImageBinder) bundle.getBinder("bitmap");
                            bitmap = imageBinder.getBitmap();
                        }
                        if(bitmap==null)
                            getTimeItemList().add(0,new TimeItem(returnedTitle,date, returnedDescription,
                                bmp));
                        else
                        getTimeItemList().add(0,new TimeItem(returnedTitle,date, returnedDescription,
                                bitmap));
                    }


                    timeAdapter.notifyDataSetChanged();
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    int position=data.getIntExtra("position", 0);
                    timeItemList.remove(position);
                    timeAdapter.notifyDataSetChanged();
                }
                if (resultCode == RESULT_UPDATE) {
                int position = data.getIntExtra("position", 0);
                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                String date=data.getStringExtra("date");
                TimeItem timeItem=timeItemList.get(position);
                timeItem.setTitle(title);
                timeItem.setDescription(description);
                    try {
                        timeItem.setDate(simpleDateFormat.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timeAdapter.notifyDataSetChanged();
            }
                break;



            default:
        }

    }



    public ArrayList<TimeItem> getTimeItemList(){return timeItemList;}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
