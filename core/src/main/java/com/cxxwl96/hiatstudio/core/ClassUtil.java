/*
 * Copyright (c) 2021-2022, jad (cxxwl96@sina.com).
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassUtil
 *
 * @author cxxwl96
 * @since 2023/1/1 14:59
 */
@Slf4j
public class ClassUtil {
    /**
     * 获取已加载的类以及url中的类
     *
     * @param loader 类加载器
     * @return 已加载的类以及url中的类
     */
    public static List<Class<?>> getClasses(ClassLoader loader) {
        Class<?> loaderClass = loader.getClass();
        while (loaderClass != ClassLoader.class) {
            loaderClass = loaderClass.getSuperclass();
        }
        final Set<Class<?>> classes = new HashSet<>();
        // 获取已加载的类
        Field field = null;
        boolean accessible = false;
        try {
            field = loaderClass.getDeclaredField("classes");
            accessible = field.isAccessible();
            field.setAccessible(true);
            final List<Class<?>> list = (List<Class<?>>) field.get(loader);
            classes.addAll(list);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            throw new RuntimeException(exception.getMessage());
        } finally {
            if (field != null) {
                field.setAccessible(accessible);
            }
        }
        // 获取url中的类
        classes.addAll(getClassesFromUrls(loader));
        return new ArrayList<>(classes);
    }

    private static Collection<Class<?>> getClassesFromUrls(ClassLoader loader) {
        if (!(loader instanceof URLClassLoader)) {
            return new ArrayList<>();
        }
        final ArrayList<Class<?>> classes = new ArrayList<>();
        final URL[] urls = ((URLClassLoader) loader).getURLs();
        for (URL url : urls) {
            if (url.getPath().endsWith(FileUtil.JAR_FILE_EXT)) {
                classes.addAll(getClassesFromJar(url.getPath(), loader));
            } else {
                classes.addAll(getClassesFromClassFile(new File(url.getPath()), loader));
            }
        }
        classes.addAll(getClassesFromUrls(loader.getParent()));
        return classes;
    }

    private static Collection<Class<?>> getClassesFromJar(String path, ClassLoader loader) {
        final List<Class<?>> classes = new ArrayList<>();
        Enumeration<JarEntry> entries = null;
        try {
            entries = new JarFile(path).entries();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        while (entries != null && entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith(FileUtil.CLASS_EXT)) {
                // 将class文件路径转为类路径
                final String referencePath = jarEntry.getName()
                    .substring(0, jarEntry.getName().length() - FileUtil.CLASS_EXT.length())
                    .replaceAll("/", ".");
                try {
                    final Class<?> clazz = loader.loadClass(referencePath);
                    classes.add(clazz);
                } catch (NoClassDefFoundError | UnsupportedClassVersionError | ClassNotFoundException exception) {
                    // no processing
                }
            }
        }
        return classes;
    }

    private static Collection<Class<?>> getClassesFromClassFile(File file, ClassLoader loader) {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        final List<Class<?>> classes = new ArrayList<>();
        if (file.isFile() && file.getAbsolutePath().endsWith(FileUtil.CLASS_EXT)) {
            // 将class文件路径转为类路径
            String classpath = ClassUtil.class.getClassLoader().getResource(".").getPath();
            final String name = file.getAbsolutePath().replace(classpath, "");
            final String referencePath = name.substring(0, name.length() - FileUtil.CLASS_EXT.length())
                .replaceAll("/", ".");
            try {
                final Class<?> clazz = loader.loadClass(referencePath);
                classes.add(clazz);
            } catch (NoClassDefFoundError | UnsupportedClassVersionError | ClassNotFoundException exception) {
                // no processing
            }
        }
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    classes.addAll(getClassesFromClassFile(listFile, loader));
                }
            }
        }
        return classes;
    }
}

