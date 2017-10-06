package ch.hsr.mge.gadgeothek.ui.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hsr.mge.gadgeothek.R;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.ui.ImageProvider;

class ReservationsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ReservationsRecyclerAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    private final List<Reservation> reservations;
    private final DateFormat dateFormat;
    private OnReservationClickedListener onReservationClickedListener;

    ReservationsRecyclerAdapter(List<Reservation> loans, DateFormat dateFormat, OnReservationClickedListener onReservationClickedListener) {
        this.reservations = loans;
        this.dateFormat = dateFormat;
        this.onReservationClickedListener = onReservationClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (reservations.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_EMPTY_LIST_PLACEHOLDER) {
            View view = layoutInflater.inflate(R.layout.reservation_recycler_empty_item, parent, false);
            return new EmptyRecyclerItemViewHolder(view);
        }

        View view = layoutInflater.inflate(R.layout.reservation_recycler_item, parent, false);
        return new RecyclerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        if (holder.isEmpty()) {
            holder.iconImageView.setImageDrawable(ImageProvider.getIconFor("?"));
            return;
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                Reservation reservation = reservations.get(position);
                onReservationClickedListener.onReservationClicked(reservation, position);
                return false;
            }
        });

        Reservation reservation = reservations.get(position);
        holder.nameTextView.setText(reservation.getGadget().getName());
        holder.lentUntilDateTextView.setText("Lent until " + dateFormat.format(reservation.getReservationDate()));
        holder.iconImageView.setImageDrawable(ImageProvider.getIconFor(reservation.getGadget()));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public int getItemCount() {
        /* To show the empty view placeholder, we always need to return at least 1 */
        return Math.max(1, reservations.size());
    }

    interface OnReservationClickedListener {
        void onReservationClicked(Reservation reservation, int position);
    }

    static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.primaryTextView)
        TextView nameTextView;
        @BindView(R.id.secondaryTextView)
        TextView lentUntilDateTextView;
        @BindView(R.id.iconImageView)
        ImageView iconImageView;

        RecyclerItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public boolean isEmpty() {
            return false;
        }
    }

    private static class EmptyRecyclerItemViewHolder extends RecyclerItemViewHolder {

        EmptyRecyclerItemViewHolder(View parent) {
            super(parent);
        }

        public boolean isEmpty() {
            return true;
        }
    }
}
