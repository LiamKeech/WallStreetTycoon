package com.example.wallstreettycoon.minigames.miniGame3;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.GameEvent;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.GameObserver;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Model;
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEventType;

public class miniGame3 extends AppCompatActivity implements GameObserver {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mini_game3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Model model = new Model();
        model.setGameObserver(this);

        NetworkView networkView = findViewById(R.id.networkView);
        networkView.setModel(model);

        TimerView timerView = findViewById(R.id.timerView);
        timerView.setTimer(model.getTimer());

    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        //gameover open dialog fragment
        switch(gameEvent.getType()){
            case GAME_OVER:
                if((boolean)gameEvent.getCargo()){
                    Game.getInstance().getStockPriceFunction(50).onGameEvent(new com.example.wallstreettycoon.model.GameEvent(GameEventType.MARKET_EVENT, "Market factor changed", 100000.0));

                }
                else{
                    Game.getInstance().getStockPriceFunction(50).onGameEvent(new com.example.wallstreettycoon.model.GameEvent(GameEventType.MARKET_EVENT, "Market factor changed", -100.0));
                }
                //add win or loss condition
                Bundle bundle = new Bundle();
                bundle.putBoolean("win", (boolean) gameEvent.getCargo());
                DialogFragment endDialogFragment = new miniGame3EndDialogFragment();
                endDialogFragment.setArguments(bundle);
                endDialogFragment.show(getSupportFragmentManager(), "miniGame3End");
                break;
        }

    }
}