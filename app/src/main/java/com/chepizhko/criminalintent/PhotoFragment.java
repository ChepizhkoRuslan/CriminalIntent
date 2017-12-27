package com.chepizhko.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;


public class PhotoFragment extends DialogFragment{
    private static final String ARG_PHOTO = "crime_photo";
    private ImageView mImageViewPhoto;
    private File mPhotoFile;
    private Bitmap bitmap;


    public static PhotoFragment newInstance(File photo) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, photo);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPhotoFile = (File) getArguments().getSerializable(ARG_PHOTO);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);

        mImageViewPhoto = (ImageView)v.findViewById(R.id.crime_dialog_photo);

        // построить объект Bitmap из uri
//        Uri uri = FileProvider.getUriForFile(getActivity(), "com.chepizhko.criminalintent.fileprovider", mPhotoFile);
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mImageViewPhoto.setImageBitmap(bitmap);
        // построить объект Bitmap из файла
        updatePhotoView();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
    // Чтобы загрузить объект Bitmap в ImageView, добавьте метод для обновления mPhotoView
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mImageViewPhoto.setImageDrawable(null);
        } else {
            // Чтобы построить объект Bitmap на базе файла, достаточно воспользоваться классом BitmapFactory
            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.getPath());
            //Bitmap bitmap = PictureUtils.getScaledBitmap ( mPhotoFile.getPath(), getActivity() );

            mImageViewPhoto.setImageBitmap(bitmap);
        }
    }
}
