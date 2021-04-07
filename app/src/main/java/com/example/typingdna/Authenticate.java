package com.example.typingdna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.typingdna.typingdnarecorderandroid.TypingDNARecorderMobile;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Authenticate extends AppCompatActivity  {

    private TypingDNARecorderMobile tdna;
    private EditText username;
    String tp;
    String id;
    String Key= "xxxxxxx85x";
    String Secret= "xxxxxcex";
    String originalString = Key+":"+Secret;
    String encodedString;
    int clickcount=0;
    String addmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText password = findViewById(R.id.password);
        username = findViewById(R.id.username);


//encoding the apikey and api secret to  base64
        Base64.Encoder encoder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoder = Base64.getEncoder();
            encodedString = encoder.encodeToString(originalString.getBytes());
            System.out.println(encodedString);
        }


        //Initializes the typingDNA recorder and starts recording.
        tdna = new TypingDNARecorderMobile(this);
        tdna.start();

        //Adds a target to the recorder. You can add multiple elements. All the typing evens will be recorded for this component.
        tdna.addTarget(R.id.password);
        tdna.addTarget(R.id.username);


        Button btnregister = (Button) findViewById(R.id.btnregister);

        btnregister.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v){
                                              clickcount=clickcount+1;
                                              switch (clickcount) {
                                                    case 1:
                                                      addmore="please add two more patterns to attain a successful authentication";
                                                      break;

                                                      case 2:
                                                          addmore="please add one more patterns to attain a successful authentication";
                                                          break;
                                                      case 3:
                                                      addmore="successfully enrolled, please login";
                                                      break;
                                                      case 4:
                                                          addmore="successful login";
                                              }


                                              //call the authenticate method to start the authentication process
                                             Authenticate();

                                              //guiding the user

                                                      AlertDialog.Builder builder = new AlertDialog.Builder(
                                                              Authenticate.this);
                                                      builder.setTitle("Adding Typing pattern");
                                                      builder.setMessage(addmore);
                                                      builder.setPositiveButton("OK",
                                                              new DialogInterface.OnClickListener() {
                                                                  public void onClick(DialogInterface dialog,
                                                                                      int which) {
                                                                      Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
                                                                  }
                                                              });
                                                      builder.show();
                                                  }
                                              });

    }



        public  void Authenticate (){
//  getting the users typing pattern.
            String text ="";
            tp = tdna.getTypingPattern(0, 0, text,0);

            //hashing the users ID
            String identity=username.getText().toString();
            id=md5(identity);
//sending a post request.
            String posturli = "https://api.typingdna.com/auto/"+id;
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject postData = new JSONObject();
            try {
                postData.put("id", id);
                postData.put("tp", tp);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, posturli, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    try {
                        JSONObject myJsonObject = new JSONObject(response.toString());
                        String result = myJsonObject.getString("result");

// during  verification, the matching machine compares the typing pattern that the user provided with the one that is registered and if
// they match the user successfully logs in.
                        if(result.equals("1")){
                            Intent intent= new Intent(Authenticate.this,MainActivity.class);
                            startActivity(intent);
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
            }){@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization",  "Basic " +encodedString);
                return headers;
            }
            };
            requestQueue.add(jsonObjectRequest);

        }
      public  void checkUser (){
        //first call the getTypingPattern method to start record the users password before enrolling or verifying

            String posturl = "https://api.typingdna.com/user/"+id;
            List<String> jsonResponses = new ArrayList<>();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, posturl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    try {
                        JSONObject myJsonObject = new JSONObject(response.toString());
                        String message = myJsonObject.getString("message");
                        String  success= myJsonObject.getString("success");
                        String mobilecount = myJsonObject.getString("mobilecount");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
