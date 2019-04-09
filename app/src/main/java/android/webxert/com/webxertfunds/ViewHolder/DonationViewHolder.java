package android.webxert.com.webxertfunds.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webxert.com.webxertfunds.R;
import android.widget.TextView;

public class DonationViewHolder extends RecyclerView.ViewHolder {

  public  TextView donated_at, donated_by, value;

    public DonationViewHolder(@NonNull View itemView) {
        super(itemView);

        donated_at = itemView.findViewById(R.id.donated_at);
        donated_by = itemView.findViewById(R.id.donated_by);
        value= itemView.findViewById(R.id.value);

    }
}
