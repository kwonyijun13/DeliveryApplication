package com.kwonyijun.deliveryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // INSTANTIATE FIREBASE
        firebaseAuth = FirebaseAuth.getInstance();

        // CLOSE BUTTON
        ImageButton closeButton = findViewById(R.id.close_ImageButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // EMAIL
        TextInputLayout emailInputLayout = findViewById(R.id.email_TextInputLayout);
        TextInputEditText emailEditText = findViewById(R.id.email_TextInputEditText);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    emailInputLayout.setEndIconDrawable(R.drawable.ic_check);
                    emailInputLayout.setError(null);
                } else {
                    emailInputLayout.setError("이메일을 올바르게 입력해주세요.");
                }
            }
        });

        // PASSWORD
        TextInputEditText passwordEditText = findViewById(R.id.password_TextInputEditText);
        TextView helperTextView = findViewById(R.id.helper_TextView);
        TextView firstHelperTextView = findViewById(R.id.first_helper_TextView);
        TextView secondHelperTextView = findViewById(R.id.second_helper_TextView);
        TextView thirdHelperTextView = findViewById(R.id.third_helper_TextView);
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    firstHelperTextView.setVisibility(View.VISIBLE);
                    secondHelperTextView.setVisibility(View.VISIBLE);
                    thirdHelperTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();

                int greenColor = ContextCompat.getColor(getApplicationContext(), R.color.green);
                int redColor = ContextCompat.getColor(getApplicationContext(), R.color.red);

                // minimum 8 max 20 + one number or special char
                // ^: start of line
                // (?=.*[a-zA-Z]): needs at least one alphabet (upper/lower) anywhere in the string
                // (?=.*[0-9!@#$%^&*]): needs at least one digit / special char anywhere in the string
                if (input.matches("^(?=.*[a-zA-Z])(?=.*[0-9!@#$%^&*])[a-zA-Z0-9]{8,20}")) {
                    firstHelperTextView.setText("✓ 영문/숫자/특수문자 2가지 이상 조합(8~20자)");
                    firstHelperTextView.setTextColor(greenColor);
                } else {
                    firstHelperTextView.setText("x 영문/숫자/특수문자 2가지 이상 조합(8~20자)");
                    firstHelperTextView.setTextColor(redColor);
                }

                // 3 continuous same character check
                // (?!.*(.)\\1\\1): does not allow 3 consecutive same chars
                if (!input.matches("^(?!.*(.)\\1\\1)")) {
                    secondHelperTextView.setText("✓ 3개 이상 연속되거나 동일한 문자/숫자 제외");
                    secondHelperTextView.setTextColor(greenColor);
                } else {
                    secondHelperTextView.setText("x 3개 이상 연속되거나 동일한 문자/숫자 제외");
                    secondHelperTextView.setTextColor(redColor);
                }

                // password has id/email check
                String email = emailEditText.getText().toString().trim();
                String[] firstPart = email.split("@");
                String[] secondPart = email.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                String id = firstPart[0];
                String num = secondPart[0];
                if (!input.contains(id) && !input.contains(num)) {
                    thirdHelperTextView.setText("✓ 아이디(이메일) 제외");
                    thirdHelperTextView.setTextColor(greenColor);
                } else {
                    thirdHelperTextView.setText("x 아이디(이메일) 제외");
                    thirdHelperTextView.setTextColor(redColor);
                }

                if (input.matches("^(?=.*[a-zA-Z])(?=.*[0-9!@#$%^&*])[a-zA-Z0-9]{8,20}") && !input.matches("^(?!.*(.)\\1\\1)")
                && !input.contains(id) && !input.contains(num)) {
                    firstHelperTextView.setVisibility(View.GONE);
                    secondHelperTextView.setVisibility(View.GONE);
                    thirdHelperTextView.setVisibility(View.GONE);
                    helperTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        // CONFIRM PASSWORD
        TextInputLayout confirmInputLayout = findViewById(R.id.password_confirm_TextInputLayout);
        TextInputEditText confirmEditText = findViewById(R.id.password_confirm_TextInputEditText);
        confirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.equals(passwordEditText.getText().toString().trim())) {
                    confirmInputLayout.setError(null);
                } else {
                    confirmInputLayout.setError("비밀번호가 일치하지 않습니다.");
                }
            }
        });

        // NAME
        TextInputLayout nameInputLayout = findViewById(R.id.name_TextInputLayout);
        TextInputEditText nameEditText = findViewById(R.id.name_TextInputEditText);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (!input.matches(".*\\d.*")) {
                    nameInputLayout.setError(null);
                } else {
                    nameInputLayout.setError("이름을 정학히 입력하세요.");
                }
            }
        });

        // PHONE NUMBER
        TextInputLayout numberInputLayout = findViewById(R.id.phone_number_TextInputLayout);
        TextInputEditText numberEditText = findViewById(R.id.phone_number_TextInputEditText);
        numberInputLayout.setEndIconVisible(false);
        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.length() == 11) {
                    numberInputLayout.setEndIconVisible(true);
                    numberInputLayout.setError(null);
                } else {
                    numberInputLayout.setEndIconVisible(false);
                    numberInputLayout.setError("휴대폰 번호를 정확하게 입력하세요.");
                }
            }
        });

        // REGISTER BUTTON
        AppCompatCheckBox agreeCheckBox = findViewById(R.id.agree_Checkbox);
        Button registerButton = findViewById(R.id.register_Button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirm = confirmEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String number = numberEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || name.isEmpty() || number.isEmpty()) {
                    Snackbar.make(registerButton, "Please fill in all the fields", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(confirm)) {
                    Snackbar.make(registerButton, "Passwords do not match", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!agreeCheckBox.isChecked()) {
                    Snackbar.make(registerButton, "Please check the boxes", Snackbar.LENGTH_LONG).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sendEmailVerification();
                                    // set display name
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();
                                    user.updateProfile(profileUpdates);

                                    // SIGN IN THE USER
                                    firebaseAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    } else {
                                                        Log.i("Areum", "signInWithEmail:failure", task.getException());
                                                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void sendEmailVerification() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}