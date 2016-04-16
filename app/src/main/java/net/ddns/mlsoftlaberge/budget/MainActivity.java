package net.ddns.mlsoftlaberge.budget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.ddns.mlsoftlaberge.budget.contacts.ContactAdminFragment;
import net.ddns.mlsoftlaberge.budget.contacts.ContactsListFragment;
import net.ddns.mlsoftlaberge.budget.notes.NotesEditFragment;
import net.ddns.mlsoftlaberge.budget.notes.NotesFragment;
import net.ddns.mlsoftlaberge.budget.products.ProductDetailFragment;
import net.ddns.mlsoftlaberge.budget.products.ProductsListFragment;
import net.ddns.mlsoftlaberge.budget.sensors.ConversationFragment;
import net.ddns.mlsoftlaberge.budget.sensors.SensorFragment;
import net.ddns.mlsoftlaberge.budget.sensors.DiscussionFragment;
import net.ddns.mlsoftlaberge.budget.utils.BudgetFragment;
import net.ddns.mlsoftlaberge.budget.utils.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements ContactsListFragment.OnContactsInteractionListener,
        ProductsListFragment.OnProductsInteractionListener,
        ProductDetailFragment.OnProductsInteractionListener,
        NavigationView.OnNavigationItemSelectedListener ,
        NotesFragment.OnNoteListener, 
        NotesEditFragment.OnNoteListener {

    private static final String TAG = "MainActivity";

    private String defaultLanguage;
    private String defaultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if (BuildConfig.DEBUG) {
        //    Utils.enableStrictMode();
        //}
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        defaultLanguage = sharedPref.getString("pref_key_default_language", "");
        defaultFragment = sharedPref.getString("pref_key_default_fragment", "");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // prepare a floating button, but hide it for later purpose
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // initiate the first menu item as auto-selected
        conversationfragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentfragment == 4) {
                contactslistfragment();
            } else if (currentfragment == 6) {
                inventoryfragment();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            settingsactivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // choose the fragment to instantiate and connect to main content screen
        if (id == R.id.nav_notes) {
            notesfragment();
        } else if (id == R.id.nav_sensor) {
            sensorfragment();
        } else if (id == R.id.nav_discussion) {
            discussionfragment();
        } else if (id == R.id.nav_conversation) {
            conversationfragment();
        } else if (id == R.id.nav_budget) {
            budgetfragment();
        } else if (id == R.id.nav_contactslist) {
            contactslistfragment();
        } else if (id == R.id.nav_contact) {
            contactadminfragment(null);
        } else if (id == R.id.nav_inventory) {
            inventoryfragment();
        } else if (id == R.id.nav_product) {
            productfragment(null);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        // close the drawer to see the selected fragment
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // =====================================================================================
    // fragments holders to keep them in memory
    private NotesFragment notesFragment = null;
    private BudgetFragment budgetFragment = null;
    private SensorFragment sensorFragment = null;
    private DiscussionFragment discussionFragment = null;
    private ConversationFragment conversationFragment = null;
    private ContactsListFragment contactslistFragment = null;
    private ContactAdminFragment contactadminFragment = null;
    private ProductsListFragment inventoryFragment = null;
    private ProductDetailFragment productdetailFragment = null;
    private int currentfragment = 0;
    private Uri currentcontacturi = null;
    private Uri currentproducturi = null;

    // =====================================================================================
    // first fragment incorporation in the display
    public void notesfragment() {
        setTitle("Notes Fragment");

        if (notesFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            notesFragment = new NotesFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            notesFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, notesFragment).commit();
        currentfragment = 1;
    }

        // redisplay the list if a note has been modified
    @Override
    public void onNoteModified(long id) {
        if(notesFragment!=null) notesFragment.displayListView();
    }

    // do nothing if note selected
    @Override
    public void onNoteSelected(long id) {
        // the edit fragment is called by the notesfragment
    }

    // =====================================================================================
    // second fragment incorporation in the display
    public void budgetfragment() {
        setTitle("Budget Fragment");

        if (budgetFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            budgetFragment = new BudgetFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            budgetFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, budgetFragment).commit();
        currentfragment = 2;
    }

    // =====================================================================================
    // contacts fragment incorporation in the display
    public void contactslistfragment() {
        setTitle("Contacts List");
        if (contactslistFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            contactslistFragment = new ContactsListFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            contactslistFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, contactslistFragment).commit();
        currentfragment = 3;
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
        contactadminfragment(contactUri);
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact is no longer selected.
     */
    @Override
    public void onSelectionCleared() {

    }

    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return super.onSearchRequested();
    }

    // =====================================================================================

    public void contactadminfragment(Uri contactUri) {
        setTitle("Contact Detail");
        if (contactUri != null) currentcontacturi = contactUri;
        // create fragment if necessary and switch to it
        contactadminFragment = ContactAdminFragment.newInstance(currentcontacturi);
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        //contactadminFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, contactadminFragment, TAG).commit();
        currentfragment = 4;
    }

    // =====================================================================================

    public void inventoryfragment() {
        setTitle("Products List");
        if (inventoryFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            inventoryFragment = new ProductsListFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            inventoryFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, inventoryFragment).commit();
        currentfragment = 5;
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param productUri The product Uri to the selected product.
     */
    @Override
    public void onProductSelected(Uri productUri) {
        productfragment(productUri);
    }

    /**
     * This interface callback lets the main products list fragment notify
     * this activity that a product is no longer selected.
     */
    @Override
    public void onNewProductSelected() {
        currentproducturi=null;
        productfragment(null);
    }

    // =====================================================================================

    public void productfragment(Uri productUri) {
        setTitle("Product Detail");
        if (productUri != null) currentproducturi = productUri;
        // start an activity with product
        //Intent i = new Intent(this, ProductDetailFragment.class);
        //i.putExtra(MyProductContentProvider.CONTENT_ITEM_TYPE, currentproducturi);
        // Activity returns an result if called with startActivityForResult
        //startActivity(i);
        // create fragment if necessary and switch to it
        productdetailFragment = ProductDetailFragment.newInstance(currentproducturi);
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        //contactadminFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, productdetailFragment, TAG).commit();
        currentfragment = 6;
    }

    /**
     * This interface callback lets the main products list fragment notify
     * this activity that a product is no longer selected.
     */
    @Override
    public void onProductSaved() {
        inventoryfragment();
    }


    // =====================================================================================

    // =====================================================================================
    // sensor fragment incorporation in the display
    public void sensorfragment() {
        setTitle("Sensor Fragment");

        if (sensorFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            sensorFragment = new SensorFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            sensorFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, sensorFragment).commit();
        currentfragment = 7;
    }


    // =====================================================================================
    // sensor fragment incorporation in the display
    public void discussionfragment() {
        setTitle("Discussion Fragment");

        if (discussionFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            discussionFragment = new DiscussionFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            discussionFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, discussionFragment).commit();
        currentfragment = 8;
    }

    // =====================================================================================
    // sensor fragment incorporation in the display
    public void conversationfragment() {
        setTitle("Conversation Fragment");

        if (conversationFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            conversationFragment = new ConversationFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            conversationFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, conversationFragment).commit();
        currentfragment = 9;
    }

    // =====================================================================================
    // settings activity incorporation in the display
    public void settingsactivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }



}
