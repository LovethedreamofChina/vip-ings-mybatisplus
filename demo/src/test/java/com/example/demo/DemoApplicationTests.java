package com.example.demo;

import com.example.demo.mapper.BaseTableMapper;
import com.example.demo.table.TestLabel;
import com.example.demo.table.TestTable;
import com.example.demo.utils.BaseQueryWrapper;
import com.example.demo.utils.BaseTableMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
//        queryWrapper.joinTable(TestLabel::getId).joinTable(TestTable::getId);
        queryWrapper.addNoColumn(TestLabel::getId).addNoColumn(TestTable::getName).limit(1);
        queryWrapper.orderByAsc("test_table.id");
        Map<Class, Object> obj = queryWrapper.runSql();
        System.out.println(obj.get(TestTable.class));

//        queryWrapper.runUpdate(obj);
    }

    @Test
    void contextLoadsOnt() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
        Map<Class, Object> m = new HashMap<>();
        TestLabel testLabel = new TestLabel();
        testLabel.setLabel("11111");
        testLabel.setName("name");
        TestTable testTable = new TestTable();
        testTable.setName("name1");
        testTable.setAge("110");
        m.put(TestTable.class, testTable);
        m.put(TestLabel.class, testLabel);
        queryWrapper.eq(TestLabel::getId, 2);
        queryWrapper.joinTable(TestTable::getId).joinTable(TestLabel::getId);
        queryWrapper.runUpdate(m);
    }

    @Resource
    private BaseTableMapper baseTableMapper;
}
