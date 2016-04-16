package net.ddns.mlsoftlaberge.budget.sensors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.budget.R;
import net.ddns.mlsoftlaberge.budget.utils.SettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by mlsoft on 05/04/16.
 */
public class ConversationFragment extends Fragment implements RecognitionListener {

    private static final String TAG="ConversationFragment";

    private static final int REQUEST_CODE = 1234;
    Button Start;
    TextView Error;
    TextView Status;

    TextToSpeech t1;

    AudioManager mAudioManager;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    Timer mTimer;
    boolean islistening;

    EditText Speech;
    TextView Speak;

    Button Setquestion;
    TextView Lastquestion;
    Button Setanswer;
    TextView Lastanswer;
    Button Savetable;
    TextView Stattable;

    boolean autoListen;
    String speakLanguage;
    String listenLanguage;

    public ConversationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_fragment, container, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen",false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");

        Start = (Button) view.findViewById(R.id.start_rec);

        Error = (TextView) view.findViewById(R.id.error);
        Status = (TextView) view.findViewById(R.id.status);

        Speech = (EditText) view.findViewById(R.id.speech);
        Speak = (TextView) view.findViewById(R.id.speak);

        Setquestion = (Button) view.findViewById(R.id.set_question);
        Lastquestion = (TextView) view.findViewById(R.id.last_question);

        Setanswer = (Button) view.findViewById(R.id.set_answer);
        Lastanswer = (TextView) view.findViewById(R.id.last_answer);

        Savetable = (Button) view.findViewById(R.id.save_table);
        Stattable = (TextView) view.findViewById(R.id.stat_table);

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
        Setquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setquestion();
            }
        });
        Setanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setanswer();
            }
        });
        Savetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savematcher();
            }
        });

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.budget");
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,5000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,500);

        //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);

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
        Speak.setText(toSpeak);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen",false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");

        if(speakLanguage.equals("FR")) {
            t1.setLanguage(Locale.FRENCH);
        } else if(speakLanguage.equals("EN")) {
            t1.setLanguage(Locale.US);
        } else {
            // default prechoosen language
        }
        t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
        while (t1.isSpeaking()) {
            SystemClock.sleep(200);
        }
        if(autoListen) {
            SystemClock.sleep(1000);
            listen();
        }
    }


    public void listen() {
        //if(islistening) return;
        //mSpeechRecognizer.stopListening();
        //SystemClock.sleep(100);
        //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
        islistening=true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen",false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");

        if(listenLanguage.equals("FR")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        } else if(listenLanguage.equals("EN")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        } else {
            // automatic
        }
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    // =================================================================================
    // listener for the speech recognition service


    @Override
    public void onBeginningOfSpeech() {
        islistening=true;
        Status.setText("Status: " + "BeginningOfSpeech");

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        islistening=false;
        Status.setText("Status: " + "EndOfSpeech");
        //SystemClock.sleep(100);
        //listen();
    }

    @Override
    public void onError(int error) {
        //mSpeechRecognizer.stopListening();
        islistening=false;
        Error.setText("Error: " + error);
        //SystemClock.sleep(300);
        //listen();
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Status.setText("Event: " + eventType);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Status.setText("Status: " + "PartialResults");

    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        islistening=true;
        Status.setText("Status: " + "ReadyForSpeech");

    }

    @Override
    public void onResults(Bundle results) {
        Status.setText("Status: " + "Results");
        islistening=false;

        ArrayList<String> dutexte = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (dutexte != null && dutexte.size() > 0) {
            Speech.setText(dutexte.get(0));
            processvoice(dutexte.get(0));
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    // =================================================================================
    // sentence recognition and appropriate response

    public class Matcher {
        String lang;
        String match1;
        String match2;
        String match3;
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
            if(voice.contains(mMatcher[i].match1)) {
                if(voice.contains(mMatcher[i].match2)) {
                    if (voice.contains(mMatcher[i].match3)) {
                        if (mMatcher[i].lang.contains("FR")) {
                            t1.setLanguage(Locale.FRENCH);
                        } else {
                            t1.setLanguage(Locale.US);
                        }
                        String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
                        String tospeak = String.format(mMatcher[i].response, monnom);
                        speak(tospeak);
                        break;
                    }
                }
            }
        }
        if(i>=nbmatcher) {
            t1.setLanguage(Locale.FRENCH);
            speak(voice);
        }
    }

    public void setmatcher(String lan, String mat1, String mat2, String mat3, String res) {
        if(nbmatcher>=MAXMATCHER) return;
        mMatcher[nbmatcher]=new Matcher();
        mMatcher[nbmatcher].lang=new String(lan);
        mMatcher[nbmatcher].match1=new String(mat1);
        mMatcher[nbmatcher].match2=new String(mat2);
        mMatcher[nbmatcher].match3=new String(mat3);
        mMatcher[nbmatcher].response=new String(res);
        nbmatcher++;
    }

    public void setmatcher(String line) {
        if(nbmatcher>=MAXMATCHER) return;
        String field=null;
        int p=0;
        int q=0;
        mMatcher[nbmatcher]=new Matcher();
        q=line.indexOf(':');
        field=line.substring(p,q);
        mMatcher[nbmatcher].lang=field;
        p=q+1;
        q=line.indexOf(':',p);
        field=line.substring(p,q);
        mMatcher[nbmatcher].match1=field;
        p=q+1;
        q=line.indexOf(':',p);
        field=line.substring(p,q);
        mMatcher[nbmatcher].match2=field;
        p=q+1;
        q=line.indexOf(':',p);
        field=line.substring(p,q);
        mMatcher[nbmatcher].match3=field;
        p=q+1;
        q=line.indexOf(':',p);
        field=line.substring(p,q);
        mMatcher[nbmatcher].response=field;
        nbmatcher++;
    }

    public void loadmatcher() {
        mMatcher=new Matcher[MAXMATCHER];
        nbmatcher=0;
        setmatcher("FR","french","","","Certainement Maître.");
        setmatcher("EN","english","","","Certainly Master.");

        setmatcher("FR","je m'appelle martin","","","Bonjour maître.");
        setmatcher("FR","je m'appelle","","","salut, %s.");

        setmatcher("FR","dis bonjour à mon ami","","","bonjour jacynthe.");
        setmatcher("FR","dis bonjour à ma mère","","","bonjour raymonde laberge.");
        setmatcher("FR","dis bonjour à mon père","","","bonjour jean-pierre laberge.");
        setmatcher("FR","dis bonjour à pierre","","","salut pierre, mais que fais tu en prison?");
        setmatcher("FR","dis bonjour à jacynthe","","","bonjour chérie.");
        setmatcher("FR","dis bonjour à ","","","bonjour %s.");

        setmatcher("FR","parle-moi de jacynthe","","","%s est une très jolie fille.");
        setmatcher("FR","parle-moi de jacinthe","","","%s est une jolie fille.");
        setmatcher("FR","parle-moi de pierre","","","Bof, %s est à orsainville.");
        setmatcher("FR","parle-moi de guy","","","%s est pas mal phoqué.");
        setmatcher("FR","parle-moi de ","","","je ne connais pas %s.");

        setmatcher("FR","pierre","pinotte","","Pierre vends pas de pinotte. Ça se peut pas.");
        setmatcher("FR","pierre","drogue","","Pierre a jamais vendu de drogue de toute sa vie. Promis juré.");
        setmatcher("FR","pierre","fraude","","Pierre a jamais fait ça. Je te jure.");
        setmatcher("FR","pierre","vie","","Pierre ne vends pas de drogue. Réveille!");
        setmatcher("FR","pierre","fait","","Pierre n'a pas fait ça. Voyons donc.");
        setmatcher("FR","pierre","où","","Pierre est en prison");
        setmatcher("FR","pierre","","","Pierre est dans le gros trouble");

        setmatcher("FR","bonjour","","","bonjour monsieur.");
        setmatcher("FR","salut","","","salut toi même.");
        setmatcher("FR","oui","","","Bien moi aussi");
        setmatcher("FR","non","","","Je te cré pas");

        setmatcher("EN","fuck","","","go shit yourself");
        setmatcher("EN","what","","","something secret");
        setmatcher("EN","who","","","someone you know");
        setmatcher("EN","why","","","I dont know");
        setmatcher("EN","when","","","very soon");
        setmatcher("EN","where","","","not very far");

        setmatcher("FR","phoque","","","ça vaut pas la peine, de laisser ceux qu'on aime, pour aller faire tourner, des ballons sur son nez.");
        setmatcher("FR","alaska","","","ça vaut pas la peine, de laisser ceux qu'on aime, pour aller faire tourner, des ballons sur son nez.");

        if(!isExternalStorageReadable()) return;

        loadMatcherFile();

        Stattable.setText("File loaded");

    }

    public void setquestion() {
        Lastquestion.setText(Speech.getText());
    }

    public void setanswer() {
        Lastanswer.setText(Speech.getText());
    }

    public void savematcher() {
        setmatcher("FR",Lastquestion.getText().toString(),"","",Lastanswer.getText().toString());
        speak(Lastquestion.getText().toString());
        speak(Lastanswer.getText().toString());

        // save the table
        if(!isExternalStorageWritable()) return;

        saveMatcherFile();

        Stattable.setText("File saved");

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            Toast.makeText(getActivity(), "Directory not Created", Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    public void saveMatcherFile() {
        File directory = getAlbumStorageDir("Conversation");
        try {
            FileWriter out = new FileWriter(new File(directory, "conversation.txt"));
            for(int i=0;i<nbmatcher;++i) {
                out.write(mMatcher[i].lang);
                out.write(":");
                out.write(mMatcher[i].match1);
                out.write(":");
                out.write(mMatcher[i].match2);
                out.write(":");
                out.write(mMatcher[i].match3);
                out.write(":");
                out.write(mMatcher[i].response);
                out.write(":");
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "IOException", Toast.LENGTH_SHORT).show();
        } finally {
            Toast.makeText(getActivity(), "Save Successful", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMatcherFile() {
        File directory = getAlbumStorageDir("Conversation");
        BufferedReader in = null;
        String line;
        try {
            in = new BufferedReader(new FileReader(new File(directory, "conversation.txt")));
            nbmatcher=0;
            while ((line = in.readLine()) != null) {
                // decode the line
                setmatcher(line);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity(), "FileNotFoundException", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "IOException", Toast.LENGTH_SHORT).show();
        } finally {
            Toast.makeText(getActivity(), "Load Successful", Toast.LENGTH_SHORT).show();
        }
    }

}
