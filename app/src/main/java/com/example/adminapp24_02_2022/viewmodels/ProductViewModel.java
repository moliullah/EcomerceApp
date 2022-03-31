package com.example.adminapp24_02_2022.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adminapp24_02_2022.models.ProductModel;
import com.example.adminapp24_02_2022.models.PurchaseModel;
import com.example.adminapp24_02_2022.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private static final FirebaseFirestore db=FirebaseFirestore.getInstance();
    public MutableLiveData<List<String>>catagoriesListLiveData=new MutableLiveData<>();
    public MutableLiveData<List<ProductModel>>productModelListLiveData=new MutableLiveData<>();
   // public MutableLiveData<List<ProductModel>> productListLiveData = new MutableLiveData<>();
    public ProductViewModel() {
        getAllCatagoriesItems();
        getAllProducts();
    }
    public void getAllCatagoriesItems(){
        db.collection(Constants.DbCollection.COLLECTION_CATEGORY)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    final List<String> items = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        items.add(doc.get("name", String.class));
                    }
                   catagoriesListLiveData.postValue(items);
                });
    }
    public LiveData<ProductModel> getProductByProductId(String productId) {
     final MutableLiveData<ProductModel> productLiveData =
               new MutableLiveData<>();
        db.collection(Constants.DbCollection.COLLECTION_PRODUCT)
                .document(productId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                 productLiveData.postValue(
                            value.toObject(ProductModel.class));
                });
        return productLiveData;
    }
    public void addNewProduct(ProductModel productModel, PurchaseModel purchaseModel){
        WriteBatch writeBatch=db.batch();
        final DocumentReference pDoc=db.collection(Constants.DbCollection.COLLECTION_PRODUCT).document();
        productModel.setProductId(pDoc.getId());
        final DocumentReference purDoc=db.collection(Constants.DbCollection.COLLECTION_PURCHASE).document();
        purchaseModel.setPurchaseId(purDoc.getId());
        purchaseModel.setProductId(pDoc.getId());

        writeBatch.set(pDoc,productModel);
        writeBatch.set(purDoc,purchaseModel);
        writeBatch.commit().addOnSuccessListener(unused -> {

        }).addOnFailureListener(e -> {

        });
    }
    public void getAllProducts(){
        db.collection(Constants.DbCollection.COLLECTION_PRODUCT).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;

                final List<ProductModel> items = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    items.add(doc.toObject(ProductModel.class));
                }
                productModelListLiveData.postValue(items);
            }
        });
    }
}
