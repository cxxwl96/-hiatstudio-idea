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

package com.cxxwl96.hiatstudio.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基础参数注入注解。
 * 与BeanParams作为javaBean接收类似，只不过这里是使用String、基本数据类型及其封装类型作为参数接收。
 * 在参数个数较少的情况下建议使用基础参数注入，避免每个功能接口接收参数都新建一个JavaBean。
 *
 * @author cxxwl96
 * @since 2023/2/27 15:34
 */
@Documented
@Target( {ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BasicParam {
    // 需要接收参数的位置。即接收第几个参数，索引从0开始
    int index();

    // 是否必填参数。如果设置为false，则不管该参数前是否有校验的注解，都不作校验。
    // 例如：@BasicParam(index = 0, required = false) @NotEmpty String name
    boolean require() default true;
}
