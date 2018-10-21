package alex_bou.stucom.com.alex_bou;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private final int GALLERY_IMAGE_REQUEST = 1;
    private final int CAMERA_IMAGE_REQUEST = 2;
    private File imgFile;
    private CircleImageView imgProfile;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editorPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set layout to display.
        setContentView(R.layout.activity_setting);

        // Get shared preferences.
        prefs = getPreferences(MODE_PRIVATE);

        // Get text inputs from layout.
        TextInputEditText inputName = findViewById(R.id.inputName);
        TextInputEditText inputEmail = findViewById(R.id.inputEmail);

        // Set text inputs value if already saved in shared preferences, else set to empty string.
        inputName.setText(prefs.getString("name",""));
        inputEmail.setText(prefs.getString("email",""));

        // Add listener to inputs: when its text changes values are saved in shared preferences.
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                editorPrefs = prefs.edit();
                editorPrefs.putString("name",s.toString());
                editorPrefs.apply();
            }
        });
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                editorPrefs = prefs.edit();
                editorPrefs.putString("email",s.toString());
                editorPrefs.apply();
            }
        });

        // Get image view from layout.
        imgProfile = findViewById(R.id.imgProfile);

        // Get image file from internal files directory.
        imgFile = new File(getFilesDir(), "imgProfile.jpg");

        // If image file exists assign its uri to profile image
        if (imgFile.exists()) {
            imgProfile.setImageURI(Uri.fromFile(imgFile));
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imgProfile.setImageURI(data.getData());
            imgFile.delete();
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                saveImageFromBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
            imgProfile.setImageBitmap(bitmapImage);
            imgFile.delete();
            saveImageFromBitmap(bitmapImage);
        }
    }

    private void saveImageFromBitmap(Bitmap bitmapImage) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgFile);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onImageClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.imgDialogTitle);
        String[] animals = {getString(R.string.txtGallery), getString(R.string.txtCamera)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // gallery selected
                        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                        startActivityForResult(intentGallery, GALLERY_IMAGE_REQUEST);
                        break;
                    case 1: // camera selected
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,CAMERA_IMAGE_REQUEST);

                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
