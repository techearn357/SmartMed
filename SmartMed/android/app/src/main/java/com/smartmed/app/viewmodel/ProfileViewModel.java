package com.smartmed.app.viewmodel;

import androidx.lifecycle.ViewModel;
import com.smartmed.app.utils.SharedPrefManager;

/** ViewModel for user profile. */
public class ProfileViewModel extends ViewModel {
    public String getUserName() { return SharedPrefManager.getInstance().getUserName(); }
    public String getUserEmail() { return SharedPrefManager.getInstance().getUserEmail(); }
}
