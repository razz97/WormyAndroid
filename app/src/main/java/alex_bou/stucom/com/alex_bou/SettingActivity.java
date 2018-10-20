package alex_bou.stucom.com.alex_bou;

import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

public class SettingActivity extends AppCompatActivity {
    TextInputEditText inputName;
    TextInputEditText inputEmail;
    SharedPreferences prefs;
    SharedPreferences.Editor editorPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        prefs = getPreferences(MODE_PRIVATE);

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);

        inputName.setText(prefs.getString("name",""));
        inputEmail.setText(prefs.getString("email",""));

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


    }
}
