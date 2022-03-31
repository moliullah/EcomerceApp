package com.example.adminapp24_02_2022;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adminapp24_02_2022.databinding.FragmentAdminLoginBinding;
import com.example.adminapp24_02_2022.viewmodels.LoginViewModel;

public class AdminLoginFragment extends Fragment {
    private FragmentAdminLoginBinding binding;
    private LoginViewModel loginViewModel;
    public AdminLoginFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAdminLoginBinding.inflate(inflater);
        loginViewModel=new ViewModelProvider(requireActivity()).get(LoginViewModel.class);


        binding.adminLoginBtnId.setOnClickListener(v->{
            final String email=binding.adminEmailId.getText().toString();
            final String pass=binding.adminPasswordId.getText().toString();
            loginViewModel.login(email,pass);
        });
        loginViewModel.getErrorMsgLiveData().observe(getViewLifecycleOwner(),ersMsg->{
            binding.adminErrorMessageId.setText(ersMsg);
        });
     loginViewModel.getStateLiveData().observe(getViewLifecycleOwner(), new Observer<LoginViewModel.AuthState>() {
            @Override
            public void onChanged(LoginViewModel.AuthState authState) {
                if (authState== LoginViewModel.AuthState.AUTHENTICATED){
                    Navigation.findNavController(container).navigate(R.id.alf_to_df);
                }
            }
        });
        return binding.getRoot();
    }
}