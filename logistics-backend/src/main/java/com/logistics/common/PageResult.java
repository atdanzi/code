package com.logistics.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;
    private List<T> records;

    public PageResult(IPage<T> page) {
        this.total = page.getTotal();
        this.records = page.getRecords();
    }
}
