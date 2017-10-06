package ch.hsr.mge.gadgeothek.ui.reservations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.ui.ImageProvider;

class NewReservationsArrayAdapter extends ArrayAdapter<Gadget> {

    private final ArrayList<Gadget> originalGadgetList;
    private final ArrayList<Gadget> filteredGadgetList;
    private final ArrayList<Gadget> selectedGadgetList;
    private OnSelectionChangedListener onSelectionChangedListener;


    interface OnSelectionChangedListener {
        void onSelectionChanged(ArrayList<Gadget> selected);
    }

    public NewReservationsArrayAdapter(Context context, int textViewResourceId, ArrayList<Gadget> gadgets, OnSelectionChangedListener onSelectionChangedListener) {
        super(context, textViewResourceId, gadgets);
        this.onSelectionChangedListener = onSelectionChangedListener;
        this.originalGadgetList = new ArrayList<>();
        this.filteredGadgetList = new ArrayList<>();
        this.selectedGadgetList = new ArrayList<>();
        this.originalGadgetList.addAll(gadgets);
        this.filteredGadgetList.addAll(gadgets);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                filteredGadgetList.clear();

                if (constraint != null) {
                    for (Gadget m : originalGadgetList) {
                        if (m.getName().toLowerCase(Locale.getDefault()).contains(constraint)) {
                            filteredGadgetList.add(m);
                        }
                    }

                    filterResults.values = filteredGadgetList;
                    filterResults.count = filteredGadgetList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll(filteredGadgetList);
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.gadget_listview_item, null);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.primaryTextView);
            TextView lentUntilDateTextView = (TextView) convertView.findViewById(R.id.secondaryTextView);
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);

            ReservationsRecyclerAdapter.RecyclerItemViewHolder holder =
                    new ReservationsRecyclerAdapter.RecyclerItemViewHolder(
                            convertView, nameTextView, lentUntilDateTextView, iconImageView);

            convertView.setTag(holder);
        }

        final CheckableLinearLayout layout = (CheckableLinearLayout) convertView;

        ReservationsRecyclerAdapter.RecyclerItemViewHolder holder =
                (ReservationsRecyclerAdapter.RecyclerItemViewHolder) convertView.getTag();

        final Gadget gadget = filteredGadgetList.get(position);
        holder.nameTextView.setText(gadget.getName());
        holder.lentUntilDateTextView.setText("Lent until ???");
        holder.iconImageView.setImageDrawable(ImageProvider.getIconFor(gadget));
/*
        if (selectedGadgetList.contains(gadget)) {
            layout.setChecked(true);
        } else {
            layout.setChecked(false);
        }*/

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.toggle();
                if (selectedGadgetList.contains(gadget)) {
                    selectedGadgetList.remove(gadget);
                } else {
                    selectedGadgetList.add(gadget);
                }
                onSelectionChangedListener.onSelectionChanged(selectedGadgetList);
            }
        });

        return convertView;
    }

    public void setData(List<Gadget> gadgets) {
        originalGadgetList.clear();
        filteredGadgetList.clear();
        originalGadgetList.addAll(gadgets);
        filteredGadgetList.addAll(gadgets);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedGadgetList.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Gadget> getSelectedGadgets() {
        return selectedGadgetList;
    }
}