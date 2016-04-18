package net.ddns.mlsoftlaberge.budget.products;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.budget.R;
import net.ddns.mlsoftlaberge.budget.products.contentprovider.MyProductContentProvider;
import net.ddns.mlsoftlaberge.budget.products.database.ProductTable;

/*
 * ProductDetailFragment allows to enter a new product item
 * or to change an existing
 */
public class ProductDetailFragment extends Fragment {
	private Spinner mCategory;
	private EditText mUpcText;
	private EditText mTitleText;
	private EditText mBodyText;

    public static final String EXTRA_PRODUCT_URI =
            "net.ddns.mlsoftlaberge.budget.products.EXTRA_PRODUCT_URI";

    // Product selected listener that allows the activity holding this fragment to be notified of
    // a product being selected or saved
    private OnProductsInteractionListener mOnProductSavedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Assign callback listener which the holding activity must implement. This is used
            // so that when a contact item is interacted with (selected by the user) the holding
            // activity will be notified and can take further action such as populating the contact
            // detail pane (if in multi-pane layout) or starting a new activity with the contact
            // details (single pane layout).
            mOnProductSavedListener = (OnProductsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductsInteractionListener");
        }
    }



    private Uri productUri;

    /**
     * Factory method to generate a new instance of the fragment given a contact Uri. A factory
     * method is preferable to simply using the constructor as it handles creating the bundle and
     * setting the bundle as an argument.
     *
     * @param uri The product Uri to load
     * @return A new instance of {@link ProductDetailFragment}
     */
    public static ProductDetailFragment newInstance(Uri uri) {
        // Create new instance of this fragment
        final ProductDetailFragment fragment = new ProductDetailFragment();

        // Create and populate the args bundle
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_PRODUCT_URI, uri);

        // Assign the args bundle to the new fragment
        fragment.setArguments(args);

        // Return fragment
        return fragment;
    }

    /**
     * Fragments require an empty constructor.
     */
    public ProductDetailFragment() {
    }

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
		// Inflate the list fragment layout
		View view = inflater.inflate(R.layout.product_edit, container, false);

		mCategory = (Spinner) view.findViewById(R.id.category);
		mUpcText = (EditText) view.findViewById(R.id.product_edit_upc);
		mTitleText = (EditText) view.findViewById(R.id.product_edit_name);
		mBodyText = (EditText) view.findViewById(R.id.product_edit_description);
		Button confirmButton = (Button) view.findViewById(R.id.product_edit_button);

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(mTitleText.getText().toString())) {
					makeToast();
				} else {
                    saveState();
                    // inform activity we are finished
                    mOnProductSavedListener.onProductSaved();
                }
			}

		});

        return view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // If not being created from a previous state
        if (savedInstanceState == null) {
            // Sets the argument extra as the currently displayed contact
            productUri = (getArguments() != null ?
                    (Uri) getArguments().getParcelable(EXTRA_PRODUCT_URI) : null);
        } else {
            // If being recreated from a saved state, sets the contact from the incoming
            // savedInstanceState Bundle
            productUri = ((Uri) savedInstanceState.getParcelable(EXTRA_PRODUCT_URI));
        }
        if (productUri!=null) {
            fillData(productUri);
        }
    }

	public void setProduct(Uri uri) {
		if(uri!=null) {
			fillData(uri);
		}
	}

	private void fillData(Uri uri) {
		String[] projection = { ProductTable.COLUMN_NAME,ProductTable.COLUMN_UPC,
				ProductTable.COLUMN_DESCRIPTION, ProductTable.COLUMN_CATEGORY };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			String category = cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_CATEGORY));

			for (int i = 0; i < mCategory.getCount(); i++) {

				String s = (String) mCategory.getItemAtPosition(i);
				if (s.equalsIgnoreCase(category)) {
					mCategory.setSelection(i);
				}
			}

			mUpcText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_UPC)));
			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_NAME)));
			mBodyText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_DESCRIPTION)));

			// Always close the cursor
			cursor.close();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(MyProductContentProvider.CONTENT_ITEM_TYPE, productUri);
        outState.putParcelable(EXTRA_PRODUCT_URI, productUri);
	}

	@Override
	public void onPause() {
		super.onPause();
		// saveState();
	}

	private void saveState() {
		String category = (String) mCategory.getSelectedItem();
		String upc = mUpcText.getText().toString();
		String name = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();

		// Only save if either name or description
		// is available

		if (description.length() == 0 && name.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(ProductTable.COLUMN_CATEGORY, category);
		values.put(ProductTable.COLUMN_UPC, upc);
		values.put(ProductTable.COLUMN_NAME, name);
        values.put(ProductTable.COLUMN_DESCRIPTION, description);

		if (productUri == null) {
			// New product
			productUri = getActivity().getContentResolver().insert(
					MyProductContentProvider.CONTENT_URI, values);
		} else {
            // Update product
			getActivity().getContentResolver().update(productUri, values, null, null);
		}

    }

	private void makeToast() {
		Toast.makeText(getActivity(), "Please maintain a name",
				Toast.LENGTH_LONG).show();
	}

    /**
     * This interface must be implemented by any activity that loads this fragment. When an
     * interaction occurs, such as touching an item from the ListView, these callbacks will
     * be invoked to communicate the event back to the activity.
     */
    public interface OnProductsInteractionListener {
        /**
         * Called when the product is finished to work with
         * or a product search is taking place or is finishing.
         */
        public void onProductSaved();
    }


}
