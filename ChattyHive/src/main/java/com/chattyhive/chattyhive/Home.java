package com.chattyhive.chattyhive;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Home extends Activity {

    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if ((mUsername==null) || (mUsername.isEmpty())) {
            Intent inte = null;
            inte = new Intent(this, LoginActivity.class);
            inte.putExtra(LoginActivity.EXTRA_EMAIL,mUsername);
            //TODO:recoger el resultado
            startActivityForResult(inte);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
}