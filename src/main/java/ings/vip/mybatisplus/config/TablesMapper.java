package ings.vip.mybatisplus.config;

import ings.vip.mybatisplus.table.TestLabel;
import ings.vip.mybatisplus.table.TestTable;
import ings.vip.mybatisplus.utils.BaseTableMap;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-30 16:05
 */
@Data
public class TablesMapper {
    private BaseTableMap tableMap = new BaseTableMap(TestLabel .class, TestTable.class);
}
