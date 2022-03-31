package com.example.adminapp24_02_2022;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adminapp24_02_2022.adapters.DashboardAdapter;
import com.example.adminapp24_02_2022.callback.OnDashboardItemSelectedListener;
import com.example.adminapp24_02_2022.databinding.FragmentDashboardBinding;
import com.example.adminapp24_02_2022.models.DashboardItem;
import com.example.adminapp24_02_2022.utils.Constants;
import com.example.adminapp24_02_2022.viewmodels.LoginViewModel;

public class DashboardFragment extends Fragment implements OnDashboardItemSelectedListener {
    private FragmentDashboardBinding binding;
    private LoginViewModel loginViewModel;
    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater);
        loginViewModel = new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class);
        loginViewModel.getStateLiveData().observe(getViewLifecycleOwner(), authState -> {
            if (authState == LoginViewModel.AuthState.UNAUTHENTICATED) {
                Navigation.findNavController(container)
                        .navigate(R.id.df_to_alf);
            }
        });
       final DashboardAdapter adapter=new DashboardAdapter(this,DashboardItem.getDashboardItems());
        final GridLayoutManager glm = new GridLayoutManager(getActivity(),
                2);
        binding.dashboardRV.setLayoutManager(glm);
        binding.dashboardRV.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onDashboardItemSelect(String item) {
        switch (item) {
            case Constants.Item.ADD_PRODUCT:
                Navigation.findNavController(getActivity(),
                        R.id.fragmentContainerView)
                        .navigate(R.id.dbf_to_apf);
                break;
            case Constants.Item.VIEW_PRODUCT:
                Navigation.findNavController(getActivity(),
                        R.id.fragmentContainerView)
                        .navigate(R.id.db_to_plf);
                break;
            case Constants.Item.VIEW_ORDERS:

                break;
            case Constants.Item.ADD_CATEGORY:

                break;
            case Constants.Item.VIEW_REPORTS:

                break;
            case Constants.Item.SETTINGS:

                break;
            case Constants.Item.VIEW_USERS:

                break;
        }
    }
}