package android.webxert.com.webxertfunds;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webxert.com.webxertfunds.ViewHolder.DonationViewHolder;
import android.webxert.com.webxertfunds.auth.Login;
import android.webxert.com.webxertfunds.common.ConstantManager;
import android.webxert.com.webxertfunds.model.Donation;
import android.webxert.com.webxertfunds.model.User;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Donation, DonationViewHolder> adapter;
    RecyclerView donation_list;
    TextView noDonationsTV;
    int sum = 0;
    String donors = "";
    Toolbar toolbar;
    FloatingActionButton addDonation;


    ProgressDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();

        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Getting Donations");
        dialog.setMessage("Please Wait");

        if (ConstantManager.DONATIONS != 0)
            dialog.show();
        noDonationsTV = findViewById(R.id.no_donation_tv);
        if (ConstantManager.DONATIONS == 0)
            noDonationsTV.setVisibility(View.VISIBLE);
        donation_list = findViewById(R.id.donation_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        addDonation = findViewById(R.id.add_donation);
        donation_list.setLayoutManager(new LinearLayoutManager(this));
        displayDonations();

        addDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder donationDialog = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.donation_dialog, null);
                donationDialog.setView(view);
                donationDialog.setTitle("Donate");
                final EditText donatePriceEDT = view.findViewById(R.id.donation_price);
                donationDialog.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (!TextUtils.isEmpty(donatePriceEDT.getText().toString())) {
                            if (ConstantManager.DONATIONS == 0)
                                ConstantManager.DONATIONS = 1;
                            noDonationsTV.setVisibility(View.GONE);
                            sendDonationToDB(donatePriceEDT.getText().toString());
                        }
                    }
                });
                donationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                donationDialog.show();

            }
        });

    }

    private void sendDonationToDB(String s) {
        Donation donation = new Donation();
        String push_key = FirebaseDatabase.getInstance().getReference("Donations").push().getKey();
        donation.setDonation_id(push_key);
        donation.setDonated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
        donation.setFrom(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        donation.setValue(s);
        final ProgressDialog Pdialog;
        Pdialog = new ProgressDialog(this);
        Pdialog.setTitle("Submitting Donation");
        Pdialog.setMessage("Please Wait");
        FirebaseDatabase.getInstance().getReference("Donations").push().setValue(donation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Pdialog.dismiss();
                if (task.isSuccessful())
                    Toast.makeText(MainActivity.this, "Donation added successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Donation unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDonations() {
        Query query = FirebaseDatabase.getInstance().getReference("Donations");

        //query.keepSynced(true);
        FirebaseRecyclerOptions<Donation> options = new FirebaseRecyclerOptions.Builder<Donation>()
                .setQuery(query, Donation.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Donation, DonationViewHolder>(options) {

            @NonNull
            @Override
            public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new DonationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.doantion_view, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull DonationViewHolder holder, int position, @NonNull Donation model) {
                holder.value.setText(model.getValue() + " PKR rs");
                holder.donated_at.setText(model.getDonated_at());
                holder.donated_by.setText("Donated by: " + model.getFrom());

            }
        };


        adapter.startListening();

        dialog.dismiss();
        donation_list.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null)
            adapter.stopListening();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {

            AlertDialog.Builder donationDialog = new AlertDialog.Builder(MainActivity.this);
            //View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.donation_dialog, null);
            //donationDialog.setView(view);
            donationDialog.setTitle("Confirmation");
            donationDialog.setMessage("Are you sure?");
            donationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    getSharedPreferences(ConstantManager.SHARED_PREFERENCES, MODE_PRIVATE).edit().clear().apply();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            donationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            donationDialog.show();
            return true;
        } else if (id == R.id.total) {


            final ProgressDialog dialogP = new ProgressDialog(MainActivity.this);
            dialogP.setTitle("Getting Information");
            dialogP.setMessage("Please Wait");
            dialogP.show();
            FirebaseDatabase.getInstance().getReference("Donations").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()
                            ) {
                        Donation donor = data.getValue(Donation.class);

                        sum += Integer.parseInt(donor.getValue());
                    }

                    dialogP.dismiss();

                    AlertDialog.Builder donationDialog = new AlertDialog.Builder(MainActivity.this);
                    //View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.donation_dialog, null);
                    //donationDialog.setView(view);
                    donationDialog.setTitle("Total Donation");
                    donationDialog.setMessage(Html.fromHtml("Webxert total balance is <b>" + sum + "</b> pkr rupees"));

                    donationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            sum = 0;
                        }
                    });
                    donationDialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (id == R.id.contributions) {
            final ProgressDialog dialogP = new ProgressDialog(MainActivity.this);
            dialogP.setTitle("Getting Information");
            dialogP.setMessage("Please Wait");
            dialogP.show();
            FirebaseDatabase.getInstance().getReference("Donors").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()
                            ) {
                        User donor = data.getValue(User.class);
                        donors += donor.getEmail() + "\n";
                    }
                    if (donors.contains("null"))
                        donors.replace("null", "");

                    dialogP.dismiss();

                    AlertDialog.Builder donationDialog = new AlertDialog.Builder(MainActivity.this);
                    //View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.donation_dialog, null);
                    //donationDialog.setView(view);
                    donationDialog.setTitle("Webxert Donors");
                    donationDialog.setMessage(donors);

                    donationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    donationDialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (id == R.id.participants) {
            Intent intent = new Intent(MainActivity.this, Contributors.class);
            startActivity(intent);
//            final ProgressDialog dialogP = new ProgressDialog(MainActivity.this);
//            dialogP.setTitle("Getting Information");
//            dialogP.setMessage("Please Wait");
//            dialogP.show();
//            FirebaseDatabase.getInstance().getReference("Donations").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()
//                            ) {
//                        Donation donor = data.getValue(Donation.class);
//
//                    }
//
//                    dialogP.dismiss();
//
//                    AlertDialog.Builder donationDialog = new AlertDialog.Builder(MainActivity.this);
//                    //View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.donation_dialog, null);
//                    //donationDialog.setView(view);
//                    donationDialog.setTitle("Webxert Donors");
//                    donationDialog.setMessage(donors);
//                    donationDialog.show();
//                    donationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }


        return super.onOptionsItemSelected(item);
    }
}
