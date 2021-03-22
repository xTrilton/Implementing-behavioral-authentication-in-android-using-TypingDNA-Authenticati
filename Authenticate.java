package com.example.typingdna;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.typingdna.typingdnarecorderandroid.TypingDNARecorderMobile;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Authenticate extends AppCompatActivity {
    String typingpattern;
    private RequestQueue mRequestQueue;
    private TypingDNARecorderMobile tdna;
    private EditText password;
    private EditText username;
    private Button btnregister;
    String tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes the typingDNA recorder and starts recording.
        tdna = new TypingDNARecorderMobile(this);
        tdna.start();

        password = findViewById(R.id.password);
        username = findViewById(R.id.username);

        //Adds a target to the recorder. You can add multiple elements. All the typing evens will be recorded for this component.
        tdna.addTarget(R.id.password);

        btnregister = (Button) findViewById(R.id.btnregister);

        btnregister.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v){

                                              //sendAndRequestResponse();

                                          }
                                      }

        );
    }
    @Override
    protected void onDestroy() {
        //Stops the overlay service and ends the recording of further typing events.
        tdna.stop();
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        //Stops the overlay service and ends the recording of further typing events.
        tdna.pause();
        super.onPause();
    }

    @Override
    protected void onStop(){
        //Stops the overlay service and ends the recording of further typing events.
        tdna.stop();
        super.onStop();
    }

    @Override
    protected void onResume(){
        //Starts recording the typing evens and also starts the overlay service.
        tdna.start();
        super.onResume();
    }

    public void reset(View view) {
        //Resets the history stack of recorded typing events.
        tdna.reset();

        //resultOutput.setText("");
        //textField.setText("");
        //textField.requestFocus();
    }

    public void getTypingPattern(View view){
        int type = 1; // 1,2 for diagram pattern (short identical texts - 2 for extended diagram), 0 for any-text typing pattern (random text)
        switch (view.getId()) {
            case (R.id.getType1):
                type = 1;
                break;
            case (R.id.getType2):
                type = 2;
                break;
            case (R.id.getType0):
                type = 0;
                break;
        }
        int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
        String text = password.getText().toString(); // (Only for type 1 and type 2) a typed string that you want the typing pattern for
        int textId = 0; // (Optional, only for type 1 and type 2) a personalized id for the typed text, 0 = ignore
        boolean caseSensitive = false; // (Optional, only for type 1 and type 2) Used only if you pass a text for type 1
        Integer targetId = password.getId(); //(Optional, only for type 1 and type 2) Specifies if pattern is obtain only from text typed in a certain target
        tp = tdna.getTypingPattern(type, length, text, textId, targetId, caseSensitive);
        if(tp != null){
           // resultOutput.setText(tp);
        } else {
          //  resultOutput.setText("");
        }
    }

        public  void savePattern (){

            String  autoendpoint="";
            String id= String.valueOf(username.getText()); //**
            String postUrl = "https://api.typingdna.com/auto/" +id;
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject postData = new JSONObject();
            try {
                postData.put("typingpattern", tp );

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace(); 
                }
            });

            requestQueue.add(jsonObjectRequest);

        }


        public void verifyPattern(){
            String url = "https://api.typingdna.com/user/:id?type={type}&textid={textid} ";
            List<String> jsonResponses = new ArrayList<>();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String email = jsonObject.getString("email");

                            jsonResponses.add(email);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            requestQueue.add(jsonObjectRequest);


        }

}
