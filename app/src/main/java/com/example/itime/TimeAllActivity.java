package com.example.itime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.itime.MainActivity.RESULT_UPDATE;

public class TimeAllActivity extends AppCompatActivity {
    Button buttonReturn;
    Button buttonDelete;
    Button buttonShare;
    Button buttonUpdate;
    Context context=this;
    Bitmap bitmap,bm;
    TextView titleTextView2,descriptionTextView2;
    TextView datetextview,counttextview;
    ImageView imageView;
    private MyCount mc1,mc2;
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM  dd,yyyy HH:mm:ss EEE", Locale.ENGLISH);
    SimpleDateFormat sdFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    int count;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 3:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", 0);
                    String title = data.getStringExtra("title");
                    String description = data.getStringExtra("description");
                    String date=data.getStringExtra("date");
                    Date date3 = new Date();
                    Date date2=new Date(System.currentTimeMillis());
                    try {
                     date3=sdFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    titleTextView2.setText(title);
                    descriptionTextView2.setText(description);
                    datetextview.setText(simpleDateFormat2.format(date3));

                    int count2=0;
                    count2 = TimeItem.getGapCount(date3, date2);
                    mc1.cancel();
                    mc2 = new MyCount(count2*1000, 1000);

                    mc2.start();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_all);

        buttonReturn=findViewById(R.id.button_return);
        buttonDelete=findViewById(R.id.button_delete);
        buttonShare=findViewById(R.id.button_share);
        buttonUpdate=findViewById(R.id.button_update);
        titleTextView2=findViewById(R.id.title_text_view2);
        descriptionTextView2=findViewById(R.id.description_text_view2);
        datetextview=findViewById(R.id.data_text_view);
        counttextview=findViewById(R.id.count_text_view);
        imageView=findViewById(R.id.imageView);

        final Intent intent=getIntent();
        final String title=intent.getStringExtra("Title");
        String date=intent.getStringExtra("Date");
        final String description=intent.getStringExtra("Description");
        final int position=intent.getIntExtra("position", 0);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ImageBinder imageBinder = (ImageBinder) bundle.getBinder("bitmap");
                bitmap = imageBinder.getBitmap();
                imageView.setImageBitmap(bitmap);
            }
        }
        //final int count=intent.getIntExtra("count", 0);
        Date date1=new Date(System.currentTimeMillis());
        int count=0;
        try {
            count = TimeItem.getGapCount(simpleDateFormat2.parse(date), date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        titleTextView2.setText(title);
        descriptionTextView2.setText(description);
        datetextview.setText(date);

        mc1 = new MyCount(count*1000, 1000);

        mc1.start();
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent();
                intent.putExtra("title", titleTextView2.getText().toString().trim());
                intent.putExtra("description", descriptionTextView2.getText().toString().trim());
                intent.putExtra("position", position);
                intent.putExtra("date", datetextview.getText().toString().trim());
                setResult(RESULT_UPDATE, intent);
                finish();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(context)
                        .setMessage("Delete this moment?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent1=new Intent();
                                intent1.putExtra("position", position);
                                setResult(RESULT_OK, intent1);
                                TimeAllActivity.this.finish();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(TimeAllActivity.this,CreatNewActivity.class);
                intent2.putExtra("title", title);
                intent2.putExtra("description", description);
                intent2.putExtra("date",datetextview.getText().toString() );
                Bundle bundle = new Bundle();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    bundle.putBinder("bitmap2", new ImageBinder(bitmap));
                }
                intent2.putExtras(bundle);
                startActivityForResult(intent2,3);
            }
        });


    }

    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override

        public void onFinish() {
            counttextview.setText("finish");
        }
        @Override
        public void onTick(long millisUntilFinished) {
            long days=millisUntilFinished / (1000 * 60 * 60 * 24 );
            long hours=millisUntilFinished / (1000*60*60) -days*24;
            long minutes=millisUntilFinished / (1000*60)-days*24*60-hours*60;
            long seconds=millisUntilFinished / (1000)-days*24*60*60-hours*60*60-minutes*60;
            counttextview.setText( days+ " DAYS "+
                    +hours + " HRS "+minutes+" MNIS "+seconds+" SECS");

            }
    }

}
