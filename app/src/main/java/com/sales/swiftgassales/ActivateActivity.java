package com.sales.swiftgassales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActivateActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference SalesRef = db.collection("Sales_team");
    CollectionReference ActiveRef = db.collection("Active_shops");
    CollectionReference vendorRef = db.collection("SwiftGas_Vendor");

    private ProgressDialog progressDialog;
    private TextView activated,Shopname,VendorName,ShopNo;
    private Button Btn_activate;
    private EditText InputShopNo;
    private CircleImageView ShopImage;

    private String shopNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        mAuth = FirebaseAuth.getInstance();
        activated = findViewById(R.id.activated);
        Btn_activate = findViewById(R.id.btn_activate);
        InputShopNo = findViewById(R.id.shop_no);
        Shopname = findViewById(R.id.ActiveShopName);
        VendorName = findViewById(R.id.ActiveVendorName);
        ShopNo = findViewById(R.id.ActiveShopNo);
        ShopImage = findViewById(R.id.ShopImage);


        Btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shopNumber = InputShopNo.getText().toString().trim();
                if (shopNumber.equals("")){
                    ToastBack("Please provide shop number");
                }else {

                    ActivateShop(shopNumber);
                    Btn_activate.setEnabled(false);
                }
            }
        });
        LoadDetails();
    }



    String  activeFee;
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

                            String shopNo = doc.getString("Shop_No");
                            String shopName = doc.getString("ShopName");
                            String vendorName = doc.getString("first_name");
                            String userID = doc.getString("User_ID");
                            activeFee = doc.getString("Activation_fee");
                            Shopname.setText(shopName);
                            VendorName.setText(vendorName);
                            ShopNo.setText(shopNo);
                            BatchActivate(userID);

                        }else {
                            ToastBack("Shop number is't available..");
                            progressDialog.dismiss();
                        }
                    }
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
                    if(User_image != null) {
                        Picasso.with(ActivateActivity.this).load(User_image).into(ShopImage);
                    }
                    activated.setText(ActiveNo+"");

                } else {

                }


            }
        });

    }

    private void BatchActivate(String id) {

        if (activeFee.equals("200")){
            ToastBack("Shop is already activated");
            progressDialog.dismiss();
        }else if (activeFee.equals("00")){
            ToastBack("Shop is already activated");
            progressDialog.dismiss();
        }else {

            WriteBatch batch;
            batch = db.batch();
            DocumentReference doc1 = vendorRef.document(id);
            batch.update(doc1, "Activation_fee", "200");
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        SaleShopCount();

                    }else {

                        ToastBack(task.getException().getMessage());
                        progressDialog.dismiss();
                        Btn_activate.setEnabled(true);
                    }
                }
            });

        }

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
}