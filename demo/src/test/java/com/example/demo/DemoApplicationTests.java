package com.example.demo;

import com.example.demo.mapper.BaseTableMapper;
import com.example.demo.table.TestLabel;
import com.example.demo.table.TestTable;
import com.example.demo.utils.BaseQueryWrapper;
import com.example.demo.utils.BaseTableMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
//        queryWrapper.joinTable(TestLabel::getId).joinTable(TestTable::getId);
        queryWrapper.addNoColumn(TestLabel::getId).addNoColumn(TestTable::getName).limit(1);
        Map<Class, Object> obj = queryWrapper.runSql();
        System.out.println(obj.get(TestTable.class));
        queryWrapper.runUpdate(obj);

    }

    @Resource
    private BaseTableMapper baseTableMapper;
}
