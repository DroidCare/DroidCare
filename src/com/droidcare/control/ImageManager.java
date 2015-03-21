package com.droidcare.control;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 
 * @author Edwin Candinegara
 * Encodes an image {@link Bitmap} object into a Base64 encoded String and decodes a Base64 encoded String into
 * an image {@link Bitmap} object.
 */

public class ImageManager {
	/**
	 * An instance of {@link ImageManager}. Use this to promote singleton design pattern.
	 */
	private static ImageManager instance = new ImageManager();
    private static Context context;
    private static float MAX_DIMENS;

    /**
	 * Returns {@link #instance}.
	 * @return	returns {@link #instance}
	 */
	public static ImageManager getInstance () {
        return instance;
	}

    /**
     * Set ImageManager application context.
     * @param context Application context.
     */
    public static void init(Context context) {
        ImageManager.context = context;
        MAX_DIMENS = convertDpToPixel(180f, context);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     * Credits to:
     *      - <a href="http://stackoverflow.com/a/8490361">Stack Overflow answer</a>
     *
     * @param dp 		A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context 	Context to get resources and device specific display metrics
     * @return 			A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (dp * displayMetrics.density);
    }

	/**
	 * Encodes an image {@link Bitmap} into a Base64 encoded String and returns the encoded String. 
	 * @param image	{@link Bitmap} object of the image.
	 * @return		a Base64 encoded String.
	 */
	public String encodeImageBase64 (Bitmap image) {
        Bitmap rescaledImage = rescaleImage(image);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if(rescaledImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                || rescaledImage.compress(Bitmap.CompressFormat.PNG, 100, baos));
		
		byte[] b = baos.toByteArray();

        // Sent this encoded STRING to the PHP server
		return Base64.encodeToString(b, Base64.DEFAULT)
                .replace("\n", "").replace("\r", "");
	}
	
	/**
	 * Decodes a Base64 encoded String into an image {@link Bitmap} object and returns the image {@link Bitmap} object.
	 * @param encodedImage	a Base64 encoded String.
	 * @return				an image {@link Bitmap} object.
	 */
	public Bitmap decodeImageBase64 (String encodedImage) {
        Log.d("DEBUGGING", "encodedImage=" + encodedImage);
		byte[] imageByte = Base64.decode(encodedImage, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
	}
	
	/**
	 * Rescales the original image bitmap
	 * @param image	the image to be rescaled
	 * @return		the rescaled image
	 */
    public Bitmap rescaleImage(Bitmap image) {
        float oriWidth = image.getWidth(),
                oriHeight = image.getHeight();
        float scaling = Math.min(MAX_DIMENS / oriWidth, MAX_DIMENS / oriHeight);

        float scaledWidth = scaling * oriWidth,
                scaledHeight = scaling * oriHeight;

        return Bitmap.createScaledBitmap(image, (int) scaledWidth, (int) scaledHeight, true);
    }
}
