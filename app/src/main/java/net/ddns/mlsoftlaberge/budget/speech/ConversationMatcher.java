package net.ddns.mlsoftlaberge.budget.speech;

import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by mlsoft on 16/04/16.
 */
public class ConversationMatcher {

    // =================================================================================
    // sentence recognition and appropriate response

    public class Matcher {
        String lang;
        String match1;
        String match2;
        String match3;
        String response;
        String tospeak;
    }

    Matcher mMatcher[];
    final int MAXMATCHER = 50;
    int nbmatcher = 0;

    public Matcher matchmatcher(String voiceorig) {
        if (nbmatcher == 0) loadmatcher();
        int i;
        String voice = voiceorig.toLowerCase();
        for (i = 0; i < nbmatcher; ++i) {
            if (voice.contains(mMatcher[i].match1)) {
                if (voice.contains(mMatcher[i].match2)) {
                    if (voice.contains(mMatcher[i].match3)) {
                        String monnom = voice.substring(voice.lastIndexOf(' ') + 1);
                        mMatcher[i].tospeak = String.format(mMatcher[i].response, monnom);
                        return (mMatcher[i]);
                    }
                }
            }
        }
        return (null);
    }

    public Matcher getmatcher(int no) {
        if (nbmatcher == 0) loadmatcher();
        if (no >= nbmatcher) return (null);
        return (mMatcher[no]);
    }

    public int getnbmatcher() {
        if (nbmatcher == 0) loadmatcher();
        return (nbmatcher);
    }

    public void loadmatcher() {
        mMatcher = new Matcher[MAXMATCHER];
        nbmatcher = 0;
        if (!isExternalStorageReadable()) return;
        loadMatcherFile();
    }

    public void savematcher() {
        // save the table
        if (!isExternalStorageWritable()) return;
        saveMatcherFile();
    }

    public void setmatcher(String lan, String mat1, String mat2, String mat3, String res) {
        if (nbmatcher >= MAXMATCHER) return;
        mMatcher[nbmatcher] = new Matcher();
        mMatcher[nbmatcher].lang = new String(lan);
        mMatcher[nbmatcher].match1 = new String(mat1);
        mMatcher[nbmatcher].match2 = new String(mat2);
        mMatcher[nbmatcher].match3 = new String(mat3);
        mMatcher[nbmatcher].response = new String(res);
        mMatcher[nbmatcher].tospeak = new String("");
        nbmatcher++;
    }

    public void setmatcher(String line) {
        if (nbmatcher >= MAXMATCHER) return;
        String field = null;
        int p = 0;
        int q = 0;
        mMatcher[nbmatcher] = new Matcher();
        q = line.indexOf(':');
        field = line.substring(p, q);
        mMatcher[nbmatcher].lang = field;
        p = q + 1;
        q = line.indexOf(':', p);
        field = line.substring(p, q);
        mMatcher[nbmatcher].match1 = field;
        p = q + 1;
        q = line.indexOf(':', p);
        field = line.substring(p, q);
        mMatcher[nbmatcher].match2 = field;
        p = q + 1;
        q = line.indexOf(':', p);
        field = line.substring(p, q);
        mMatcher[nbmatcher].match3 = field;
        p = q + 1;
        q = line.indexOf(':', p);
        field = line.substring(p, q);
        mMatcher[nbmatcher].response = field;
        mMatcher[nbmatcher].tospeak = "";
        nbmatcher++;
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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            //Toast.makeText(getActivity(), "Directory not Created", Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    public void saveMatcherFile() {
        File directory = getAlbumStorageDir("Conversation");
        try {
            FileWriter out = new FileWriter(new File(directory, "conversation.txt"));
            for (int i = 0; i < nbmatcher; ++i) {
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
            //Toast.makeText(getActivity(), "IOException", Toast.LENGTH_SHORT).show();
        } finally {
            //Toast.makeText(getActivity(), "Save Successful", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMatcherFile() {
        File directory = getAlbumStorageDir("Conversation");
        BufferedReader in = null;
        String line;
        try {
            in = new BufferedReader(new FileReader(new File(directory, "conversation.txt")));
            nbmatcher = 0;
            while ((line = in.readLine()) != null) {
                // decode the line
                setmatcher(line);
            }
        } catch (FileNotFoundException e) {
            //Toast.makeText(getActivity(), "FileNotFoundException", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //Toast.makeText(getActivity(), "IOException", Toast.LENGTH_SHORT).show();
        } finally {
            //Toast.makeText(getActivity(), "Load Successful", Toast.LENGTH_SHORT).show();
        }
    }

}
