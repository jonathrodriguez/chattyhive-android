package com.chattyhive.chattyhive;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.chattyhive.backend.Controller;

public class Explore extends Activity {

    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);

        this.Initialize();
    }

    private void Initialize() {
        this.controller = Controller.getRunningController();


    }
}
