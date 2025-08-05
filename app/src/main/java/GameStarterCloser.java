import android.app.Application;

import com.example.wallstreettycoon.Game;

public class GameStarterCloser extends Application {
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Game.saveToFile(Game.currentUser.getUserUsername() + ".ser");
        }
    }
    @Override
    public void onTerminate(){
        super.onTerminate();
        if (Game.gameInstance != null) {
            Game.saveGame();
        }
    }
}
