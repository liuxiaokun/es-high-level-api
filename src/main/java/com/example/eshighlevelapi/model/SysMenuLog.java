package com.example.eshighlevelapi.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * es的菜单浏览数据实体
 *
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月9日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SysMenuLog {

    private String name;

    private String mobile;

    private String email;

    @JSONField(name = "menu_name")
    private String menuName;

    @JSONField(name = "menu_time")
    private Date menuTime;

    @JSONField(name = "menu_url")
    private String menuUrl;


    public static void main(String[] args) {
        SysMenuLog sysMenuLog = new SysMenuLog();
        sysMenuLog.setMenuUrl("http");
        sysMenuLog.setMenuTime(new Date());
        System.out.println(JSON.toJSONString(sysMenuLog));
    }
}
