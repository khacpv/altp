package com.example.gcs.faster5.ui.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.gcs.faster5.R;
import com.example.gcs.faster5.util.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kien on 07/25/2016. Get Location from JSON
 */
public class PopupLogin extends AppCompatActivity {
    EditText mEditTextLocation;
    TextView mTextViewLocation;
    private static String url = "http://209.58.180.196/json/"; //URL to get JSON Array
    private static final String TAG_CITY = "city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().hide();
        setContentView(R.layout.popup_login);
        strictMode();

        mEditTextLocation = (EditText) findViewById(R.id.edittext_location);
        mEditTextLocation.setEnabled(false);
        mTextViewLocation = (TextView) findViewById(R.id.text_location);

        Button btnDismiss = (Button) findViewById(R.id.button_close);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        setLayout();
        parserJSON();
    }

    public void parserJSON() {
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(url);
        try {
            String city = json.getString(TAG_CITY);
            mTextViewLocation.setText(city.toUpperCase());
            Log.e("CITYYYYYY", " " + city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .8));
    }

    public void strictMode(){
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}
