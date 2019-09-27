package ru.gamingcore.staffstats.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Base64;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtil {
    public static Bitmap convert(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.NO_WRAP
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
        return out.toByteArray();
    }

    // nv12: true = NV12, false = NV21
    public static byte[] YUV_420_888toNV(ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer, boolean nv12) {
        byte[] nv;

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv = new byte[ySize + uSize + vSize];

        yBuffer.get(nv, 0, ySize);
        if (nv12) {//U and V are swapped
            vBuffer.get(nv, ySize, vSize);
            uBuffer.get(nv, ySize + vSize, uSize);
        } else {
            uBuffer.get(nv, ySize, uSize);
            vBuffer.get(nv, ySize + uSize, vSize);
        }
        return nv;
    }

    public static byte[] YUV_420_888toI420SemiPlanar(ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer,
                                                     int width, int height, boolean deInterleaveUV) {
        byte[] data = YUV_420_888toNV(yBuffer, uBuffer, vBuffer, deInterleaveUV);
        int size = width * height;
        if (deInterleaveUV) {
            byte[] buffer = new byte[3 * width * height / 2];

            // De-interleave U and V
            for (int i = 0; i < size / 4; i += 1) {
                buffer[i] = data[size + 2 * i + 1];
                buffer[size / 4 + i] = data[size + 2 * i];
            }
            System.arraycopy(buffer, 0, data, size, size / 2);
        } else {
            for (int i = size; i < data.length; i += 2) {
                byte b1 = data[i];
                data[i] = data[i + 1];
                data[i + 1] = b1;
            }
        }
        return data;
    }
}

