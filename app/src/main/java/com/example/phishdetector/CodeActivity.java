package com.example.phishdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeActivity extends AppCompatActivity {
    EditText e1;
    InputStream inputStream1, inputStream2;
    String[] data;
    FirebaseAuth mAuth;
    Button result, btnLogOut;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        e1 = findViewById(R.id.url);
        boolean matchfound;
        result = findViewById(R.id.submit);
        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(CodeActivity.this, MainActivity.class));
        });
    }

    public void submit(View view) throws URISyntaxException, IOException {
        String input = e1.getText().toString();
        String ans = null;
        input = input.trim();
        if (input.length() == 0 | input.contains(" ")) {
            Toast.makeText(this, "Enter correct URL to proceed", Toast.LENGTH_LONG).show();
        } else {
            boolean output = Patterns.WEB_URL.matcher(input).matches();
            if (output == false) {
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_LONG).show();
            } else {
                URI url = urigenerator(input);
                input = url.toString();
                String host = url.getHost();

                try {
                    inputStream1 = getResources().openRawResource(R.raw.legitimate);
                    inputStream2 = getResources().openRawResource(R.raw.phishing);
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream1));
                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
                    String csvLine1;
                    String csvLine2;
                    boolean matchfound = false;
                    while ((csvLine1 = reader1.readLine()) != null) {
                        data = csvLine1.split(",");
                        try {
                            String regex = data[0].toString();
                            Pattern pattern = Pattern.compile(host, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(regex);
                            matchfound = matcher.find();
                            if (matchfound) {
                                ans = "Legitimate";
                                break;
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    if (!matchfound) {
                        while ((csvLine2 = reader2.readLine()) != null) {
                            data = csvLine2.split(",");
                            try {
                                String regex = data[0].toString();
                                Pattern pattern = Pattern.compile(host, Pattern.CASE_INSENSITIVE);
                                Matcher matcher = pattern.matcher(regex);
                                matchfound = matcher.find();
                                if (matchfound) {
                                    ans = "Phishing";
                                    break;
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    }
                    if (!matchfound) {
                        ans = "Not Sure";
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Error in reading CSV file:" + ex);
                }
                Intent intent = new Intent(CodeActivity.this, ResultActivity.class);
                intent.putExtra("host", host);
                intent.putExtra("input", input);
                intent.putExtra("ans", ans);
                startActivity(intent);
            }

        }
    }


    private URI urigenerator(String input) throws URISyntaxException {
        if (input.startsWith("http:/") | input.startsWith("https:/")) {
            if (!(input.contains("http://") | input.contains("https://"))) {
                input = input.replaceAll("http:/", "http://");
                input = input.replaceAll("https:/", "https://");
            }
        }
        else {
            input = "https://" + input;
        }
        URI url = new URI(input);
        return url;
    }
}