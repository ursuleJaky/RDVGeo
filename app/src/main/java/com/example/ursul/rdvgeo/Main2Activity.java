package com.example.ursul.rdvgeo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private EditText etAdr;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void pickContact(View view){
        Intent openContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        openContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(openContactIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){ //TODO mettre ceci dans un thread à part
        // TODO récupérer aussi le nom du contact
        if(requestCode == 1){ //vérifier à quelle requête on répond
            if(resultCode == RESULT_OK){//checker que la requête un succès
                Uri contactUri = data.getData(); //récupérer l'uri du contact sélectionné
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor cursor = getContentResolver().query(contactUri, projection, null,null, null);
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                EditText etDest = (EditText) findViewById(R.id.etDest);
                if(etDest.getText().toString().equals(""))
                    etDest.setText(number);
                else
                    etDest.append(','+number);
            }
        }
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                TextView tvCoord = (TextView) findViewById(R.id.tvCoord);
                tvCoord.setText("Coordonnées RDV : "+result);
            }
        }
    }

    public void retrieveCoord(View view) throws IOException {
        etAdr = (EditText) findViewById(R.id.etAdr);
        if(etAdr.getText().toString().equals("")){ // TODO gérer les cas où l'adresse est mauvaise
            Toast.makeText(this, "Vous devez entrer une adresse", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("adr", etAdr.getText().toString());
            startActivityForResult(intent,2);
        }
    }

    public void retrieveMyPosition(View view) throws IOException {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("adr", "");
        startActivityForResult(intent,2);
    }

    public void send (View view){ //TODO mettre ceci dans un thread à part
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
        EditText dest = (EditText) findViewById(R.id.etDest);
        String numero = dest.getText().toString();

        TextView coord = (TextView) findViewById(R.id.tvCoord);
        String smsCoord = coord.getText().toString();
        if(numero.equals("")){
            Toast.makeText(this, "Vous devez avoir au moins un destinataire", Toast.LENGTH_LONG).show();
        } else if(smsCoord.equals("")){
            Toast.makeText(this, "Vous devez récupérer des coordonnées GPS", Toast.LENGTH_LONG).show();
        } else {
            List<String> numeros = getNumeros(numero);
            for (int i = 0; i < numeros.size(); i++) {
                String num = numeros.get(i);
                if(!verifNumero(num)){
                    Toast.makeText(this, "Le numéro" + num + " n'est pas valide", Toast.LENGTH_LONG).show();
                } else {
                    SmsManager.getDefault().sendTextMessage(num, null, smsCoord, null, null);
                }
            }
            coord.setText("");
            dest.setText("");
            etAdr.setText("");
        }
    }

    public void retour(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private List<String> getNumeros(String numero) {
        List<String> liste = new ArrayList<String>();
        String number = "";
        for (int i = 0; i < numero.length(); i++) {
            if (numero.charAt(i) == ',') {
                liste.add(number);
                number= "";
            } else {
                number += numero.charAt(i);
            }
        }
        liste.add(number);
        return liste;
    }

    public boolean verifNumero(String numero){
        return numero.matches("\\+?[0-9]*");
    }
}
