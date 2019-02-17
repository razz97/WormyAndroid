package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.stucom.abou.game.model.Dao;
import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.utils.MyVolley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import alex_bou.stucom.com.alex.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    final static String URL = "https://api.flx.cat/dam2game/user";

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
        // Set layout to display.
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get layout objects from layout.
        inputName = findViewById(R.id.inputName);
        emailText = findViewById(R.id.emailText);
        imgProfile = findViewById(R.id.imgProfile);
        submitButton = findViewById(R.id.submitChanges);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        emailText.setText("Email: " + LoggedUser.getInstance().getEmail());

        // Set text inputs value if already saved in shared preferences, else set to empty string.
         inputName.setText(LoggedUser.getInstance().getName());
        // inputEmail.setText(prefs.getString("email",""));

        if (LoggedUser.getInstance().getImage() != null) {
            Picasso.get().load(LoggedUser.getInstance().getImage()).into(imgProfile);
        }

        // Get image file from internal files directory.
         imgFile = new File(getFilesDir(), "imgProfile.jpg");

         //If image file exists assign its uri to profile image
         if (imgFile.exists()) {
             //imgProfile.setImageURI(Uri.fromFile(imgFile));
         }

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // This is fired when an activity returns some data after calling startActivityForResult.
        // First check if resultCode is OK and check request code specified in the intent.
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Set layout image URI to new image selected from gallery.
            InputStream stream;
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap realImage = BitmapFactory.decodeStream(stream);
                base64Image = encodeToBase64(realImage);
                imgProfile.setImageBitmap(realImage);
                saveImageFromBitmap(realImage);
                imgFile.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //imgProfile.setImageURI(data.getData());
            //try {
                // Try to fetch bitmap from media store. It was the easiest way to store it in internal memory,
                // this way I can still access the picture when deleted from the gallery.
              //  Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                // Save bitmap in internal storage see method below.
             //   saveImageFromBitmap(bitmapImage);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        }
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap realImage = BitmapFactory.decodeStream(stream);
            String result = encodeToBase64(realImage);
            // Get bitmap of the image taken in camera activity.
            Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
            // Delete last image in internal storage.
            imgFile.delete();
            // Set layout image bitmap to new bitmap generated by the camera.
            imgProfile.setImageBitmap(bitmapImage);
            // Save bitmap in internal storage see method below.
            saveImageFromBitmap(bitmapImage);
        }
    }

    private void updateUser() {
        final ProgressDialog progress = ProgressDialog.show(this,null,null);
        progress.setContentView(new ProgressBar(this));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest request = new StringRequest(Request.Method.PUT, SettingActivity.URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        new Dao().downloadUserData(new Runnable() { @Override public void run() {}});
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token",LoggedUser.getInstance().getToken());
                if (base64Image != null) {
                    params.put("image",base64Image);
                    base64Image = null;
                }
                params.put("name",inputName.getText().toString());
                return params;
            }
        };
        MyVolley.getInstance().add(request);

    }

    /**
     * Saves image to application's internal storage.
     *
     * @param bitmapImage bitmap representing image.
     */
    private void saveImageFromBitmap(Bitmap bitmapImage) {
        FileOutputStream fos = null;
        try {
            // Create a new FileOutputStream for image file (this means a Stream for writing on the file)
            fos = new FileOutputStream(imgFile);
            // Use the compress method on the BitMap object to write image to the OutputStream.
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Always close Output stream reader.
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows dialog asking to choose from picking from gallery or taking a picture, then starts the corresponding activity.
     *
     * @param v image view
     */
    public void onImageClick(View v) {
        LoggedUser.getInstance().setName(inputName.getText().toString());
        // Create a builder for generating the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set title for the dialog
        builder.setTitle(R.string.imgDialogTitle);
        // Generate options.
        String[] options = {getString(R.string.txtGallery), getString(R.string.txtCamera), getString(R.string.txtDelete)};
        // Set items and its onClick function.
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
        // Create dialog.
        AlertDialog dialog = builder.create();
        // Show dialog.
        dialog.show();
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }


}
