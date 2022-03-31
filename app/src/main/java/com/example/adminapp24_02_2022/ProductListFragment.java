package com.example.adminapp24_02_2022;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adminapp24_02_2022.adapters.ProductAdapter;
import com.example.adminapp24_02_2022.callback.OnProductItemClickListener;
import com.example.adminapp24_02_2022.databinding.FragmentProductListBinding;
import com.example.adminapp24_02_2022.viewmodels.ProductViewModel;


public class ProductListFragment extends Fragment implements OnProductItemClickListener {
    private final String TAG=ProductListFragment.class.getSimpleName();
    private FragmentProductListBinding binding;
    private ProductViewModel productViewModel;
    private String category;

    public ProductListFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater);
        productViewModel = new ViewModelProvider(requireActivity())
                .get(ProductViewModel.class);
        final ProductAdapter adapter = new ProductAdapter(this);
        binding.productRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.productRV.setAdapter(adapter);
        productViewModel.productModelListLiveData.observe(getViewLifecycleOwner(),
                productList -> {
                    adapter.submitList(productList);
                });
        return binding.getRoot();
    }

    @Override
    public void onProductItemClicked(String productId) {
        final Bundle bundle = new Bundle();
        bundle.putString("pid", productId);
        Navigation.findNavController(getActivity(), R.id.fragmentContainerView)
                .navigate(R.id.plf_to_pdf, bundle);
    }

}