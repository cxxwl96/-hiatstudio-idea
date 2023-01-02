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

package com.cxxwl96.hiatstudio.component;

import com.cxxwl96.hiatstudio.baseservice.IService;
import com.cxxwl96.hiatstudio.core.ClassUtil;
import com.cxxwl96.hiatstudio.core.MavenInvokeException;
import com.cxxwl96.hiatstudio.core.MavenInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * MainClass
 *
 * @author cxxwl96
 * @since 2023/1/1 15:00
 */
public class MainClass {
    public static void main(String[] args)
        throws MavenInvokeException, InstantiationException, IllegalAccessException, NoSuchMethodException,
        InvocationTargetException {
        // 动态加载类
        final ClassLoader loader = MavenInvoker.invoke();

        //
        long start = System.currentTimeMillis();
        final List<Class<?>> classes = ClassUtil.getClasses(loader); // 获取所有的类


        for (int i = 0; i < classes.size(); i++) {
            final Class<?> clazz = classes.get(i);
            if (IService.class.isAssignableFrom(clazz) && IService.class != clazz) {
                System.out.println("founded class: " + clazz.getName());
                final Object instance = clazz.newInstance();
                final Method method = clazz.getDeclaredMethod("function");
                method.invoke(instance);
            }
        }
        System.out.println(System.currentTimeMillis() - start + "ms");
    }
}
