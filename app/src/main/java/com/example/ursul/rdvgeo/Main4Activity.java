package com.example.ursul.rdvgeo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main4Activity extends AppCompatActivity {

    private transient Data data;
    TextView dest;
    TextView rep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        String destinataire = getIntent().getExtras().getString("destinataire");
        String reponse = getIntent().getExtras().getString("reponse");

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        data = new Data();
        data.load(preferences);
        dest = (TextView) findViewById(R.id.dest);
        rep = (TextView) findViewById(R.id.rep);
        dest.setText('\n'+destinataire);
        rep.setText('\n'+reponse);
        load();
        store();
    }

    public void load(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        data = new Data();
        data.load(preferences);
        if(data.getDestinataires() != null && data.getReponses() != null){
            dest.append('\n'+data.getDestinataires());
            rep.append('\n'+data.getReponses());
        }
    }

    public void store(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        data = new Data(dest.getText().toString(), rep.getText().toString());
        data.store(preferences);
    }

    public void retour(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void reinit(View view){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        data.reset(preferences);
        dest.setText("");
        rep.setText("");
    }

    //pour rendre les données persistentes
    private class Data{

        String dest;
        String rep;

        public Data(String destinataire, String reponse){
            this.dest = destinataire;
            this.rep = reponse;
        }

        public Data(){
            this(null, null);
        }

        public String getDestinataires(){
            return dest;
        }

        public String getReponses(){
            return rep;
        }

        void load(SharedPreferences preferences){
            dest = preferences.getString("dest", null);
            rep = preferences.getString("rep", null);
        }

        void store(SharedPreferences preferences){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("dest", dest);
            editor.putString("rep", rep);
            editor.commit();
        }
        void reset(SharedPreferences preferences){
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
        }
    }
}
