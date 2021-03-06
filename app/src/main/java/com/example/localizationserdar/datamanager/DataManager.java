package com.example.localizationserdar.datamanager;

import com.example.localizationserdar.datamodels.Beacon;
import com.example.localizationserdar.datamodels.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.LinkedList;
import java.util.List;

import static com.example.localizationserdar.utils.Constants.COLLECTION_BEACONS;
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

    @Override
    public void createBeaconInfoReward(User user, Beacon beacon, DataListener<Boolean> listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            listener.onData(null, new FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER));
        }

        WriteBatch createBeaconInfoReward = FirebaseFirestore.getInstance().batch();
        DocumentReference subBeaconDocRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(user.userId).collection(COLLECTION_BEACONS).document(beacon.beaconId);

        createBeaconInfoReward.set(subBeaconDocRef, beacon.toMap());
        createBeaconInfoReward.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onData(true, null);
            } else {
                listener.onData(false, task.getException());
            }
        });
    }

    @Override
    public void getCurrentUser(DataListener<User> listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            listener.onData(null, new FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER));
        } else {
            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(firebaseUser.getUid());

            FirebaseFirestore.getInstance().runTransaction(transaction -> {
                DocumentSnapshot userDoc = transaction.get(userDocRef);

                User currentUser = userDoc.toObject(User.class);
                if (currentUser != null) {
                    if (!currentUser.email.equals(firebaseUser.getEmail())) {
                        currentUser.email = firebaseUser.getEmail();
                    }

                    if (currentUser.email == null || currentUser.email.isEmpty()) {
                        currentUser.email = firebaseUser.getEmail();
                    }

                    transaction.set(userDocRef, currentUser.toMap(), SetOptions.merge());
                    return currentUser;
                } else {
                    return null;
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    User currentUser = task.getResult();
                    listener.onData(currentUser, null);
                } else {
                    listener.onData(null, task.getException());
                }
            });
        }
    }

    @Override
    public void updateUser(User user, DataListener<Boolean> listener) {
        WriteBatch updateUserDetails = FirebaseFirestore.getInstance().batch();
        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(user.userId);

        updateUserDetails.update(userDocRef, user.toMap());

        updateUserDetails.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onData(true, null);
            } else {
                listener.onData(false, task.getException());
            }
        });
    }

    @Override
    public void updateBeacon(User user, Beacon beacon, DataListener<Boolean> listener) {
        WriteBatch updateBeaconDetails = FirebaseFirestore.getInstance().batch();
        DocumentReference subBeaconDocRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(user.userId).collection(COLLECTION_BEACONS).document(beacon.beaconId);

        updateBeaconDetails.update(subBeaconDocRef, beacon.toMap());
        updateBeaconDetails.commit().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                listener.onData(true, null);
            } else {
                listener.onData(false, task.getException());
            }
        });
    }

    @Override
    public void getBeacons(DataListener<List<Beacon>> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            listener.onData(null, new FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER));
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(COLLECTION_BEACONS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            List<Beacon> beacons = new LinkedList<>();
                            if (querySnapshot == null || querySnapshot.isEmpty()) {
                                listener.onData(null, null);
                                return;
                            }
                            for (QueryDocumentSnapshot document: querySnapshot) {
                                beacons.add(document.toObject(Beacon.class));
                            }
                            listener.onData(beacons, null);
                        } else {
                            listener.onData(null, task.getException());
                        }
                    });
        }
    }

    @Override
    public void getBeaconsBelongsToUser(User user, DataListener<List<Beacon>> listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            listener.onData(null, new FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER));
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(COLLECTION_USERS).document(user.userId).collection(COLLECTION_BEACONS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            List<Beacon> beacons = new LinkedList<>();
                            if (querySnapshot == null || querySnapshot.isEmpty()) {
                                listener.onData(null, null);
                                return;
                            }
                            for (QueryDocumentSnapshot document: querySnapshot) {
                                beacons.add(document.toObject(Beacon.class));
                            }
                            listener.onData(beacons, null);
                        } else {
                            listener.onData(null, task.getException());
                        }
                    });
        }
    }


}
