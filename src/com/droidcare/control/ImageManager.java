package com.droidcare.control;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

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
	
	/**
	 * Returns {@link #instance}.
	 * @return	returns {@link #instance}
	 */
	public static ImageManager getInstance () {
		instance = new ImageManager();
		return instance;
	}
	
	/**
	 * Encodes an image {@link Bitmap} into a Base64 encoded String and returns the encoded String. 
	 * @param image	{@link Bitmap} object of the image.
	 * @return		a Base64 encoded String.
	 */
	public String encodeImageBase64 (Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		
		// Sent this encoded STRING to the PHP server
		return imageEncoded;
	}
	
	/**
	 * Decodes a Base64 encoded String into an image {@link Bitmap} object and returns the image {@link Bitmap} object.
	 * @param encodedImage	a Base64 encoded String.
	 * @return				an image {@link Bitmap} object.
	 */
	public Bitmap decodeImageBase64 (String encodedImage) {
		byte[] imageByte = Base64.decode(encodedImage, 0);
		return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
	}
}
