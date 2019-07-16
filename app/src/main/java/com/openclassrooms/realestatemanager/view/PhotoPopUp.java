package com.openclassrooms.realestatemanager.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;

public class PhotoPopUp {

    private Context context;
    private String photoText;

    public PhotoPopUp(Context context) {
        this.context = context;
    }

    public void popUpDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Description");

        // set the custom layout
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
        builder.setView(customLayout);
        ImageView image = customLayout.findViewById(R.id.popup_image);
        Picasso.get().load(uri).into(image);

        Log.e("uri", uri);

     // Set up the buttons
        builder.setPositiveButton("Add photo", (dialog, i) -> {

            //When user validate
            EditText input = customLayout.findViewById(R.id.popup_edit_tv);
            photoText = input.getText().toString();
            SharedPreferences pref = context.getSharedPreferences("RealEstate", Context.MODE_PRIVATE);
            pref.edit().putString("Photo text", photoText).apply();
            dialog.dismiss();

        });
        builder.setNegativeButton("Annuler", (dialog, i) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }


}
