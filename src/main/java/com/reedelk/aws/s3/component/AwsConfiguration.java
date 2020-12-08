package com.reedelk.aws.s3.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Shared
@Component(service = AwsConfiguration.class, scope = PROTOTYPE)
public class AwsConfiguration implements Implementor {

    @Property("Access Key Id")
    @Hint("ABB5JTAPLKJFAAN198NBBCC")
    @Example("ABB5JTAPLKJFAAN198NBBCC")
    @Description("The AWS access key id")
    private String accessKeyId;

    @Property("Secret Key Id")
    @Password
    @Hint("Ag1uONBN6h06nYbVk3+/1C1bX1OPnIwLbI0aPq4ViuA")
    @Example("Ag1uONBN6h06nYbVk3+/1C1bX1OPnIwLbI0aPq4ViuA")
    @Description("The AWS secret key id")
    private String secretKeyId;

    @Property("Region")
    @Example("US_EAST_1")
    @Description("Sets the region to be used by the client. This will be used to determine both the" +
            " service endpoint (eg: https://sns.us-west-1.amazonaws.com) and signing region (eg: us-west-1)" +
            " for requests.")
    private AwsRegion region;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretKeyId() {
        return secretKeyId;
    }

    public void setSecretKeyId(String secretKeyId) {
        this.secretKeyId = secretKeyId;
    }

    public AwsRegion getRegion() {
        return region;
    }

    public void setRegion(AwsRegion region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AwsConfiguration that = (AwsConfiguration) o;
        return Objects.equals(accessKeyId, that.accessKeyId) &&
                Objects.equals(secretKeyId, that.secretKeyId) &&
                region == that.region;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessKeyId, secretKeyId, region);
    }
}
