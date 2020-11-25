package com.theartofdev.edmodo.cropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.integration.webp.decoder.WebpDecoder;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class GifDrawableUtils {

    public static ArrayList<Bitmap> getBitmapsFromGifDrawable(GifDrawable gifDrawable) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (gifDrawable.getFrameCount() <= 1) {
            bitmaps.add(gifDrawable.getFirstFrame());
        } else {
            bitmaps = new CustomGifDecoder<StandardGifDecoder, GifDrawable>().build(gifDrawable, true);
        }
        return bitmaps;
    }

    public static ArrayList<Uri> getUrisFromGifDrawable(Context context, GifDrawable gifDrawable) {
        ArrayList<Uri> uris = new ArrayList<>();
        if (gifDrawable.getFrameCount() <= 1) {
            Uri uri = BitmapUtils.getImageUri(context, gifDrawable.getFirstFrame());
            uris.add(uri);
        } else {
            ArrayList<Bitmap> bitmaps = new CustomGifDecoder<StandardGifDecoder, GifDrawable>().build(gifDrawable, true);
            for (int i = 0; i < bitmaps.size(); i++) {
                Uri uri = BitmapUtils.getImageUri(context, bitmaps.get(i));
                uris.add(uri);
            }
        }
        return uris;
    }

    public static ArrayList<Bitmap> getBitmapsFromGifDrawable(WebpDrawable gifDrawable) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        if (gifDrawable.getFrameCount() <= 1) {
            bitmaps.add(gifDrawable.getFirstFrame());
        } else {
            bitmaps = new CustomGifDecoder<WebpDecoder, WebpDrawable>().build(gifDrawable, false);
        }
        return bitmaps;
    }

    private static void fillBitmaps(GifDrawable gifDrawable, ArrayList<Bitmap> bitmaps) {
        StandardGifDecoder standardGifDecoder = getGifDecoder(gifDrawable);
        for (int i = 0; i < standardGifDecoder.getFrameCount(); i++) {
            Bitmap bitmap = standardGifDecoder.getNextFrame();
            standardGifDecoder.advance();
        }
    }

    private static class CustomGifDecoder<T extends GifDecoder, D extends Drawable> {

        public T standardGifDecoderType;
        public GifDecoder standardGifDecoder;

        public ArrayList<Bitmap> build(D gifDrawable, boolean isStandardGifDecoder) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            try {
                standardGifDecoder = null;
                Drawable.ConstantState constantState = gifDrawable.getConstantState();
                Field frameLoader;
                frameLoader = constantState.getClass().getDeclaredField("frameLoader");
                frameLoader.setAccessible(true);
                Object gifFrameLoader = (Object) frameLoader.get(constantState);
                if (isStandardGifDecoder) {
                    Field gifDecoder = gifFrameLoader.getClass().getDeclaredField("gifDecoder");
                    gifDecoder.setAccessible(true);
                    standardGifDecoder = (StandardGifDecoder) gifDecoder.get(gifFrameLoader);
                } else {
                    Field gifDecoder = gifFrameLoader.getClass().getDeclaredField("webpDecoder");
                    gifDecoder.setAccessible(true);
                    standardGifDecoder = (WebpDecoder) gifDecoder.get(gifFrameLoader);
                }
                for (int i = 0; i < standardGifDecoder.getFrameCount(); i++) {
                    Bitmap bitmap = standardGifDecoder.getNextFrame();
                    int delay = standardGifDecoder.getNextDelay();
                    standardGifDecoder.advance();
                    bitmaps.add(bitmap);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return bitmaps;
        }
    }

    private static StandardGifDecoder getGifDecoder(GifDrawable gifDrawable) {
        StandardGifDecoder standardGifDecoder = null;
        try {
            Drawable.ConstantState constantState = gifDrawable.getConstantState();
            Field frameLoader = constantState.getClass().getDeclaredField("frameLoader");
            frameLoader.setAccessible(true);
            Object gifFrameLoader = (Object) frameLoader.get(constantState);
            Field gifDecoder = gifFrameLoader.getClass().getDeclaredField("gifDecoder");
            gifDecoder.setAccessible(true);
            standardGifDecoder = (StandardGifDecoder) gifDecoder.get(gifFrameLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return standardGifDecoder;
    }

    private static WebpDecoder getWebpDecoder(WebpDrawable webpDrawable) {
        WebpDecoder standardWebpDecoder = null;
        try {
            Drawable.ConstantState constantState = webpDrawable.getConstantState();
            Field frameLoader = constantState.getClass().getDeclaredField("frameLoader");
            frameLoader.setAccessible(true);
            Object gifFrameLoader = (Object) frameLoader.get(constantState);
            Field gifDecoder = gifFrameLoader.getClass().getDeclaredField("webpDecoder");
            gifDecoder.setAccessible(true);
            standardWebpDecoder = (WebpDecoder) gifDecoder.get(gifFrameLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return standardWebpDecoder;
    }


}
