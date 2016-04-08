
package net.ddns.mlsoftlaberge.budget.notes;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import net.ddns.mlsoftlaberge.budget.R;

public class NotesFragment extends Fragment {

    private NotesDbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;

    private ListView mListView;
    private EditText myFilter;

    private OnNoteListener mCallback;

    // Container Activity must implement this interface
    public interface OnNoteListener {
        public void onNoteSelected(long id);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Let this fragment contribute menu items
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items
        inflater.inflate(R.menu.notes_list_menu, menu);
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_note:
                createnote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the list fragment layout
        View view = inflater.inflate(R.layout.notes_fragment, container, false);

        mListView = (ListView) view.findViewById(R.id.listView1);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                // String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                // Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();

                // inform the activity that note is selected
                mCallback.onNoteSelected(id);
                // edit the item in a dialog
                editnote(cursor);
            }
        });

        myFilter = (EditText) view.findViewById(R.id.myFilter);

        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });


        dbHelper = new NotesDbAdapter(getContext());
        dbHelper.open();

        //Clean all data
        //dbHelper.deleteAllNotes();
        //Add some data
        //dbHelper.insertSomeNotes();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // display the list
        displayListView();
    }

    //Generate ListView from SQLite Database
    public void displayListView() {


        Cursor cursor = dbHelper.fetchAllNotes();

        // The desired columns to be bound
        String[] columns = new String[]{
                NotesDbAdapter.KEY_NAME,
                NotesDbAdapter.KEY_DESCRIPTION,
                NotesDbAdapter.KEY_PRIORITY
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.name,
                R.id.description,
                R.id.priority,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                getContext(), R.layout.notes_info,
                cursor,
                columns,
                to,
                0);

        // Assign adapter to ListView
        mListView.setAdapter(dataAdapter);

        // set filter to adapter
        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchNotesByName(constraint.toString());
            }
        });

    }


    private long note_id=0;
    private String note_name=null;
    private String note_description=null;
    private long note_priority=0;

    public void editnote(Cursor cursor) {

        // --- collect the variables from the cursor
        // Get the description from this row in the database.
        note_id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        // Get the description from this row in the database.
        note_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        // Get the description from this row in the database.
        note_description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        // Get the description from this row in the database.
        note_priority = cursor.getLong(cursor.getColumnIndexOrThrow("priority"));

        NotesEditFragment noteseditFragment = NotesEditFragment.newInstance(note_id);
        noteseditFragment.show(getActivity().getSupportFragmentManager(), "notesedit");

    }

    public void createnote() {

        NotesEditFragment noteseditFragment = NotesEditFragment.newInstance(-1);
        noteseditFragment.show(getActivity().getSupportFragmentManager(), "notescreate");

    }


}
