package com.openclassrooms.realestatemanager.view.popUps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;

/**
 * Popup dialog showing map
 */

public class MapPopUp {

    private String uri;
    private Context context;

    public MapPopUp(String uri, Context context) {
        this.uri = uri;
        this.context = context;
    }

    public void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);

        // set the custom layout
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.popup_map, null);
        builder.setView(customLayout);
        ImageView image = customLayout.findViewById(R.id.popup_map_image);
        Picasso.get().load(uri).into(image);

        builder.setNegativeButton("Close", (dialog, i) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}

