package com.example.watchdog.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.watchdog.Account;
import com.example.watchdog.R;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    TextView name,mail,phone;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        Account account = new Account();
        name = root.findViewById(R.id.name);
        mail = root.findViewById(R.id.mail);
        phone = root.findViewById(R.id.phone);
        name.setText(account.getName());
        mail.setText(account.getEmailId());
        phone.setText(account.getPhoneno());
        return root;
    }
}