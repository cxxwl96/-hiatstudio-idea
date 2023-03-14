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

package com.cxxwl96.hiatstudio.validate.metadata;

import com.alibaba.fastjson.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import lombok.Getter;

/**
 * 方法参数或类字段的元数据
 * 这里存储的是方法参数或者类字段的元数据
 *
 * @author cxxwl96
 * @since 2023/3/14 14:09
 */
public class ElementMetadata {
    // 方法参数或类字段实体
    private final Object parameterOrField;

    // 方法参数或类字段的类型
    @Getter
    private final Class<?> type;

    // 方法参数或类字段所在的索引
    @Getter
    private final int index;

    // 方法参数或类字段的名称
    @Getter
    private final String name;

    public ElementMetadata(Object parameterOrField, int index, String name) {
        this.parameterOrField = parameterOrField;
        if (parameterOrField instanceof Parameter) {
            this.type = ((Parameter) parameterOrField).getType();
        } else if (parameterOrField instanceof Field) {
            this.type = ((Field) parameterOrField).getType();
        } else {
            throw new IllegalArgumentException(
                "ParameterOrField mast be " + Parameter.class.getName() + " or " + Field.class.getName());
        }
        this.index = index;
        this.name = name;
    }

    public <T> T getParameterOrField(Class<T> parameterOrFieldClass) {
        return TypeUtils.cast(parameterOrField, parameterOrFieldClass, null);
    }

    /**
     * 是否方法参数
     *
     * @return 是否方法参数
     */
    public boolean onParameter() {
        return parameterOrField instanceof Parameter;
    }

    /**
     * 是否类字段
     *
     * @return 是否类字段
     */
    public boolean onField() {
        return parameterOrField instanceof Field;
    }
}
