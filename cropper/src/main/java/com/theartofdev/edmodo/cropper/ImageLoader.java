package com.theartofdev.edmodo.cropper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

@SuppressLint("CheckResult")
public class ImageLoader {

    interface ImageLoaderListener {
        void onLoadedGifDrawable(GifDrawable gifDrawable);

        void onLoadedDrawable(Bitmap bitmap);

        void onLoadedWebpDrawable(WebpDrawable webpDrawable);
    }

    interface ImageMetadataListener {
        void onImageLoaded(ImageMetadata imageMetadata);
    }

    public static void loadUrl(Context context, String url, final ImageMetadataListener imageMetadataListener) {
        String fileType = "";
        int lastIndexOfPoint = url.lastIndexOf(".");
        if (lastIndexOfPoint > 0) {
            fileType = url.substring(lastIndexOfPoint + 1);
        }

        RequestManager requestManager = Glide.with(context);

        if (fileType.equals("gif")) {
            requestManager.asGif();
        } else if (fileType.equals("webp")) {
            requestManager.asDrawable()
                    .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(new CircleCrop()));
        } else {
            requestManager.asBitmap();
        }
        requestManager.load(url)
                .into(new CustomLoader<Drawable>(new ImageLoaderListener() {
                    @Override
                    public void onLoadedGifDrawable(GifDrawable gifDrawable) {
                        imageMetadataListener.onImageLoaded(new ImageMetadata(gifDrawable.getFirstFrame(), gifDrawable.getIntrinsicWidth(), gifDrawable.getIntrinsicHeight()));
                    }

                    @Override
                    public void onLoadedDrawable(Bitmap bitmap) {
                        imageMetadataListener.onImageLoaded(new ImageMetadata(bitmap, bitmap.getWidth(), bitmap.getHeight()));
                    }

                    @Override
                    public void onLoadedWebpDrawable(WebpDrawable webpDrawable) {
                        imageMetadataListener.onImageLoaded(new ImageMetadata(webpDrawable.getFirstFrame(), webpDrawable.getIntrinsicWidth(), webpDrawable.getIntrinsicHeight()));
                    }
                }));
    }

    public static void loadUrl(Context context, String url, ImageLoaderListener imageLoaderListener) {
        String fileType = "";
        int lastIndexOfPoint = url.lastIndexOf(".");
        if (lastIndexOfPoint > 0) {
            fileType = url.substring(lastIndexOfPoint + 1);
        }

        RequestManager requestManager = Glide.with(context);

        if (fileType.equals("gif")) {
            requestManager.asGif();
        } else if (fileType.equals("webp")) {
            requestManager.asDrawable()
                    .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(new CircleCrop()));
        } else {
            requestManager.asBitmap();
        }
        requestManager.load(url)
                .into(new CustomLoader<Drawable>(imageLoaderListener));

    }

    public static class CustomLoader<T> extends CustomTarget<T> {

        private final ImageLoaderListener imageLoaderListener;

        public CustomLoader(ImageLoaderListener imageLoaderListener) {
            this.imageLoaderListener = imageLoaderListener;
        }

        @Override
        public void onResourceReady(@NonNull T resource, @Nullable Transition<? super T> transition) {
            if (resource instanceof GifDrawable) {
                GifDrawable gifDrawable = (GifDrawable) resource;
                imageLoaderListener.onLoadedGifDrawable(gifDrawable);
            } else if (resource instanceof Bitmap) {
                Bitmap bitmap = (Bitmap) resource;
                imageLoaderListener.onLoadedDrawable(bitmap);
            } else if (resource instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                imageLoaderListener.onLoadedDrawable(bitmap);
            } else if (resource instanceof WebpDrawable) {
                WebpDrawable drawable = (WebpDrawable) resource;
                imageLoaderListener.onLoadedWebpDrawable(drawable);
            }
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {

        }
    }

}
