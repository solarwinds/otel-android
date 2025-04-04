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

package com.solarwinds.instrumentation.view.agent;

import static net.bytebuddy.matcher.ElementMatchers.is;

import android.view.View;
import com.solarwinds.instrumentation.view.library.internal.ViewSubstitutions;
import java.io.IOException;
import net.bytebuddy.asm.MemberSubstitution;
import net.bytebuddy.build.AndroidDescriptor;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;

public class AndroidViewPlugin implements Plugin {
    private final AndroidDescriptor androidDescriptor;

    public AndroidViewPlugin(AndroidDescriptor androidDescriptor) {
        this.androidDescriptor = androidDescriptor;
    }

    @Override
    public DynamicType.Builder<?> apply(
            DynamicType.Builder<?> builder,
            TypeDescription typeDescription,
            ClassFileLocator classFileLocator) {
        try {
            return builder.visit(
                    MemberSubstitution.relaxed()
                            .method(
                                    is(
                                            View.class.getDeclaredMethod(
                                                    "setOnClickListener",
                                                    View.OnClickListener.class)))
                            .replaceWith(
                                    ViewSubstitutions.class.getDeclaredMethod(
                                            "substitutionForSetOnClickListener",
                                            View.class,
                                            View.OnClickListener.class))
                            .on(MethodDescription::isMethod));

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        // No operation.
    }

    @Override
    public boolean matches(TypeDescription target) {
        return androidDescriptor.getTypeScope(target) == AndroidDescriptor.TypeScope.LOCAL;
    }
}
