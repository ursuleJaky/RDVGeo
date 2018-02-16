package com.example.ursul.rdvgeo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity {

    String coord, demandeur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        demandeur = getIntent().getExtras().getString("demandeur");
        coord = getIntent().getExtras().getString("coord");
        TextView tvDemandeur = (TextView) findViewById(R.id.tvDemandeur);
        TextView tvCoord = (TextView) findViewById(R.id.tvCoord);
        tvDemandeur.setText(demandeur);
        tvCoord.setText(coord);
    }

    public void openMap(View view){
        Intent intent = new Intent(this, MapsActivity2.class);
        if(coord.contains("N")){
            intent.putExtra("lat", parseCoordinate(coord.substring(0, coord.indexOf('N')+1)));
            intent.putExtra("longi", parseCoordinate(coord.substring(coord.indexOf('N')+1)));
        }
        if(coord.contains("S")){
            intent.putExtra("lat", parseCoordinate(coord.substring(0, coord.indexOf('S')+1)));
            intent.putExtra("longi", parseCoordinate(coord.substring(coord.indexOf('S')+1)));
        }
        startActivityForResult(intent, 1);
    }

    public double parseCoordinate(String s) {
        if(s.contains("N") || s.contains("E"))
            return Double.parseDouble(s.substring(0, s.length()-1));
        else if(s.contains("S") || s.contains("O"))
            return -Double.parseDouble(s.substring(0, s.length()-1));
        else
            return 0;
    }

    public void accepteRdv(View view){
        String sms = "Réponse : j'accepte le RDV";
        sendSMS(sms);
    }

    public void refuseRdv(View view){
        String sms = "Réponse : je refuse le RDV";
        sendSMS(sms);
    }

    public void sendSMS(String sms){ //TODO mettre ceci dans un thread à part
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
            return;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            return;
        }

        SmsManager.getDefault().sendTextMessage(demandeur , null, sms, null, null);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
