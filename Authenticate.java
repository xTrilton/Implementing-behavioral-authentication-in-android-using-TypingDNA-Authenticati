package com.example.typingdna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Authenticate extends AppCompatActivity {
    String typingpattern;
    private RequestQueue mRequestQueue;
    private TypingDNARecorderMobile tdna;
    private EditText password;
    private EditText username;
    private Button btnregister;
    String tp="0,3.1,0,0,53,41345015,0,-1,-1,0,-1,-1,0,-1,-1,2,321,108,2,207,55,1,0,0,1,1,1,902248182,1,1,0,0,0,1,1366,768,2,0,89,0,1947159449|3413,24|25,23|10,12|6,23|8,26|6,20|4,17|6,11|5,6|11,112|75,86|4,57|134,161|15,136|6,59|14,232|36,127|75,86|68,42|9,98|60,46|22,114|30,150|4,17|110,98|4,61|127,71|5,100|116,162|15,165|6,78|6,139|75,117|84,126|78,110|36,81|52,113|5,120|37,74|57,61|37,34|20,138|6,89|55,89|41,114|37,74|106,275|4,54|37,69|5,104|92,105|72,91|61,107";
    String id;
    String Key= "ae677f305017f0939347eced4d2b085d";
    String Secret= "a36f01786ce7344cd617c0b5af8fea35";
    String originalString = Key+":"+Secret;
    String encodedString;
    TextView showtxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showtxt=findViewById(R.id.showtxt);

        Base64.Encoder encoder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoder = Base64.getEncoder();
            encodedString = encoder.encodeToString(originalString.getBytes());

            System.out.println(encodedString);
        }


        //Initializes the typingDNA recorder and starts recording.
        tdna = new TypingDNARecorderMobile(this);
        tdna.start();

        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
         String identity=username.getText().toString();
         id=md5(identity);
       // showtxt.setText(id);

        Toast toast = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT);
        toast.setMargin(50, 50);
        toast.show();
        //Adds a target to the recorder. You can add multiple elements. All the typing evens will be recorded for this component.
        tdna.addTarget(R.id.password);

        btnregister = (Button) findViewById(R.id.btnregister);

        btnregister.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v){
                                             Authenticate();


                                          }
                                      }

        );
    }



    public void getTypingPattern(View view){
        int type = 1; // 1,2 for diagram pattern (short identical texts - 2 for extended diagram), 0 for any-text typing pattern (random text)


        int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
        String text = password.getText().toString(); // (Only for type 1 and type 2) a typed string that you want the typing pattern for
        int textId = 0; // (Optional, only for type 1 and type 2) a personalized id for the typed text, 0 = ignore
        boolean caseSensitive = false; // (Optional, only for type 1 and type 2) Used only if you pass a text for type 1
        Integer targetId = password.getId(); //(Optional, only for type 1 and type 2) Specifies if pattern is obtain only from text typed in a certain target
        tp = tdna.getTypingPattern(type, length, text, textId, targetId, caseSensitive);
        if(tp != null){
            showtxt.setText(tp);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "no typing pattern found", Toast.LENGTH_SHORT);
            toast.setMargin(50, 50);
            toast.show();
        }
    }

        public  void Authenticate (){
        //first call the getTypingPattern method to start record the users password before enrolling or verifying
            int type = 1; // 1,2 for diagram pattern (short identical texts - 2 for extended diagram), 0 for any-text typing pattern (random text)


            int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
            String text = password.getText().toString(); // (Only for type 1 and type 2) a typed string that you want the typing pattern for
            int textId = 0; // (Optional, only for type 1 and type 2) a personalized id for the typed text, 0 = ignore
            boolean caseSensitive = false; // (Optional, only for type 1 and type 2) Used only if you pass a text for type 1
            Integer targetId = password.getId(); //(Optional, only for type 1 and type 2) Specifies if pattern is obtain only from text typed in a certain target
            String typo = tdna.getTypingPattern(type, length, text, textId, targetId, caseSensitive);
            if(typo != null){
                showtxt.setText(typo);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "no typing pattern found", Toast.LENGTH_SHORT);
                toast.setMargin(50, 50);
                toast.show();
            }


            String posturli = "https://api.typingdna.com/auto/boemosessful";
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject postData = new JSONObject();
            try {
                postData.put("id", "boemosessful");
                postData.put("tp", typo);



            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, posturli, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }){@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization",  "Basic YWU2NzdmMzA1MDE3ZjA5MzkzNDdlY2VkNGQyYjA4NWQ6YTM2ZjAxNzg2Y2U3MzQ0Y2Q2MTdjMGI1YWY4ZmVhMzU=");
                return headers;
            }
            };
            requestQueue.add(jsonObjectRequest);

        }
        public  void checkUser (){
        //first call the getTypingPattern method to start record the users password before enrolling or verifying

            String posturl = "https://api.typingdna.com/user/hellothere";
            List<String> jsonResponses = new ArrayList<>();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, posturl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                         //  showtxt.setText(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }){@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
               headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization",  "Basic YWU2NzdmMzA1MDE3ZjA5MzkzNDdlY2VkNGQyYjA4NWQ6YTM2ZjAxNzg2Y2U3MzQ0Y2Q2MTdjMGI1YWY4ZmVhMzU=");
                return headers;
            }
            };

            requestQueue.add(jsonObjectRequest);


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
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
