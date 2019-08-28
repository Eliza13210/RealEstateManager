package com.openclassrooms.realestatemanager.view.PopUps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;

public class MapPopUp {

    private String uri;
    private Context context;

    public MapPopUp(String uri, Context context) {
        this.uri = uri;
        this.context = context;
    }

    public void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);

        // set the custom layout
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.popup_map, null);
        builder.setView(customLayout);
        ImageView image = customLayout.findViewById(R.id.popup_map_image);
        Picasso.get().load(uri).into(image);

        Log.e("uri", uri);

        builder.setNegativeButton("Close", (dialog, i) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}

