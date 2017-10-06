package ch.hsr.mge.gadgeothek.ui.reservations;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;
import ch.hsr.mge.gadgeothek.ui.GadgeothekActivity;

public class ReservationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ReservationsFragment.class.getSimpleName();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private ReservationsRecyclerAdapter reservationsRecyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.reservations_fragment, container, false);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.recyclerView);

        refreshLayout = (SwipeRefreshLayout) fragment.findViewById(R.id.swipeLayout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(this);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (LibraryService.isLoggedIn()) {
            refreshReservations();
        } else {
            snack("You are not logged in!?");
        }
    }

    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        refreshReservations();
    }

    private void refreshReservations() {
        LibraryService.getReservationsForCustomer(new Callback<List<Reservation>>() {
            @Override
            public void onCompletion(List<Reservation> newReservations) {
                setupAdapter();
                reservations.clear();
                reservations.addAll(newReservations);
                reservationsRecyclerAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String message) {
                setupAdapter();
                reservations.clear();
                reservationsRecyclerAdapter.notifyDataSetChanged();
                snack(getActivity().getString(R.string.msg_update_failed) + message);
                refreshLayout.setRefreshing(false);
            }

            private void setupAdapter() {
                if (reservationsRecyclerAdapter == null) {
                    setupRecyclerView(recyclerView);
                }
            }
        });
    }

    private void snack(String msg) {
        final Snackbar snackbar = Snackbar.make(refreshLayout, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss_snackbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        final FragmentActivity activity = getActivity();
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        DateFormat dateFormat = GadgeothekActivity.getDateFormat(activity);
        reservationsRecyclerAdapter = new ReservationsRecyclerAdapter(reservations, dateFormat, new ReservationsRecyclerAdapter.OnReservationClickedListener() {
            @Override
            public void onReservationClicked(final Reservation reservation, final int position) {
                final Toolbar toolbar = ((GadgeothekActivity) activity).getToolbar();
                toolbar.startActionMode(new ActionMode.Callback() {
                    private ActionMode mode;

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        this.mode = mode;
                        mode.getMenuInflater().inflate(R.menu.reservation, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        cancelReservation(reservation, position);
                        mode.finish();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                });
            }
        });
        recyclerView.setAdapter(reservationsRecyclerAdapter);
    }


    private void cancelReservation(final Reservation reservation, final int position) {
        LibraryService.deleteReservation(reservation, new Callback<Boolean>() {
            @Override
            public void onCompletion(Boolean input) {
                reservations.remove(position);
                reservationsRecyclerAdapter.notifyItemRemoved(position);
                Snackbar.make(refreshLayout, getActivity().getString(R.string.msg_reservation_cancelled), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                snack(getActivity().getString(R.string.msg_cancelling_failed) + message);
            }
        });
    }
}
