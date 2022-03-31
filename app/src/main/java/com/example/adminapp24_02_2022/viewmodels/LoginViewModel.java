package com.example.adminapp24_02_2022.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adminapp24_02_2022.callback.OnCheckAdminListener;
import com.example.adminapp24_02_2022.repos.AdminRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {
    public enum AuthState{
        AUTHENTICATED,UNAUTHENTICATED
    }
    private MutableLiveData<AuthState>stateLiveData;
    private MutableLiveData<String>errorMsgLiveData;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public LoginViewModel() {
        stateLiveData=new MutableLiveData<>();
        errorMsgLiveData=new MutableLiveData<>();
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        if (user==null){
            stateLiveData.postValue(AuthState.UNAUTHENTICATED);
        }else {
            stateLiveData.postValue(AuthState.AUTHENTICATED);
        }
    }
    public MutableLiveData<AuthState> getStateLiveData() {
        return stateLiveData;
    }
    public MutableLiveData<String> getErrorMsgLiveData() {
        return errorMsgLiveData;
    }
    public FirebaseUser getUser() {
        return user;
    }
    public void login(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkAdmin(authResult.getUser().getUid());

            }
        }).addOnFailureListener(e->{
            errorMsgLiveData.postValue(e.getLocalizedMessage());
        });
    }
    private void checkAdmin(String uid) {
        AdminRepository.checkAdmin(uid, new OnCheckAdminListener() {
            @Override
            public void doesAdminExist(boolean status) {
                if (status) {
                    user = auth.getCurrentUser();
                    stateLiveData.postValue(AuthState.AUTHENTICATED);
                }else {
                   errorMsgLiveData.postValue("You are not an Admin");
                    auth.signOut();
                }

            }
        });
    }
    public void logout() {
        auth.signOut();
        stateLiveData.postValue(AuthState.UNAUTHENTICATED);
    }
}
