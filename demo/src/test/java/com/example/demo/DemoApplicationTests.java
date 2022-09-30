package com.example.demo;

import com.example.demo.mapper.BaseTableMapper;
import com.example.demo.table.TestLabel;
import com.example.demo.table.TestTable;
import com.example.demo.utils.BaseQueryWrapper;
import com.example.demo.utils.BaseTableMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        BaseQueryWrapper<BaseTableMap> queryWrapper = new BaseQueryWrapper<BaseTableMap>(baseTableMapper, TablesMapper::getTableMap);
        queryWrapper.joinTable(TestLabel::getId);
        queryWrapper.joinTable(TestTable::getId);
        List<Map<Class, Object>> obj = queryWrapper.runSqlAll();
        System.out.println(obj.get(0));
    }

    @Resource
    private BaseTableMapper baseTableMapper;
}

