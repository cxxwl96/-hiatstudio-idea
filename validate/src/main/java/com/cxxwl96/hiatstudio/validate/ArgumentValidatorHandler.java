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

import com.alibaba.fastjson.util.TypeUtils;
import com.cxxwl96.hiatstudio.validate.metadata.ElementMetadata;
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Locale;

/**
 * 参数校验接口
 *
 * @param <A> 需要实现的校验注解
 * @author cxxwl96
 * @since 2023/3/3 14:09
 */
public interface ArgumentValidatorHandler<A extends Annotation> extends Initializable<A>, Constraintable {
    Logger log = LoggerFactory.getLogger(ArgumentValidatorHandler.class);

    /**
     * 参数校验处理
     *
     * @param metadata 校验元数据
     * @param chain 校验链
     * @param element 方法参数或类字段的元数据
     * @return 校验通过参数的值
     * @throws Exception 参数校验失败异常
     */
    Object handle(ValidationMetadata metadata, ValidationChain chain, ElementMetadata element) throws Exception;

    /**
     * 复杂的类型转换
     *
     * @param paramName 参数名
     * @param paramValue 参数值。需要转换的数据，可以是基本数据类型的字符串形式，也可以是json字符串
     * @param paramTypeClass 需要转换的参数类型
     * @return 转换之后的对象
     */
    default Object typeCast(String paramName, Object paramValue, Class<?> paramTypeClass) {
        // 复杂的类型转换，基本数据类型及字符串的转换
        try {
            return TypeUtils.cast(paramValue, paramTypeClass, null);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            final String error = String.format(Locale.ROOT,
                "The type of parameter \"%s\" does not match the type of the input parameter. An \"%s\" is expected, but \"%s\" is entered.",
                paramName, paramTypeClass.getName(), paramValue);
            throw new ClassCastException(error);
        }
    }
}
