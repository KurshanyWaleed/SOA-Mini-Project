package com.dmwm.sopproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LogIn extends AppCompatActivity {

    EditText email, password;
    Button loginbtn;
    String localHost = "192.168.1.17";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        loginbtn = findViewById(R.id.buttonlogin);


        loginbtn.setOnClickListener(v -> {
            String _email = email.getText().toString().trim();
            String _pass = password.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches())
            {
                email.setError("Invalid Email");
                email.setFocusable(true);
                //email.setPadding(0,0,0,0);
            }
            else if (_pass.length()<6)
            {
                password.setError("Invalid password Password length at least 6 characters ! ");
                password.setFocusable(true);
                //password.setPadding(0,10,0,0);
            }
            else {
            }
            Background background = new Background(getApplicationContext());
            background.execute(_email, _pass);
        });
    }


        private class Background extends AsyncTask <String,Void,String> {
            AlertDialog dialog;
            Context context;
            public Background(Context context){
                this.context = context;
            }

            @Override
            protected void onPreExecute() {

                dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("Etat de connexion");
            }
    @Override
    protected String doInBackground(String... strings) {
        String result  ="";
        String _email = strings[0];
        String _pass = strings[1];
        //pour savoir votre adresse ip: lancer la commande "ipconfig" avec le programme cmd
        String connstr = "http://"+localHost+"/loginClient/login.php";
        try {
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);
            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("_email","UTF-8") + "=" + URLEncoder.encode(_email,"UTF-8") +
                    "&&" + URLEncoder.encode("_pass", "UTF-8")+ "=" + URLEncoder.encode(_pass,"UTF-8");
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
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error",e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        dialog.setMessage(s);
        try {
            dialog.show();
        } catch (Exception e){
            Log.e("errorpost",e.getMessage());
          
        }

        if (s.contains("succes")){
            Intent intent = new Intent();
            intent.setClass(context.getApplicationContext(), Dashboread.class);
            startActivity(intent);
        }else {
            Toast.makeText(LogIn.this, "Failed to authenticate ! Please check your Email or Password  ! ", Toast.LENGTH_SHORT).show();
        }
    }
}
}