package com.neusoft.dxz.web.demo.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class Demo02Param {

    private String name;
    private List<String> keys;
    private List<DemoInnerParam> inners;
}
