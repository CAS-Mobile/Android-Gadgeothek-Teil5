package ch.hsr.mge.gadgeothek.ui.loans;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.modules.GadgeothekApplication;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;
import ch.hsr.mge.gadgeothek.ui.GadgeothekActivity;

public class LoansFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    LibraryService libraryService;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ArrayList<Loan> loans = new ArrayList<>();
    private LoansRecyclerAdapter loansRecyclerAdapter;

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((GadgeothekApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loans_fragment, container, false);
        ButterKnife.bind(this, view);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (libraryService.isLoggedIn()) {
            refreshLoans();
        } else {
            snack("You are not logged in!?");
        }
    }

    public void onRefresh() {
        refreshLoans();
    }

    private void refreshLoans() {
        refreshLayout.setRefreshing(true);
        libraryService.getLoansForCustomer(new Callback<List<Loan>>() {
            @Override
            public void onCompletion(List<Loan> newLoans) {
                setupAdapter();
                loans.clear();
                loans.addAll(newLoans);
                loansRecyclerAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String message) {
                setupAdapter();
                loans.clear();
                loansRecyclerAdapter.notifyDataSetChanged();
                snack("Update failed: " + message);
                refreshLayout.setRefreshing(false);
            }

            private void setupAdapter() {
                if (loansRecyclerAdapter == null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    DateFormat dateFormat = GadgeothekActivity.getDateFormat(getActivity());
                    loansRecyclerAdapter = new LoansRecyclerAdapter(loans, dateFormat);
                    recyclerView.setAdapter(loansRecyclerAdapter);
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
}
