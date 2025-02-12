package com.solarwinds.android;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;

/**
 * Creates a Solarwinds OTel resource
 */
public final class SolarwindsResourceProvider {
    public static Resource create() {
        return Resource.create(
                Attributes.of(
                        stringKey("sw.data.module"), "apm"
                )
        );
    }
}
