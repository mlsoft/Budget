package net.ddns.mlsoftlaberge.budget.speech;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.ddns.mlsoftlaberge.budget.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by mlsoft on 05/04/16.
 */
public class DiscussionFragment extends Fragment implements RecognitionListener {

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


    public DiscussionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discussion_fragment, container, false);

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
    // listener for the speech recognition service


    public void processvoice(String voiceorig) {
        String voice = voiceorig.toLowerCase();
        if (voice.contains("test")) {
            speak("moi aussi je test");

        } else if (voice.contains("what")) {
            t1.setLanguage(Locale.US);
            speak("It is whatever you want.");
            t1.setLanguage(Locale.FRENCH);

        } else if (voice.contains("who")) {
            t1.setLanguage(Locale.US);
            speak("It is who you know.");
            t1.setLanguage(Locale.FRENCH);

        } else if (voice.contains("when")) {
            t1.setLanguage(Locale.US);
            speak("It is when you want.");
            t1.setLanguage(Locale.FRENCH);

        } else if (voice.contains("salut")) {
            String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
            if(monnom.contains("android")) {
                speak("Salut maître.");
            } else {
                speak("Je m'appelle pas " + monnom);
            }

        } else if (voice.contains("tabarnak")) {
            speak("Sacre pas apres moi, mon nesti");

        } else if (voice.contains("dis bonjour à mon ami")) {
            speak("Bonjour jacynthe");

        } else if (voice.contains("dis bonjour à mon père")) {
            speak("Bonjour Jean Pierre Laberge");

        } else if (voice.contains("dis bonjour à ma mère")) {
            speak("Bonjour Raymonde Laberge");

        } else if (voice.contains("dis bonjour à")) {
            String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
            speak("Bonjour " + monnom);

        } else if (voice.contains("comment ça va")) {
            speak("ça va bien merci");

        } else if (voice.contains("fuck")) {
            speak("tu peux te le mettre dans le cul");

        } else if (voice.contains("phoque")) {
            speak("Ça vaut pas la peine de laisser ceux qu'on aime, pour aller faire tourner un ballon sur son nez");

        } else if (voice.contains("merci")) {
            speak("Bienvenue maître. Tu est le plus grand.");

        } else if (voice.contains("je m'appelle")) {
            String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
            speak("enchanté de te connaitre " + monnom);

        } else if (voice.contains("identifie")) {
            String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
            if (monnom.contains("jacinthe")) {
                speak(monnom + " est mon namie");
            } else if (monnom.contains("martin")) {
                speak(monnom + " est mon maître");
            } else if (monnom.contains("guy")) {
                speak(monnom + " est pas mal phoqué");
            } else if (monnom.contains("pierre")) {
                speak(monnom + " est en prison");
            } else {
                speak("Je ne connais pas " + monnom);
            }

        } else if (voice.contains("pierre")) {
            if(voice.contains("pinotte")) {
                speak("Pierre a jamais vendu de pinotte de toute sa vie, voyons donc.");
            } else if(voice.contains("drogue") || voice.contains("drug")) {
                    speak("Pierre a jamais vendu de drogue de toute sa vie. Voyons donc.");
            } else {
                speak("Pierre est dans le trouble");
            }

        } else if (voice.contains("drogue")) {
            speak("oublie pas la bière et le sexe");

        } else if (voice.contains("quitter")) {
            getActivity().finish();

        } else {
            speak(voice + ". Réponse inconnue.");
        }

        //discuss.setLength(0);
        //discuss.append(voice);
        //discuss.append("\n");
        //discuss.append(Speech.getText());
        //discuss.append("\n");
        //Discussion.setText(discuss);
    }

}
