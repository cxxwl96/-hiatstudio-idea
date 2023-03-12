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

package com.cxxwl96.hiatstudio.validate.annotations;

import com.cxxwl96.hiatstudio.validate.CustomValidatorHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数校验。
 * 仅用于功能接口上，用于参数个数校验，此注解可配合@BasicParam、@BeanParams、@ListParams使用。
 * <p>
 * 注：此注解的参数个数校验优先级高于以上一切可能带有参数个数校验注解的优先级。
 * 例如@BeanParams自动校验属性个数、@ListParams自带参数个数。当设置了此注解校验参数个数时，将优先使用此注解校验参数个数
 *
 * @author cxxwl96
 * @since 2023/2/27 15:08
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamValidator {
    // 需要校验的参数个数，size大于等于0才做校验
    int size() default -1;

    // 自定义校验处理器
    Class<? extends CustomValidatorHandler>[] customValidatorHandler() default {};
}
