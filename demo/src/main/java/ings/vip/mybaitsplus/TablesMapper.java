package ings.vip.mybaitsplus;

import ings.vip.mybaitsplus.table.TestLabel;
import ings.vip.mybaitsplus.table.TestTable;
import ings.vip.mybaitsplus.utils.BaseTableMap;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-30 16:05
 */
@Data
public class TablesMapper {
    private BaseTableMap tableMap = new BaseTableMap(TestLabel .class, TestTable.class);
}
