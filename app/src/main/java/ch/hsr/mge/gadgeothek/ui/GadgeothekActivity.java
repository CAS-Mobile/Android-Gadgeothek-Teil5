package ch.hsr.mge.gadgeothek.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.modules.GadgeothekApplication;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;
import ch.hsr.mge.gadgeothek.ui.loans.LoansFragment;
import ch.hsr.mge.gadgeothek.ui.reservations.NewReservationActivity;
import ch.hsr.mge.gadgeothek.ui.reservations.ReservationsFragment;

public class GadgeothekActivity extends AppCompatActivity {

    private static final String ACTIVE_TAB = GadgeothekActivity.class.getCanonicalName() + ".ACTIVE_TAB";
    @Inject
    LibraryService libraryService;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ViewPager viewPager;

    public static DateFormat getDateFormat(Context ctx) {
        final String format = Settings.System.getString(ctx.getContentResolver(), Settings.System.DATE_FORMAT);
        if (TextUtils.isEmpty(format)) {
            return android.text.format.DateFormat.getMediumDateFormat(ctx);
        } else {
            return new SimpleDateFormat(format);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgeothek);

        ((GadgeothekApplication) getApplication()).getComponent().inject(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide(); // hidden on the first view page

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoansFragment(), getString(R.string.tab_loans));
        adapter.addFragment(new ReservationsFragment(), getString(R.string.tab_reservations));
        viewPager.setAdapter(adapter);
        if (savedInstanceState != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(ACTIVE_TAB, 0), false);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        fab.show();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gadgeothek, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ACTIVE_TAB, viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                libraryService.logout(new Callback<Boolean>() {
                    @Override
                    public void onCompletion(Boolean input) {
                        SharedPreferences preferences = getSharedPreferences(AbstractAuthenticationActivity.PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        // disable auto-login for the next time
                        editor.putBoolean(AbstractAuthenticationActivity.LAST_AUTOLOGIN_FAILED, true);
                        editor.apply();
                        Intent intent = new Intent(GadgeothekActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
                        finish();
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void onAddReservationButton(View view) {
        Intent intent = new Intent(this, NewReservationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
    }
}
