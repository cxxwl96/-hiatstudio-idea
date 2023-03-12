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

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 校验结果
 *
 * @author cxxwl96
 * @since 2023/3/3 14:54
 */
@Data
@Accessors(chain = true)
public class ValidationResult {
    // 校验是否成功
    private boolean success;

    // 错误消息
    private String errorMessage;

    // 参数列表的值
    private Object[] paramValues;

    /**
     * 无校验实体时默认校验结果成功，且无参数列表的值
     *
     * @return 校验结果
     */
    public static ValidationResult success() {
        return new ValidationResult().setSuccess(true).setParamValues(new Object[] {});
    }

    /**
     * 默认失败
     *
     * @return 校验结果
     */
    public static ValidationResult failed() {
        return new ValidationResult().setSuccess(false);
    }
}
