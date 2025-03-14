package com.solarwinds.android;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import android.app.Application;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.opentelemetry.android.config.OtelRumConfig;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    public void verifyAttributesSupplierIsModifiedWhenSessionProviderIsNotNull() {
        SolarwindsRumBuilder solarwindsRumBuilder = new SolarwindsRumBuilder();
        OtelRumConfig otelRumConfig = new OtelRumConfig();
        otelRumConfig
                .setGlobalAttributes(Attributes.of(AttributeKey.stringKey("attr"), "value"))
                .disableNetworkAttributes()
                .disableInstrumentationDiscovery();

        solarwindsRumBuilder
                .otelRumConfig(otelRumConfig)
                .apiToken("token")
                .collectorUrl("http://localhost")
                .sessionProvider(() -> "new-session-id")
                .build(application);

        assertInstanceOf(SessionIdAppender.class, otelRumConfig.getGlobalAttributesSupplier());
    }
}
