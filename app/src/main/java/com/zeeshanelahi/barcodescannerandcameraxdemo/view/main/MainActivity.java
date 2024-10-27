package com.zeeshanelahi.barcodescannerandcameraxdemo.view.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zeeshanelahi.barcodescannerandcameraxdemo.R;
import com.zeeshanelahi.barcodescannerandcameraxdemo.databinding.ActivityMainBinding;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.StringValue;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment.InputFragment;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment.MenuFragment;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment.QueueListFragment;
import com.zeeshanelahi.barcodescannerandcameraxdemo.view.fragment.ScanFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentChangeListener{

    private final String TAG = "MainActivity";
    private ActivityMainBinding viewBinding;

    private FragmentManager fragmentManager;
    private Fragment activeFragment;
    private Fragment fragmentMenu;
    private Fragment fragmentScan;
    private Fragment fragmentInput;
    private Fragment fragmentQueue;

    private BottomNavigationView bottomNavigationView;

    private InternetStateListenerer internetStateListenerer = new InternetStateListenerer() {
        @Override
        public void onConnected() {
            viewBinding.warningTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onDisconnected() {
            viewBinding.warningTextView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        //tat action bar
        getSupportActionBar().hide();
        setContentView(viewBinding.getRoot());
        setUpViewEvents();

        setUpFragment();
        setUpBottomNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        InternetBroadCastReceiver.getInstance(internetStateListenerer).activeBroadCast(this);

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
        fragmentInput = new InputFragment();
        fragmentQueue = new QueueListFragment();

        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentMenu, "fragmentMenu").commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentScan, "fragmentScan").hide(fragmentScan).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentInput, "fragmentInput").hide(fragmentInput).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentQueue, "fragmentQueue").hide(fragmentQueue).commit();

        activeFragment = fragmentMenu;
    }

    private void setUpViewEvents() {
    }

    public void onChangFragment(int id) {
        if (id == StringValue.INPUT_FRAGMENT) {
            //change to input fragment
            fragmentManager.beginTransaction().hide(activeFragment).show(fragmentInput).commit();
            activeFragment = fragmentInput;
            bottomNavigationView.setSelectedItemId(R.id.input_mssv);
        } else if (id == StringValue.SCAN_FRAGMENT) {
            //change to queue fragment
            fragmentManager.beginTransaction().hide(activeFragment).show(fragmentScan).commit();
            activeFragment = fragmentScan;
            bottomNavigationView.setSelectedItemId(R.id.scan_qr_code);
        }
    }

    @Override
    public void onChangeFragment(int id) {
        onChangFragment(id);
    }
}