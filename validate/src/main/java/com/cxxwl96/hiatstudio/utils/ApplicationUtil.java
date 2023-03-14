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

package com.cxxwl96.hiatstudio.utils;

import java.lang.reflect.Field;
import java.util.Locale;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * IDEA应用工具
 *
 * @author cxxwl96
 * @since 2023/3/11 22:42
 */
@Slf4j
public class ApplicationUtil {
    /**
     * 获取语言环境，不设置默认为ENGLISH
     *
     * @return 语言环境
     */
    public static Locale getLocale() {
        final String locale = System.getProperty("Locale");
        // 没有传入此参数默认ENGLISH
        if (StrUtil.isBlank(locale)) {
            return Locale.ENGLISH;
        }
        for (Field field : Locale.class.getDeclaredFields()) {
            if (field.getType().equals(Locale.class) && field.getName().equalsIgnoreCase(locale)) {
                try {
                    return (Locale) field.get(null);
                } catch (IllegalAccessException exception) {
                    log.warn("Illegal access {} language. detail message: {}", locale, exception.getMessage(),
                        exception);
                    return Locale.ENGLISH;
                }
            }
        }
        log.warn("{} language not found.", locale);
        // 传入的语言参数不存在默认ENGLISH
        return Locale.ENGLISH;
    }
}
