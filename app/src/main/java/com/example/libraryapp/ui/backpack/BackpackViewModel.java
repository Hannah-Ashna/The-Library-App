package com.example.libraryapp.ui.backpack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BackpackViewModel extends ViewModel{

    private final MutableLiveData<String> mText;

    public BackpackViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is backpack fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
