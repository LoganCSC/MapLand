package com.barrybecker4.mapland.screens.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.barrybecker4.mapland.backend.mapLandApi.model.GameBean;

import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.screens.GameManagementActivity;


/**
 * Shown when the user enters a region that they can buy.
 * @author Barry Becker
 */
public class NewGameDialogFragment extends DialogFragment {

    private View view;

    /** required parameter-less constructor */
    public NewGameDialogFragment() {
    }

    /**
     * Args cannot be passed directly because this dialog is created using
     * the parameter-less constructor when the phone rotates.
     * @param savedInstanceState existing state
     * @return alert dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.new_game_dlg, null);

        // = (CompoundButton) findViewById(R.id.traffic_toggle);
        final EditText nameInput = (EditText) view.findViewById(R.id.new_game_name_input);
        final NumberPicker numPlayersPicker = (NumberPicker) view.findViewById(R.id.num_players_picker);
        numPlayersPicker.setMinValue(2);
        numPlayersPicker.setMaxValue(20);
        numPlayersPicker.setValue(3);
        final EditText numHoursPicker = (EditText) view.findViewById(R.id.num_hours_picker);
        numHoursPicker.setText("24");
        numHoursPicker.addTextChangedListener(new DurationTextWatcher());
        final NumberPicker regionValueIncPicker = (NumberPicker) view.findViewById(R.id.new_game_region_value_inc);
        regionValueIncPicker.setMinValue(0);
        regionValueIncPicker.setMaxValue(100);
        regionValueIncPicker.setValue(10);

        // Home: 37.602768, -122.0752
        final EditText notesInput = (EditText) view.findViewById(R.id.new_game_notes_input);
        final EditText nwLatInput = (EditText) view.findViewById(R.id.bounds_nw_lat);
        nwLatInput.setText("37.7");
        final EditText nwLngInput = (EditText) view.findViewById(R.id.bounds_nw_lng);
        nwLngInput.setText("-121.9");
        final EditText seLatInput = (EditText) view.findViewById(R.id.bounds_se_lat);
        seLatInput.setText("37.5");
        final EditText seLngInput = (EditText) view.findViewById(R.id.bounds_se_lng);
        seLngInput.setText("-122.1");


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GameBean newGame = new GameBean();
                        newGame.setGameName(nameInput.getText().toString());
                        newGame.setNumPlayers(numPlayersPicker.getValue());
                        newGame.setDuration(getDuration(numHoursPicker));
                        double incPct = (double) regionValueIncPicker.getValue() / 100.0;
                        newGame.setRegionCostPercentIncrease(incPct);
                        newGame.setNotes(notesInput.getText().toString());
                        newGame.setNwLatitudeCoord(Double.parseDouble(nwLatInput.getText().toString()));
                        newGame.setNwLongitudeCoord(Double.parseDouble(nwLngInput.getText().toString()));
                        newGame.setSeLatitudeCoord(Double.parseDouble(seLatInput.getText().toString()));
                        newGame.setSeLongitudeCoord(Double.parseDouble(seLngInput.getText().toString()));

                        ((GameManagementActivity) getActivity()).createNewGame(newGame);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private int getDuration(EditText numHoursPicker) {
        int duration = Integer.parseInt(numHoursPicker.getText().toString());
        if (duration < 2) {
            duration = 2;
        }
        else if (duration > 2000) {
            duration = 2000;
        }
        return duration;
    }

    private class DurationTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            final EditText numHoursPicker = (EditText) view.findViewById(R.id.num_hours_picker);
            try {
                int v = Integer.parseInt(s.toString());
            }
            catch (NumberFormatException e) {
                numHoursPicker.setText("Invalid");
            }
        }
    }

}