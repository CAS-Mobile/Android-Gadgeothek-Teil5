package ch.hsr.mge.gadgeothek.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;


public class AbstractAuthenticationActivity extends AppCompatActivity {

    public static final String PREFERENCES = "PREFERENCES";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String LAST_AUTOLOGIN_FAILED = "LAST_AUTOLOGIN_FAILED";

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gadgeothek, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgress(final View loginFormView, final View progressView, final boolean show) {
        int shortAnimTime = getShortAnimationTime();

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView
                .animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView
                .animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    protected int getShortAnimationTime() {
        return getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    protected void showInDismissableSnackbar(View parent, String message) {
        final Snackbar snackbar = Snackbar.make(parent, "Login failed: " + message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss_snackbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void login(final String email, final String password, final boolean isAutoLogin) {

        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        String url = preferences.getString(
                getString(R.string.settings_server_address),
                getString(R.string.settings_default_server));

        LibraryService.setServerAddress(url);

        LibraryService.login(email, password, new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean success) {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(EMAIL, email);
                editor.putString(PASSWORD, password);
                editor.putBoolean(LAST_AUTOLOGIN_FAILED, false);
                editor.apply();
                loginSucceeded(isAutoLogin);
            }

            @Override
            public void onError(String message) {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(LAST_AUTOLOGIN_FAILED, true);
                editor.apply();
                loginFailed(message);
            }
        });
    }

    protected void loginFailed(String message) {
    }

    protected void loginSucceeded(boolean isAutoLogin) {
    }

    protected void startMainActivity(boolean isAutoLogin) {
        Intent intent = new Intent(AbstractAuthenticationActivity.this, GadgeothekActivity.class);
        startActivity(intent);
        if (!isAutoLogin) {
            // No animation when logging in automatically to make it faster
            overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
        }
        finish();
    }
}
