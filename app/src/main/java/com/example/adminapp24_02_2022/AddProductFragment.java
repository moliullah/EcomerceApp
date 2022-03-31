package com.example.adminapp24_02_2022;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.adminapp24_02_2022.databinding.FragmentAddProductBinding;
import com.example.adminapp24_02_2022.models.ProductModel;
import com.example.adminapp24_02_2022.models.PurchaseModel;
import com.example.adminapp24_02_2022.pickers.DatePickerDialogeFragment;
import com.example.adminapp24_02_2022.utils.Constants;
import com.example.adminapp24_02_2022.viewmodels.LoginViewModel;
import com.example.adminapp24_02_2022.viewmodels.ProductViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
public class AddProductFragment extends Fragment {
    private final String TAG = AddProductFragment.class.getSimpleName();
    private FragmentAddProductBinding binding;
    private ProductViewModel productViewModel;
    private LoginViewModel loginViewModel;
    String dateString,imageUrl,category;
    int year,month,day;
    private ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Uri photoUri = result.getData().getData();
                                if (photoUri != null) {
                                    binding.productImageView.setImageURI(photoUri);
                                }else {
                                    final Bitmap bitmap = (Bitmap) result.getData()
                                            .getExtras().get("data");
                                    binding.productImageView.setImageBitmap(bitmap);
                                }

                                binding.savaBtnId.setText("Please wait");
                                binding.savaBtnId.setEnabled(false);
                             uploadImage(photoUri);
                            }
                        }
                    });
    public AddProductFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddProductBinding.inflate(inflater);
        loginViewModel=new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        productViewModel=new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        binding.dateBtnId.setOnClickListener(v->{
            new DatePickerDialogeFragment().show(getChildFragmentManager(),null);
        });
        getChildFragmentManager().setFragmentResultListener(Constants.REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.containsKey(Constants.DATE_KEY)){
                    // Log.e("TAG","OK");
                    dateString=result.getString(Constants.DATE_KEY);
                    Log.e("Date",dateString);
                    year=result.getInt(Constants.YEAR);
                    month=result.getInt(Constants.MONTH);
                    day=result.getInt(Constants.DAY);
                    //Log.e("Date",dateString);
                    binding.dateBtnId.setText(dateString);
                }
            }
        });

        binding.cameraBtnId.setOnClickListener(v->{
            Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            launcher.launch(takePictureIntent);
        });
        binding.galleryBtnId.setOnClickListener(v->{
            final Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
    /*    loginViewModel.getStateLiveData().observe(getViewLifecycleOwner(), new Observer<LoginViewModel.AuthState>() {
            @Override
            public void onChanged(LoginViewModel.AuthState authState) {
                if (authState== LoginViewModel.AuthState.UNAUTHENTICATED){
                    Navigation.findNavController(container).navigate(R.id.apf_to_alf);
                }
            }
        });*/
        productViewModel.catagoriesListLiveData.observe(getViewLifecycleOwner(), catlist -> {
            final ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line,
                           catlist);
            binding.spinnerId.setAdapter(adapter);
        });
        binding.spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if (position > 0) {
                    category = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.savaBtnId.setOnClickListener(v->{
            saveProduct();
        });
        return binding.getRoot();
    }
    private void saveProduct(){
        final String name=binding.productNameId.getText().toString();
        final String purchasePrice=binding.productPurchasePriceId.getText().toString();
        final String salePrice=binding.productSalePriceId.getText().toString();
        final String description=binding.productDescriptionId.getText().toString();
        final String quentity=binding.productQuentityId.getText().toString();
        if (name.isEmpty()||purchasePrice.isEmpty()
                ||salePrice.isEmpty()||description.isEmpty()||quentity.isEmpty()){
            Toast.makeText(getActivity(),"provide all the field",Toast.LENGTH_SHORT).show();
            return;
        }
        if (category == null) {
            Toast.makeText(getActivity(),
                    "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dateString == null) {
            Toast.makeText(getActivity(),
                    "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl == null) {
            Toast.makeText(getActivity(), "Please select an image",
                    Toast.LENGTH_SHORT).show();
            return;
        }
       final ProductModel productModel=
                new ProductModel(null,name,category,
                        Double.parseDouble(salePrice),imageUrl,description,true);
       final PurchaseModel purchaseModel=new PurchaseModel(null,null,dateString,year,month,day,
                Double.parseDouble(purchasePrice),Integer.parseInt(quentity));


      //  Log.e(TAG, productModel.toString());
       // Log.e(TAG, purchaseModel.toString());
        productViewModel.addNewProduct(productModel,purchaseModel);
    }
    private void uploadImage(Uri photoUri) {
        //Log.e("firebasestorage", photoUri.toString());
        final StorageReference photoRef =
                FirebaseStorage.getInstance().getReference()
                        .child("images/"+System.currentTimeMillis());

        // Get the data from an ImageView as bytes
        binding.productImageView.setDrawingCacheEnabled(true);
        binding.productImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.productImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebasestorage", exception.getLocalizedMessage());
                binding.savaBtnId.setText("Save");
                binding.savaBtnId.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebasestorage", "Uploaded");
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    binding.savaBtnId.setText("Save");
                    binding.savaBtnId.setEnabled(true);
                    Log.e("firebasestorage", downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}