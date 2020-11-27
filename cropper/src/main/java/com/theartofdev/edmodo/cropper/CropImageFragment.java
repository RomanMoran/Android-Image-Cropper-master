package com.theartofdev.edmodo.cropper;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CropImageFragment extends Fragment implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener {

    public interface ShotCropCompleteListener {
        void onCroppingComplete(List<Bitmap> bitmaps);
    }

    public static ShotCropCompleteListener mShotCropCompleteListener;

    public static CropImageFragment newInstance(String url, CropImageOptions cropImageOptions, ShotCropCompleteListener shotCropCompleteListener) {
        mShotCropCompleteListener = shotCropCompleteListener;
        CropImageFragment cropImageFragment = new CropImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CropImage.CROP_IMAGE_EXTRA_SOURCE_URL, url);
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, cropImageOptions);
        cropImageFragment.setArguments(bundle);
        return cropImageFragment;
    }

    /**
     * The crop image view library widget used in the activity
     */
    private CropImageView mCropImageView;

    /**
     * Persist Url if specific permissions are required
     */
    private String mUrl;

    /**
     * the options that were set for the crop image
     */
    private CropImageOptions mOptions;

    private Button btnOk;
    private Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.crop_image_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCropImageView = view.findViewById(R.id.cropImageView);
        btnOk = view.findViewById(R.id.btnApply);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImage();
            }
        });
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUrl = arguments.getString(CropImage.CROP_IMAGE_EXTRA_SOURCE_URL, "");
            mOptions = arguments.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS);
            mCropImageView.setImageUrlFromGlide(mUrl);
        }

    }

    /**
     * Execute crop image and save the result tou output uri.
     */
    protected void cropImage() {
        if (mOptions.noOutputImage) {
            //setResult(null, null, 1);
        } else {
            ImageLoader.loadUrl(requireContext(), mUrl, new ImageLoader.ImageLoaderListener() {
                @Override
                public void onLoadedGifDrawable(GifDrawable gifDrawable) {
                    ArrayList<Bitmap> bitmaps = GifDrawableUtils.getBitmapsFromGifDrawable(gifDrawable);
                    mCropImageView.saveCroppedImagesAsync(
                            bitmaps,
                            mOptions.outputCompressFormat,
                            mOptions.outputCompressQuality,
                            mOptions.outputRequestWidth,
                            mOptions.outputRequestHeight,
                            mOptions.outputRequestSizeOptions);
                }

                @Override
                public void onLoadedDrawable(Bitmap bitmap) {
                    ArrayList<Bitmap> bitmaps = new ArrayList<>(Arrays.asList(bitmap));
                    mCropImageView.saveCroppedImagesAsync(
                            bitmaps,
                            mOptions.outputCompressFormat,
                            mOptions.outputCompressQuality,
                            mOptions.outputRequestWidth,
                            mOptions.outputRequestHeight,
                            mOptions.outputRequestSizeOptions);
                }

                @Override
                public void onLoadedWebpDrawable(WebpDrawable webpDrawable) {
                    ArrayList<Bitmap> bitmaps = GifDrawableUtils.getBitmapsFromGifDrawable(webpDrawable);
                    mCropImageView.saveCroppedImagesAsync(
                            bitmaps,
                            mOptions.outputCompressFormat,
                            mOptions.outputCompressQuality,
                            mOptions.outputRequestWidth,
                            mOptions.outputRequestHeight,
                            mOptions.outputRequestSizeOptions);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnCropImageCompleteListener(null);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (mOptions.initialCropWindowRectangle != null) {
                mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
            }
            if (mOptions.initialRotation > -1) {
                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            }
        } else {
            //setResult(null, error, 1);
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        //setResult(result.getUri(), result.getError(), result.getSampleSize());
    }

    @Override
    public void onMultipleCropImageComplete(CropImageView view, List<Bitmap> result) {
        if (mShotCropCompleteListener != null) {
            mShotCropCompleteListener.onCroppingComplete(result);
        }
    }
}
