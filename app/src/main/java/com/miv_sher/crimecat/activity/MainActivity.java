package com.miv_sher.crimecat.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.miv_sher.crimecat.R;
import com.miv_sher.crimecat.fragment.CrimeListFragment;
import com.miv_sher.crimecat.fragment.HappyCrimeFragment;
import com.miv_sher.crimecat.fragment.MyCrimeFragment;
import com.miv_sher.crimecat.fragment.RecentCrimeFragment;
import com.miv_sher.crimecat.fragment.TopCrimeFragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView status;
    private Menu mainMenu;
    Class fragmentClass = RecentCrimeFragment.class;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // [END initialize_auth]
        status = (TextView)findViewById(R.id.textView_login);
       // status.setText(getString(R.string.google_status_fmt));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Fragment fragment = null;
        //fragmentClass = RecentCrimeFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Вставляем фрагмент, заменяя текущий фрагмент
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        // Выделяем выбранный пункт меню в шторке
        //item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle("Ищут дом");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_new_crime);
        if (fragmentClass == RecentCrimeFragment.class)
        {
            item.setVisible(true);
        }
        else if (fragmentClass == TopCrimeFragment.class)
        {
            item.setVisible(false);
        }
        else if (fragmentClass == MyCrimeFragment.class)
        {
            item.setVisible(true);
        }
        else if (fragmentClass == HappyCrimeFragment.class)
        {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:

                Intent intent = new Intent(this, NewCrimeActivity.class);
                startActivity(intent);
                // startActivity(new Intent(CrimeListFragment.this, NewCrimeActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
       // String name = data.getStringExtra("name");
       // tvName.setText("Your name is " + name);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Создадим новый фрагмент
        Fragment fragment = null;
        //Class fragmentClass = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_desk) {
            fragmentClass = RecentCrimeFragment.class;
            invalidateOptionsMenu();
        } else if (id == R.id.nav_happy) {
            fragmentClass = HappyCrimeFragment.class;
            invalidateOptionsMenu();
        } else if (id == R.id.nav_yours) {
            fragmentClass = MyCrimeFragment.class;
            invalidateOptionsMenu();
        } else if (id == R.id.nav_fav) {
            fragmentClass = TopCrimeFragment.class;
            invalidateOptionsMenu();
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Вставляем фрагмент, заменяя текущий фрагмент
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        // Выделяем выбранный пункт меню в шторке
        item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
