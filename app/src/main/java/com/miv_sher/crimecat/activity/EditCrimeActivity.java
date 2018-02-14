package com.miv_sher.crimecat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miv_sher.crimecat.model.*;

import com.miv_sher.crimecat.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class EditCrimeActivity extends BaseActivityNewCrime {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    public static final String CRIME_key =
            "com.miv_sher.crimecat.criminalintent.crime_id";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://helpanimals-f5035.appspot.com");
    String link = "https://firebasestorage.googleapis.com/v0/b/helpanimals-f5035.appspot.com/o/%D0%BE%D0%B2%D1%87%D0%B0%D1%80%D0%BA%D0%B0.jpg?alt=media&token=72128011-366a-4e24-acc5-40abf5cd8f05";
    //String link =null;


    // [END declare_database_ref]

    public FirebaseAuth firebaseAuth;
    public FirebaseUser user;

    public EditText mTitle;
    // public EditText mDate;

    public RadioGroup mType;
    public RadioButton dogRadioButton;
    public RadioButton catRadioButton;
    public RadioButton horseRadioButton;

    public RadioGroup mSex;
    public RadioButton maleRadioButton;
    public RadioButton femaleRadioButton;

    public EditText mAge;
    public EditText mCity;
    public EditText mPhone;

    public EditText mDescription;

    public ImageView mPhoto;

    String mPostKey;
    String title1;
    String type1;
    String sex1;
    String age1;
    String city1;
    String phone1;
    String photo1;
    String desc1;



    // private
    private FloatingActionButton mSubmitButton;
    private Button mLoadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_crime);



        firebaseAuth = FirebaseAuth.getInstance();
        //user = FirebaseUser.
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
            mPostKey = getIntent().getStringExtra(CRIME_key);
        title1 = getIntent().getStringExtra("title");
        type1 = getIntent().getStringExtra("type");
        sex1 = getIntent().getStringExtra("sex");
        age1 = getIntent().getStringExtra("age");
        city1 = getIntent().getStringExtra("city");
        phone1 = getIntent().getStringExtra("phone");
        photo1 = getIntent().getStringExtra("photo");
        desc1 = getIntent().getStringExtra("desc");
        DatabaseReference globalPostRef = mDatabase.child("posts").child(mPostKey);
        DatabaseReference userPostRef = mDatabase.child("user-posts").child(getUid()).child(mPostKey);
        DatabaseReference userFavRef = mDatabase.child("user-fav").child(getUid()).child(mPostKey);

        mTitle = (EditText) findViewById(R.id.crime_title_edit_text);
        mTitle.setText(title1);

        mType = (RadioGroup)findViewById(R.id.radioGroup);
        dogRadioButton = (RadioButton)findViewById(R.id.radioButton_dog);
        catRadioButton = (RadioButton)findViewById(R.id.radioButton_cat);
        horseRadioButton = (RadioButton)findViewById(R.id.radioButton_horse);
        switch (type1)
        {
            case "0":
                dogRadioButton.setChecked(true);
                break;
            case "1":
                catRadioButton.setChecked(true);
                break;
            case "2":
                horseRadioButton.setChecked(true);
                break;
        }

        mSex = (RadioGroup)findViewById(R.id.radioGroup2);
        maleRadioButton = (RadioButton)findViewById(R.id.radioButton_male);
        femaleRadioButton = (RadioButton)findViewById(R.id.radioButton_female);
        switch (sex1)
        {
            case "0":
                maleRadioButton.setChecked(true);
                break;
            case "1":
                femaleRadioButton.setChecked(true);
                break;
        }



        mAge = (EditText) findViewById(R.id.editText_age);
        mAge.setText(age1);
        mCity = (EditText) findViewById(R.id.editText_city);
        mCity.setText(city1);
        mPhone = (EditText) findViewById(R.id.editText_phone);
        mPhone.setText(phone1);
        //mMail = (EditText) findViewById(R.id.editText_mail);
        mDescription = (EditText) findViewById(R.id.editText_description);
        mDescription.setText(desc1);
        mPhoto = (ImageView) findViewById(R.id.imageView_photo);
       // StorageReference islandRef = storage.getReferenceFromUrl("gs://helpanimals-f5035.appspot.com/90krUbBzxXQwm0WtwQ71AFDtJJ72Рыбак");
        Picasso.with(this).load(photo1).into(mPhoto);
        mLoadButton = (Button) findViewById(R.id.button_photo);

        // создаем обработчик нажатия
        mLoadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });



        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        //ImageView imageView = (ImageView) findViewById(R.id.imageView);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPhoto.setImageBitmap(bitmap);
                }
        }
    }

    private void submitPost() {
        final String title = mTitle.getText().toString();
        //Определяем тип животного
        final String type;
        if (dogRadioButton.isChecked()) {
            type = "0";
        }
        else if (catRadioButton.isChecked()){
            type = "1";
        }
        else {
            type = "2";
        }
        //Определяем пол
        final String sex;
        if (maleRadioButton.isChecked()) {
            sex = "0";
        }
        else {
            sex = "1";
        }

        final String age = mAge.getText().toString();
        final String city = mCity.getText().toString();
        final String phone = mPhone.getText().toString();
        //final String mail = mMail.getText().toString();
        final String desc = mDescription.getText().toString();
        final Bitmap photo = ((BitmapDrawable)mPhoto.getDrawable()).getBitmap();


        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitle.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(type)) {
            //mType.setError(REQUIRED);
            // mType.
            return;
        }

        if (TextUtils.isEmpty(sex)) {
            //mTitle.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(age)) {
            mAge.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(city)) {
            mCity.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhone.setError(REQUIRED);
            return;
        }





        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        //User user = dataSnapshot.getValue(User.class);
                        user = firebaseAuth.getCurrentUser();
                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(EditCrimeActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(user.getUid(), user.getDisplayName(), title, type, sex, age, city, phone, photo, desc);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        //mTitleField.setEnabled(enabled);
        //mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title,
                              String type,String sex,String age, String city,
                              String phone,Bitmap photo,String desc) {

        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);


        loadPhoto(userId, title);

        Crime crime = new Crime(userId, username, title, dateString,type,sex,age,city,phone, user.getEmail(), desc, link);

        Map<String, Object> postValues = crime.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + mPostKey, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + mPostKey, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    private void loadPhoto(String userid, String title)
    {
        StorageReference mountainsRef = storageRef.child(userid + title);
        mPhoto.setDrawingCacheEnabled(true);
        mPhoto.buildDrawingCache();
        Bitmap bitmap = mPhoto.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        //  final String lin;

        mountainsRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");
                        Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        //getUrl(downloadUrl);
                    }
                });

        //link = mountainsRef.

        // Listen for state changes, errors, and completion of the upload.
        /*uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
//                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
 //               link = downloadUrl.toString();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
       //         Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
       //         link = downloadUrl.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                link = downloadUrl.toString();

            }
        });*/


    }


}
