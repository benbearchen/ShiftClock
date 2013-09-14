package org.bxmy.shiftclock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EditWatchActivity extends Activity {

    private int mIndex = -1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_watch);

        bind();

        mIndex = getIntent().getIntExtra("index", -1);
        initWatch(mIndex);
    }

    private void bind() {
        // bind more controls

        Button ok = (Button) findViewById(R.id.button_ok);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onOK();
            }
        });

        Button cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
    }

    private void initWatch(int position) {
    }

    private void onOK() {
        finish();
    }

    private void onCancel() {
        finish();
    }
}
