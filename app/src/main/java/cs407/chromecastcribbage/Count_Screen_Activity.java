package cs407.chromecastcribbage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.cast.games.GameManagerState;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class Count_Screen_Activity extends AppCompatActivity implements GameManagerClient.Listener{

    ImageView card1;
    ImageView card2;
    ImageView card3;
    ImageView card4;
    ImageView turnCard;
    TextView countView1;
    TextView countView2;
    TextView totalScore;
    Hand hand;
    Hand cribHand;
    Button confirmButton;
    TextView turnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count__screen_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ChromeCast Cribbage");
        setSupportActionBar(toolbar);

        Welcome_Activity.mCastConnectionManager.getGameManagerClient().setListener(this);

        Bundle prev = getIntent().getExtras();

        card1 = (ImageView) findViewById(R.id.cardOneIV);
        card2 = (ImageView) findViewById(R.id.cardTwoIV);
        card3 = (ImageView) findViewById(R.id.cardThreeIV);
        card4 = (ImageView) findViewById(R.id.cardFourIV);
        turnCard = (ImageView) findViewById(R.id.turnCardIV);
        countView1 = (TextView) findViewById(R.id.countString1);
        countView2 = (TextView) findViewById(R.id.countString2);
        totalScore = (TextView) findViewById(R.id.totalScore1);
        hand = new Hand();
        cribHand = new Hand();

        String code1 = prev.getString("card1");
        String code2 = prev.getString("card2");
        String code3 = prev.getString("card3");
        String code4 = prev.getString("card4");
        String code5 = prev.getString("turnCard");

        hand.addCard(new Card(String.valueOf(code1.charAt(0)),String.valueOf(code1.charAt(1))));
        hand.addCard(new Card(String.valueOf(code2.charAt(0)),String.valueOf(code2.charAt(1))));
        hand.addCard(new Card(String.valueOf(code3.charAt(0)),String.valueOf(code3.charAt(1))));
        hand.addCard(new Card(String.valueOf(code4.charAt(0)),String.valueOf(code4.charAt(1))));
        hand.addCard(new Card(String.valueOf(code5.charAt(0)),String.valueOf(code5.charAt(1))));


        Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(0).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card1);
        Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(1).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card2);
        Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(2).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card3);
        Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(3).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card4);
        Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(4).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(turnCard);

        hand.sortByValueLowHigh();


        //String[] score = countString.split("Total Score: ");

        confirmButton = (Button) findViewById(R.id.confirmHandButton);
        turnText = (TextView) findViewById(R.id.turnText);


        String countString = Counter.count(hand);
        //String countString = prev.getString("countString");
        String[] score = countString.split("\n");
        String breakdown1 = "";
        String breakdown2 = "";
        int counter = 0;
        for(String line : score){
            if(!line.endsWith("0") && !line.startsWith("Total Score: ")){
                if(counter < 5){
                    breakdown1 = breakdown1 + line + "\n";
                } else{
                    breakdown2 = breakdown2 + line + "\n";
                }
            }
        }
        countView1.setText(breakdown1);
        countView2.setText(breakdown2);
        String[] count = score[9].split("Total Score: ");
        totalScore.setText(count[1]);


        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("count", "Yes");
            jsonMessage.put("handCountString", breakdown1+breakdown2);
            jsonMessage.put("handCount", count[1]);
        } catch (JSONException e) {
            Log.e("json", "Error creating JSON message", e);
            return;
        }
        Welcome_Activity.mCastConnectionManager.getGameManagerClient().sendGameMessage(jsonMessage);

        //TODO: When the Chromecast is ready move to the deal screen
    }

    public void confirmHand(View view) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("move", "Next");
        } catch (JSONException e) {
            Log.e("json", "Error creating JSON message", e);
            return;
        }
        Welcome_Activity.mCastConnectionManager.getGameManagerClient().sendGameMessage(jsonMessage);
        confirmButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStateChanged(GameManagerState gameManagerState, GameManagerState gameManagerState1) {

    }

    @Override
    public void onGameMessageReceived(String playerId, JSONObject message) {
        if (message.has("yourTurn")) {
            try {
                String msg = message.getString("yourTurn");
                if(msg.equals("Crib")){
                    confirmButton.setText("Confirm the Crib Shown");
                }
                confirmButton.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(message.has("cribCard1")){

            try {
                String code1 = message.getString("cribCard1");
                String code2 = message.getString("cribCard2");
                String code3 = message.getString("cribCard3");
                String code4 = message.getString("cribCard4");
                String code5 = message.getString("cribCard5");

                cribHand.addCard(new Card(String.valueOf(code1.charAt(0)),String.valueOf(code1.charAt(1))));
                cribHand.addCard(new Card(String.valueOf(code2.charAt(0)),String.valueOf(code2.charAt(1))));
                cribHand.addCard(new Card(String.valueOf(code3.charAt(0)),String.valueOf(code3.charAt(1))));
                cribHand.addCard(new Card(String.valueOf(code4.charAt(0)),String.valueOf(code4.charAt(1))));
                cribHand.addCard(new Card(String.valueOf(code5.charAt(0)),String.valueOf(code5.charAt(1))));
                hand.sortByValueLowHigh();

                String countString = Counter.count(cribHand);
                String[] score = countString.split("\n");
                String breakdown1 = "";
                String breakdown2 = "";
                int counter = 0;
                for(String line : score){
                    if(!line.endsWith("0") && !line.startsWith("Total Score: ")){
                        if(counter < 5){
                            breakdown1 = breakdown1 + line + "\n";
                        } else{
                            breakdown2 = breakdown2 + line + "\n";
                        }
                    }
                }
                String[] count = score[9].split("Total Score: ");

                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("crib", "Yes");
                    jsonMessage.put("handCountString", breakdown1+breakdown2);
                    jsonMessage.put("handCount", count[1]);
                } catch (JSONException e) {
                    Log.e("json", "Error creating JSON message", e);
                    return;
                }
                Welcome_Activity.mCastConnectionManager.getGameManagerClient().sendGameMessage(jsonMessage);

                Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(0).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card1);
                Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(1).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card2);
                Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(2).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card3);
                Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(3).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(card4);
                Picasso.with(this).load("https://deckofcardsapi.com/static/img/"+hand.getCard(4).getFileName()+".png").placeholder(R.drawable.back).error(R.drawable.error).into(turnCard);

                countView1.setText(breakdown1);
                countView2.setText(breakdown2);
                totalScore.setText(count[1]);
                turnText.setText("Your Crib Count");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }  else if(message.has("toDealScreen")) {
            Intent intent = new Intent(this, Deal_Activity.class);
            startActivity(intent);
        }
    }


}
