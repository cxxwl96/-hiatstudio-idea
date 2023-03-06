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

package com.cxxwl96.hiatstudio.core.validate.handler;

import com.cxxwl96.hiatstudio.core.validate.ArgumentValidatorHandler;
import com.cxxwl96.hiatstudio.core.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.core.validate.annotations.ListParams;
import com.cxxwl96.hiatstudio.core.validate.annotations.ValidatorHandler;

import java.lang.reflect.Parameter;

/**
 * 处理器：@ListParams注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/3 17:46
 */
@ValidatorHandler(annotation = ListParams.class)
public class ListParamsHandler implements ArgumentValidatorHandler {
    /**
     * 参数校验处理
     *
     * @param metadata 校验元数据
     * @param parameter 参数
     * @param index 参数索引
     * @param paramName 参数名
     * @return 校验通过参数的值
     * @throws Exception 参数校验失败异常
     */
    @Override
    public Object handle(ValidationMetadata metadata, Parameter parameter, int index, String paramName)
        throws Exception {
        // TODO Validate
        return metadata.getParamValues();
    }
}
