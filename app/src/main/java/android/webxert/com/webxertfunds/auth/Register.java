package android.webxert.com.webxertfunds.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webxert.com.webxertfunds.MainActivity;
import android.webxert.com.webxertfunds.R;
import android.webxert.com.webxertfunds.common.ConstantManager;
import android.webxert.com.webxertfunds.model.User;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Register extends AppCompatActivity {
    SharedPreferences.Editor writer;
    EditText email, password, name;
    Button signup;
    FirebaseAuth m_auth;
    DatabaseReference db_ref;
    private TextView donateMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        Typeface font = Typeface.createFromAsset(
                getAssets(),
                "fonts/Billabong.ttf");
        donateMe = findViewById(R.id.slogon);
        donateMe.setTypeface(font);

        m_auth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference("Donors");

        signup = findViewById(R.id.register);
        writer = getSharedPreferences(ConstantManager.SHARED_PREFERENCES, MODE_PRIVATE).edit();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                progressDialog.setTitle("Registering");
                progressDialog.setMessage("Please wait");
                if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString()) &&
                        !TextUtils.isEmpty(name.getText().toString())) {
                    progressDialog.show();

                    m_auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                m_auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Register.this);
                                            alertDialog.setMessage("Registered successfully verification sent! Verify your account by clicking the verification link!");

                                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    pushUserToDB();
                                                    dialog.dismiss();

                                                    writer.putString(ConstantManager.EMAIL, email.getText().toString());
                                                    writer.apply();

                                                    Intent intent = new Intent(Register.this, Login.class);
                                                    startActivity(intent);

                                                }
                                            });
                                            alertDialog.show();

                                        } else
                                            Toast.makeText(Register.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else
                                Toast.makeText(Register.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else
                    Toast.makeText(Register.this, "Some field(s) empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pushUserToDB() {
        User user = new User();
        user.setEmail(email.getText().toString());
        user.setName(name.getText().toString());
        user.setPassword(password.getText().toString());
        user.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
        user.setId(m_auth.getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Donors").push().setValue(user);
    }
}
