package com.sales.swiftgassales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;

public class ActivateActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference SalesRef = db.collection("Sales_team");
    CollectionReference ActiveRef = db.collection("Active_shops");
    CollectionReference vendorRef = db.collection("SwiftGas_Vendor");

    private ProgressDialog progressDialog;
    private TextView activated;
    private Button Btn_activate;
    private EditText InputShopNo;
    private String shopNumber;
    private ActiveShopAdapter adapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        mAuth = FirebaseAuth.getInstance();
        activated = findViewById(R.id.activated);
        Btn_activate = findViewById(R.id.btn_activate);
        InputShopNo = findViewById(R.id.shop_no);
        mRecyclerView = findViewById(R.id.recycler_activate);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchProduct();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });

        Btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shopNumber = InputShopNo.getText().toString().trim();
                if (shopNumber.equals("")){
                    ToastBack("Please provide shop number");
                }else if (shopNumber.length() < 8){
                    ToastBack("Invalid shop number");
                }else if (shopNumber.length() > 8){
                    ToastBack("Invalid shop number");
                } else {

                    ActivateShop(shopNumber);
                    Btn_activate.setEnabled(false);
                }
            }
        });
        LoadDetails();
        FetchProduct();
    }



    String  activeFee,shopNo,shopName,vendorName,userID,VendorImage;
        private void ActivateShop(String shopNumber) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please activating");
            progressDialog.setCancelable(false);
            progressDialog.show();


            vendorRef.whereEqualTo("Shop_No",shopNumber)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null) {

                        return;
                    }
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){

                        if (doc.exists()){

                             shopNo = doc.getString("Shop_No");
                             shopName = doc.getString("ShopName");
                             vendorName = doc.getString("first_name");
                             userID = doc.getString("User_ID");
                            activeFee = doc.getString("Activation_fee");
                            VendorImage = doc.getString("User_Image");
                            if (activeFee.equals("200")){
                                ToastBack("Shop is already activated");
                                progressDialog.dismiss();
                            }else if (activeFee.equals("00")){
                                ToastBack("Shop is already activated");
                                progressDialog.dismiss();
                            }else {
                                BatchActivate(userID);
                            }

                        }else {
                            ToastBack("Shop number is't available..");
                            progressDialog.dismiss();
                        }
                    }
                }
            });

    }
    private void FetchProduct() {

            Query query = ActiveRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
            FirestoreRecyclerOptions<ActiveShop> transaction = new FirestoreRecyclerOptions.Builder<ActiveShop>()
                    .setQuery(query, ActiveShop.class)
                    .setLifecycleOwner(this)
                    .build();
            adapter = new ActiveShopAdapter(transaction);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(adapter);


            adapter.setOnItemClickListener(new ActiveShopAdapter.OnItemCickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {



                }
            });



    }



    private String User_image,Phone_no,pin;
    private long ActiveNo;
    private void LoadDetails() {

        SalesRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {

                    ActiveNo = documentSnapshot.getLong("Activated_shops");
                    User_image = documentSnapshot.getString("User_Image");
                    activated.setText(ActiveNo+"");

                } else {

                }


            }
        });

    }

    private void BatchActivate(String id) {



            WriteBatch batch;
            batch = db.batch();
            DocumentReference doc1 = vendorRef.document(id);
            batch.update(doc1, "Activation_fee", "200");
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        SaleShopCount();
                        saveActiveShop();

                    }else {

                        ToastBack(task.getException().getMessage());
                        progressDialog.dismiss();
                        Btn_activate.setEnabled(true);
                    }
                }
            });



    }

    private void saveActiveShop() {


        String DocID = ActiveRef.document().getId();

        HashMap<String,Object> save= new HashMap<>();
        save.put("shopName",shopName);
        save.put("shopNo",shopNo);
        save.put("vendorName",vendorName);
        save.put("timestamp", FieldValue.serverTimestamp());
        save.put("image",VendorImage);
        save.put("User_ID",mAuth.getCurrentUser().getUid());
        ActiveRef.document(DocID).set(save).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if (task.isSuccessful()){
                 FetchProduct();
             }else {

                 ToastBack(task.getException().getMessage());
             }
            }
        });
    }

    private void SaleShopCount() {

        long total = ActiveNo + 1;
        WriteBatch batch;
        batch = db.batch();
        DocumentReference doc1 = SalesRef.document(mAuth.getCurrentUser().getUid());
        batch.update(doc1, "Activated_shops", total);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ToastBack("This Shop was activated");
                    Btn_activate.setEnabled(true);
                    progressDialog.dismiss();
                }else {
                    ToastBack(task.getException().getMessage());
                    Btn_activate.setEnabled(true);
                }
            }
        });
    }


    private Toast backToast;
    private void ToastBack(String message) {
        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#242A37"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FF8900"));
        backToast.show();
    }



    private long backPressedTime;
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
            return;
        } else {
            ToastBack("Double tap to exit");
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FetchProduct();
    }
}