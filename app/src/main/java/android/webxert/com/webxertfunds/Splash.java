package android.webxert.com.webxertfunds;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webxert.com.webxertfunds.auth.Login;
import android.webxert.com.webxertfunds.auth.Register;
import android.webxert.com.webxertfunds.common.ConstantManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {

    DatabaseReference dbRef;

    @Override
    protected void onStart() {
        super.onStart();
        dbRef = FirebaseDatabase.getInstance().getReference("Donations");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent;
                if (dataSnapshot.getChildrenCount() > 0)
                    ConstantManager.DONATIONS = (int) dataSnapshot.getChildrenCount();
                Log.e("PREFRS", getSharedPreferences(ConstantManager.SHARED_PREFERENCES, MODE_PRIVATE).getString(ConstantManager.EMAIL, "null"));
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }


                dbRef.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
