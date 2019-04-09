package com.ffrowies.infnserver;

import android.content.Intent;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BottomAppBar bottomAppBar;
    FloatingActionButton fab;
    TextView txvAboutDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvAboutDev = (TextView) findViewById(R.id.txvAboutDev);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomersList.class);
                startActivity(intent);
            }
        });

        bottomAppBar = (BottomAppBar) findViewById(R.id.bootom_app_bar);
        bottomAppBar.replaceMenu(R.menu.main_menu);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.action_reservations:
                        Toast.makeText(MainActivity.this, "Reservations clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_groups:
                        Toast.makeText(MainActivity.this, "Groups clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_camera:
                        Toast.makeText(MainActivity.this, "Camera clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}
