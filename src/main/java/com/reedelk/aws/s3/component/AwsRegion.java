package com.reedelk.aws.s3.component;

import com.amazonaws.regions.Regions;
import com.reedelk.runtime.api.annotation.DisplayName;

public enum AwsRegion {

    @DisplayName("us-gov-west-1 - AWS GovCloud (US)")
    GovCloud(Regions.GovCloud),
    @DisplayName("us-gov-east-1 - AWS GovCloud (US-East)")
    US_GOV_EAST_1(Regions.US_GOV_EAST_1),
    @DisplayName("us-east-1 - US East (N. Virginia)")
    US_EAST_1(Regions.US_EAST_1),
    @DisplayName("us-east-2 - US East (Ohio)")
    US_EAST_2(Regions.US_EAST_2),
    @DisplayName("us-west-1 - US West (N. California)")
    US_WEST_1(Regions.US_WEST_1),
    @DisplayName("us-west-2 - US West (Oregon)")
    US_WEST_2(Regions.US_WEST_2),
    @DisplayName("eu-west-1 - EU (Ireland)")
    EU_WEST_1(Regions.EU_WEST_1),
    @DisplayName("eu-west-2 - EU (London)")
    EU_WEST_2(Regions.EU_WEST_2),
    @DisplayName("eu-west-3 - EU (Paris)")
    EU_WEST_3(Regions.EU_WEST_3),
    @DisplayName("eu-central-1 - EU (Frankfurt)")
    EU_CENTRAL_1(Regions.EU_CENTRAL_1),
    @DisplayName("eu-north-1 - EU (Stockholm)")
    EU_NORTH_1(Regions.EU_NORTH_1),
    @DisplayName("eu-south-1 - EU (Milan)")
    EU_SOUTH_1(Regions.EU_SOUTH_1),
    @DisplayName("ap-east-1 - Asia Pacific (Hong Kong)")
    AP_EAST_1(Regions.AP_EAST_1),
    @DisplayName("ap-south-1 - Asia Pacific (Mumbai)")
    AP_SOUTH_1(Regions.AP_SOUTH_1),
    @DisplayName("ap-southeast-1 - Asia Pacific (Singapore)")
    AP_SOUTHEAST_1(Regions.AP_SOUTHEAST_1),
    @DisplayName("ap-southeast-2 - Asia Pacific (Sydney)")
    AP_SOUTHEAST_2(Regions.AP_SOUTHEAST_2),
    @DisplayName("ap-northeast-1 - Asia Pacific (Tokyo)")
    AP_NORTHEAST_1(Regions.AP_NORTHEAST_1),
    @DisplayName("ap-northeast-2 - Asia Pacific (Seoul)")
    AP_NORTHEAST_2(Regions.AP_NORTHEAST_2),
    @DisplayName("sa-east-1 - South America (Sao Paulo)")
    SA_EAST_1(Regions.SA_EAST_1),
    @DisplayName("cn-north-1 - China (Beijing)")
    CN_NORTH_1(Regions.CN_NORTH_1),
    @DisplayName("cn-northwest-1 - China (Ningxia)")
    CN_NORTHWEST_1(Regions.CN_NORTHWEST_1),
    @DisplayName("ca-central-1 - Canada (Central)")
    CA_CENTRAL_1(Regions.CA_CENTRAL_1),
    @DisplayName("me-south-1 - Middle East (Bahrain)")
    ME_SOUTH_1(Regions.ME_SOUTH_1),
    @DisplayName("af-south-1 - Africa (Cape Town)")
    AF_SOUTH_1(Regions.AF_SOUTH_1);

    private final Regions region;

    AwsRegion(com.amazonaws.regions.Regions region) {
        this.region = region;
    }

    public Regions get() {
        return region;
    }
}
