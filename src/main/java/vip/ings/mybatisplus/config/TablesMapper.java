package vip.ings.mybatisplus.config;

import vip.ings.mybatisplus.table.TestLabel;
import vip.ings.mybatisplus.table.TestTable;
import vip.ings.mybatisplus.base.BaseTableMap;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-30 16:05
 */
@Data
public class TablesMapper {
    private BaseTableMap tableMap = new BaseTableMap(TestLabel .class, TestTable.class);
}
