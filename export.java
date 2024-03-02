import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class RedisToS3Exporter {

    public static void main(String[] args) {
        // Redis connection parameters
        String redisHost = "hrs_redis_host";
        int redisPort = 6379;
        String redisPassword = "hrs_redis_password";

        // S3 bucket and object key
        String s3BucketName = "hrs_s3_bucket";
        String s3ObjectKey = "exported_data.csv";

        // Connect to Redis
        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.auth(redisPassword);

            // Scan all keys in Redis
            ScanParams scanParams = new ScanParams().count(100); // we can adjust count as needed
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                for (String key : scanResult.getResult()) {
                    // Retrieve data from Redis
                    Map<String, String> redisData = jedis.hgetAll(key);

                    // Convert data to required CSV format
                    String dataAsString = convertToCSV(redisData);

                    // Append data to the S3 object
                    appendDataToS3(s3BucketName, s3ObjectKey, dataAsString);
                }
                cursor = scanResult.getStringCursor();
            } while (!cursor.equals("0"));

            System.out.println("Data export process completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertToCSV(Map<String, String> data) {
	// convert data into csv format
        StringBuilder csvBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            csvBuilder.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }
        return csvBuilder.toString();
    }

    private static void appendDataToS3(String bucketName, String objectKey, String data) {
        // Initialize S3 client
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();

        // Upload data to S3
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, objectKey, data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}