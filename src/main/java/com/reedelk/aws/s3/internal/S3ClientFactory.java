package com.reedelk.aws.s3.internal;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.reedelk.aws.s3.component.AwsConfiguration;
import com.reedelk.aws.s3.component.AwsRegion;
import com.reedelk.runtime.api.component.ProcessorSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S3ClientFactory {

    private static final Map<AwsConfiguration, ClientHolder> CONFIG_CLIENT_MAP = new HashMap<>();

    public static synchronized AmazonS3 from(AwsConfiguration configuration, ProcessorSync user) {
        if (CONFIG_CLIENT_MAP.containsKey(configuration)) {
            ClientHolder clientHolder = CONFIG_CLIENT_MAP.get(configuration);
            clientHolder.users.add(user);
            return clientHolder.s3;
        }


        String accessKeyId = configuration.getAccessKeyId();
        String secretKeyId = configuration.getSecretKeyId();
        AwsRegion region = configuration.getRegion();

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKeyId);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region.get())
                .build();

        ClientHolder holder = new ClientHolder(s3, user);
        CONFIG_CLIENT_MAP.put(configuration, holder);
        return s3;
    }

    public static synchronized void release(AwsConfiguration configuration, ProcessorSync processorSync) {
        if (CONFIG_CLIENT_MAP.containsKey(configuration)) {
            ClientHolder clientHolder = CONFIG_CLIENT_MAP.get(configuration);
            clientHolder.users.remove(processorSync);
            // If there are no more processors using it, we must shutdown the s3 client
            // and remove the config from the map.
            if (clientHolder.users.isEmpty()) {
                clientHolder.s3.shutdown();
                CONFIG_CLIENT_MAP.remove(configuration);
            }
        }
    }

    static class ClientHolder {

        private final AmazonS3 s3;
        private final List<ProcessorSync> users = new ArrayList<>();

        public ClientHolder(AmazonS3 s3, ProcessorSync user) {
            this.s3 = s3;
            this.users.add(user);
        }
    }
}
