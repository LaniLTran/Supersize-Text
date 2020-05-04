package com.example.bigtext;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    EditText edit;
    TextView text;
    private Toolbar toolbar;
    SharedPreferences prefs;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Resize layout when soft keyboard appears
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Retrieve IDs
        edit = (EditText)findViewById(R.id.edit_text);
        text = (TextView)findViewById(R.id.text_view);
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        // Set up Preference to store key-value pairs (see afterTextChanged)
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Display BOTH saved edit and text when resuming from another activity
        edit.setText(prefs.getString("autosave", ""));
        text.setText(prefs.getString("autosave", ""));

        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        String s = sharedPreferences.getString("font_family_key", null);
//        Typeface fontFamily = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + s);
        String textColor = sharedPreferences.getString("text_color_key", "white");
        String bgColor = sharedPreferences.getString("bg_color_key", "black");

//        text.setTypeface(fontFamily);
        text.setTextColor(Color.parseColor(textColor));
        text.setBackgroundColor(Color.parseColor(bgColor));

        // Event listener for button click
        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(edit.getCompoundDrawables()[2]!=null){
                        if(event.getX() >= (edit.getRight()- edit.getLeft() - edit.getCompoundDrawables()[2].getBounds().width())) {
                            edit.setText("");
                        }
                    }
                }
                return false;
            }
        });

        // Event listener for text change
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Display clear button if EditText contains character(s) otherwise hide the button
                if (edit.getText().length() > 0) {
                    edit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0);
                } else {
                    edit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                // Update text content in real time
                String content = s.toString();
                text.setText(content);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Save text input when resuming from another activity
                prefs.edit().putString("autosave", s.toString()).apply();
            }
        });

    }

    // Hide soft keyboard when tapping anywhere other than EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handleReturn = super.dispatchTouchEvent(ev);
        View view = getCurrentFocus();
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if(view instanceof EditText){
            View innerView = getCurrentFocus();

            if (ev.getAction() == MotionEvent.ACTION_UP &&
                    !getLocationOnScreen((EditText) innerView).contains(x, y)) {

                InputMethodManager input = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }

        return handleReturn;
    }

    // Ensure that options menu isn't included in EditText component
    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }

    // Open or close options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Display options menu selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.favorites_item:
//                startActivity(new Intent(this, FavoritesActivity.class));
//                return true;
//            case R.id.history_item:
//                startActivity(new Intent(this, HistoryActivity.class));
//                return true;
            case R.id.settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.exit_item:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
