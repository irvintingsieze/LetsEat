package com.example.letseat.Boundary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.letseat.Constants;
import com.example.letseat.Control.SystemControl;
import com.example.letseat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/*
Display user details like name, email, cover photo, profile photo.
Enable user to change name, cover photo, profile photo and password
 */
public class ProfileUI extends Fragment {

    //Permissions to be requested
    private String cameraPermissions[];
    private String storagePermissions[];

    private ProgressDialog pd;
    private Uri image_uri;

    //for checking profile or cover photo
    private String profileOrCoverPhoto;

    //path where user profile and cover image will be stored
    String storagePath = "Users_Profile_Cover_Imgs/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v =  inflater.inflate(R.layout.activity_profile_ui, container, false);

        getActivity().setTitle("User Profile");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        StorageReference storageReference = getInstance().getReference();
        //firebase storage ref

        //Array of permissions
        cameraPermissions = new String []{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init
        final ImageView avatarIV = v.findViewById(R.id.avatarIV);
        final TextView nameTV = v.findViewById(R.id.nameTV);
        final TextView emailTV = v.findViewById(R.id.emailTV);
        final ImageView coverIV = v.findViewById(R.id.coverIV);
        FloatingActionButton fab = v.findViewById(R.id.fab);

        pd = new ProgressDialog(getActivity());
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    SystemControl sc = new SystemControl();
                    ArrayList<String> userDetails = sc.getUserDetails(ds);
                    String name = "Name: " + userDetails.get(0);
                    String email = "Email: " + userDetails.get(1);
                    String image = "" + userDetails.get(2);
                    String cover = "" + userDetails.get(3);
                    nameTV.setText(name);
                    emailTV.setText(email);
                    RequestOptions requestOptionsImage = new RequestOptions().centerCrop().placeholder(R.drawable.editprofile).error(R.drawable.editprofile);
                    Glide.with(getActivity()).load(image).apply(requestOptionsImage).into(avatarIV);
                    RequestOptions requestOptionsCover = new RequestOptions().centerCrop().placeholder(R.drawable.purple_background).error(R.drawable.purple_background);
                    Glide.with(getActivity()).load(cover).apply(requestOptionsCover).into(coverIV);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        return v;
    }

    private boolean checkStoragePermission(){

        //Check if storage permission is enabled
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission(){
        requestPermissions(storagePermissions, Constants.STORAGE_REQ_CODE);
    }

    private boolean checkCameraPermission(){
        //Check if storage permission is enabled
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED) &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

    }

    private void requestCameraPermission(){
        requestPermissions(cameraPermissions,Constants.CAMERA_REQ_CODE);
    }

    private void showEditProfileDialog(){
        pd.setMessage("Updating ");
        String fabOptions [] = {"Edit Profile picture","Edit Cover Photo", "Edit Name", "Change Password" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose edit options");
        //set items to dialog
        builder.setItems(fabOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        pd.setMessage("Updating Profile Picture");
                        profileOrCoverPhoto = "image";
                        showImagePicDialog();
                        //Edit Profile Picture
                        break;
                    case 1:
                        pd.setMessage("Updating Cover Photo");
                        profileOrCoverPhoto = "cover";
                        showImagePicDialog();
                        //Edit cover photo
                        break;
                    case 2:
                        pd.setMessage("Updating Name");
                        showNameUpdateDialog();
                        //Edit name
                        break;
                    case 3:
                        pd.setMessage("Changing Password");
                        showChangePasswordDialog();
                        break;

                }
            }
        });
        builder.create().show();

    }

    private void showImagePicDialog(){
        String fabOptions [] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose image from ");
        //set items to dialog
        builder.setItems(fabOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        //camera
                        if (!checkCameraPermission()){
                            requestCameraPermission();
                        }else{
                            pickFromCamera();
                        }
                        break;
                    case 1:
                        //Gallery
                        if (!checkStoragePermission()){
                            requestStoragePermission();
                        }else{
                            pickFromGallery();
                        }
                        break;

                }
            }
        });
        builder.create().show();

    }

    private void showNameUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update name");
        //Layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation((LinearLayout.VERTICAL));
        linearLayout.setPadding(20,10,20,10);

        //EditText
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter your name");
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //Button for update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text
                final String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    SystemControl sc = new SystemControl();
                    sc.setUserDetails(0, value, pd);
                }else{
                    Toast.makeText(getActivity(),"Enter your name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button for cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //Create n show dialog
        builder.create().show();
    }

    private void showChangePasswordDialog(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_password, null);
        final EditText passwordEt = view.findViewById(R.id.passwordET);
        final EditText newPasswordEt = view.findViewById(R.id.newPasswordET);
        final Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validation
                String oldPassword = passwordEt.getText().toString().trim();
                String newPassword = newPasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)){
                    passwordEt.setError("Please enter your old password!");
                    passwordEt.setFocusable(true);
                    return;
                }
                else if (newPassword.length()<6){
                    newPasswordEt.setError("New Password needs to be at least 6 characters");
                    newPasswordEt.setFocusable(true);
                    return;
                }else if (!Constants.PASSWORD_AUTH.matcher(newPassword).matches()) {
                    newPasswordEt.setError("Password too weak!");
                    newPasswordEt.setFocusable(true);
                    return;
                }

                dialog.dismiss();
                updatePassword(oldPassword,newPassword);

            }
        });
    }

    private void updatePassword(String oldPassword, final String newPassword){
        pd.show();
        //get current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Success authentication, can update

                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Password Updated...", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Auth failed!
                pd.dismiss();
                Toast.makeText(getActivity(), ""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //handle permission cases

        switch (requestCode){
            case Constants.CAMERA_REQ_CODE:{

                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&&writeStorageAccepted){
                        //permission ok
                        pickFromCamera();
                    }
                    else{
                        //permission not ok
                        Toast.makeText(getActivity(),"Please enable permissions", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
            case Constants.STORAGE_REQ_CODE:{

                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permission ok
                        pickFromGallery();
                    }
                    else{
                        //permission not ok
                        Toast.makeText(getActivity(),"Please enable gallery permissions", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==Constants.IMAGE_PICK_GALLERY_CODE){
                //get URI of image
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode == Constants.IMAGE_PICK_CAMERA_CODE){
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        StorageReference storageReference = getInstance().getReference();
        pd.show();
        //path and name of image to be stored in firebase storage
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_"+ user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);

        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();


                        //check if image is uploaded
                        if(uriTask.isSuccessful()){
                            //image uploaded
                            HashMap<String, Object> results = new HashMap<>();
                            //HERE NEED TO CHANGE
                            results.put(profileOrCoverPhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Image Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Image Update error...", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else{
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "THIS IS THE ERROR" +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //Put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent,Constants.IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery(){
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Constants.IMAGE_PICK_GALLERY_CODE);

    }

}