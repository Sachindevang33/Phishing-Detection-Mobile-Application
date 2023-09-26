package com.example.phishdetector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ResultActivity extends AppCompatActivity {
    String host, input, ans;
    TextView t1, t2, t3;
    Button check;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        check = findViewById(R.id.button);
        host = getIntent().getStringExtra("host");
        input = getIntent().getStringExtra("input");
        ans = getIntent().getStringExtra("ans");
        try {
            URI url = new URI(input);
            t1.setText("Status: " + ans + "\n\n" + "Your Input: " + input);
            t2.setText("Host Name: " + host);
           // t3.setText("Host Name: "+host);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread.start();
                check.setEnabled(false);


            }
        });
    }


    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
                try {
                    URL obj1 = new URL(input); //URL Connection Created...
                    HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection(); //Http URL Connection Created...
                    String conn = con1.getResponseMessage();
                    con1.disconnect();
                    t3.setText("Connection Status: " + conn);
                    thread.interrupt();
                    if(ans.equals("Phishing")) {

                        t2.append(" \n \nThe given URL is a potential phishing website, please don't access the link.");

                    }
                    else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(input));
                        startActivity(intent);
                    }
                  //  t3.setText(ans);


                }
                catch (Exception e) {
                    String conn = e.getLocalizedMessage();
                    t3.setText("Connection Status: "+conn);
                    thread.interrupt();
                    if(ans.equals("Phishing")) {

                        t2.append(" \n \nThe given URL is a potential phishing website, please don't access the link.");

                    }
                }


        }
    });


    /*private String isConnectedToServer(String url, int timeout) {

        try {
            String link_url = "https://www.facebook.com";
            URL obj1 = new URL(link_url); //URL Connection Created...
            HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection(); //Http URL Connection Created...
            String conn = con1.getResponseMessage();
            con1.disconnect();
            return conn ;
        }
        catch (Exception e) {
            String conn = String.valueOf(e);
            return conn;
        }

     */
            /*try{
                URL myUrl = new URL("https://www.google.co.uk");
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(timeout);
                connection.connect();
                return true;
            } catch (Exception e) {
                return false;
            }*/

    }

