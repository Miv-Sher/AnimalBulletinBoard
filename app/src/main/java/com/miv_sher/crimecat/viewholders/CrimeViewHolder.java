package com.miv_sher.crimecat.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.miv_sher.crimecat.R;
import com.miv_sher.crimecat.model.Crime;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CrimeViewHolder extends RecyclerView.ViewHolder {

    String mUid;
    String mAuthor;
    public TextView mTitle;
    public TextView mDate;

    public TextView mPhone;
    public TextView mMail;


    public TextView mAge;
    public TextView mCity;

   public TextView mDescription;

    public ImageView mPhoto;
    public ImageView mType;
    public ImageView mSex;

    public ImageView starView;
    public TextView numStarsView;
    public ImageButton mEdit;
    public ImageButton mDelete;
    public ImageView tick;

    Context context;




    public CrimeViewHolder(View itemView) {
        super(itemView);


        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);

        mTitle = (TextView) itemView.findViewById(R.id.title_textView);
        mDate = (TextView) itemView.findViewById(R.id.date_textView);

        mPhone = (TextView) itemView.findViewById(R.id.text_phone);
        mMail = (TextView) itemView.findViewById(R.id.text_mail);

        mAge = (TextView) itemView.findViewById(R.id.age_textView);
        mCity = (TextView) itemView.findViewById(R.id.textView_city);

        mDescription = (TextView) itemView.findViewById(R.id.textView_desc);
        mPhoto = (ImageView) itemView.findViewById(R.id.imageView_photo);
        mType = (ImageView) itemView.findViewById(R.id.imageView_type);
        mSex = (ImageView) itemView.findViewById(R.id.imageView_sex);

        mEdit = (ImageButton) itemView.findViewById(R.id.imageButton_edit);
        mDelete = (ImageButton) itemView.findViewById(R.id.imageButton_delete);
        tick = (ImageView)itemView.findViewById(R.id.imageView_happy);
    }



    public void bindToPost(Crime crime, View.OnClickListener starClickListener) {
        mTitle.setText(crime.title);
        mDate.setText(crime.date);
        numStarsView.setText(String.valueOf(crime.starCount));
        starView.setOnClickListener(starClickListener);
        mAge.setText(crime.age);
        mCity.setText(crime.city);
        mDescription.setText(crime.description);

        mPhone.setText(crime.phone);
        mMail.setText(crime.mail);

        if (crime.uid.equals(getUid()) && crime.happy.equals("false"))
        {
            mEdit.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.VISIBLE);
            //mTitle.setText(crime.title + " HAPPY");
        }

        if (crime.happy.equals("true"))
        {
            tick.setVisibility(View.VISIBLE);
        }
        //holder.txtHeader.setText(mDataset.get(position).getPost_text());

       // context =
       // Picasso.with(context).load(crime.mImage).into(mPhoto);

       // Picasso.with().load("http://i.imgur.com/DvpvklR.png").into(imageView);


       switch (crime.type){
            case "0":
                mType.setImageResource(R.drawable.dog);
                break;
            case "1":
                mType.setImageResource(R.drawable.cat);
                break;
            case "2":
                mType.setImageResource(R.drawable.horse);
                break;

        }
        switch (crime.sex){
            case "0":
                mSex.setImageResource(R.drawable.male);
                break;
            case "1":
                mSex.setImageResource(R.drawable.female);
                break;

        }
       // mPhoto.setImageBitmap(crime.mImage);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
