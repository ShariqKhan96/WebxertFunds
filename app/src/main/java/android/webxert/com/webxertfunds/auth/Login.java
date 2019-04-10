package android.webxert.com.webxertfunds.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webxert.com.webxertfunds.MainActivity;
import android.webxert.com.webxertfunds.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button signin;
    FirebaseAuth m_auth;
    TextView donateMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        Typeface font = Typeface.createFromAsset(
                getAssets(),
                "fonts/Billabong.ttf");
        donateMe = findViewById(R.id.slogon);
        donateMe.setTypeface(font);


        TextView noAcc = findViewById(R.id.no_account);
        noAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        signin = findViewById(R.id.signin);
        m_auth = FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {

                    final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setTitle("Signing In");
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();
                    m_auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                if (m_auth.getCurrentUser().isEmailVerified()) {

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(Login.this, "Your email is not verified!", Toast.LENGTH_LONG).show();
                                }
                            } else
                                Toast.makeText(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                } else
                    Toast.makeText(Login.this, "Some field(s) empty!", Toast.LENGTH_SHORT).show();

            }
        });


    }
}
