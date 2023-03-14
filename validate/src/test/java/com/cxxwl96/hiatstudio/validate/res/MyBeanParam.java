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

package com.cxxwl96.hiatstudio.validate.res;

import com.cxxwl96.hiatstudio.validate.annotations.IgnoreField;
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * MyBeanParam
 *
 * @author cxxwl96
 * @since 2023/3/14 19:07
 */
@Data
public class MyBeanParam {
    @NotBlank
    private String name;

    @Min(10)
    @Max(20)
    private int age;

    private boolean married;

    @Pattern(regexp = "[1-9][0-9]{4,10}")
    private String qq;

    @IgnoreField
    private String ignoreField;

    // 接收JSON字符串对应的类型
    @JsonParam(index = 4)
    @Size(max = 2)
    private List<String> addresses;

    // 接收JSON字符串对应的类型
    @JsonParam(index = 5)
    private JsonObject jsonObj;

    // 接收JSON字符串对应的类型，通过jsonpath接收
    @JsonParam(index = 5, jsonPath = "$.key2")
    private List<String> key2;

    // setter方式注入
    private void setName(String name) {
        this.name = name;
        // 这里可以做其他操作
    }
}
