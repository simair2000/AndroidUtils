package com.simair.android.androidutils.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtil {

    public static Bitmap imgRotate(Bitmap bmp, int degree){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

    public static byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream) ;
        return stream.toByteArray() ;
    }

    public static int GetExifOrientation(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     *
     * @param imageView
     * @param radius 0 < radius <= 25
     */
    public static void makeBlur(Context context, ImageView imageView, float radius) {
        if(imageView != null) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if(drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                Bitmap blurredBitmap = blurRenderScript(context, bitmap, radius);
                imageView.setImageBitmap(blurredBitmap);
            }
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap bitmap, float radius) {
        if(bitmap != null) {
            bitmap = RGB565toARGB8888(bitmap);
            Bitmap tBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            RenderScript renderScript = RenderScript.create(context);

            Allocation blurInput = Allocation.createFromBitmap(renderScript, bitmap);
            Allocation blurOutput = Allocation.createFromBitmap(renderScript, tBitmap);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            blur.setInput(blurInput);
            blur.setRadius(radius);
            blur.forEach(blurOutput);

            blurOutput.copyTo(tBitmap);
            renderScript.destroy();

            return tBitmap;
        }
        return null;
    }

    public static Bitmap RGB565toARGB8888(Bitmap bitmap) {
        if(bitmap != null) {
            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
            return result;
        }
        return null;
    }
}
