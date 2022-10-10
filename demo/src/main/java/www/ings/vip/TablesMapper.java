package www.ings.vip;

import www.ings.vip.table.TestLabel;
import www.ings.vip.table.TestTable;
import www.ings.vip.utils.BaseTableMap;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-30 16:05
 */
@Data
public class TablesMapper {
    private BaseTableMap tableMap = new BaseTableMap(TestLabel .class, TestTable.class);
}
