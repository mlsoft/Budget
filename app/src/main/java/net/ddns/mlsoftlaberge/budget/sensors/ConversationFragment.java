package net.ddns.mlsoftlaberge.budget.sensors;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.RecognizerResultsIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.budget.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mlsoft on 05/04/16.
 */
public class ConversationFragment extends Fragment implements RecognitionListener {

    private static final int REQUEST_CODE = 1234;
    Button Start;
    TextView Speech;
    TextView Discussion;
    TextView Status;
    ArrayList<String> matches_text;
    TextToSpeech t1;
    StringBuffer discuss;

    AudioManager mAudioManager;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    Timer mTimer;
    boolean islistening;


    public ConversationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_fragment, container, false);

        Start = (Button) view.findViewById(R.id.start_rec);
        Speech = (TextView) view.findViewById(R.id.speech);
        Discussion = (TextView) view.findViewById(R.id.discussion);
        Status = (TextView) view.findViewById(R.id.status);

        discuss = new StringBuffer();

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
                //t1.setLanguage(Locale.FRENCH);
                //speak("Parlez");
                listen();
            }
        });

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.budget");
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,1000);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,1000);

        //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);

        mTimer = new Timer();
        islistening=false;

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void speak(String toSpeak) {
        mSpeechRecognizer.stopListening();
        islistening=false;
        Speech.setText(toSpeak);
        t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
        while (t1.isSpeaking()) {
            SystemClock.sleep(200);
        }
        //SystemClock.sleep(1000);
    }


    public void listen() {
        mSpeechRecognizer.stopListening();
        //SystemClock.sleep(100);
        //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    // =================================================================================
    // listener for the speech recognition service


    @Override
    public void onBeginningOfSpeech() {
        islistening=true;
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        islistening=false;
    }

    @Override
    public void onError(int error) {
        islistening=false;
        //Status.setText("Status: " + error);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        islistening=true;
    }

    @Override
    public void onResults(Bundle results) {
        islistening=false;
        ArrayList<String> dutexte = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float duconf[] = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (dutexte != null && dutexte.size() > 0) {
            Speech.setText(dutexte.get(0));
            discuss.setLength(0);
            for(int i=0;i<dutexte.size();++i) {
                discuss.append(dutexte.get(i));
                discuss.append(String.format(" %f",duconf[i]));
                discuss.append("\n");
            }
            Discussion.setText(discuss);
            Discussion.invalidate();
            processvoice(dutexte.get(0));
        }
        //listen();
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    // =================================================================================
    // sentence recognition and appropriate response

    public class Matcher {
        String lang;
        String match;
        String response;
    }

    Matcher mMatcher[];
    final int MAXMATCHER = 50;
    int nbmatcher =0;


    public void processvoice(String voiceorig) {
        if(nbmatcher==0) loadmatcher();
        int i;
        String voice = voiceorig.toLowerCase();
        for(i=0;i<nbmatcher;++i) {
            if(voice.contains(mMatcher[i].match)) {
                if(mMatcher[i].lang.contains("FR")) {
                    t1.setLanguage(Locale.FRENCH);
                } else {
                    t1.setLanguage(Locale.US);
                }
                String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
                String tospeak = String.format(mMatcher[i].response,monnom);
                speak(tospeak);
                break;
            }
        }
        if(i>=nbmatcher) {
            t1.setLanguage(Locale.FRENCH);
            speak(voice);
        }
    }

    public void loadmatcher() {
        mMatcher=new Matcher[MAXMATCHER];
        nbmatcher=0;
        setmatcher("FR","french","Certainement Maître.");
        setmatcher("EN","english","Certainly Master.");

        setmatcher("FR","je m'appelle","salut, %s. mon bon ami.");
        setmatcher("FR","phoque","ça vaut pas la peine");
        setmatcher("FR","bonjour","bonjour monsieur");
        setmatcher("FR","salut","salut toi même");
        setmatcher("FR","pierre","Pierre est dans le gros trouble");
        setmatcher("FR","oui","Bien moi aussi");
        setmatcher("FR","non","Je te cré pas");
        setmatcher("EN","fuck","go shit yourself");
        setmatcher("EN","what","something secret");
        setmatcher("EN","who","someone you know");
        setmatcher("EN","why","I dont know");
        setmatcher("EN","when","very soon");
        setmatcher("EN","where","not very far");
    }

    public void setmatcher(String lan, String mat, String res) {
        if(nbmatcher>=MAXMATCHER) return;
        mMatcher[nbmatcher]=new Matcher();
        mMatcher[nbmatcher].lang=new String(lan);
        mMatcher[nbmatcher].match=new String(mat);
        mMatcher[nbmatcher].response=new String(res);
        nbmatcher++;
    }

}
