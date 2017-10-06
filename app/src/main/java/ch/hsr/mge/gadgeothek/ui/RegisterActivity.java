package ch.hsr.mge.gadgeothek.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.modules.GadgeothekApplication;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class RegisterActivity extends AbstractAuthenticationActivity {

    @Inject
    LibraryService libraryService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.email)
    EditText emailEditText;

    @BindView(R.id.password)
    EditText passwordEditText;

    @BindView(R.id.name)
    EditText nameEditText;

    @BindView(R.id.matrikelnr)
    EditText matrikelNrEditText;

    @BindView(R.id.login_progress)
    View progressView;

    @BindView(R.id.login_form)
    View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ((GadgeothekApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.registerButton)
    public void attemptLogin() {

        // Reset errors.
        emailEditText.setError(null);
        passwordEditText.setError(null);
        nameEditText.setError(null);
        matrikelNrEditText.setError(null);

        // Store values at the time of the login attempt.
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String matrikelNr = matrikelNrEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid matrikel number
        if (TextUtils.isEmpty(matrikelNr)) {
            matrikelNrEditText.setError(getString(R.string.error_field_required));
            focusView = matrikelNrEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        }

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(loginFormView, progressView, true);
            hideSoftKeyboard(loginFormView);

            libraryService.register(email, password, name, matrikelNr, new Callback<Boolean>() {
                @Override
                public void onCompletion(Boolean success) {
                    showProgress(loginFormView, progressView, false);
                    if (success) {
                        libraryService.login(email, password, new Callback<Boolean>() {
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
                        passwordEditText.setError(getString(R.string.error_incorrect_password));
                        passwordEditText.requestFocus();
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

