package com.codelessda.plugin

/**
 * Created by nailperry on 2017/3/4.
 */

public class MethodCell {
    // 原方法名
    String name
    // 原方法描述
    String desc
    // 方法所在的接口或类
    String parent
    // 采集数据的方法名
    String agentName
    // 采集数据的方法描述
    String agentDesc
    // 采集数据的方法参数说明（ 0：this，1+：普通参数 ）
    int paramsStart
    // 采集数据的方法参数个数
    int paramsCount

    MethodCell(String name, String desc, String parent, String agentName, String agentDesc, int paramsStart, int paramsCount) {
        this.name = name
        this.desc = desc
        this.parent = parent
        this.agentName = agentName
        this.agentDesc = agentDesc
        this.paramsStart = paramsStart
        this.paramsCount = paramsCount
    }
}