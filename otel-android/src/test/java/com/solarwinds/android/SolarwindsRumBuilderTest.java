package com.solarwinds.android;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import android.app.Application;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.opentelemetry.android.config.OtelRumConfig;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;

@RunWith(AndroidJUnit4.class)
public class SolarwindsRumBuilderTest {
    private AutoCloseable mocks;

    @Mock Application application;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void verifyAttributesSupplierIsNotModifiedWhenSessionProviderIsNull() {
        SolarwindsRumBuilder solarwindsRumBuilder = new SolarwindsRumBuilder();
        OtelRumConfig otelRumConfig = new OtelRumConfig();
        otelRumConfig.setGlobalAttributes(Attributes.of(AttributeKey.stringKey("attr"), "value"));

        solarwindsRumBuilder.otelRumConfig(otelRumConfig)
                .apiToken("token")
                .collectorUrl("http://localhost")
                .build(application);
        assertFalse(otelRumConfig.getGlobalAttributesSupplier() instanceof SessionIdAppender);
    }

    @Test
    public void verifyAttributesSupplierIsModifiedWhenSessionProviderIsNotNull() {
        SolarwindsRumBuilder solarwindsRumBuilder = new SolarwindsRumBuilder();
        OtelRumConfig otelRumConfig = new OtelRumConfig();
        otelRumConfig.setGlobalAttributes(Attributes.of(AttributeKey.stringKey("attr"), "value"));

        solarwindsRumBuilder.otelRumConfig(otelRumConfig)
                .apiToken("token")
                .collectorUrl("http://localhost")
                .sessionProvider(() -> "new-session-id")
                .build(application);

        assertInstanceOf(SessionIdAppender.class, otelRumConfig.getGlobalAttributesSupplier());
    }
}