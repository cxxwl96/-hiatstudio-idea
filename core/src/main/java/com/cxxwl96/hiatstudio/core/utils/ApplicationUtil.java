/*
 * Copyright (c) 2021-2023, jad (cxxwl96@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cxxwl96.hiatstudio.core.utils;

import java.lang.reflect.Field;
import java.util.Locale;

import lombok.SneakyThrows;

/**
 * IDEA应用工具
 *
 * @author cxxwl96
 * @since 2023/3/11 22:42
 */
public class ApplicationUtil {
    /**
     * 获取语言环境，不设置默认为ENGLISH
     *
     * @return 语言环境
     */
    @SneakyThrows
    public static Locale getLocale() {
        final String locale = System.getProperty("Locale");
        for (Field field : Locale.class.getDeclaredFields()) {
            if (field.getType().equals(Locale.class) && field.getName().equalsIgnoreCase(locale)) {
                return (Locale) field.get(null);
            }
        }
        return Locale.ENGLISH;
    }
}
