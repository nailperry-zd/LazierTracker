package com.netease.mobidroid.plugin

public class MethodCell{
    String name
    String desc

    MethodCell(String name, String desc) {
        this.name = name
        this.desc = desc
    }

    String getName() {
        return name
    }

    String getDesc() {
        return desc
    }
}