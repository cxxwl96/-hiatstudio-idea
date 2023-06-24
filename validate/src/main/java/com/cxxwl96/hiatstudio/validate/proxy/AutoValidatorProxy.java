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
package com.cxxwl96.hiatstudio.validate.proxy;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

/**
 * AutoValidatorProxy
 *
 * @author cxxwl96
 * @since 2023/6/25 06:06
 */
public abstract class AutoValidatorProxy {
    public static <T> T buildBean(Object bean, Class<?>... beanInterfaces) {
        ClassLoader classLoader = bean.getClass().getClassLoader();
        Object instance = Proxy.newProxyInstance(classLoader, beanInterfaces, new JdkValidatorProxy(bean));
        return (T) instance;
    }

    public static <T> T buildBean(Class<T> beanClass) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback(new CglibValidatorProxy());
        Object instance = enhancer.create();
        return (T) instance;
    }
}
