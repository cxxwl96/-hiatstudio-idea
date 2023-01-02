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

package com.cxxwl96.hiatstudio.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;

/**
 * DynamicServiceProxy
 *
 * @author cxxwl96
 * @since 2023/1/1 19:27
 */
@Log4j2
public class DynamicServiceProxy {
    // 类池
    private static Map<String, Class<?>> classPool;

    // 单实例池
    private static Map<String, Object> instancePool;

    public static <T> T newLocalService(Class<T> interfaceClass) {
        if (interfaceClass == null) {
            throw new NullPointerException("Interface class must not be null");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName() + " must be an interface class");
        }
        if ("service".equals(currentModel())) {
            // TODO 从ate获取
            return null;
        }
        // 从本地加载器获取
        if (classPool == null) {
            synchronized (DynamicServiceProxy.class) {
                if (classPool == null) {
                    // 动态加载类
                    final ClassLoader loader = MavenInvoker.invoke();
                    // 获取所有的类
                    final List<Class<?>> classes = ClassUtil.getClasses(loader);
                    // 创建类池
                    buildClassPool(classes);
                }
            }
        }
        // 创建实例
        T instance = newInstance(interfaceClass, classPool.getOrDefault(interfaceClass.getName(), null));
        return null;
    }

    private static <T> T newInstance(Class<T> interfaceClass, Class<?> clazz) {
        try {
            clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException exception) {
            // TODO Auto-generated catch block
        }
        return null;
    }

    /**
     * 当前模式
     *
     * @return service: 服务；idea: idea启动
     */
    private static String currentModel() {
        // TODO 获取当前模式
        return "idea";
    }

    /**
     * 创建类池
     *
     * @param classes 类
     */
    private static void buildClassPool(List<Class<?>> classes) {
        classPool = new HashMap<>();
        for (Class<?> clazz : classes) {
            classPool.put(clazz.getName(), clazz);
        }
    }
}
