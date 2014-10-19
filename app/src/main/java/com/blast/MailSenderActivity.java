package com.blast;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MailSenderActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //copy this try/catch block into MainActivity
                try {
                    GMailSender sender = new GMailSender("anemailtousedude@gmail.com", "originalpassword");
                    sender.sendMail("Hey, where are you?",
                            "Call me!!",
                            "anemailtousedude@gmail.com",
                            "brockuniera@utexas.edu");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }

            }
        });

    }
}