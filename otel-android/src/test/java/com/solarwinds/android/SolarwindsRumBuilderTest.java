package com.solarwinds.android;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.opentelemetry.android.config.OtelRumConfig;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;

@RunWith(AndroidJUnit4.class)
public class SolarwindsRumBuilderTest {

    @Before
    public void setup() {
        WorkManagerTestInitHelper.initializeTestWorkManager(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void verifyAttributesSupplierIsNotModifiedWhenSessionProviderIsNull() {
        SolarwindsRumBuilder solarwindsRumBuilder = new SolarwindsRumBuilder();
        OtelRumConfig otelRumConfig = new OtelRumConfig();
        otelRumConfig.setGlobalAttributes(Attributes.of(AttributeKey.stringKey("attr"), "value"))
                .disableNetworkAttributes()
                .disableInstrumentationDiscovery();

        solarwindsRumBuilder.otelRumConfig(otelRumConfig)
                .apiToken("token")
                .collectorUrl("http://localhost")
                .build(ApplicationProvider.getApplicationContext());
        assertFalse(otelRumConfig.getGlobalAttributesSupplier() instanceof SessionIdAppender);
    }

    @Test
    public void verifyAttributesSupplierIsModifiedWhenSessionProviderIsNotNull() {
        SolarwindsRumBuilder solarwindsRumBuilder = new SolarwindsRumBuilder();
        OtelRumConfig otelRumConfig = new OtelRumConfig();
        otelRumConfig.setGlobalAttributes(Attributes.of(AttributeKey.stringKey("attr"), "value"))
                .disableNetworkAttributes()
                .disableInstrumentationDiscovery();

        solarwindsRumBuilder.otelRumConfig(otelRumConfig)
                .apiToken("token")
                .collectorUrl("http://localhost")
                .sessionProvider(() -> "new-session-id")
                .build(ApplicationProvider.getApplicationContext());

        assertInstanceOf(SessionIdAppender.class, otelRumConfig.getGlobalAttributesSupplier());
    }
}