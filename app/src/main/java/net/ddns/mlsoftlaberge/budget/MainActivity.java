package net.ddns.mlsoftlaberge.budget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import net.ddns.mlsoftlaberge.budget.contacts.ContactAdminFragment;
import net.ddns.mlsoftlaberge.budget.contacts.ContactsBudgetFragment;
import net.ddns.mlsoftlaberge.budget.contacts.ContactsListFragment;
import net.ddns.mlsoftlaberge.budget.notes.NotesEditFragment;
import net.ddns.mlsoftlaberge.budget.notes.NotesFragment;
import net.ddns.mlsoftlaberge.budget.products.ProductDetailFragment;
import net.ddns.mlsoftlaberge.budget.products.ProductsListFragment;
import net.ddns.mlsoftlaberge.budget.speech.ConversationFragment;
import net.ddns.mlsoftlaberge.budget.speech.PerroquetFragment;
import net.ddns.mlsoftlaberge.budget.speech.DiscussionFragment;
import net.ddns.mlsoftlaberge.budget.trycorder.TrycorderActivity;
import net.ddns.mlsoftlaberge.budget.budget.BudgetFragment;
import net.ddns.mlsoftlaberge.budget.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements ContactsListFragment.OnContactsInteractionListener,
        ProductsListFragment.OnProductsInteractionListener,
        ProductDetailFragment.OnProductsInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        NotesFragment.OnNoteListener,
        NotesEditFragment.OnNoteListener {

    private static final String TAG = "MainActivity";

    // =====================================================================================
    // preferences values loaded at start
    private boolean tabbedMode;
    private String defaultLanguage;
    private String defaultFragment;

    // =====================================================================================
    // fragments holders to keep them in memory
    private NotesFragment notesFragment = null;
    private BudgetFragment budgetFragment = null;
    private ContactsBudgetFragment contactsbudgetFragment = null;
    private PerroquetFragment perroquetFragment = null;
    private DiscussionFragment discussionFragment = null;
    private ConversationFragment conversationFragment = null;
    private ContactsListFragment contactslistFragment = null;
    private ContactAdminFragment contactadminFragment = null;
    private ProductsListFragment productslistFragment = null;
    private ProductDetailFragment productdetailFragment = null;
    private int currentfragment = 0;
    private Uri currentcontacturi = null;
    private Uri currentproducturi = null;


    // =====================================================================================
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    // the tab layout holder
    private TabLayout tabLayout;

    // will host fragment content when in drawer mode
    private LinearLayout linLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if (BuildConfig.DEBUG) {
        //    Utils.enableStrictMode();
        //}
        super.onCreate(savedInstanceState);

        // initialize defaults preferences once
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // read the needed preferences for this module
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        defaultLanguage = sharedPref.getString("pref_key_default_language", "");
        defaultFragment = sharedPref.getString("pref_key_default_fragment", "");
        tabbedMode = sharedPref.getBoolean("pref_key_tabbed_mode", false);

        if (tabbedMode) {
            // load the initial screen in tabbed mode
            setContentView(R.layout.activity_main_tab);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);


        } else {
            // load the initial screen in drawer mode
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // initialize the drawer to switch between modules
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            // initialize the navigation menu
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            linLayout = (LinearLayout) findViewById(R.id.main_content);
        }

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

    }

    @Override
    public void onStart() {
        super.onStart();
        autostart();
    }

    public void autostart() {
        // initiate the first menu item as auto-selected depending on settings
        if(tabbedMode) {
            if (defaultFragment.equals("Budget")) {
                mViewPager.setCurrentItem(0);
            } else if (defaultFragment.equals("Notes")) {
                    mViewPager.setCurrentItem(1);
            } else if (defaultFragment.equals("Conversation")) {
                mViewPager.setCurrentItem(2);
            } else if (defaultFragment.equals("Discussion")) {
                mViewPager.setCurrentItem(3);
            } else if (defaultFragment.equals("Perroquet")) {
                mViewPager.setCurrentItem(4);
            } else if (defaultFragment.equals("Contacts")) {
                mViewPager.setCurrentItem(5);
            } else if (defaultFragment.equals("Products")) {
                mViewPager.setCurrentItem(7);
            } else if (defaultFragment.equals("CBudget")) {
                mViewPager.setCurrentItem(9);
            } else if (defaultFragment.equals("Trycorder")) {
                trycorderactivity();
            } else if (defaultFragment.equals("Settings")) {
                settingsactivity();
            } else {
                mViewPager.setCurrentItem(0);
            }
            hideSoftKeyboard(mViewPager);
        } else {
            if (defaultFragment.equals("Budget")) {
                budgetfragment();
            } else if (defaultFragment.equals("Notes")) {
                notesfragment();
            } else if (defaultFragment.equals("Conversation")) {
                conversationfragment();
            } else if (defaultFragment.equals("Discussion")) {
                discussionfragment();
            } else if (defaultFragment.equals("Perroquet")) {
                perroquetfragment();
            } else if (defaultFragment.equals("Contacts")) {
                contactslistfragment();
            } else if (defaultFragment.equals("Products")) {
                productslistFragment();
            } else if (defaultFragment.equals("CBudget")) {
                contactsbudgetfragment();
            } else if (defaultFragment.equals("Trycorder")) {
                trycorderactivity();
            } else if (defaultFragment.equals("Settings")) {
                settingsactivity();
            } else {
                budgetfragment();
            }
            hideSoftKeyboard(linLayout);
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // =====================================================================================
    // section for tabbed mode
    // =====================================================================================

    private int pagePosition=0;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container,position,object);
            pagePosition=position;
            if(pagePosition==6) {
                if (contactadminFragment != null)
                    contactadminFragment.setContact(currentcontacturi);
            }
            if(pagePosition==8) {
                if (productdetailFragment != null)
                    productdetailFragment.setProduct(currentproducturi);
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0) {
                budgetFragment = new BudgetFragment();
                return budgetFragment;
            } else if(position==1) {
                notesFragment = new NotesFragment();
                return notesFragment;
            } else if(position==2) {
                conversationFragment = new ConversationFragment();
                return conversationFragment;
            } else if(position==3) {
                discussionFragment = new DiscussionFragment();
                return discussionFragment;
            } else if(position==4) {
                perroquetFragment = new PerroquetFragment();
                return perroquetFragment;
            } else if(position==5) {
                contactslistFragment = new ContactsListFragment();
                return contactslistFragment;
            } else if(position==6) {
                contactadminFragment = new ContactAdminFragment();
                return contactadminFragment;
            } else if(position==7) {
                productslistFragment = new ProductsListFragment();
                return productslistFragment;
            } else if(position==8) {
                productdetailFragment = new ProductDetailFragment();
                return productdetailFragment;
            } else if(position==9) {
                contactsbudgetFragment = new ContactsBudgetFragment();
                return contactsbudgetFragment;
            }
            budgetFragment = new BudgetFragment();
            return budgetFragment;
        }

        @Override
        public int getCount() {
            // Show 10 total pages.
            return 10;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Budget";
                case 1:
                    return "Notes";
                case 2:
                    return "Conversation";
                case 3:
                    return "Discussion";
                case 4:
                    return "Perroquet";
                case 5:
                    return "Contacts";
                case 6:
                    return "Client";
                case 7:
                    return "Inventaire";
                case 8:
                    return "Produit";
                case 9:
                    return "CBudget";
            }
            return null;
        }
    }

    // =====================================================================================
    // section common for drawer and tabbed modes
    // =====================================================================================

    @Override
    public void onBackPressed() {
        if(tabbedMode) {
            int tabno = mViewPager.getCurrentItem();
            if(tabno==8) {
                mViewPager.setCurrentItem(7);
            } else if(tabno==6) {
                mViewPager.setCurrentItem(5);
            } else {
                super.onBackPressed();
            }
            return;
        }
        // to do only in drawer mode
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentfragment == 4) {
                contactslistfragment();
            } else if (currentfragment == 6) {
                productslistFragment();
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
        } else if (id == R.id.action_trycorder) {
            trycorderactivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // =====================================================================================
    // callbacks of fragments to handle in the activity

    // ================================== NOTES ========================================
    // redisplay the list if a note has been modified
    @Override
    public void onNoteModified(long id) {
        if (notesFragment != null) notesFragment.displayListView();
    }

    // do nothing if note selected
    @Override
    public void onNoteSelected(long id) {
        // the edit fragment is called by the notesfragment
    }

    // ================================== ContactsList ========================================
    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
        currentcontacturi=contactUri;
        if(tabbedMode) {
            mViewPager.setCurrentItem(6);
            if(contactadminFragment!=null) contactadminFragment.setContact(currentcontacturi);
        } else {
            contactadminfragment(contactUri);
        }
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

    // ================================== ProductsList ========================================
    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param productUri The product Uri to the selected product.
     */
    @Override
    public void onProductSelected(Uri productUri) {
        currentproducturi=productUri;
        if(tabbedMode) {
            mViewPager.setCurrentItem(8);
            if(productdetailFragment!=null) productdetailFragment.setProduct(currentproducturi);
        } else {
            productfragment(productUri);
        }
    }

    /**
     * This interface callback lets the main products list fragment notify
     * this activity that a product is no longer selected.
     */
    @Override
    public void onNewProductSelected() {
        currentproducturi = null;
        if(tabbedMode) {
            mViewPager.setCurrentItem(8);
            if(productdetailFragment!=null) productdetailFragment.setProduct(currentproducturi);
        } else {
            productfragment(null);
        }
    }

    // ================================== ProductsDetail ========================================

    /**
     * This interface callback lets the main products list fragment notify
     * this activity that a product is no longer selected.
     */
    @Override
    public void onProductSaved() {
        if(tabbedMode) {
            mViewPager.setCurrentItem(7);
        } else {
            productslistFragment();
        }
    }


    // =====================================================================================
    // settings activity incorporation in the display
    public void settingsactivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    // =====================================================================================
    // trycorder activity incorporation in the display
    public void trycorderactivity() {
        Intent i = new Intent(this, TrycorderActivity.class);
        startActivity(i);
    }

    // =====================================================================================
    // section for navigation with a drawer
    // =====================================================================================

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // choose the fragment to instantiate and connect to main content screen
        if (id == R.id.nav_notes) {
            notesfragment();
        } else if (id == R.id.nav_perroquet) {
            perroquetfragment();
        } else if (id == R.id.nav_trycorder) {
            trycorderactivity();
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
            productslistFragment();
        } else if (id == R.id.nav_product) {
            productfragment(null);
        } else if (id == R.id.nav_contactsbudget) {
            contactsbudgetfragment();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        // close the drawer to see the selected fragment
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

    public void productslistFragment() {
        setTitle("Products List");
        if (productslistFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            productslistFragment = new ProductsListFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            productslistFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, productslistFragment).commit();
        currentfragment = 5;
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

    // =====================================================================================

    // =====================================================================================
    // perroquet fragment incorporation in the display
    public void perroquetfragment() {
        setTitle("Perroquet Fragment");

        if (perroquetFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            perroquetFragment = new PerroquetFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            perroquetFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, perroquetFragment).commit();
        currentfragment = 7;
    }


    // =====================================================================================
    // discussion fragment incorporation in the display
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
    // conversation fragment incorporation in the display
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
    // conversation fragment incorporation in the display
    public void contactsbudgetfragment() {
        setTitle("Contacts Budget Fragment");

        if (contactsbudgetFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            contactsbudgetFragment = new ContactsBudgetFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            contactsbudgetFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, contactsbudgetFragment).commit();
        currentfragment = 10;
    }


}
