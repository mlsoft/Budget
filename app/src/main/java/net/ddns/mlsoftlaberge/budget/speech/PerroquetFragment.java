package net.ddns.mlsoftlaberge.budget.speech;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.budget.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by mlsoft on 05/04/16.
 */
public class PerroquetFragment extends Fragment {

    private static final int REQUEST_CODE = 1234;
    Button Start;
    TextView Speech;
    TextView Discussion;
    ArrayList<String> matches_text;
    TextToSpeech t1;
    StringBuffer discuss;

    public PerroquetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.sensor_fragment, container, false);

        Start = (Button) view.findViewById(R.id.start_rec);
        Speech = (TextView) view.findViewById(R.id.speech);
        Discussion = (TextView) view.findViewById(R.id.discussion);
        discuss=new StringBuffer();

        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.FRENCH);
                }
            }
        });

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });

        return view;
    }

    public void speak() {
        String toSpeak = Speech.getText().toString();
        Toast.makeText(getContext(), toSpeak, Toast.LENGTH_SHORT).show();
        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        while(t1.isSpeaking()) {
            SystemClock.sleep(100);
        }
        SystemClock.sleep(500);
    }


    public void listen() {
        if (isConnected()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            //Intent intent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK) {

            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Speech.setText(matches_text.get(0));

            processvoice(matches_text.get(0));

            speak();

            listen();

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void processvoice(String voice) {
        if(voice.contains("test")) {
            Speech.setText("moi aussi je test");
        }
        else if(voice.contains("fuck")) {
            Speech.setText("tu peux te le mettre dans le cul");
        }
        else if(voice.contains("phoque")) {
            Speech.setText("sa vaut pas la peine de laisser ceux qu'on aime");
        }
        else if(voice.contains("merci")) {
            Speech.setText("bienvenue maître");
        }
        else if(voice.contains("je m'appelle")) {
            String monnom = voice.substring(voice.lastIndexOf(' ')+1);
            Speech.setText("enchanté de te connaitre " + monnom);
        }
        else if(voice.contains("identifie")) {
            String monnom = voice.substring(voice.lastIndexOf(' ')+1);
            if(monnom.contains("Jacinthe")) {
                Speech.setText(monnom + " est mon namie");
            } else if(monnom.contains("Martin")) {
                    Speech.setText(monnom + " est mon maître" );
            } else if(monnom.contains("Guy")) {
                Speech.setText(monnom + " est pas mal phoqué" );
            } else if(monnom.contains("Pierre")) {
                Speech.setText(monnom + " est en prison" );
            } else {
                Speech.setText("Je ne connais pas " + monnom);
            }
        }
        else if(voice.contains("quitter")) {
            getActivity().finish();
        }
        else {
            // faire de quoi par défaut
        }
        discuss.append(voice);
        discuss.append("\n");
        discuss.append(Speech.getText());
        discuss.append("\n");
        Discussion.setText(discuss);
    }

}
