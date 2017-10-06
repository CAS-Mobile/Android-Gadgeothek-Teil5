package ch.hsr.mge.gadgeothek.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class RegisterActivity extends AbstractAuthenticationActivity {

    private EditText emailView;
    private EditText passwordView;
    private EditText nameView;
    private EditText matrikelNrView;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the login form.
        emailView = (EditText) findViewById(R.id.email);
        nameView = (EditText) findViewById(R.id.name);
        matrikelNrView = (EditText) findViewById(R.id.matrikelnr);
        passwordView = (EditText) findViewById(R.id.password);

        ((Button) findViewById(R.id.registerButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);
        nameView.setError(null);
        matrikelNrView.setError(null);

        // Store values at the time of the login attempt.
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        String name = nameView.getText().toString();
        String matrikelNr = matrikelNrView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid matrikel number
        if (TextUtils.isEmpty(matrikelNr)) {
            matrikelNrView.setError(getString(R.string.error_field_required));
            focusView = matrikelNrView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        }

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(loginFormView, progressView, true);
            hideSoftKeyboard(loginFormView);

            LibraryService.register(email, password, name, matrikelNr, new Callback<Boolean>() {
                @Override
                public void onCompletion(Boolean success) {
                    showProgress(loginFormView, progressView, false);
                    if (success) {
                        LibraryService.login(email, password, new Callback<Boolean>() {
                            @Override
                            public void onCompletion(Boolean success) {
                                startMainActivity(/* isAutoLogin = */false);
                            }

                            @Override
                            public void onError(String message) {
                                showProgress(loginFormView, progressView, false);
                                showInDismissableSnackbar(loginFormView, message);
                            }
                        });
                    } else {
                        passwordView.setError(getString(R.string.error_incorrect_password));
                        passwordView.requestFocus();
                    }
                }

                @Override
                public void onError(String message) {
                    showProgress(loginFormView, progressView, false);
                    showInDismissableSnackbar(loginFormView, message);
                }
            });

            showProgress(loginFormView, progressView, true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
    }
}

