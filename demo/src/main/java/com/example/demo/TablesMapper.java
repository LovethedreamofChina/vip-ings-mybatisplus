package com.example.demo;

import com.example.demo.table.TestLabel;
import com.example.demo.table.TestTable;
import com.example.demo.utils.BaseTableMap;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-30 16:05
 */
@Data
public class TablesMapper {
    private BaseTableMap tableMap = new BaseTableMap(TestLabel .class, TestTable .class);
}
