package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.Random;

public class Main4Activity extends AppCompatActivity {

    /* Hint:
        1. This creates the Whack-A-Mole layout and starts a countdown to ready the user
        2. The game difficulty is based on the selected level
        3. The levels are with the following difficulties.
            a. Level 1 will have a new mole at each 10000ms.
                - i.e. level 1 - 10000ms
                       level 2 - 9000ms
                       level 3 - 8000ms
                       ...
                       level 10 - 1000ms
            b. Each level up will shorten the time to next mole by 100ms with level 10 as 1000 second per mole.
            c. For level 1 ~ 5, there is only 1 mole.
            d. For level 6 ~ 10, there are 2 moles.
            e. Each location of the mole is randomised.
        4. There is an option return to the login page.
     */
    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    CountDownTimer readyTimer;
    CountDownTimer newMolePlaceTimer;
    private TextView score;
    Integer selectedLevel;
    String userName;
    private final int[] scores = {0};
    Button backBtn;
    MyDBHandler dbHandler = new MyDBHandler(this,null,null,1);

    private void readyTimer(){
        /*  HINT:
            The "Get Ready" Timer.
            Log.v(TAG, "Ready CountDown!" + millisUntilFinished/ 1000);
            Toast message -"Get Ready In X seconds"
            Log.v(TAG, "Ready CountDown Complete!");
            Toast message - "GO!"
            belongs here.
            This timer countdown from 10 seconds to 0 seconds and stops after "GO!" is shown.
         */
       readyTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                Toast.makeText(getApplicationContext(), "Ready CountDown!" + millisUntilFinished/1000 ,Toast.LENGTH_SHORT).show();

            }

            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Ready CountDown Complete! " + selectedLevel,Toast.LENGTH_SHORT).show();
                setNewMole();
                readyTimer.cancel();
                placeMoleTimer(selectedLevel);


            }
        };
        readyTimer.start();


    }
    private void placeMoleTimer(int level){
        /* HINT:
           Creates new mole location each second.
           Log.v(TAG, "New Mole Location!");
           setNewMole();
           belongs here.
           This is an infinite countdown timer.
         */
        newMolePlaceTimer = new CountDownTimer((11 - level) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                setNewMole();
                Log.v(TAG, "New Mole Location!");
            }

            public void onFinish() {
               newMolePlaceTimer.start();


            }
        };

        newMolePlaceTimer.start();
    }
    private static final int[] BUTTON_IDS = {
            /* HINT:
                Stores the 9 buttons IDs here for those who wishes to use array to create all 9 buttons.
                You may use if you wish to change or remove to suit your codes.*/
            R.id.button4,
            R.id.button5,
            R.id.button6,
            R.id.button7,
            R.id.button8,
            R.id.button9,
            R.id.button10,
            R.id.button11,
            R.id.button12,
    };
    private OnClickListener click = new OnClickListener() {
        public void onClick(View v) {
            for(int id = 0; id < BUTTON_IDS.length; id++) {
                if (v.getId() == BUTTON_IDS[id]) {
                    doCheck((Button) findViewById(BUTTON_IDS[id]));
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        /*Hint:
            This starts the countdown timers one at a time and prepares the user.
            This also prepares level difficulty.
            It also prepares the button listeners to each button.
            You may wish to use the for loop to populate all 9 buttons with listeners.
            It also prepares the back button and updates the user score to the database
            if the back button is selected.
         */
        score = (TextView) findViewById(R.id.score);
        backBtn = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        selectedLevel = intent.getIntExtra("level",0);
        userName = intent.getStringExtra("username");

        readyTimer();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserScore();
                Intent intent = new Intent(Main4Activity.this, Main3Activity.class);
                intent.putExtra("username",userName);
                startActivity(intent);
            }
        });

        for(int id = 0; id < BUTTON_IDS.length; id++){
            /*  HINT:
            This creates a for loop to populate all 9 buttons with listeners.
            You may use if you wish to remove or change to suit your codes.
            */
            Button butt = findViewById(BUTTON_IDS[id]);
            butt.setOnClickListener(click);
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        readyTimer();
    }
    private void doCheck(Button checkButton)
    {
        /* Hint:
            Checks for hit or miss
            Log.v(TAG, FILENAME + ": Hit, score added!");
            Log.v(TAG, FILENAME + ": Missed, point deducted!");
            belongs here.
        */
        if (checkButton.getText().equals("*")) {
            scores[0]++;
            Log.v(TAG, "Hit,score added!");
        }
        else {
            scores[0]--;
            Log.v(TAG, "Missed,score deducted!");
        }
        score.setText(scores[0] + "");
        setNewMole();

    }

    public void setNewMole()
    {
        /* Hint:
            Clears the previous mole location and gets a new random location of the next mole location.
            Sets the new location of the mole. Adds additional mole if the level difficulty is from 6 to 10.
         */

        for(int id = 0; id < BUTTON_IDS.length ;id++){
            Button butt = findViewById(BUTTON_IDS[id]);
            butt.setText("0");
        }

        Random ran = new Random();
        int randomLocation = ran.nextInt(9);
        Button moleButton = findViewById(BUTTON_IDS[randomLocation]);
        moleButton.setText("*");
        Log.v(TAG, "New mole location!");
        if(selectedLevel >= 6)
        {
            boolean b = true;
            int randomLocation2 = ran.nextInt(9);
            while (b)
            {
                if(randomLocation != randomLocation2)
                {
                    b = false;
                }
                randomLocation2 = ran.nextInt(9);
            }

            moleButton = findViewById(BUTTON_IDS[randomLocation2]);
            moleButton.setText("*");
        }

    }

    private void updateUserScore()
    {

     /* Hint:
        This updates the user score to the database if needed. Also stops the timers.
        Log.v(TAG, FILENAME + ": Update User Score...");
      */
        UserData userData = dbHandler.findUser(userName);
        if(scores[0] > userData.getScores().get(selectedLevel -1))
        {
            userData.getScores().set(selectedLevel-1,scores[0]);
        }
        dbHandler.deleteAccount(userName);
        dbHandler.addUser(userData);

    }

}
