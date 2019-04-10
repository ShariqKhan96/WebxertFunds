package android.webxert.com.webxertfunds;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webxert.com.webxertfunds.dbhelper.DBHelper;
import android.webxert.com.webxertfunds.interfaces.DonatorsReceivedListener;
import android.webxert.com.webxertfunds.model.Donation;
import android.webxert.com.webxertfunds.model.User;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Contributors extends AppCompatActivity implements DonatorsReceivedListener {

    List<User> donors = new ArrayList<>();
    DatabaseReference donorsRef;
    DonatorsReceivedListener donatorsReceivedListener;
    int sum = 0;

    //HashMap<String, String> hashMap = new HashMap<>();


    RecyclerView donatorsList;
    TextView no_donation_tv;


    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle("Getting information");
        dialog.setMessage("Please Wait");
        dialog.show();

        no_donation_tv = findViewById(R.id.no_donation_tv);
        donatorsList = findViewById(R.id.donation_list);


        donatorsReceivedListener = this;
        donorsRef = FirebaseDatabase.getInstance().getReference("Donors");
        donorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot fbDonor : dataSnapshot.getChildren()
                        ) {
                    User donor = fbDonor.getValue(User.class);
                    donors.add(donor);
                    new DBHelper(Contributors.this).deleteAll(donor.getEmail());
                }


                if (donors.size() == 0)
                    no_donation_tv.setVisibility(View.VISIBLE);


                donatorsReceivedListener.onDontatorsFetched();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDontatorsFetched() {

        for (int i = 0; i < donors.size(); i++) {
            Log.e("DONORSIZE", donors.size() + "");
            DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("Donations");
            Query query = donationsRef.orderByChild("from").equalTo(donors.get(i).getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()
                            ) {
                        Donation donation = data.getValue(Donation.class);
                        sum += Integer.parseInt(donation.getValue());
                        Log.e("sum", sum + "");
                        new DBHelper(Contributors.this).addDonation(donation.getFrom(), Integer.parseInt(donation.getValue()));
                    }
                    sum = 0;

                    getDonors();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    dialog.dismiss();
                    Log.e("DBERROR", databaseError.getMessage());
                }
            });
        }
    }

    private void getDonors() {

        dialog.dismiss();
        donatorsList.setLayoutManager(new LinearLayoutManager(this));
        donatorsList.setAdapter(new DonorsAdapter(new DBHelper(this).getDonors()));

    }
}
