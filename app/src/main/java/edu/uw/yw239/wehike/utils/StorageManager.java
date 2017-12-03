package edu.uw.yw239.wehike.utils;

import android.net.Uri;
import android.os.Handler;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.SharedAccessAccountPermissions;
import com.microsoft.azure.storage.SharedAccessAccountPolicy;
import com.microsoft.azure.storage.SharedAccessAccountResourceType;
import com.microsoft.azure.storage.SharedAccessAccountService;
import com.microsoft.azure.storage.SharedAccessProtocols;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

import edu.uw.yw239.wehike.MyApplication;

/**
 * Created by Yun on 12/2/2017.
 */

public class StorageManager {

    public interface OnImageUploadListener {
        void onUploaded(String imageUrl);
        void onFailed(Exception ex);
    }

    /**
     * Upload the image
     * @return The uploaded image url
     * @throws Exception
     */
    public static void uploadImage(final Uri imageUri, final OnImageUploadListener listener) {
        final Handler handler = new Handler();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    final InputStream imageStream = MyApplication.getContext().getContentResolver().openInputStream(imageUri);
                    final int imageLength = imageStream.available();
                    final CloudBlobContainer container = getContainer();
                    final String imageName = randomString(20);
                    container.createIfNotExists();

                    CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
                    imageBlob.upload(imageStream, imageLength);

                    handler.post(new Runnable() {
                        public void run() {
                            String imageUrl = container.getStorageUri().getPrimaryUri() + "/" + imageName;
                            listener.onUploaded(imageUrl);
                        }
                    });
                }
                catch(final Exception ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onFailed(ex);
                        }
                    });
                }
            }});
        th.start();
    }

    // Constants
    // TODO: should pass the connection from backend instead of storing in client side. For both security and managment concerns.
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=wehikestoragev2;AccountKey=Z4l6GtSKb3f716ED76OHVz8uUHNfkW+XRLaw/blhYUcUcUOlH5/fSCXDy9yVutE7nRoRFEBgQpq1iQ0hnq14hg==;EndpointSuffix=core.windows.net";
    private static final String containerName = "images";

    private static CloudBlobContainer getContainer() throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(generateSasConnectionString());

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(containerName);
        return container;
    }

    private static String generateSasConnectionString() throws Exception {
        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);

        // Create a new access policy for the account.
        SharedAccessAccountPolicy policy = new SharedAccessAccountPolicy();
        policy.setPermissions(EnumSet.of(SharedAccessAccountPermissions.READ, SharedAccessAccountPermissions.WRITE, SharedAccessAccountPermissions.LIST));
        policy.setServices(EnumSet.of(SharedAccessAccountService.BLOB));
        policy.setResourceTypes(EnumSet.allOf(SharedAccessAccountResourceType.class));

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date nextDay = calendar.getTime();
        policy.setSharedAccessExpiryTime(nextDay);
        policy.setProtocols(SharedAccessProtocols.HTTPS_HTTP);

        // Return the SAS connection string.
        return String.format("SharedAccessSignature=%s;BlobEndpoint=%s;", account.generateSharedAccessSignature(policy), account.getBlobEndpoint());
    }

    private static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(validChars.charAt(rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }
}
