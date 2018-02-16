package com.example.ursul.rdvgeo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by ursul on 04/01/2018.
 */

public class SMSReceiver extends BroadcastReceiver {

    String sms, demandeur;

    @Override
    public void onReceive(Context context, Intent intent) { //TODO mettre ceci dans un thread à part
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                demandeur = msgs[i].getOriginatingAddress();
                sms = msgs[i].getMessageBody().toString();
            }
            if(sms.contains("Coordonnées RDV : ")){
                Intent intent1 = new Intent(context, Main3Activity.class);
                intent1.putExtra("demandeur", demandeur);
                intent1.putExtra("coord", sms.substring(19));
                context.startActivity(intent1);
            }
            if(sms.contains("Réponse : ")){ //TODO allonger le temps d'affichage du toast ou faire une pop up ou utiliser sharedpref pour la persistance des données dans la quatrième activité
                //int i = Toast.LENGTH_LONG + 5;
               // Toast.makeText(context, "Expéditeur : "+demandeur+" "+sms, Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(context, Main4Activity.class);
                intent2.putExtra("destinataire", demandeur);
                intent2.putExtra("reponse", sms.substring(10));
                context.startActivity(intent2);
            }
        }
    }
}
