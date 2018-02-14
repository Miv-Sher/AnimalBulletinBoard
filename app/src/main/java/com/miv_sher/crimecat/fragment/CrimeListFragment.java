package com.miv_sher.crimecat.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.miv_sher.crimecat.R;
import com.miv_sher.crimecat.activity.CrimeActivity;
//import com.miv_sher.crimecat.activity.CrimeDetailActivity;
import com.miv_sher.crimecat.activity.CrimePagerActivity;
import com.miv_sher.crimecat.activity.EditCrimeActivity;
import com.miv_sher.crimecat.activity.NewCrimeActivity;
import com.miv_sher.crimecat.model.Crime;
import com.miv_sher.crimecat.model.CrimeLab;
import com.miv_sher.crimecat.viewholders.*;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public abstract class CrimeListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Crime, CrimeViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public ImageButton phone_button;
    public ImageButton mailButton;
    public ImageButton deleteButton;
    public ImageButton editButton;
    public TextView mPhone;
    public TextView mMail;
    TextView mTitle;
    public ImageView mPhoto;

    AlertDialog.Builder ad;
    AlertDialog.Builder ad2;

    public CrimeListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.crime_recycler_view);
        mRecycler.setHasFixedSize(true);


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Crime, CrimeViewHolder>(Crime.class, R.layout.list_item_crime,
                CrimeViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final CrimeViewHolder viewHolder, final Crime model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                       /* Intent intent = new Intent(getActivity(), CrimeActivity.class);
                        intent.putExtra(CrimeActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);*/
                    }
                });

                mPhoto = (ImageView) viewHolder.itemView.findViewById(R.id.imageView_avatar);
                Picasso.with(getContext()).load(model.image).into(mPhoto);

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);

                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                        // Create new post at /user-posts/$userid/$postid and at
                        // /posts/$postid simultaneously
                        //String key = mDatabase.child("posts").push().getKey();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/user-fav/"  + user.getUid() + "/" + postRef.getKey(), model);
                        mDatabase.updateChildren(childUpdates);

                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                        DatabaseReference userFavRef = mDatabase.child("user-fav").child(user.getUid()).child(postRef.getKey());
                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                        onStarUnClicked(userFavRef);
                    }
                });


                phone_button = (ImageButton) viewHolder.itemView.findViewById(R.id.imageButton_call);
                mailButton = (ImageButton) viewHolder.itemView.findViewById(R.id.imageButton_mail);
                mPhone = (TextView) viewHolder.itemView.findViewById(R.id.text_phone);
                mMail = (TextView) viewHolder.itemView.findViewById(R.id.text_mail);
                mTitle = (TextView) viewHolder.itemView.findViewById(R.id.title_textView);



                View.OnClickListener oclBtnPhone = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = String.format("tel:%s", model.phone);
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                    }
                };

                phone_button.setOnClickListener(oclBtnPhone);

                View.OnClickListener oclBtnMail = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto: " + model.mail.toString()));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Объявление " + model.title);
                        startActivity(Intent.createChooser(emailIntent, "Send feedback"));

                    }
                };

                mailButton.setOnClickListener(oclBtnMail);

                deleteButton = (ImageButton)viewHolder.itemView.findViewById(R.id.imageButton_delete);
                editButton = (ImageButton)viewHolder.itemView.findViewById(R.id.imageButton_edit);

                View.OnClickListener oclBtnDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        ad = new AlertDialog.Builder(getContext());

                        ad.setTitle("Удалить объявление");  // заголовок
                        ad.setMessage("Вы уверены?"); // сообщение
                        ad.setPositiveButton("Да", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                ad2 = new AlertDialog.Builder(getContext());
                                //ad2.setTitle("Удалить объявление");  // заголовок
                                ad2.setMessage("Нашло ли животное хозяина?"); // сообщение
                                ad2.setPositiveButton("Да", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                                        DatabaseReference userFavRef = mDatabase.child("user-fav").child(user.getUid()).child(postRef.getKey());

                                        // Run two transactions
                                        onHappyClicked(globalPostRef);
                                        onHappyClicked(userPostRef);
                                        onHappyClicked(userFavRef);

                                        Toast.makeText(getContext(), "Счастье",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                ad2.setNegativeButton("Нет", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {

                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                                        DatabaseReference userFavRef = mDatabase.child("user-fav").child(user.getUid()).child(postRef.getKey());

                                        // Run two transactions
                                        onDeleteClicked(globalPostRef);
                                        onDeleteClicked(userPostRef);
                                        onDeleteClicked(userFavRef);


                                    }
                                });
                                ad2.setCancelable(true);
                                ad2.setOnCancelListener(new OnCancelListener() {
                                    public void onCancel(DialogInterface dialog) {
                                        Toast.makeText(getContext(), "Ой",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                ad2.show();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            }
                        });
                        ad.setNegativeButton("Нет", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {

                                Toast.makeText(getContext(), "Возможно вы правы", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        ad.setCancelable(true);
                        ad.setOnCancelListener(new OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                Toast.makeText(getContext(), "Вы ничего не выбрали",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        ad.show();
                    }

                };

                deleteButton.setOnClickListener(oclBtnDelete);

                View.OnClickListener oclBtnEdit = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EditCrimeActivity.class);
                        intent.putExtra(EditCrimeActivity.CRIME_key, postRef.getKey());
                        intent.putExtra("title", model.title);
                        intent.putExtra("type", model.type);
                        intent.putExtra("sex", model.sex);
                        intent.putExtra("age", model.age);
                        intent.putExtra("city", model.city);
                        intent.putExtra("phone", model.phone);
                        intent.putExtra("photo", model.image);
                        intent.putExtra("desc", model.description);
                        startActivity(intent);
                    }
                };

                editButton.setOnClickListener(oclBtnEdit);

            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Crime p = mutableData.getValue(Crime.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onStarUnClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Crime p = mutableData.getValue(Crime.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p = null;
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }


    private void onHappyClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Crime p = mutableData.getValue(Crime.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                p.happy = "true";

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onDeleteClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Crime p = mutableData.getValue(Crime.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                          // Set value and report transaction success
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }



    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
