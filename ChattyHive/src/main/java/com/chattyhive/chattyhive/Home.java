package com.chattyhive.chattyhive;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Home extends Activity {
    static final int OP_CODE_LOGIN = 1;
    String mUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if ((mUsername==null) || (mUsername.isEmpty())) {
            Intent inte;
            inte = new Intent(this, LoginActivity.class);
            inte.putExtra(LoginActivity.EXTRA_EMAIL,mUsername);
            startActivityForResult(inte,OP_CODE_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OP_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                mUsername = data.getStringExtra(LoginActivity.EXTRA_EMAIL);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
}