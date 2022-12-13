package ings.vip.mybaitsplus;

import ings.vip.mybaitsplus.mapper.TableMapper;
import ings.vip.mybaitsplus.utils.BaseQueryWrapper;
import ings.vip.mybaitsplus.utils.BaseTableMap;
import ings.vip.mybaitsplus.table.TestLabel;
import ings.vip.mybaitsplus.table.TestTable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
//        queryWrapper.joinTable(TestLabel::getId).joinTable(TestTable::getId);
        queryWrapper.myAddNoColumn(TestLabel::getId).myAddNoColumn(TestTable::getName).myLimit(1);
        queryWrapper.orderByAsc("test_table.id");
        Map<Class, Object> obj = queryWrapper.runSql();
        if (ObjectUtils.isEmpty(obj)) {
            return;
        }
        System.out.println(obj.get(TestTable.class));

        List<Map<Class, Object>> listObj = queryWrapper.runSqlAll();
        if (ObjectUtils.isEmpty(obj)) {
            return;
        }
        System.out.println(listObj);
    }

    @Test
    void runUpdate() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
        Map<Class, Object> m = new HashMap<>();
        TestLabel testLabel = new TestLabel();
        testLabel.setLabel("test");
        testLabel.setName("name");
        TestTable testTable = new TestTable();
        testTable.setName("name1");
        testTable.setAge("110");
        m.put(TestTable.class, testTable);
        m.put(TestLabel.class, testLabel);
        queryWrapper.myEq(TestLabel::getId, 2);
        queryWrapper.joinTable(TestTable::getId).joinTable(TestLabel::getId);
        queryWrapper.runUpdate(m);
    }

    @Test
    void runDelete() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
        queryWrapper.myEq(TestLabel::getId, 1);
        queryWrapper.joinTable(TestTable::getId).joinTable(TestLabel::getId);
        int res = queryWrapper.runDelete();
        System.out.println(res);
    }

    @Resource
    private TableMapper baseTableMapper;
}
