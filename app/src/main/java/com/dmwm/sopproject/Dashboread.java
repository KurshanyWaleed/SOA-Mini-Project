package com.dmwm.sopproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLOutput;

public class Dashboread extends AppCompatActivity {
    ActionBar actionBar;
    Button syncBtn;
    SQLiteDatabase db;
    String output2;
    String SqliteIdTest;
    //private RequestQueue mqueue;

    String localHost = "192.168.1.18";
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dachbord);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        syncBtn = findViewById(R.id.buttonSync);




        progressDialog = new ProgressDialog(this);
        db = openOrCreateDatabase("gem", MODE_PRIVATE, null);

        db.execSQL("create table IF NOT EXISTS clients (ID INTEGER PRIMARY KEY ,nom TEXT,adresse TEXT,tel TEXT,fax TEXT,email TEXT,contact TEXT,telcontact TEXT,valsync INTEGER );");

        db.execSQL("create table  IF NOT EXISTS employes (ID INTEGER PRIMARY KEY ,login,TEXT,pwd TEXT,prenom TEXT,nom TEXT,email TEXT,actif TEXT,valsync INTEGRE);");

        db.execSQL("create table IF NOT EXISTS sites (ID INTEGER PRIMARY KEY ,longitude TEXT,latitude TEXT,adresse TEXT,rue TEXT," +
                "codepostal TEXT,ville TEXT,contact TEXT,telcontact TEXT,client_id INTEGER,valsync INTEGER ,FOREIGN KEY(client_id) REFERENCES clients(ID));");

        db.execSQL("create table IF NOT EXISTS priorites (ID INTEGER PRIMARY KEY ,nom TEXT,valsync INTEGER);");

        db.execSQL("create table IF NOT EXISTS contrats (ID INTEGER PRIMARY KEY ,datedebut TEXT,datefin TEXT,redevence TEXT,client_id INTEGER, valsync INTEGER,FOREIGN KEY(client_id) REFERENCES clients(ID));");

        db.execSQL("create table IF NOT EXISTS interventions (ID INTEGER PRIMARY KEY ,titre TEXT,datedebut TEXT,datefin TEXT,heuredebutplan TEXT,commentaire Text,heurebuteffect TEXT,heurefineffect TEXT," +
                "termine TEXT,datedeminaison TEXT," +
                "validee TEXT,datedevalidation TEXT,priorite_id INTEGER,site_id INTEGER,valsync INTEGER,FOREIGN KEY(priorite_id) REFERENCES priorites(ID),FOREIGN KEY(site_id) REFERENCES sites(ID));");

        db.execSQL("create table IF NOT EXISTS employes_interventions (ID INTEGER PRIMARY KEY ,employe_id INTEGER,intervention_id INTEGER," +
                "FOREIGN KEY(intervention_id) REFERENCES interventions(ID),FOREIGN KEY(employe_id) REFERENCES employes(ID));");

        db.execSQL("create table IF NOT EXISTS images (ID INTEGER PRIMARY KEY ,nom TEXT,img TEXT,dateCapture TEXT,intervention_id INTEGER,valsync INTEGER,FOREIGN KEY(intervention_id) REFERENCES interventions(ID));");

        db.execSQL("create table IF NOT EXISTS taches (ID INTEGER PRIMARY KEY ,reference TEXT,nom TEXT,dure TEXT,prix REAL,prixheure REAL," +
                "dateaction TEXT,intervention_id INTEGER,valsync INTEGE,FOREIGN KEY(intervention_id) REFERENCES interventions(ID));");


        //mqueue = Volley.newRequestQueue(this);

        syncBtn.setOnClickListener(v -> {
            Background bgS = new Background(getApplicationContext());
            //jsonParse();
            bgS.execute();
        });


    }
    private class Background extends AsyncTask<String, Void, String> {
        AlertDialog dialog;
        Context context;

        public Background(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            dialog = new AlertDialog.Builder(context).create();
            dialog.setTitle("Etat de connexion");
        }

        @Override
        protected String doInBackground(String... strings) {

           /* String url ="http:/192.168.43.249/loginClient/getAllcontrats.php";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("contrats");

                        for (int i=0 ; i<jsonArray.length();i++){
                            JSONArray Contrats = jsonArray.getJSONArray(i);
                            String id = Contrats.getString(i);
                            System.out.println(id+"lllllllllllllllllllllllllllllllllllll");

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
            });*/
            //mqueue.add(request);


            try {
                URL urll = new URL("http://"+localHost+"/loginClient/getAllcontrats.php");
                HttpURLConnection connection = null;

                connection = (HttpURLConnection) urll.openConnection();


                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String _table_name = "contrats";
                String _id ;
                String output;
                System.out.println("Output from Server");
                while ((output = bufferedReader.readLine()) != null) {
                    System.out.println(output + "");
                    JSONArray jsonArray1=new JSONArray(output);
                    // output2=output.substring(0,output.length()-1);
                    // System.out.println(output2);
                    ObjectMapper objectMapper = new ObjectMapper();

                    for (int  j =0 ; j< jsonArray1.length();j++ ){
                        JSONObject cls = (JSONObject) jsonArray1.get(j);
                        Contrats client =objectMapper.readValue(cls.toString(), Contrats.class);
                        //traitement
                        try {
                            Cursor c = db.rawQuery("SELECT * FROM contrats ", null);
                            if (c.moveToFirst()) {
                                do {
                                    // Passing values
                                    SqliteIdTest = c.getString(0);
                                    if (!client.getClient_id().equals(SqliteIdTest)) {
                                        client.setValsync("1");
                                        db.execSQL("insert into  contrats (id,datedebut,datefin,redevence,client_id,valsync) values (?,?,?,?,?,?);", new String[]{client.getId(), client.getDatedebut(), client.getDatefin(), client.getRedevence(), client.getClient_id(), client.getValsync()});
                                        _id =client.getId() ;
                                        try {
                                            URL url = new URL("http://"+localHost+"/loginClient/updateValsync.php");
                                            HttpURLConnection http = (HttpURLConnection) url.openConnection();
                                            http.setRequestMethod("POST");
                                            http.setDoInput(true);
                                            http.setDoOutput(true);

                                            OutputStream ops = http.getOutputStream();
                                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                                            String data = URLEncoder.encode("_table_name","UTF-8") + "=" + URLEncoder.encode(_table_name,"UTF-8") +
                                                    "&&" + URLEncoder.encode("_id", "UTF-8")+ "=" + URLEncoder.encode(_id,"UTF-8");
                                            System.out.println("id to send"+_id);
                                            System.out.println(data);
                                            String result="";
                                            writer.write(data);
                                            writer.flush();
                                            writer.close();
                                            InputStream ips = http.getInputStream();
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
                                            String ligne ="";
                                            while ((ligne = reader.readLine())!= null){
                                                result = result + ligne;
                                                // ou bien result += ligne;

                                            }
                                            reader.close();
                                            ips.close();
                                            http.disconnect();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.e("error",e.getMessage());
                                        }

                                    }else{

                                    }
                                    // Do something Here with values
                                } while (c.moveToNext());
                            }
                        }catch (Exception e){
                            Log.e("Existing", "the id is already existing in the Sqlite database ! ");
                        }


                        //fin de traitement
                        System.out.println(client.getId());
                        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInf = wifiMan.getConnectionInfo();
                        int ipAddress = wifiInf.getIpAddress();
                        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
                        System.out.println( "ip de telephone"+ip);
                            /*System.out.println(client.getDatedebut());
                            System.out.println(client.getValsync());*/
                    }
                }

                connection.disconnect();


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "";

        }

        @Override
        protected void onPostExecute(String s) {
            dialog.setMessage(s);
            try {
                dialog.show();
            } catch (Exception e) {
                Log.e("errorpost", e.getMessage());

            }

            if (s.contains("Error updating recorded")) {
            } else {
                Toast.makeText(getApplicationContext(), "Error updating recorded ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            startActivity(new Intent(getApplicationContext(), LogIn.class));
            finish();

        }
        return  super.onOptionsItemSelected(item);
    }
}