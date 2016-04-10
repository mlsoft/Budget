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
                speak("Parlez");
                listen();
            }
        });

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.budget");
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
        if (dutexte != null && dutexte.size() > 0) {
            Speech.setText(dutexte.get(0));
            processvoice(dutexte.get(0));
        }
        listen();
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

    Matcher mMatcher[] = new Matcher[50];
    final int MAXMATCHER = 50;
    int nbmatcher =0;


    public void processvoice(String voiceorig) {
        if(nbmatcher==0) loadmatcher();
        String voice = voiceorig.toLowerCase();
        for(int i=0;i<nbmatcher;++i) {
            if(voice.contains(mMatcher[i].match)) {
                String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
                String tospeak = String.format(mMatcher[i].response,monnom);
                speak(tospeak);
                break;
            }
        }
    }

    public void loadmatcher() {
        setmatcher("FR","je m'appelle","salut, %s. mon bon ami.");
        setmatcher("FR","phoque","ça vaut pas la peine");
        setmatcher("EN","fuck","go shit yourself");
    }

    public void setmatcher(String lan, String mat, String res) {
        if(nbmatcher>=MAXMATCHER) return;
        mMatcher[nbmatcher].lang=lan;
        mMatcher[nbmatcher].match=mat;
        mMatcher[nbmatcher].response=res;
        nbmatcher++;
    }

}
