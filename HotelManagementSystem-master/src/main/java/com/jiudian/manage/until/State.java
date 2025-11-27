package com.jiudian.manage.until;

public enum  State {

    //枚举常量（本身就是该枚举类型的实例对象），使用他们的时候，就和使用类变量一样使用
    //这种设计让枚举既具备类的静态访问特性，又具备对象的实例特性
    //public static final State SuccessCode = new State("code", "0");（静态变量可以是些变量可以是基本数据类型，也可以是对象引用）
    SuccessCode("code","0"),SuccessMessage("message","成功"),
    ErrorCode("code","-1"),ErrorMessage("message","失败")
    ;


    public String name;
    public String message;
    State(String name,String message) {
        this.name = name;
        this.message = message;
    }
    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

}
