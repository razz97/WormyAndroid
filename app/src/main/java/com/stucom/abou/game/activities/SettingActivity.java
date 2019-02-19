package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Base64;

import com.squareup.picasso.Picasso;
import com.stucom.abou.game.model.AccessApi;
import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.utils.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import alex_bou.stucom.com.alex.R;
import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SettingActivity extends AppCompatActivity {

    final int GALLERY_IMAGE_REQUEST = 1;
    final int CAMERA_IMAGE_REQUEST = 2;

    TextInputEditText inputName;
    TextView emailText;
    Button submitButton;
    File imgFile;
    CircleImageView imgProfile;
    String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        imgFile = new File(getFilesDir(), "imgProfile.jpg");
        bindViews();
        setActivityState();
        setViewsData();
    }

    private void setViewsData() {
        emailText.setText("Email: " + LoggedUser.getInstance().getEmail());
        inputName.setText(LoggedUser.getInstance().getName());
        if (LoggedUser.getInstance().getImage() != null)
            Picasso.get().load(LoggedUser.getInstance().getImage()).into(imgProfile);
        else if (imgFile.exists())
            imgProfile.setImageURI(Uri.fromFile(imgFile));
    }

    private void bindViews() {
        inputName = findViewById(R.id.inputName);
        emailText = findViewById(R.id.emailText);
        imgProfile = findViewById(R.id.imgProfile);
        submitButton = findViewById(R.id.submitChanges);
    }

    private void setActivityState() {
        if (!App.isOnline()) {
            Snackbar.make(findViewById(android.R.id.content),"Internet connection is missing.",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setActivityState();
                }
            }).show();
        } else {
            submitButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUser();
                }
            } );
            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClick();
                }
            } );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputName.getText() != null)
            LoggedUser.getInstance().setName(inputName.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK) {
            onGalleryImageResult(data);
        } if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            onCameraImageResult(data);
        }
    }

    private void onCameraImageResult(@Nullable Intent data) {
        if (data != null && data.getExtras() != null) {
            Bitmap realImage = (Bitmap)  data.getExtras().get("data");
            if (realImage != null) {
                base64Image = encodeToBase64(realImage);
                updateUser();
                imgFile.delete();
                imgProfile.setImageBitmap(realImage);
                saveImageFromBitmap(realImage);
            } else
                showCameraError();
        } else
            showCameraError();
    }

    private void onGalleryImageResult(@Nullable Intent data) {
        InputStream stream;
        try {
            if (data != null && data.getData() != null) {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap realImage = BitmapFactory.decodeStream(stream);
                base64Image = encodeToBase64(realImage);
                imgProfile.setImageBitmap(realImage);
                imgFile.delete();
                saveImageFromBitmap(realImage);
                updateUser();
            } else
                showCameraError();
        } catch (FileNotFoundException e) {
            showCameraError();
        }
    }

    private void updateUser() {
        final ProgressDialog progress = ProgressDialog.show(this,null,null);
        progress.setContentView(new ProgressBar(this));
        progress.setCancelable(false);
        progress.show();
        String name = inputName.getText() == null ? null : inputName.getText().toString();
        AccessApi.getInstance().updateServerUser(new AccessApi.ApiListener<String>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable String data) {
                switch (result) {
                    case  OK:
                        Picasso.get().load(LoggedUser.getInstance().getImage()).into(imgProfile);
                        base64Image = null;
                        break;
                    case ERROR_TOKEN:
                        Log.d("infoDebug","token error when updating");
                        // TODO Control error
                        break;
                    case ERROR_CONNECTION:
                        Snackbar.make(findViewById(android.R.id.content),"Could not connect to the server.",Snackbar.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }, base64Image,name);
    }

    private void onImageClick() {
        if (inputName.getText() != null)
            LoggedUser.getInstance().setName(inputName.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.imgDialogTitle);
        String[] options = {getString(R.string.txtGallery), getString(R.string.txtCamera), getString(R.string.txtDelete)};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // gallery selected
                        Intent intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*");
                        startActivityForResult(intentGallery, GALLERY_IMAGE_REQUEST);
                        break;
                    case 1: // camera selected
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
                        break;
                    case 2: // delete selected
                        imgFile.delete();
                        imgProfile.setImageResource(R.drawable.usr);
                }
            }
        });
        builder.create().show();
    }

    private void showCameraError() {
        Snackbar.make(findViewById(android.R.id.content),"There was an error with the camera.",Snackbar.LENGTH_LONG).show();
    }

    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void saveImageFromBitmap(Bitmap bitmapImage) {
        try (FileOutputStream fos = new FileOutputStream(imgFile)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
