package net.ddns.mlsoftlaberge.budget.products;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import net.ddns.mlsoftlaberge.budget.R;
import net.ddns.mlsoftlaberge.budget.products.contentprovider.MyProductContentProvider;
import net.ddns.mlsoftlaberge.budget.products.database.ProductTable;

/*
 * ProductsListFragment displays the existing product items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete existing ones via a long press on the item
 */

public class ProductsListFragment extends ListFragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	// private Cursor cursor;
	private SimpleCursorAdapter adapter;

    // Product selected listener that allows the activity holding this fragment to be notified of
    // a product being selected
    private OnProductsInteractionListener mOnProductSelectedListener;


    // need an empty constructor
	public ProductsListFragment() {
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Assign callback listener which the holding activity must implement. This is used
            // so that when a contact item is interacted with (selected by the user) the holding
            // activity will be notified and can take further action such as populating the contact
            // detail pane (if in multi-pane layout) or starting a new activity with the contact
            // details (single pane layout).
            mOnProductSelectedListener = (OnProductsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductsInteractionListener");
        }
    }


    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Let this fragment contribute menu items
        setHasOptionsMenu(true);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the list fragment layout
		View view = inflater.inflate(R.layout.product_list, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.getListView().setDividerHeight(2);
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu items
		inflater.inflate(R.menu.product_list_menu, menu);
	}

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createProduct();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri.parse(MyProductContentProvider.CONTENT_URI + "/"
					+ info.id);
			getActivity().getContentResolver().delete(uri, null, null);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createProduct() {
		// Old method to invoke the other class
		//Intent i = new Intent(getActivity(), ProductDetailFragment.class);
		//startActivity(i);
        mOnProductSelectedListener.onNewProductSelected();
	}

	// Opens the second activity if an entry is clicked
	//@Override
	//public void onListItemClick(ListView l, View v, int position, long id) {
	//	super.onListItemClick(l, v, position, id);
	//	Intent i = new Intent(getActivity(), ProductDetailFragment.class);
	//	Uri productUri = Uri.parse(MyProductContentProvider.CONTENT_URI + "/" + id);
	//	i.putExtra(MyProductContentProvider.CONTENT_ITEM_TYPE, productUri);
	//	// Activity returns an result if called with startActivityForResult
	//	startActivity(i);
	//}

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // Creates a product lookup Uri from product ID
        Uri productUri = Uri.parse(MyProductContentProvider.CONTENT_URI + "/" + id);

        // Notifies the parent activity that the user selected a contact. In a two-pane layout, the
        // parent activity loads a ContactAdminFragment that displays the details for the selected
        // contact. In a single-pane layout, the parent activity starts a new activity that
        // displays contact details in its own Fragment.
        mOnProductSelectedListener.onProductSelected(productUri);
    }


    private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { ProductTable.COLUMN_NAME };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getContext(), R.layout.product_row, null, from,
				to, 0);

		setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// Creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { ProductTable.COLUMN_ID, ProductTable.COLUMN_NAME };
		CursorLoader cursorLoader = new CursorLoader(getContext(),
				MyProductContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}

    /**
     * This interface must be implemented by any activity that loads this fragment. When an
     * interaction occurs, such as touching an item from the ListView, these callbacks will
     * be invoked to communicate the event back to the activity.
     */
    public interface OnProductsInteractionListener {
        /**
         * Called when a product is selected from the ListView.
         *
         * @param productUri The product Uri.
         */
        public void onProductSelected(Uri productUri);

        /**
         * Called when the ListView selection is cleared like when
         * a product search is taking place or is finishing.
         */
        public void onNewProductSelected();
    }


}