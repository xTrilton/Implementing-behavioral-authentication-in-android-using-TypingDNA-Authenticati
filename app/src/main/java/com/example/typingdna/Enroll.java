package com.example.typingdna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Enroll extends AppCompatActivity  {

    private TypingDNARecorderMobile tdna;
    private EditText musername;
    String tp;
    String id;
    private EditText password;
    String Key= "ae677f305017f0939347eced4d2b085d";
    String Secret= "a36f01786ce7344cd617c0b5af8fea35";
    String originalString = Key+":"+Secret;
    String encodedString;
    int clickcount=0;
    String addpattern ="Please add two more patterns to get enrolled";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText password1 = findViewById(R.id.password1);
        musername = findViewById(R.id.username1);


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
        tdna.addTarget(R.id.password1);
        tdna.addTarget(R.id.username1);


        Button btnregister = (Button) findViewById(R.id.btnregister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //call the authenticate method to start the authentication process
                Authenticate();


                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Enroll.this);
                builder.setTitle("Adding Typing pattern");
                builder.setMessage(addpattern);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                tdna.reset();

                                musername.setText("");
                                password1.setText("");
                                musername.requestFocus();
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
        String identity=musername.getText().toString();
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
                    String enrollment= myJsonObject.getString("enrollment");


                    if(enrollment.equals("1")){
                        Toast.makeText(getApplicationContext(),"it worked",Toast.LENGTH_LONG).show();
                        clickcount=clickcount+1;

                        if(clickcount==1){

                            addpattern="Please add one more pattern, to get enrolled";
                        }
                        if (clickcount==2){

                            addpattern="Done";
                        }

                        if(clickcount==3){

                            Intent intent= new Intent(Enroll.this,MainActivity.class);
                            intent.putExtra("message_key", "successful enrollment");
                            startActivity(intent);

                        }


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