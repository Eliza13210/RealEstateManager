package com.openclassrooms.realestatemanager.view.PopUps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.view.PhotoAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;

public class AddPhotoPopUp {

    private Context context;
    private String photoText;

    public AddPhotoPopUp(Context context) {
        this.context = context;
    }

    public void popUpDialog(String uri, List<Photo> photos, PhotoAdapter photoAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Description");

        // set the custom layout
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
        builder.setView(customLayout);
        ImageView image = customLayout.findViewById(R.id.popup_image);
        Picasso.get().load(uri).into(image);

        // Set up the buttons
        builder.setPositiveButton("Add photo", (dialog, i) -> {

            //When user validate
            EditText input = customLayout.findViewById(R.id.popup_edit_tv);
            photoText = input.getText().toString();

            Photo photo = new Photo(null, 1, uri, photoText);
            photos.add(photo);
            photoAdapter.updateData(photos);

            Toast.makeText(context, "Photo added", Toast.LENGTH_SHORT).show();

            dialog.dismiss();

        });
        builder.setNegativeButton("Annuler", (dialog, i) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }
}
