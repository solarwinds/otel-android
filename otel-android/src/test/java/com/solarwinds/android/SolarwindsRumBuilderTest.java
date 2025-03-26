/*
 * © SolarWinds Worldwide, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
