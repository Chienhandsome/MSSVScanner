package com.zeeshanelahi.barcodescannerandcameraxdemo.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityContainerBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment.MenuFragment;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment.ScanFragment;

public class MainActivity extends AppCompatActivity {
    
    private final String TAG = "MainActivity";
    private ActivityContainerBinding viewBinding;

    private FragmentManager fragmentManager;
    private Fragment activeFragment;

    private Fragment fragmentMenu;
    private Fragment fragmentScan;
    private Fragment fragmentInput;
    private Fragment fragmentQueue;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        setUpViewEvents();
        
        setUpFragment();
        setUpBottomNavigation();
    }

    private void setUpBottomNavigation() {
        bottomNavigationView = viewBinding.bottomNavigation;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                fragmentManager.beginTransaction().hide(activeFragment).show(fragmentMenu).commit();
                activeFragment = fragmentMenu;
                return true;
            } else if (itemId == R.id.scan_qr_code) {
                fragmentManager.beginTransaction().hide(activeFragment).show(fragmentScan).commit();
                activeFragment = fragmentScan;
                return true;
            } else if (itemId == R.id.input_mssv) {
                fragmentManager.beginTransaction().hide(activeFragment).show(fragmentInput).commit();
                activeFragment = fragmentInput;
                return true;
            } else if (itemId == R.id.queue_list) {
                fragmentManager.beginTransaction().hide(activeFragment).show(fragmentQueue).commit();
                activeFragment = fragmentQueue;
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void setUpFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentMenu = new MenuFragment();
        fragmentScan = new ScanFragment();
//        fragmentInput = new InputFragment();
//        fragmentQueue = new QueueFragment();

        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentMenu, "fragmentMenu").commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentScan, "fragmentScan").hide(fragmentScan).commit();
//        fragmentManager.beginTransaction().add(R.id.main_container, fragmentInput, "fragmentInput").hide(fragmentInput).commit();
//        fragmentManager.beginTransaction().add(R.id.main_container, fragmentQueue, "fragmentQueue").hide(fragmentQueue).commit();

        activeFragment = fragmentMenu;

    }

    private void setUpViewEvents() {
    }
}
