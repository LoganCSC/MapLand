package com.barrybecker4.mapland.screens.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.server.tasks.RegionTransferer;

/**
 * @author Barry Becker
 */
public class BuyRegionDialogFragment extends DialogFragment {

    private UserBean user;
    private RegionBean region;

    /** required parameterless constructor */
    public BuyRegionDialogFragment() {
    }

    /**
     * Set user and region data.
     * Ideally we should use setArgs(Bundle) but the things I was to pass are beans
     * and do not implement Parcelable or Serializable.
     * That means that the dialog might fail if it needs to be recreated when there is a rotation.
     * Another option might be to have the activity implement an interface like onRegionBought
     * and call that in the ok handler.
     */
    public void setData(UserBean user, RegionBean region) {
        this.user = user;
        this.region = region;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String msg = "Do you want to buy this region from "
                + region.getOwnerId() + "?\n It costs " + region.getCost()
                + " and you have " + user.getCredits();

        builder.setMessage(msg)
                .setPositiveButton("Buy!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.i("STATE_CHANGE", "The region you are in is owned by " + region.getOwnerId()
                                + " Transferring ownership...");
                        String oldOwner = region.getOwnerId();

                        // This does 3 things: User has this region added, region has its owner set to user,
                        // and the old owner has this region removed from its list.
                        RegionTransferer.transferRegionOwnership(region, user, getActivity());
                        Toast.makeText(getActivity(), "Transferring ownership of "
                                + region.getRegionId() + " from " + oldOwner
                                + " to " + user.getUserId(), Toast.LENGTH_SHORT).show();
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