package ch.hsr.mge.gadgeothek.ui.reservations;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class NewReservationActivityFragment extends Fragment implements NewReservationsArrayAdapter.OnSelectionChangedListener {

    private static final String TAG = NewReservationActivityFragment.class.getSimpleName();
    private static final int MAX_RESERVATIONS = 3;
    private ArrayList<Gadget> gadgetList = new ArrayList<>();
    private NewReservationsArrayAdapter dataAdapter;
    private ActionMode actionMode;
    private View layout;
    private int numberOfReservationsLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.new_reservation_fragment, container, false);

        dataAdapter = new NewReservationsArrayAdapter(getActivity(), R.layout.reservation_recycler_item, gadgetList, this);

        final ListView listView = (ListView) layout.findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);
        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        LibraryService.getGadgets(new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(final List<Gadget> allAvailableGadgets) {
                LibraryService.getReservationsForCustomer(new Callback<List<Reservation>>() {
                    @Override
                    public void onCompletion(List<Reservation> alreadyReserved) {
                        gadgetList.clear();
                        for (Reservation reservation : alreadyReserved) {
                            allAvailableGadgets.remove(reservation.getGadget());
                        }
                        numberOfReservationsLeft = MAX_RESERVATIONS - alreadyReserved.size();
                        gadgetList.addAll(allAvailableGadgets);
                        dataAdapter.setData(allAvailableGadgets);
                    }

                    @Override
                    public void onError(String message) {
                        snack("Oops, could not get a list of available gadgets: " + message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                snack("Oops, could not get a list of available gadgets: " + message);
            }
        });

        return layout;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_reservation, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                dataAdapter.getFilter().filter(newText.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                dataAdapter.getFilter().filter(query.toLowerCase());
                return true;
            }
        });
    }

    @Override
    public void onSelectionChanged(final ArrayList<Gadget> selected) {

        final String actionTitle;
        if (selected.size() <= numberOfReservationsLeft) {
            actionTitle = "Reserve " + selected.size() + " Gadgets";
        } else {
            actionTitle = "Please select no more than " + numberOfReservationsLeft + " Gadgets";
        }

        if (selected.isEmpty()) {
            if (actionMode != null) {
                actionMode.finish();
                actionMode = null;
            }
            return;
        }
        if (actionMode != null) {
            actionMode.getMenu().getItem(0).setTitle(actionTitle);
            return;
        }

        Toolbar toolbar = ((NewReservationActivity) getActivity()).getToolbar();
        actionMode = toolbar.startActionMode(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0, 0, 0, actionTitle);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (dataAdapter.getSelectedGadgets().size() <= numberOfReservationsLeft) {
                    reserveSelectedGadgets();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                dataAdapter.clearSelection();
                actionMode = null;
            }
        });
    }

    private void reserveSelectedGadgets() {
        // We copy the list so we can modify it
        final List<Gadget> gadgets = Collections.synchronizedList(new ArrayList<>(dataAdapter.getSelectedGadgets()));

        for (final Gadget gadget : gadgets) {
            LibraryService.reserveGadget(gadget, new Callback<Boolean>() {
                @Override
                public void onCompletion(Boolean success) {
                    gadgets.remove(gadget);

                    if (!success) {
                        snack("Could not make a reservation for " + gadget.getName());
                    } else {
                        if (gadgets.isEmpty()) {
                            getActivity().finish();
                        }
                    }
                }

                @Override
                public synchronized void onError(String message) {
                    snack("An error occurred while reserving " + gadget.getName() + ": " + message);
                }
            });
        }
    }

    private void snack(String msg) {
        final Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss_snackbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }
}
