package ch.hsr.mge.gadgeothek.ui.loans;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.ui.ImageProvider;

public class LoansRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    private final List<Loan> loans;
    private final DateFormat dateFormat;

    public LoansRecyclerAdapter(List<Loan> loans, DateFormat dateFormat) {
        this.loans = loans;
        this.dateFormat = dateFormat;
    }

    /**
     * To show a placeholder when the list is empty, we return the view type VIEW_TYPE_EMPTY_LIST_PLACEHOLDER.
     * In onCreateViewHolder, this is used to return a EmptyRecyclerItemViewHolder instance. Note that for this
     * to work, we also need to make sure the getItemCount returns at least 1, otherwise no placeholder is shown.
     */
    @Override
    public int getItemViewType(int position) {
        if (loans.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_EMPTY_LIST_PLACEHOLDER) {
            View view = layoutInflater.inflate(R.layout.recycler_empty_item, parent, false);
            return new EmptyRecyclerItemViewHolder(view);
        }
        View layout = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        TextView nameTextView = (TextView) layout.findViewById(R.id.primaryTextView);
        TextView returnDateTextView = (TextView) layout.findViewById(R.id.secondaryTextView);
        ImageView iconImageView = (ImageView) layout.findViewById(R.id.iconImageView);
        return new RecyclerItemViewHolder(layout, nameTextView, returnDateTextView, iconImageView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        if (holder.isEmptyPlaceholder()) {
            return;
        }

        Loan loan = loans.get(position);
        holder.nameTextView.setText(loan.getGadget().getName());
        holder.returnDateTextView.setText("Return until " + dateFormat.format(loan.overDueDate()));
        holder.iconImageView.setImageDrawable(ImageProvider.getIconFor(loan.getGadget()));
    }

    public int getItemCount() {
        /* To show the empty view placeholder, we always need to return at least 1 */
        return Math.max(1, loans.size());
    }

    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView nameTextView;
        public final TextView returnDateTextView;
        public final ImageView iconImageView;

        public RecyclerItemViewHolder(final View parent, TextView nameTextView, TextView returnDateTextView, ImageView iconImageView) {
            super(parent);
            this.nameTextView = nameTextView;
            this.returnDateTextView = returnDateTextView;
            this.iconImageView = iconImageView;
        }

        public boolean isEmptyPlaceholder() {
            return false;
        }
    }

    public static class EmptyRecyclerItemViewHolder extends RecyclerItemViewHolder {

        public EmptyRecyclerItemViewHolder(View parent) {
            super(parent, null, null, null);
        }

        public boolean isEmptyPlaceholder() {
            return true;
        }
    }
}
