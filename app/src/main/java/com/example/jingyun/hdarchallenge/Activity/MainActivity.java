package com.example.jingyun.hdarchallenge.Activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jingyun.hdarchallenge.Fragments.ChecklistFragment;
import com.example.jingyun.hdarchallenge.Fragments.MessageFragment;
import com.example.jingyun.hdarchallenge.Fragments.NavigationFragment;
import com.example.jingyun.hdarchallenge.Fragments.ReportFragment;
import com.example.jingyun.hdarchallenge.R;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChecklistFragment.OnFragmentInteractionListener,
        NavigationFragment.OnFragmentInteractionListener,
        MessageFragment.OnFragmentInteractionListener
{

    private TextView userTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userTeam");
        Log.i("MainActivity","username received is "+userID);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton msgFab = (FloatingActionButton) findViewById(R.id.main_msg_fab);
        msgFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment msgFragment = new MessageFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, msgFragment);
                getSupportFragmentManager().popBackStack();
                transaction.commit();


            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        userTeamName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userIDName);
        navigationView.setNavigationItemSelectedListener(this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ChecklistFragment();

        if (id == R.id.nav_checklist) {
            fragment = new ChecklistFragment();

        } else if (id == R.id.nav_navigation) {
            Toast.makeText(this, "navigation pressed", Toast.LENGTH_SHORT).show();
            fragment = new NavigationFragment();

        } else if (id == R.id.nav_report) {
            fragment = new ReportFragment();

        } else if (id == R.id.nav_notify) {
            fragment = new MessageFragment();
            Toast.makeText(this, "notify clicked", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_signout) {
            //brings you to the login page
            Intent signoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(signoutIntent);
        }

        if (fragment!=null){
            transaction.replace(R.id.fragment_container, fragment);
            //allows you to get back to the main page when yuo press back
            getSupportFragmentManager().popBackStack();
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
