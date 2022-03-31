package com.example.adminapp24_02_2022;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adminapp24_02_2022.databinding.FragmentProductDetailsBinding;
import com.example.adminapp24_02_2022.viewmodels.ProductViewModel;


public class ProductDetailsFragment extends Fragment {
    private FragmentProductDetailsBinding binding;
    private ProductViewModel productViewModel;
    private String productId;
    public ProductDetailsFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater);
        productViewModel = new ViewModelProvider(requireActivity())
                .get(ProductViewModel.class);
        productId = getArguments().getString("pid");
        productViewModel.getProductByProductId(productId)
                .observe(getViewLifecycleOwner(), productModel -> {
                    binding.setProduct(productModel);
                });
        return binding.getRoot();
    }
}