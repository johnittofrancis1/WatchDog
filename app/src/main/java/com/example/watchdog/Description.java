package com.example.watchdog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Description extends AppCompatActivity implements LocationListener {
    String path,locationURL,crimeSelected;
    EditText description;
    ImageView img;
    Button button,submit;
    Bitmap selectedBitmap;
    String picturePath;
    FusedLocationProviderClient mFusedLocationClient;
    private static final String FILE_NAME = "EmergencyContacts.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        path = Environment.getExternalStorageDirectory().toString()+"/WatchDog";
        File folder = new File(path);
        if(!folder.exists())
        {
            folder.mkdir();
        }
        Intent i = getIntent();
        final String crimeSelected =  i.getStringExtra("crime");
        description = findViewById(R.id.description);
        img = findViewById(R.id.photo);
        button = findViewById(R.id.button);
        submit = findViewById(R.id.submit);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastlocation();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(Description.this);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = description.getText().toString();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("CrimesReported").child(crimeSelected).child("1").child("Description").setValue(desc);
                db.child("CrimesReported").child(crimeSelected).child("1").child("Location").setValue(locationURL);
                FileInputStream fis = null;
                try {
                    fis = openFileInput(FILE_NAME);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    String text;
                    while ((text = br.readLine()) != null) {
                        int i = 0;
                        text = text.replaceAll("-", "");
                        while (text.charAt(i) != ' ')
                            text = text.replaceFirst(String.valueOf(text.charAt(i)), "");
                        sendSMS(text, locationURL);
                    }
                    FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                    String s1 = "";
                    fos.write(s1.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                Uri file = Uri.fromFile(new File(path,"temp.jpg"));
                UploadTask uploadTask = storageReference.child("1").putFile(file);
                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_SHORT).show();
                            }

                        });

                Toast.makeText(getApplicationContext(),"Crime Reported",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage (Context context)
    {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your image");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takepicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(takepicture, 0);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickphoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(pickphoto, 1);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        File file = new File(path,"temp.jpg");
                        FileOutputStream fout = null;
                        try {
                            fout = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        img.setImageBitmap(selectedBitmap);
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG,85,fout);
                        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                        picturePath = String.valueOf(savedImageURI);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String n = String.valueOf(filePathColumn[0]);
                                picturePath = cursor.getString(columnIndex);
                                selectedBitmap = BitmapFactory.decodeFile(picturePath);
                                File file = new File(path,"temp.jpg");
                                FileOutputStream fout = null;
                                try {
                                    fout = new FileOutputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                img.setImageBitmap(selectedBitmap);
                                selectedBitmap.compress(Bitmap.CompressFormat.JPEG,100,fout);
                                Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                                picturePath = String.valueOf(savedImageURI);
                                cursor.close();

                            } else {
                                Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
            }

        }
    }

    @SuppressLint("MissingPermission")
    private void getLastlocation()
    {
        if(checkPermissions())
        {
            if(isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        TextView txtLat = (TextView) findViewById(R.id.loc);
                        locationURL = "http://www.google.com/maps/place/"+ location.getLatitude() + "," + location.getLongitude();
                        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                    }
                });
            }
        }
    }

    private void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
            return;
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private boolean checkPermissions()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }
    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
