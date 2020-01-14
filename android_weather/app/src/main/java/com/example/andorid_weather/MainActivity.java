package com.example.andorid_weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int days=1;                                  //count days
    List<Float> list= new ArrayList();          // use to store next 5 days's temp value
    private TextView displaystddev_result;
    private LinearLayout dyanamic_created_clound;   //linear clound id
    private TextView next5daysweatherdisplay;
    private TextView    currentWeatherdisplay;
    private RequestQueue mQueue;
    private ImageView cloud;
    private ImageView next5daysclound;
    private boolean ifclick=true;   //only can click once
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cloud = (ImageView)findViewById(R.id.ifcloud);
        cloud.setVisibility(View.GONE);                 //set cloud image not displaying in the begining.
        next5daysclound = (ImageView)findViewById(R.id.next5dayscloud);
        next5daysclound.setVisibility(View.GONE);                 //set cloud image not displaying in the begining.

        dyanamic_created_clound=(LinearLayout) findViewById(R.id. dyanamic_created_clound);
        displaystddev_result=(TextView)findViewById(R.id. stddev_result);
        next5daysweatherdisplay=(TextView)findViewById(R.id. next5daysweather_textview);
        currentWeatherdisplay=(TextView)findViewById(R.id. current_weather_textview);

        mQueue= Volley.newRequestQueue(this);
        currentweatherfunction(); //run get current weather function;
        next5daysweatherfunction();//run get next 5 days weather function;
        checkNetIsConnet();   // run check if app connceted to integer.
        calculate();       //run stddev calculation

    }
    private void currentweatherfunction(){               //current weather
        Button buttonParse = (Button) findViewById(R.id.get_current_weather);
        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url="https://twitter-code-challenge.s3.amazonaws.com/current.json";          // current json
                JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, url,null,
                        new Response.Listener<JSONObject>() {               /// if called successful
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject jsonObjectWind = response.getJSONObject("wind");           //instace wind
                                    JSONObject jsonObjectTemperature = response.getJSONObject("weather"); //instace weather
                                    JSONObject jsonObjectcloudiness= response.getJSONObject("clouds"); //instace clouds
                                    float cloudiness= Float.parseFloat(jsonObjectcloudiness.getString("cloudiness")); //get value of object
                                    if(cloudiness>50){              //if cloudiness greater than 50 then display it.
                                        if ( cloud.getVisibility() == View.GONE  ){
                                            cloud.setVisibility(View.VISIBLE);
                                        }
                                        else                        // otherwise, keep hidden.
                                            cloud.setVisibility(View.GONE);
                                    }

                                    float celsius=Float.parseFloat (jsonObjectTemperature.getString("temp"));
                                    // display in textview
                                    currentWeatherdisplay.append("Weather :\nTemp: "+celsius+" °C"+
                                            "/"+TemperatureConverter.celsiusToFahrenheit(celsius)+" °F,\n"+"Wind speed : "+jsonObjectWind.getString("speed")+
                                            ",\n"+"cloudiness: "+cloudiness+".\n");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {               // if called error
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                mQueue.add(request);

            }
        });
    }

    private void next5daysweatherfunction(){            //next 5 days weather
        Button button=(Button)findViewById(R.id.Next_5days_weather);
        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
//                next5daysweatherdisplay.setText(getString(R.string.temperature, 34f, TemperatureConverter.celsiusToFahrenheit(100)));
                if(ifclick) {
                    for (int i = 1; i <= 5; i++) {                                      //get next 5 days url
                        StringBuilder urll = new StringBuilder();
                        urll.append("https://twitter-code-challenge.s3.amazonaws.com/future_1.json");           //replace int of "future_1" by i
                        String future = "future_";
                        int indexoffuture = urll.indexOf(future);
                        urll.replace(indexoffuture + future.length(), indexoffuture + future.length() + 1, Integer.toString(i));
                        String url = urll.toString();

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {               /// if called successful
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONObject jsonObjectWind = response.getJSONObject("wind"); //instace wind
                                            JSONObject jsonObjectTemperature = response.getJSONObject("weather"); //instace weather
                                            JSONObject jsonObjectcloudiness = response.getJSONObject("clouds");//instace clouds
                                            float cloudiness = Float.parseFloat(jsonObjectcloudiness.getString("cloudiness"));
//                                    if(cloudiness>50){              //if cloudiness greater than 50 then display it.
//
//                                                ImageView imageView=new ImageView(this);
//                                        }

                                            float wind = Float.parseFloat(jsonObjectWind.getString("speed"));
                                            float celsius = Float.parseFloat(jsonObjectTemperature.getString("temp"));
                                            addcloudimage(cloudiness,celsius,wind);      //run add image function
                                            list.add(celsius);
                                            // display in textview
//                                            next5daysweatherdisplay.append("Next " + days + "days weather :\nTemp: " + celsius + " °C" +
//                                                    "/" + TemperatureConverter.celsiusToFahrenheit(celsius) + " °F,\n" + "Wind speed : " + jsonObjectWind.getString("speed")
//                                                    + ",\n" + "cloudiness: " + cloudiness + ".\n" + "\n");
                                            days++;

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {               // if called error
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        mQueue.add(request);

                    }
                    ifclick=false;
                }


            }
        });


    }
    public void addcloudimage(float cloudiness, float celsius, float wind){
//        float addcloudiness=cloudiness;

        TextView textView=new TextView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
//        textView.setLayoutParams(layoutParams);
//        dyanamic_created_clound.addView(textView);
        ImageView imageView= new ImageView(this);
        imageView.setLayoutParams(layoutParams);
//        imageView.set
        imageView.setImageResource(R.drawable.raining);
//        SpannableStringBuilder ssb = new SpannableStringBuilder(" Hello world!");
//        ssb.setSpan(new ImageSpan(context, R.drawable.image), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        tv_text.setText(ssb, TextView.BufferType.SPANNABLE);

        textView.append("Next " + days + "days weather :\nTemp: " + celsius + " °C" +
                "/" + TemperatureConverter.celsiusToFahrenheit(celsius) + " °F,\n" + "Wind speed : " + wind
                + ",\n" + "cloudiness: " + cloudiness + ".\n" + "\n");
//        dyanamic_created_clound.addView(imageView);
//        TextView textView2 = (TextView)findViewById( R.id.next5daysweather_textview );
        Spannable spannable = (Spannable)textView.getText();                    // in order to make image and text in the same line
        StyleSpan boldSpan = new StyleSpan( Typeface.BOLD );
        spannable.setSpan( boldSpan, 41, 52, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        SpannableStringBuilder ssb = new SpannableStringBuilder("Next " + days + "days weather :\nTemp: " + celsius + " °C" +
                "/" + TemperatureConverter.celsiusToFahrenheit(celsius) + " °F,\n" + "Wind speed : " + wind
                + ",\n" + "cloudiness: " + cloudiness + ".\n" + "\n" );
        Bitmap smiley = BitmapFactory.decodeResource( getResources(), R.drawable.raining );
        ssb.setSpan( new ImageSpan( smiley ), 16, 17, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        textView.setText( ssb, TextView.BufferType.SPANNABLE );
        dyanamic_created_clound.addView(textView);


//        if(cloudiness>=50){             //if greater than 50% then display cloud images
//
//
//            textView.append("Next " + days + "days weather :\nTemp: " + celsius + " °C" +
//                    "/" + TemperatureConverter.celsiusToFahrenheit(celsius) + " °F,\n" + "Wind speed : " + wind
//                    + ",\n" + "cloudiness: " + cloudiness + ".\n" + "\n");
//            dyanamic_created_clound.addView(imageView);
////
//        }else{
//            textView.append("Next " + days + "days weather :\nTemp: " + celsius + " °C" +
//                    "/" + TemperatureConverter.celsiusToFahrenheit(celsius) + " °F,\n" + "Wind speed : " + wind
//                    + ",\n" + "cloudiness: " + cloudiness + ".\n" + "\n");
//        }


    }
    private void calculate() {      //calculate stddev

        Button displayStddev = (Button) findViewById(R.id.displayStddev);
        displayStddev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double sum = 0;
                double average=0;
                double size=list.size();
                double total = 0;
                double standardDeviation=0;
                //test
//                int[] array = {1, 2, 3, 4};
//                 sum = 0;
//                for (int i = 0; i < array.length; i++) {
//                    sum += array[i];      // sum
//                }
//                System.out.println(sum);
//
//               average = sum / array.length;  // calculate average
//                System.out.println(average);
//                total = 0;
//                for (int i = 0; i < array.length; i++) {
//                    total += (array[i] - average) * (array[i] - average);   //get sde
//                }
//                 standardDeviation = Math.sqrt(total / (array.length - 1));   //get sddev
//                System.out.println(standardDeviation);    //32.55764119219941
//
//                displaystddev_result.setText(Double.toString(standardDeviation));

                for (int i = 0; i < size; i++) {
                    sum=sum+list.get(i);

                }
                average = sum / size;  // calculate average
                for (int i = 0; i < size; i++) {
                    total += (list.get(i)- average) * (list.get(i) - average);   //get sde
                }
                standardDeviation = Math.sqrt(total / (size- 1));   //get sddev
                System.out.println(standardDeviation);

                displaystddev_result.setText("next 5 days SD of the temperature: "+String.format("%.3f",standardDeviation));      //Three decimal places


            }


        });
    }


    private boolean checkNetIsConnet() {            //check if connect
        final TextView temperatureView = (TextView) findViewById(R.id.ifconnected);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {       //connected
            System.out.println("Device has connected to internet");
            temperatureView.setText("current_temperature         Device has connected to internet");
            return true;
        } else {            //disconnected
            temperatureView.setText("current_temperature         Device has disconnected to internet");
            System.out.println("current_temperature         Device has disconnected to internet");
            return false;
        }
    }


}
