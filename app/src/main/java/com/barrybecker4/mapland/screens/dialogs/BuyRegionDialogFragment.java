package com.barrybecker4.mapland.screens.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.barrybecker4.mapland.game.FormatUtil;


/**
 * @author Barry Becker
 */
public class BuyRegionDialogFragment extends DialogFragment {


    /** required parameterless constructor */
    public BuyRegionDialogFragment() {
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle args = this.getArguments();
        String oldOwner = args.getString("oldOwner");
        Double cost = args.getDouble("cost");
        Double balance = args.getDouble("balance");
        Double income = args.getDouble("income");

        String msg = "Region's income:"+ FormatUtil.formatNumber(income)+
                "?\n Price: " + FormatUtil.formatNumber(cost)
                + "\n You have " + FormatUtil.formatNumber(balance)+"\n\n" +
                "\n Do you want to buy this region from " + oldOwner +"?";

        builder.setMessage(msg)
                .setPositiveButton("Buy!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((OnRegionBoughtHandler) getActivity()).regionBoughtByCurrentUser();
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
}