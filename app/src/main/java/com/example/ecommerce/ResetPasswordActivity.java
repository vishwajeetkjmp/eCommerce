package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pagetitle, titleQues;
    private EditText phonenumber, ques1, ques2;
    private Button verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        pagetitle = findViewById(R.id.total_price);
        titleQues = findViewById(R.id.title_questions);
        phonenumber = findViewById(R.id.find_phone_number);
        ques1 = findViewById(R.id.question_1);
        ques2 = findViewById(R.id.question_2);
        verify = (Button) findViewById(R.id.verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();


        phonenumber.setVisibility(View.GONE);

        if (check.equals("settings")) {
            displayanswer();
            pagetitle.setText("Set Question");
            titleQues.setText("Please set answer for security purpose");
            verify.setText("Set");
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswer();
                }
            });
        } else if (check.equals("login")) {
            phonenumber.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }


    private void displayanswer() {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Prevalent.currentOnlineUsers.getPhone());
        data.child("Security Answer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();
                    ques1.setText(ans1);
                    ques2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void setAnswer() {
        String quest1 = ques1.getText().toString().toLowerCase();
        String quest2 = ques2.getText().toString().toLowerCase();
        if (quest1.equals("") && quest2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please Give all Details", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(Prevalent.currentOnlineUsers.getPhone());
            HashMap<String, Object> userdatamap = new HashMap<>();
            userdatamap.put("answer1", quest1);
            userdatamap.put("answer2", quest2);
            data.child("Security Answer").updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "you have filled details", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
    }
    private void verifyUser() {
        final String phone = phonenumber.getText().toString();
        final String quest1 = ques1.getText().toString().toLowerCase();
        final String quest2 = ques2.getText().toString().toLowerCase();
        if (phone.equals("") && quest1.equals("") && quest2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please Give all Details", Toast.LENGTH_LONG).show();
        } else {
            final DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(phone);
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String mphone = dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.hasChild("Security Answer")) {
                            String ans1 = dataSnapshot.child("Security Answer").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Answer").child("answer2").getValue().toString();
                            if (!ans1.equals(quest1)) {
                                Toast.makeText(ResetPasswordActivity.this, "ansqwer 1 is wrong", Toast.LENGTH_LONG).show();

                            } else if (!ans2.equals(quest2)) {
                                Toast.makeText(ResetPasswordActivity.this, "ansqwer 2 is wrong", Toast.LENGTH_LONG).show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");
                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Enter New Password Here");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newPassword.getText().toString().equals("")) {
                                            data.child("password").setValue(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ResetPasswordActivity.this, "Password is changed", Toast.LENGTH_LONG).show();

                                                        startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Please write correct aqnswers", Toast.LENGTH_LONG).show();
                        }

                    }
                    else {                            Toast.makeText(ResetPasswordActivity.this, "This user doesnt exist", Toast.LENGTH_LONG).show();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
