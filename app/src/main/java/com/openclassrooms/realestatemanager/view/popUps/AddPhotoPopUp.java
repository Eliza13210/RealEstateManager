package com.openclassrooms.realestatemanager.view.popUps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.view.PhotoAdapter;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;

/**
 * Popup dialog allowing user to add photo or video to real estate object.
 * The video/ photo will be added to the recyclerview in the activity
 */

public class AddPhotoPopUp {

    private Context context;
    private String photoText;

    public AddPhotoPopUp(Context context) {
        this.context = context;
    }

    public void popUpDialog(String uri, String type, List<Photo> photos, PhotoAdapter photoAdapter) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(context.getString(R.string.description_popup_title));

        // set the custom layout
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.popup_photo, null);
        builder.setView(customLayout);

        //THUMBNAIL VIDEO OR PHOTO
        ImageView imageView = customLayout.findViewById(R.id.popup_image);
        Glide.with(context)
                .asBitmap()
                .load(uri) // or URI/path
                .into(imageView); //imageview to set thumbnail to

        // Set up the buttons
        builder.setPositiveButton("Add photo", (dialog, i) -> {

            //When user validate
            EditText input = customLayout.findViewById(R.id.popup_edit_tv);
            photoText = input.getText().toString();

            Photo photo = new Photo(null, 1, uri, photoText, type);
            photos.add(photo);

            Log.e("popup", "add photo "  + photos.size());

            photoAdapter.updateData(photos, null);

            Toast.makeText(context, R.string.photo_added, Toast.LENGTH_SHORT).show();

            dialog.dismiss();

        });
        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, i) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }
}
