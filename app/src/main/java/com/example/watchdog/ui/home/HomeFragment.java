package com.example.watchdog.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.watchdog.Description;
import com.example.watchdog.R;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    String[] crimes = { "Road Accident", "Chain Snatching", "Heist", "Robbery", "Others"};
    private HomeViewModel homeViewModel;
    Spinner spinner;
    String crimeSelected;
    Button button;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("Report a Crime");
            }
        });
        spinner = (Spinner)root.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,crimes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
        button = (Button)root.findViewById(R.id.report);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Description.class);
                i.putExtra("crime",crimeSelected);
                startActivity(i);
            }
        });
        return root;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        crimeSelected = crimes[position];
        Toast.makeText(getActivity(),"_"+crimeSelected,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}