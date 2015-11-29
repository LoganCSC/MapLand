package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.GameBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.MapLandApiService;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to add a new location with a certain user as owner.
 * Note: The user also needs to have the id for this location added to their list of owned locations.
 */
public class GameAdder extends AsyncTask<Pair<Context, GameBean>, Void, GameBean> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void addGame(GameBean loc, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, GameBean>, Void, GameBean> task = new GameAdder(callback);

        task.execute(new Pair<>(context, loc));

        Log.i("TASK", "add game status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private GameAdder(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected GameBean doInBackground(Pair<Context, GameBean>... params) {

        context = params[0].first;
        GameBean game = params[0].second;

        try {
            // Can all these parameters be replaced with just the bean?
            return MapLandApiService.getInstance()
                    .addNewGame(game.getGameName(),
                            game.getNumPlayers(), game.getDuration(), game.getRegionCostPercentIncrease(),
                            ":" + game.getNotes(),  // cannot be ""
                            game.getNwLatitudeCoord(), game.getNwLongitudeCoord(),
                            game.getSeLatitudeCoord(), game.getSeLongitudeCoord())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(GameBean result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }

}