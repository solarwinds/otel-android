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

package com.solarwinds.android.sampling;

import static com.solarwinds.android.sampling.Constants.SW_LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fasterxml.jackson.jr.ob.JSON;
import com.solarwinds.joboe.sampling.Settings;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AndroidSettingsWorker extends Worker {
    private static Settings settings = null;
    private final String applicationName;

    private final String collectorUrl;

    private final String apiToken;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    private final Pattern regex = Pattern.compile("otel(.*)");

    static Settings getSettings() {
        return settings;
    }

    public AndroidSettingsWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        Data inputData = workerParams.getInputData();
        applicationName = inputData.getString("appName");
        collectorUrl = constructSettingsEndpoint(inputData.getString("collectorUrl"));
        apiToken = inputData.getString("apiToken");
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            fetchSettings();
            return Result.success();
        } catch (IOException e) {
            Log.e(SW_LOG_TAG, "Error getting settings");
            return Result.retry();
        }
    }

    private void fetchSettings() throws IOException {
        if (settings == null || System.currentTimeMillis() - settings.getTimestamp() > settings.getTtl() * 1000) {
            Request request = new Request.Builder()
                    .addHeader("Authorization", String.format("Bearer %s", apiToken))
                    .url(String.format("%s/v1/settings/%s/%s", collectorUrl, applicationName, "Android"))
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                JsonSettings jsonSettings = JSON.std
                        .beanFrom(JsonSettings.class, bytes);
                settings = new JsonSettingsWrapper(jsonSettings);
                Log.d(SW_LOG_TAG, "retrieved settings");
            }
        }
    }

    private String constructSettingsEndpoint(String collectorUrl) {
        Matcher matcher = regex.matcher(collectorUrl);
        if (matcher.find()) {
            return String.format("https://apm%s", matcher.group(1));
        }
        return collectorUrl;
    }
}