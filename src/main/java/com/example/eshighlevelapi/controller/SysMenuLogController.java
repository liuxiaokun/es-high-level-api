package com.example.eshighlevelapi.controller;

import com.example.eshighlevelapi.controller.base.RO;
import com.example.eshighlevelapi.dto.SearchDTO;
import com.example.eshighlevelapi.model.SysMenuLog;
import com.example.eshighlevelapi.service.SysMenuLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月9日
 */
@RestController
@RequestMapping("/sysMenuLog")
@Slf4j
public class SysMenuLogController {

    private final SysMenuLogService sysMenuLogService;

    public SysMenuLogController(SysMenuLogService sysMenuLogService) {
        this.sysMenuLogService = sysMenuLogService;
    }


    @PostMapping("add")
    public RO add(SysMenuLog sysMenuLog) {
        // 参数校验
        sysMenuLogService.add(sysMenuLog);
        return RO.success();
    }


    @GetMapping("search")
    public RO search(SearchDTO searchDTO) {
        // 参数校验
        List<SysMenuLog> searchResult = sysMenuLogService.search(searchDTO);
        return RO.success(searchResult);
    }
}
