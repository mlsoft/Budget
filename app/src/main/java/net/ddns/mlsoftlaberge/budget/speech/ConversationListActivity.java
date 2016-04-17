package net.ddns.mlsoftlaberge.budget.speech;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * Created by mlsoft on 16/04/16.
 */
public class ConversationListActivity extends AppCompatActivity {

    private String defaultLanguage;
    ConversationMatcher cMatcher;
    ConversationMatcher.Matcher mMatcher;
    int nbmatcher;

    LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // read the needed preferences for this module
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        defaultLanguage = sharedPref.getString("pref_key_default_language", "");
        // load the initial screen
        setContentView(R.layout.conversation_list_activity);

        // initialize a conversation matcher to load the data
        cMatcher = new ConversationMatcher();
        nbmatcher=cMatcher.getnbmatcher();

        mLayout = (LinearLayout) findViewById(R.id.layout_conversationlist);

        fillconversationlist();
    }

    // fill the transactions layout with the transaction views
    private void fillconversationlist() {
        // Each LinearLayout has the same LayoutParams so this can
        // be created once and used for each cumulative layouts of data
        final LinearLayout.LayoutParams tlayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        // Clears out the details layout first in case the details
        // layout has data from a previous data load still
        // added as children.
        mLayout.removeAllViews();
        int i;
        for (i = 0; i < nbmatcher; ++i) {
            // Builds the transaction layout
            // Inflates the transaction layout
            LinearLayout tlayout = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.conversation_list_item, null, false);

            // point to the 4 fields of the layout
            TextView question = (TextView) tlayout.findViewById(R.id.question);
            TextView answer = (TextView) tlayout.findViewById(R.id.answer);

            // get the current transaction
            mMatcher = cMatcher.getmatcher(i);

            // fill the fields with the table data

            question.setText(mMatcher.match1);
            answer.setText(mMatcher.response);

            // Adds the new note layout to the notes layout
            mLayout.addView(tlayout, tlayoutParams);
        }
    }




}
