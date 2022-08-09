package com.example.eshighlevelapi.service;

import com.example.eshighlevelapi.dto.SearchDTO;
import com.example.eshighlevelapi.model.SysMenuLog;

import java.util.List;

/**
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月9日
 */
public interface SysMenuLogService {

    /**
     * 新增一条文档
     *
     * @param sysMenuLog sysMenuLog
     */
    void add(SysMenuLog sysMenuLog);


    /**
     * 全文检索
     *
     * @param searchDTO searchDTO
     */
    List<SysMenuLog> search(SearchDTO searchDTO);
}
