package com.example.gcs.faster5.util;

/**
 * Created by kienht on 8/16/16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.io.ByteArrayOutputStream;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by kienht on 8/15/16.
 */
public class UploadPhotoUtils {

    public static final String HOST = "http://www.ailatrieuphu.96.lt/imgupload/upload_image.php";

    String imgPath = "";
    public static boolean isUploading = false;

    RequestParams params = new RequestParams();

    /**
     * @return true if start upload, false: not available
     * */
    public boolean isUploadAvailable(String imgPath) {
        if (imgPath != null && !imgPath.isEmpty()) {
            if (isUploading) {
                return false;
            }
            this.imgPath = imgPath;
            isUploading = true;
            return true;
        } else {
            isUploading = false;
        }
        return false;
    }

    public String encodeImage(){
        CameraUtils.autoRotateImage(imgPath);

        BitmapFactory.Options options = null;
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        String type = getMimeType(imgPath);
        boolean isPngImage = false;
        if (TextUtils.isEmpty(type) || type.contains("png") || type.contains("PNG")) {
            isPngImage = true;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmap.compress(isPngImage ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedString = Base64.encodeToString(byte_arr, 0);
        return encodedString;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void startUploadImage(Context context,String fileName,String encodeImage, AsyncHttpResponseHandler handler) {
        params.put("filename", fileName);
        params.put("image", encodeImage);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20*1000);
        client.addHeader("User-Agent",new WebView(context).getSettings().getUserAgentString());
        client.post(HOST, params, handler);
    }
}