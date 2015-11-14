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
 * Shown when the user enters a region that they can buy.
 * @author Barry Becker
 */
public class BuyRegionDialogFragment extends DialogFragment {

    /** required parameter-less constructor */
    public BuyRegionDialogFragment() {
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

        Bundle args = this.getArguments();
        String oldOwner = args.getString("oldOwner");
        Double cost = args.getDouble("cost");
        Double balance = args.getDouble("balance");

        DialogInterface.OnClickListener cancelHandler = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        };

        String msg = "It costs " + FormatUtil.formatNumber(cost)
                + " and you have " + FormatUtil.formatNumber(balance);

        if (cost > balance) {
            builder.setMessage("You cannot buy this region from " + oldOwner + ". " + msg)
                    .setNegativeButton("OK", cancelHandler);
        }
        else {
            builder.setMessage("Do you want to want to buy this region from " +oldOwner +"? " + msg)
                    .setPositiveButton("Buy!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((OnRegionBoughtHandler) getActivity()).regionBoughtByCurrentUser();
                        }
                    })
                    .setNegativeButton("Cancel", cancelHandler);
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }
}