package com.armo.robot.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.armo.robot.utils.RxAndroidTransformer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import io.reactivex.Single;


public class QRCodeGenerator {


    public static Single<Bitmap> getQRCode(String data, int dimen) {
        return Single.defer(() -> Single.just(generateQRCode(data, dimen))).compose(RxAndroidTransformer.applySingleSchedulers());
    }

    private static Bitmap generateQRCode(String data, int dimen) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, dimen, dimen);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
