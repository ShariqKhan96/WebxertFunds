package android.webxert.com.webxertfunds;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webxert.com.webxertfunds.sqlitemodel.Donor;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DonorsAdapter extends RecyclerView.Adapter<DonorsAdapter.MyVH> {

    List<Donor> donors = new ArrayList<>();


    public DonorsAdapter(List<Donor> donors) {
        this.donors = donors;
    }

    @NonNull
    @Override
    public MyVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyVH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.donor_item, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyVH myVH, int i) {


        myVH.name.setText(donors.get(i).getDonor());
        myVH.value.setText(donors.get(i).getValue()+" pkr rs");
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public class MyVH extends RecyclerView.ViewHolder {
        TextView name, value;

        public MyVH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            value= itemView.findViewById(R.id.value);



        }
    }
}
