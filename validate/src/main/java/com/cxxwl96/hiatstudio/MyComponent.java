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

package com.cxxwl96.hiatstudio;

import com.cxxwl96.hiatstudio.ast.EnableInjectDocument;

/**
 * Person
 *
 * @author cxxwl96
 * @since 2023/3/19 15:25
 */
@EnableInjectDocument(FunctionInterface.class)
public class MyComponent extends AbstractComponent {
    /**
     * @hi-description 这个是接口的功能描述
     * @hi-param 这个是参数说明
     * @hi-return 这个是返回值说明
     * @hi-param-example 这个是参数实例
     */
    @FunctionInterface(name = "我的第一个接口")
    private int myFunction() {

        return 0;
    }
}
