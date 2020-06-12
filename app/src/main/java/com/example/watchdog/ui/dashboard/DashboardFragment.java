package com.example.watchdog.ui.dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchdog.Adapter;
import com.example.watchdog.Pojo;
import com.example.watchdog.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    Button b1;
    List<Pojo> imagesList;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    RecyclerView recyclerView;
    private static final int REQUEST_CODE = 1;
    private static final String FILE_NAME="EmergencyContacts.txt";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Emergency Contacts");
            }
        });
        imagesList = new ArrayList<>();
        b1 = (Button) root.findViewById(R.id.addcontact);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                Toast.makeText(getActivity(),"Name = "+name+"Number = "+number,Toast.LENGTH_LONG).show();
                setlayout(name,number);
            }
        }
    };

    public void setlayout(String Name,String Number)    {
        Pojo images=new Pojo();
        images.setS1(Name);
        images.setS2(Number);
        imagesList.add(images);
        recyclerViewadapter = new Adapter(imagesList, getActivity().getApplicationContext());
        recyclerView.setAdapter(recyclerViewadapter);
        store(Name,Number);
    }

    public void store(String name,String number) {
        FileOutputStream fos = null;
        try {
            fos = getActivity().getApplicationContext().openFileOutput(FILE_NAME, MODE_PRIVATE | MODE_APPEND);
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" ").append(number).append("\n");
            String result = sb.toString();
            fos.write(result.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}