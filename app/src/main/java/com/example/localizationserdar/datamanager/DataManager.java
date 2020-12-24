package com.example.localizationserdar.datamanager;

import com.example.localizationserdar.datamodels.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import static com.example.localizationserdar.utils.Constants.COLLECTION_USERS;

public class DataManager implements DataManagerInterface {

    public static final String AUTHENTICATION = "Authentication";
    public static final String INVALID_USER = "Invalid User";
    private static DataManager mInstance;

    private DataManager() {

    }

    public static DataManager getInstance() {
        if (mInstance == null) {
            mInstance = new DataManager();
        }
        return mInstance;
    }

    @Override
    public void createUser(User user, DataListener<Boolean> listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            listener.onData(null, new FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER));
        }

        WriteBatch userBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference usersDocRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(user.userId);

        userBatch.set(usersDocRef, user.toMap());
        userBatch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onData(true, null);
            } else {
                listener.onData(false, task.getException());
            }
        });

    }
}
