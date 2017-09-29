package com.zwh.weatherdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText nameCity = null;
    private Button search = null;
    private ImageView weather_now_icon = null;

    private TextView city = null;
    private TextView description = null;
    private TextView tempture = null;
    private TextView quality = null;
    private TextView wind = null;
    private TextView humidity = null;

    private LinearLayout hourForcast = null;
    private LinearLayout dayForcast = null;
    private LinearLayout comfortIndex = null;

    private ProgressDialog progressDialog = null;

    private ListView mHourListView = null;
    private ListView mDailyListView = null;

    private TextView comfortDesc = null;

    private static final String string_na = "NA";
    private static final String TAG = "zhangwenhao";

    private HashMap iconList = null;
    JSONObject nowData = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.UPDATE_WEATHER:

                    hideProgressDialog();

                    updateNowWeather();
                    updateHourWeather();
                    updateDailyWeather();
                    updateComfortDescription();
                    break;
                case Constants.UPDATE_ERROR:

                    hideProgressDialog();
                    showErrorMessage("Error occur while getting weather data, please retry!");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initIconList();
    }
    private void init(){
        nameCity = (EditText) findViewById(R.id.cityName);
        search = (Button) findViewById(R.id.search_now);
        weather_now_icon = (ImageView) findViewById(R.id.weather_icon_now);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameCity != null){
                    hideSoftKeyBoard();
                    String cityName = nameCity.getText().toString();
                    Log.e(TAG,"onclick " + cityName);
                    showProgressDialog("请求中","正在获取数据");
                    getJSON(cityName);
                }
            }
        });
        city = (TextView) findViewById(R.id.city);
        description = (TextView) findViewById(R.id.description);
        tempture = (TextView) findViewById(R.id.tempture);
        quality = (TextView) findViewById(R.id.quality);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);

        hourForcast = (LinearLayout) findViewById(R.id.hour_forcast);
        dayForcast = (LinearLayout) findViewById(R.id.day_forcast);
        comfortIndex = (LinearLayout) findViewById(R.id.comfort_index);


        mHourListView = (ListView) findViewById(R.id.hour_list);
        mDailyListView = (ListView) findViewById(R.id.daily_list);
        comfortDesc = (TextView) findViewById(R.id.comfort_desc);
        comfortDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
    }





    public void getJSON(final String city) {

        String url = "https://free-api.heweather.com/v5/weather?city="+city+"&key=f44ded18a48940e5a45f5cfdb566315b";
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        okHttpClient.newBuilder().connectTimeout(60, TimeUnit.SECONDS);
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"get weather data error ");

                Message msg = Message.obtain();
                msg.what = Constants.UPDATE_ERROR;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    Log.e(TAG,json);

                    nowData = new JSONObject(json);
                    if (nowData == null){
                        Log.e(TAG,"null");
                    }else {
                        Log.e(TAG,nowData.toString());
                    }
                    Message msg = Message.obtain();
                    msg.what = Constants.UPDATE_WEATHER;
                    mHandler.sendMessage(msg);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        call.enqueue(callback);

    }

    public void showErrorMessage(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }



    public void initIconList(){
        iconList = new HashMap();
        iconList.put("100",R.drawable.a100);
        iconList.put("101",R.drawable.a101);
        iconList.put("102",R.drawable.a102);
        iconList.put("103",R.drawable.a103);
        iconList.put("104",R.drawable.a104);
        iconList.put("200",R.drawable.a200);
        iconList.put("201",R.drawable.a201);
        iconList.put("202",R.drawable.a202);
        iconList.put("203",R.drawable.a203);
        iconList.put("204",R.drawable.a204);
        iconList.put("205",R.drawable.a205);
        iconList.put("206",R.drawable.a206);
        iconList.put("207",R.drawable.a207);
        iconList.put("208",R.drawable.a208);
        iconList.put("209",R.drawable.a209);
        iconList.put("210",R.drawable.a210);
        iconList.put("211",R.drawable.a211);
        iconList.put("212",R.drawable.a212);
        iconList.put("213",R.drawable.a213);
        iconList.put("300",R.drawable.a300);
        iconList.put("301",R.drawable.a301);
        iconList.put("302",R.drawable.a302);
        iconList.put("303",R.drawable.a303);
        iconList.put("304",R.drawable.a304);
        iconList.put("305",R.drawable.a305);
        iconList.put("306",R.drawable.a306);
        iconList.put("307",R.drawable.a307);
        iconList.put("308",R.drawable.a308);
        iconList.put("309",R.drawable.a309);
        iconList.put("310",R.drawable.a310);
        iconList.put("311",R.drawable.a311);
        iconList.put("312",R.drawable.a312);
        iconList.put("313",R.drawable.a313);
        iconList.put("400",R.drawable.a400);
        iconList.put("401",R.drawable.a401);
        iconList.put("402",R.drawable.a402);
        iconList.put("403",R.drawable.a403);
        iconList.put("404",R.drawable.a404);
        iconList.put("405",R.drawable.a405);
        iconList.put("406",R.drawable.a406);
        iconList.put("407",R.drawable.a407);
        iconList.put("500",R.drawable.a500);
        iconList.put("501",R.drawable.a501);
        iconList.put("502",R.drawable.a502);
        iconList.put("503",R.drawable.a503);
        iconList.put("504",R.drawable.a504);
        iconList.put("507",R.drawable.a507);
        iconList.put("508",R.drawable.a508);
        iconList.put("900",R.drawable.a900);
        iconList.put("901",R.drawable.a901);
        iconList.put("999",R.drawable.a999);
    }

    public boolean isStatusOk(JSONObject data){
        if (data != null){
            try {
                return "ok".equals(data.getJSONArray("HeWeather5").getJSONObject(0).getString("status"));
            }catch (JSONException e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public class NowWeatherData{
        public int iconId ;
        public String city ;
        public String description;
        public String tempture;
        public String quality;
        public String wind;
        public String humidity;

        public NowWeatherData(int iconId, String city, String description, String tempture, String quality, String wind, String humidity) {
            this.iconId = iconId;
            this.city = city;
            this.description = description;
            this.tempture = tempture;
            this.quality = quality;
            this.wind = wind;
            this.humidity = humidity;
        }


    }
    public class HourWeatherData{
        public int hour_icon;
        public String hour_time;
        public String hour_description;
        public String hour_tempture;
        public String hour_wind;

        public HourWeatherData(int hour_icon, String hour_time, String hour_description, String hour_tempture, String hour_wind) {
            this.hour_icon = hour_icon;
            this.hour_time = hour_time;
            this.hour_description = hour_description;
            this.hour_tempture = hour_tempture;
            this.hour_wind = hour_wind;
        }
    }
    public class DailyWeatherData{
        public String daily_time;
        public int daily_d_icon;
        public String daily_d_description;
        public String daily_tempture;
        public String daily_hum;

        public int daily_n_icon;
        public String daily_n_description;
        public String daily_wind;
        public String daily_sun;

        public DailyWeatherData(String daily_time, int daily_d_icon, String daily_d_description, String daily_tempture, String daily_hum, int daily_n_icon, String daily_n_description, String daily_wind, String daily_sun) {
            this.daily_time = daily_time;
            this.daily_d_icon = daily_d_icon;
            this.daily_d_description = daily_d_description;
            this.daily_tempture = daily_tempture;
            this.daily_hum = daily_hum;
            this.daily_n_icon = daily_n_icon;
            this.daily_n_description = daily_n_description;
            this.daily_wind = daily_wind;
            this.daily_sun = daily_sun;
        }
    }

    public NowWeatherData getNowWeatherData(){
        Log.e(TAG,"getNowWeatherData");
        if(nowData == null){
            Log.e(TAG,"nowData == null");
            return null;
        }
        try {


            JSONObject temp = nowData.getJSONArray("HeWeather5").getJSONObject(0);

            String city = temp.getJSONObject("basic").getString("city");

            String icon_string = temp.getJSONObject("now").getJSONObject("cond").getString("code");
            int iconId = Integer.parseInt((iconList.get(icon_string)).toString());

            String description = temp.getJSONObject("now").getJSONObject("cond").getString("txt");

            String tempture = temp.getJSONObject("now").getString("tmp") + "℃";
            String quality;
            if (temp.has("aqi")){
                quality = temp.getJSONObject("aqi").getJSONObject("city").getString("qlty");
            }else {
                quality = string_na;
            }


            String wind = temp.getJSONObject("now").getJSONObject("wind").getString("dir") +
                    " " + temp.getJSONObject("now").getJSONObject("wind").getString("sc");

            String humidity = temp.getJSONObject("now").getString("hum") + "%";

            NowWeatherData nowWeatherData = new NowWeatherData(iconId,city,description,tempture,quality,wind,humidity);
            return nowWeatherData;

        }catch(JSONException e){

            Log.e(TAG,"getNowWeatherData  JSONException" + e.getCause().getMessage());
            e.printStackTrace();
        }
        return null;

    }
    List<Map<String, Object>> getHourData(){
        if (nowData == null){
            return null;
        }
        List<HourWeatherData> hourWeatherDatas = getHourWeatherData();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (HourWeatherData hourWeatherData:hourWeatherDatas) {
            map = new HashMap<String, Object>();
            map.put("hour_icon", hourWeatherData.hour_icon);
            map.put("hour_time", hourWeatherData.hour_time);
            map.put("hour_description", hourWeatherData.hour_description);
            map.put("hour_tempture",hourWeatherData.hour_tempture);
            map.put("hour_wind",hourWeatherData.hour_wind);
            list.add(map);
        }
        return list;

    }
    List<Map<String, Object>> getDailyData(){
        if (nowData == null){
            return null;
        }
        List<DailyWeatherData> dailyWeatherDatas = getDailyWeatherData();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (DailyWeatherData dailyWeatherData:dailyWeatherDatas) {
            map = new HashMap<String, Object>();
            map.put("daily_time", dailyWeatherData.daily_time);
            map.put("daily_d_icon", dailyWeatherData.daily_d_icon);
            map.put("daily_d_description", dailyWeatherData.daily_d_description);
            map.put("daily_tempture",dailyWeatherData.daily_tempture);
            map.put("daily_hum",dailyWeatherData.daily_hum);
            map.put("daily_n_icon",dailyWeatherData.daily_n_icon);
            map.put("daily_n_description", dailyWeatherData.daily_n_description);
            map.put("daily_wind",dailyWeatherData.daily_wind);
            map.put("daily_sun",dailyWeatherData.daily_sun);
            list.add(map);
        }
        return list;
    }
    public List<HourWeatherData> getHourWeatherData(){
        List<HourWeatherData> hourWeatherDatas = new ArrayList<>();
        try {
            JSONArray hourly_data = nowData.getJSONArray("HeWeather5").getJSONObject(0).getJSONArray("hourly_forecast");
            int hourly_length = hourly_data.length();
            for (int i = 0; i < hourly_length; i++){
                JSONObject temp = hourly_data.getJSONObject(i);
                String icon_string = temp.getJSONObject("cond").getString("code");
                int hour_icon = Integer.parseInt((iconList.get(icon_string)).toString());
                String hour_time = temp.getString("date").split(" ")[1];
                String hour_description = temp.getJSONObject("cond").getString("txt");
                String hour_tempture = temp.getString("tmp") + "℃";
                String hour_wind = temp.getJSONObject("wind").getString("dir");
                HourWeatherData hourWeatherData = new HourWeatherData(hour_icon,hour_time,hour_description,hour_tempture,hour_wind);
                hourWeatherDatas.add(hourWeatherData);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return hourWeatherDatas;
    }
    public List<DailyWeatherData> getDailyWeatherData(){
        List<DailyWeatherData>  dailyWeatherDatas = new ArrayList<>();
        try {
            JSONArray daily_data = nowData.getJSONArray("HeWeather5").getJSONObject(0).getJSONArray("daily_forecast");
            int daily_length = daily_data.length();
            for (int i = 0; i < daily_length; i++){
                JSONObject temp = daily_data.getJSONObject(i);
                String daily_time = temp.getString("date");
                String icon_d_string = temp.getJSONObject("cond").getString("code_d");
                int daily_d_icon = Integer.parseInt((iconList.get(icon_d_string)).toString());

                String daily_d_description = temp.getJSONObject("cond").getString("txt_d");
                String daily_tempture = temp.getJSONObject("tmp").getString("max") + "℃"
                        + "/" + temp.getJSONObject("tmp").getString("min") + "℃";
                String daily_hum = temp.getString("hum") + "%";
                String icon_n_string = temp.getJSONObject("cond").getString("code_n");
                int daily_n_icon = Integer.parseInt((iconList.get(icon_n_string)).toString());

                String daily_n_description = temp.getJSONObject("cond").getString("txt_n");
                String daily_wind = temp.getJSONObject("wind").getString("dir");
                String daily_sun = temp.getJSONObject("astro").getString("sr") +
                        "/" +temp.getJSONObject("astro").getString("ss");
                DailyWeatherData dailyWeatherData =new DailyWeatherData(daily_time,daily_d_icon,daily_d_description,
                        daily_tempture,daily_hum,daily_n_icon,daily_n_description,daily_wind,daily_sun);
                dailyWeatherDatas.add(dailyWeatherData);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return dailyWeatherDatas;
    }
    public String getComfortDescription(){
        try {
            String desc = nowData.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("suggestion").getJSONObject("comf").getString("brf") +
                    ":" +nowData.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("suggestion").getJSONObject("comf").getString("txt");
            return desc;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return "no suggestion";
    }

    public void updateNowWeather(){
        Log.e(TAG,"updateWeather");
        if (nowData == null){
            Log.e(TAG,"nul;ss");
            return;
        }
        NowWeatherData nowWeatherData = getNowWeatherData();
        weather_now_icon.setImageResource(nowWeatherData.iconId);
        city.setText(nowWeatherData.city);
        description.setText(nowWeatherData.description);
        tempture.setText(nowWeatherData.tempture);
        quality.setText(nowWeatherData.quality);
        wind.setText(nowWeatherData.wind);
        humidity.setText(nowWeatherData.humidity);
    }
    public void updateHourWeather(){
        Log.e(TAG,"updateHourWeather");
        if (nowData == null){
            Log.e(TAG,"nul;ss");
            return;
        }
        hourForcast.setVisibility(View.VISIBLE);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,getHourData(),R.layout.hour_weather_item,
                new String[]{"hour_icon", "hour_time", "hour_description", "hour_tempture","hour_wind"},
                new int[]{R.id.hour_icon, R.id.hour_time, R.id.hour_description, R.id.hour_tempture, R.id.hour_wind});
        mHourListView.setAdapter(simpleAdapter);
    }
    public void updateDailyWeather(){
        Log.e(TAG,"updateDailyWeather");
        if (nowData == null){
            Log.e(TAG,"nul;ss");
            return;
        }
        dayForcast.setVisibility(View.VISIBLE);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,getDailyData(),R.layout.daily_weather_item,
                new String[]{"daily_time","daily_d_icon","daily_d_description","daily_tempture","daily_hum","daily_n_icon","daily_n_description","daily_wind","daily_sun"},
                new int[]{R.id.daily_time,R.id.daily_d_icon,R.id.daily_d_description,R.id.daily_tempture,R.id.daily_hum,R.id.daily_n_icon,R.id.daily_n_description,R.id.daily_wind,R.id.daily_sun});
        mDailyListView.setAdapter(simpleAdapter);
    }
    public void updateComfortDescription(){
        comfortIndex.setVisibility(View.VISIBLE);
        comfortDesc.setText(getComfortDescription());
    }

    public void showProgressDialog(String title, String message) {

        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(MainActivity.this, title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();

    }
    public void hideProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void hideSoftKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            imm.hideSoftInputFromWindow(nameCity.getWindowToken(),0);
        }
    }

}
