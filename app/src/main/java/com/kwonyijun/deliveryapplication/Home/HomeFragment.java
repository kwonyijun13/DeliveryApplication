package com.kwonyijun.deliveryapplication.Home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kwonyijun.deliveryapplication.MapActivity;
import com.kwonyijun.deliveryapplication.R;
import com.kwonyijun.deliveryapplication.RegisterActivity;
import com.kwonyijun.deliveryapplication.SignInActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage firebaseStorage;
    Boolean isLoggedIn, isEmailVerified;
    private String name, email, emailType;
    BottomSheetDialog logInBottomSheetDialog, verifyEmailBottomSheetDialog;
    MaterialButton searchButton;
    ViewPager2 viewPager2;
    List<Store> storeList = new ArrayList<>();
    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // INSTANTIATE FIREBASE
        firebaseAuth = FirebaseAuth.getInstance();
        // FIREBASE STORAGE
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child("example_image_one.jpg");
        StorageReference imageRefTwo = storageRef.child("example_image_two.jpg");

        // LOAD IMAGES FROM CLOUD STORAGE INTO IMAGEVIEW
        ImageView imageViewOne = view.findViewById(R.id.imageView_one);
        // Get the download URL
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Load the image using Picasso
                Picasso.get().load(uri).into(imageViewOne);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Haerin2", exception.getMessage());
            }
        });

        ImageView imageViewTwo = view.findViewById(R.id.imageView_two);
        imageRefTwo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageViewTwo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Haerin2", exception.getMessage());
            }
        });

        // INITIALIZE KAKAO SDK
        KakaoSdk.init(getContext(), "Yd9f1b2177d3f684540df176db329caea");

        // LOG IN / REGISTER BOTTOM DIALOG
        logInBottomSheetDialog = new BottomSheetDialog(getContext());
        logInBottomSheetDialog.setContentView(R.layout.layout_bottom_non_user_popup);
        // VERIFY EMAIL BOTTOM DIALOG
        verifyEmailBottomSheetDialog = new BottomSheetDialog(getContext());
        verifyEmailBottomSheetDialog.setContentView(R.layout.layout_bottom_email_verified_popup);

        // TOOLBAR: LOCATION BUTTON (위치 버튼)
        MaterialButton locationButton = view.findViewById(R.id.location_materialButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn == false) {
                    handleNonUser();
                } else {
                    startActivity(new Intent(getActivity(), MapActivity.class));
                }
            }
        });

        // TOOLBAR: NOTIFICATIONS BUTTON (알림 버튼)

        // SEARCH BUTTON
        searchButton = view.findViewById(R.id.search_MaterialButton);

        // SHORTCUT BUTTONS

        // VIEWPAGER2
        viewPager2 = view.findViewById(R.id.ads_ViewPager2);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // CHECK IF USER HAS LOGGED IN
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
            isLoggedIn = true;
            name = currentUser.getDisplayName();
            email = currentUser.getEmail();
            // get email type link
            int atIndex = email.indexOf("@");
            emailType = email.substring(atIndex + 1);

            isEmailVerified = currentUser.isEmailVerified();
            if (!isEmailVerified) {
                verifyEmailBottomSheetDialog.show();

                MaterialButton openEmailButton = verifyEmailBottomSheetDialog.findViewById(R.id.okay_MaterialButton);
                openEmailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (emailType.equals("gmail.com")) {
                            // OPEN GMAIL APP
                            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                            try {
                                if (intent != null) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(), "메일 앱 열기 오류입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (emailType.equals("naver.com")) {
                            // OPEN NAVER MAIL
                            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.nhn.android.mail");
                            try {
                                if (intent != null) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(), "메일 앱 열기 오류입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // OPEN OUTLOOK APP
                            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.microsoft.office.outlook");
                            try {
                                if (intent != null) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(), "메일 앱 열기 오류입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } else {
            isLoggedIn = false;
            handleNonUser();
        }
    }

    // FUNCTIONS ==========================================
    public void handleNonUser() {
        logInBottomSheetDialog.show();

        // APP LOGIN
        MaterialButton appLoginButton = logInBottomSheetDialog.findViewById(R.id.coupang_app_login_MaterialButton);
        appLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("DeliveryApp가 OO으로 로그인합니다.\n허용하시겠습니까?")
                        .setPositiveButton("허용", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // LOGIN WITH FACEBOOK / GOOGLE
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // EMAIL LOGIN
        MaterialButton emailLoginButton = logInBottomSheetDialog.findViewById(R.id.email_login_MaterialButton);
        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SignInActivity.class));
            }
        });

        // KAKAO LOGIN
        MaterialButton kakaoLoginButton = logInBottomSheetDialog.findViewById(R.id.kakao_login_MaterialButton);
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().loginWithKakaoTalk(getContext(), (token, error) -> {
                    if (error != null) {
                        Log.e("Areum", "Login failed.", error);
                        reload();
                    } else if (token != null) {
                        Log.i("Areum", "Login succeeded." + token.getAccessToken());
                    }
                    return null;
                });
            }
        });

        // REGISTER / 회원가입
        TextView registerButton = logInBottomSheetDialog.findViewById(R.id.signup_TextView);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void reload() {
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        searchButton.setText(name + "님, 치킨어때요?");
                    } else {
                        Log.e("Areum", "reload failed: " + task.getException());
                    }
                }
            });
        }
    }

    // FIREBASE
    public void retrieveAds() {
        StorageReference imageRef = firebaseStorage.getReference().child("ads.jpg");
    }
}