package ch.hsr.mge.gadgeothek.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class SplashActivity extends AbstractAuthenticationActivity {
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressView = findViewById(R.id.login_progress);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        boolean lastAutologinAttemptFailed = preferences.getBoolean(LAST_AUTOLOGIN_FAILED, true);

        // TODO Turn LibraryService into a proper service!
        String url = preferences.getString(
                getString(R.string.settings_server_address),
                getString(R.string.settings_default_server));

        LibraryService.setServerAddress(url);

        progressView.setVisibility(View.VISIBLE);
        progressView
                .animate()
                .setDuration(getShortAnimationTime())
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(View.VISIBLE);
                    }
                });

        if (!LibraryService.isLoggedIn() && !lastAutologinAttemptFailed) {
            String email = preferences.getString(EMAIL, null);
            String password = preferences.getString(PASSWORD, null);
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                login(email, password, true);
                return;
            }
        }

        delayedStartLoginActivity();
    }

    protected void loginFailed(String message) {
        delayedStartLoginActivity();
    }

    protected void loginSucceeded(boolean isAutoLogin) {
        delayedStartGadgeothekActivity();
    }

    private void delayedStartLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }

    private void delayedStartGadgeothekActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, GadgeothekActivity.class));
                finish();
            }
        }, 3000);
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.fade, R.anim.hold);
        super.finish();
    }
}