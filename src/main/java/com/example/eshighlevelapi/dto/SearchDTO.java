package com.example.eshighlevelapi.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月9日
 */
@Data
public class SearchDTO {

    private String keyword;

    private Date startDate;

    private Date endDate;

    private Integer pageSize;

    private Integer pageNum;

    public Integer getPageSize() {
        return null == pageSize ? 20 : pageSize;
    }

    public Integer getPageNum() {
        return null == pageNum ? 1 : pageNum;
    }
}
