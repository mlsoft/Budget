package net.ddns.mlsoftlaberge.budget.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * Created by mlsoft on 19/03/16.
 */
public class NotesEditFragment extends DialogFragment {

    private NotesDbAdapter dbHelper;

    // define the four variables we have to edit
    public long note_id=0;
    public String note_name=null;
    public String note_description=null;
    public String note_priority=null;

    private EditText mName;
    private EditText mDescription;
    private EditText mPriority;

    // create a note editor fragment, and populate the id variable.
    public static NotesEditFragment newInstance(long id) {
        // prepare a bundle of args;
        Bundle args = new Bundle();
        // create fragment and send it args
        NotesEditFragment fragment = new NotesEditFragment();
        fragment.setArguments(args);
        // save the record id of the record we want to edit
        fragment.note_id=id;
        return fragment;
    }

    private OnNoteListener mCallback;

    // Container Activity must implement this interface
    public interface OnNoteListener {
        public void onNoteModified(long id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNoteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNoteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.notes_edit, null);
        mName = (EditText) view.findViewById(R.id.note_name);
        mDescription = (EditText) view.findViewById(R.id.note_description);
        mPriority = (EditText) view.findViewById(R.id.note_priority);

        // pass the view to the builder
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save_note, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // save the note modified, or insert a new one
                        savenote();
                    }
                })
                .setNegativeButton(R.string.cancel_note, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NotesEditFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper = new NotesDbAdapter(getContext());
        dbHelper.open();
        loadnote();
    }

    public void loadnote() {
        // when inserting, then dont load a note
        if(note_id==(-1)) return;
        // get the note row
        Cursor cursor = dbHelper.fetchNotesById(note_id);
        if(cursor!=null) {
            // Get the fields from the row
            note_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            note_description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            note_priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"));
            // put them in the display fields
            mName.setText(note_name);
            mDescription.setText(note_description);
            mPriority.setText(note_priority);
        }
    }

    public void savenote() {
        // collect back the datas from the display fields
        note_name=mName.getText().toString();
        note_description=mDescription.getText().toString();
        note_priority=mPriority.getText().toString();
        // check for insertion or modification
        if(note_id==(-1)) {
            // insert a new note
            note_id=dbHelper.createNote(note_name,note_description,note_priority);
        } else {
            // modify the existing note
            dbHelper.modifyNote(note_id,note_name,note_description,note_priority);
        }
	    mCallback.onNoteModified(note_id);
    }
}
