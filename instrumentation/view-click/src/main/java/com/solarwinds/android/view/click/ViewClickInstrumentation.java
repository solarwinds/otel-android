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

package com.solarwinds.android.view.click;

import androidx.annotation.NonNull;
import com.google.auto.service.AutoService;
import io.opentelemetry.android.instrumentation.AndroidInstrumentation;
import io.opentelemetry.android.instrumentation.InstallationContext;

@AutoService(AndroidInstrumentation.class)
public class ViewClickInstrumentation implements AndroidInstrumentation {
    public static final String INSTRUMENTATION_NAME = "android.view.click";

    @Override
    public void install(@NonNull InstallationContext ctx) {
        ctx.getApplication().registerActivityLifecycleCallbacks(new ViewClickActivityCallback());
        EventBuilderCreator.configure(ctx);
    }

    @NonNull
    @Override
    public String getName() {
        return INSTRUMENTATION_NAME;
    }
}
