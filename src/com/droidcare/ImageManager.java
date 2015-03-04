package com.droidcare;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

/**
 * 
 * @author Edwin Candinegara
 * 
 * This class is responsible for Base64 encoding of an image and decoding a Base64 String
 *
 */

public class ImageManager {
	private static ImageManager instance;
	
	public static ImageManager getInstance () {
		instance = new ImageManager();
		return instance;
	}
	
	public static String encodeImageBase64 (Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		
		return imageEncoded;
	}
	
	public static Bitmap decodeImageBase64 (String encodedImage) {
		byte[] imageByte = Base64.decode(encodedImage, 0);
		return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
	}
}
