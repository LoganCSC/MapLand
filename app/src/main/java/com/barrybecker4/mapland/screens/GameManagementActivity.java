/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barrybecker4.mapland.screens;

import com.barrybecker4.mapland.FeatureView;
import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.backend.datamodel.GameBean;
import com.barrybecker4.mapland.game.RegionUtil;
import com.barrybecker4.mapland.screens.dialogs.NewGameDialogFragment;
import com.barrybecker4.mapland.screens.dialogs.OnNewGameCreatedHandler;
import com.barrybecker4.mapland.screens.games.GameDetails;
import com.barrybecker4.mapland.screens.games.GameDetailsList;
import com.google.android.gms.maps.model.LatLng;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This allows the user to create or join active games.
 * The can join a game that they or some other user has created.
 * Or they can configure and create a new game.
 */
public class GameManagementActivity extends ListActivity
        implements OnNewGameCreatedHandler {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_management);

        TextView mInstructionsTextView = (TextView) findViewById(R.id.game_management_instructions);
        ListAdapter adapter = new CustomArrayAdapter(this, GameDetailsList.GAMES);

        setListAdapter(adapter);

        Button mNewGameButton = (Button) findViewById(R.id.new_game_button);
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewGameDialog();
            }
        });
    }

    /** Persists the new game in the datastore to make it available to others */
    public void createNewGame(GameBean newGame) {
        // call the backend to add the persist the new game
    }

    /**
     * Show popup dialog to allow users to enter parameters for a new game definition
     */
    private void showNewGameDialog() {
        NewGameDialogFragment dialog = new NewGameDialogFragment();
        dialog.show(getFragmentManager(), "new-game-dialog");
    }

    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the screen.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<GameDetails> {

        /**
         * @param games An array containing the details of the open games to be displayed.
         */
        public CustomArrayAdapter(Context context, GameDetails[] games) {
            super(context, R.layout.feature, R.id.title, games);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            GameDetails game = getItem(position);

            featureView.setTitleId(game.name);
            String desc = game.getDescription();
            featureView.setDescription(desc);
            featureView.setContentDescription(game.name + ". " + desc);

            return featureView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_legal) {
            startActivity(new Intent(this, LegalInfoActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        GameDetails game = (GameDetails) getListAdapter().getItem(position);

        Toast.makeText(this, game.name, Toast.LENGTH_SHORT).show();
        // open a dialog asking the user if they would like to join this game.
        // If they are already in the game, perhaps ask them to leave.
        //startActivity(new Intent(this, game.activityClass));
    }
}
