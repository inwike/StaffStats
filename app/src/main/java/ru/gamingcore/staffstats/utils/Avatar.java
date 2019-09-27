package ru.gamingcore.staffstats.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Avatar {
    private static final String TAG = "INWIKE";
    private static final int SIZE_LIMIT = 1024 * 1024;

    public static Bitmap setAvatar(Context context, Uri from,int sampleSize, int quality) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            InputStream in =  context.getContentResolver().openInputStream(from);

            ExifInterface ei = new ExifInterface(in);
            String FILENAME = "avatar.jpg";

            in.close();

            in =  context.getContentResolver().openInputStream(from);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Bitmap image = BitmapFactory.decodeStream(in,null,options);

            in.close();

            OutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            image.compress(Bitmap.CompressFormat.JPEG, quality, out);

            out.close();

            in = context.openFileInput(FILENAME);

            if (in.available() > SIZE_LIMIT) {
                in.close();
                return setAvatar(context,from, (sampleSize*2), (quality/2));
            }

            image = BitmapFactory.decodeStream(in);
            in.close();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(image, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(image, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(image, 270);
                default:
                    return image;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException "+e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException "+e.getLocalizedMessage());
        }
        return null;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

}
