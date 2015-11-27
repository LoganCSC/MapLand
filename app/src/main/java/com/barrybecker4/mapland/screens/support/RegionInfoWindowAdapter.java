package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.game.FormatUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * @author Barry Becker
 */
public class RegionInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private RegionBean clickedRegion;
    private Context context;

    public RegionInfoWindowAdapter(RegionBean clickedRegion, Context context) {
        this.clickedRegion = clickedRegion;
        this.context = context;
    }

    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.info_window_layout, null);

        // Getting the position from the marker
        LatLng latLng = arg0.getPosition();

        TextView titleLine = (TextView) v.findViewById(R.id.title_line);
        TextView firstLine = (TextView) v.findViewById(R.id.first_line);
        TextView secondLine = (TextView) v.findViewById(R.id.second_line);
        TextView thirdLine = (TextView) v.findViewById(R.id.third_line);

        String m = "<b><small>" + clickedRegion.getOwnerId() + "</small></b>";
        titleLine.setText(Html.fromHtml(m));
        //titleLine.setText(clickedRegion.getOwnerId());


        firstLine.setText(Html.fromHtml("<small>Region: " + FormatUtil.formatId(clickedRegion.getRegionId()) + "</small>"));
        secondLine.setText(Html.fromHtml("<small>Cost: " + FormatUtil.formatNumber(clickedRegion.getCost()) + "</small>"));
        thirdLine.setText(Html.fromHtml("<small>Income: " + FormatUtil.formatNumber(clickedRegion.getIncome())+ "</small>"));

        // Returning the view containing InfoWindow contents
        return v;
    }

}
