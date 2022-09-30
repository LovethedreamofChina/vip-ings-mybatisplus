package com.example.demo.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.example.demo.TablesMapper;
import com.example.demo.mapper.BaseTableMapper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ljr
 * @date 2022-09-27 16:29
 */
public class BaseQueryWrapper<T> extends QueryWrapper<T> {
    private BaseTableMap    baseTableMap;
    private BaseTableMapper mapper;

    public <T, R> BaseQueryWrapper(BaseTableMapper mapper, SFunction<T, R> func) {
        this.mapper = mapper;
        this.baseTableMap = getMap(func);
    }

    public <T,R> void joinTable(SFunction<T, R> column) {
        if (!getParamNameValuePairs().containsKey("eqTable")) {
            getParamNameValuePairs().put("eqTable", " " + baseTableMap.getTableColumnSql(column));
        } else {
            getParamNameValuePairs().put("eqTable", getParamNameValuePairs().get("eqTable") + " = " + baseTableMap.getTableColumnSql(column));
        }
    }

    public <T, R> BaseQueryWrapper eq(SFunction<T, R> column, Object val) {
        super.eq(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper gt(SFunction<T, R> column, Object val) {
        super.gt(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper ge(SFunction<T, R> column, Object val) {
        super.ge(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper lt(SFunction<T, R> column, Object val) {
        super.lt(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper le(SFunction<T, R> column, Object val) {
        super.le(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper in(SFunction<T, R> column, Object val) {
        super.in(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper like(SFunction<T, R> column, Object val) {
        super.like(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper likeRight(SFunction<T, R> column, Object val) {
        super.likeRight(baseTableMap.getTableColumnSql(column), val);
        return this;
    }

    public <T, R> BaseQueryWrapper likeRight(SFunction<T, R> column, Object val, Object val1) {
        super.between(baseTableMap.getTableColumnSql(column), val, val1);
        return this;
    }

    public <T, R> BaseQueryWrapper notBetween(SFunction<T, R> column, Object val, Object val1) {
        super.notBetween(baseTableMap.getTableColumnSql(column), val, val1);
        return this;
    }

    public Map<Class, Object> runSql(BaseTableMapper mapper) {
        getParamNameValuePairs().put("cr", baseTableMap.getSelectSql());
        getParamNameValuePairs().put("table", baseTableMap.getTable());

        Map<Class, Object>  map    = new HashMap<>();
        Map<String, Object> result = mapper.getObj(this);
        if (result == null) {
            return null;
        }
        result.forEach((k, v) -> baseTableMap.getVal(k, v, map));
        return map;
    }


    public List<Map<Class, Object>> runSqlAll() {
        getParamNameValuePairs().put("cr", baseTableMap.getSelectSql());
        getParamNameValuePairs().put("table", baseTableMap.getTable());

        List<Map<Class, Object>>  list   = new ArrayList<>();
        List<Map<String, Object>> result = mapper.getObjList(this);
        if (result == null) {
            return null;
        }

        result.forEach(i -> {
            Map<Class, Object> map = new HashMap<>();
            i.forEach((k, v) -> {
                baseTableMap.getVal(k, v, map);
                list.add(map);
            });});
        return list;
    }

    @Override
    public BaseQueryWrapper<T> select(String... column) {
        return this;
    }

    private <T,R> BaseTableMap getMap(SFunction<T, R> func)  {
        SerializedLambda s = LambdaUtils.resolve(func);
        s.getImplMethodName();
        try {
            return (BaseTableMap) TablesMapper.class.getMethod(s.getImplMethodName()).invoke(BaseTableMapper.t);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("出错");
        }
    }
}
