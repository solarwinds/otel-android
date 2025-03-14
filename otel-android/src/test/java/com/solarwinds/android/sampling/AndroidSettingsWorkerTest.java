package com.solarwinds.android.sampling;


import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.solarwinds.joboe.sampling.Settings;
import com.solarwinds.joboe.sampling.SettingsArg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;

@RunWith(AndroidJUnit4.class)
public class AndroidSettingsWorkerTest {
    private MockWebServer webServer;

    @Before
    public void setup() throws IOException {
        WorkManagerTestInitHelper.initializeTestWorkManager(getApplicationContext());
        webServer = new MockWebServer();
        webServer.start();
    }

    @After
    public void tearDown() throws IOException {
        webServer.shutdown();
    }

    @Test
    public void testFetchSettings() throws ExecutionException, InterruptedException, IOException {
        Buffer buffer = new Buffer();
        InputStream resourceAsStream = AndroidSettingsWorkerTest.class.getResourceAsStream("/solarwinds-config.json");
        buffer.readFrom(resourceAsStream);

        resourceAsStream.close();
        webServer.enqueue(new MockResponse().setBody(buffer)
                .setResponseCode(200));

        Data data = new Data.Builder()
                .putString("appName", "test-app")
                .putString("collectorUrl", webServer.url("/").toString())
                .putString("apiToken", "token")
                .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(AndroidSettingsWorker.class)
                        .setInputData(data)
                        .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(request).getResult().get();
        Settings settings = AndroidSettingsWorker.getSettings();

        assertEquals(120, settings.getTtl());
        assertEquals(116, settings.getFlags());
        assertEquals(Integer.valueOf(60), settings.getArgValue(SettingsArg.METRIC_FLUSH_INTERVAL));
    }
}