package cs407.chromecastcribbage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Deal_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ChromeCast Cribbage");
        setSupportActionBar(toolbar);

        String userName = "playerName";
        String playerName = "playerName";

        if(playerName != userName){
            Button button = (Button) findViewById(R.id.dealButton);
            button.setVisibility(View.INVISIBLE);
            TextView textView = (TextView) findViewById(R.id.dealText);
            textView.setText("Waiting for " + userName + " to start dealing.");
        }

    }

    public void dealCards(View view) {
        Intent intent = new Intent(this, Discard_Activity.class);
        startActivity(intent);
    }
}
