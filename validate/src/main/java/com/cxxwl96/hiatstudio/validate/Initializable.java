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

package com.cxxwl96.hiatstudio.validate;

import java.lang.annotation.Annotation;

/**
 * 校验处理器初始化接口
 *
 * @author cxxwl96
 * @since 2023/3/12 23:19
 */
public interface Initializable<A extends Annotation> {
    /**
     * 初始化方法
     * 注意：
     * 该方法名不可更改，在Validation.invokeInitializable()方法里直接通过方法名的字符串获取该方法，
     * 因为函数式接口方式在lambda的语法上不能直接调用有参方法，资质过浅暂时还想不到其他方式获取该方法的方法名
     *
     * @param annotation 注解
     */
    default void initialize(A annotation) {
    }
}
