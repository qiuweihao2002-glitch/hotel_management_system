package com.jiudian.manage.until;

import java.util.HashMap;

//StateSignal 类的作用
//响应封装器：StateSignal 类主要用于封装后端API的响应数据，不是封装请求体
//统一响应格式：为前端提供标准化的JSON响应结构，包含状态码、消息和业务数据
//交互规范：作为后端与前端的交互约定，确保所有API接口返回一致的数据格式
//主要功能
//状态管理：通过 put(State state) 方法添加预定义的状态信息
//数据承载：通过 put(String name, Object val) 方法添加业务数据
//格式转换：getResult() 方法返回 HashMap，Spring Boot自动序列化为JSON


public class StateSignal {
    HashMap<String,Object> result = new HashMap<String,Object>();

    public void put(State state){
        result.put(state.name,state.message);
    }
    public void put(String name,Object val){
        result.put(name,val);
    }
    public HashMap<String, Object> getResult() {
        return result;
    }
}
