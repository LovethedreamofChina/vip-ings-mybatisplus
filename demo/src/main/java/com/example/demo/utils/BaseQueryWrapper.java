package com.example.demo.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.example.demo.TablesMapper;
import com.example.demo.mapper.BaseTableMapper;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author ljr
 * @date 2022-09-27 16:29
 */
public class BaseQueryWrapper<T> extends QueryWrapper<T> {
    private       BaseTableMap    baseTableMap;
    private       BaseTableMapper mapper;
    private       List<String>    noColumn = new LinkedList<>();
    public static int             ASC      = 1;
    public static int             DESC     = 1;

    public <T, R> BaseQueryWrapper(BaseTableMapper mapper, SFunction<T, R> func) {
        this.mapper = mapper;
        this.baseTableMap = getMap(func);
    }

    public <T, R> BaseQueryWrapper<T> joinTable(SFunction<T, R> column) {
        if (!getParamNameValuePairs().containsKey("eqTable")) {
            getParamNameValuePairs().put("eqTable", " " + baseTableMap.getTableColumnSql(column));
        } else {
            getParamNameValuePairs().put("eqTable", getParamNameValuePairs().get("eqTable") + " = " + baseTableMap.getTableColumnSql(column));
        }
        return (BaseQueryWrapper<T>) this;
    }

    public <T, R> BaseQueryWrapper<T> eq(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.eq(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> gt(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.gt(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> ge(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.ge(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> lt(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.lt(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> le(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.le(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> in(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.in(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> like(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.like(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> likeRight(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.likeRight(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> likeRight(SFunction<T, R> column, Object val, Object val1) {
        return (BaseQueryWrapper<T>) super.between(baseTableMap.getTableColumnSql(column), val, val1);
    }

    public <T, R> BaseQueryWrapper<T> notBetween(SFunction<T, R> column, Object val, Object val1) {
        return (BaseQueryWrapper<T>) super.notBetween(baseTableMap.getTableColumnSql(column), val, val1);
    }

    public <T, R> BaseQueryWrapper<T> orderBy(SFunction<T, R> column, int by) {
        orderByDesc();
        return (BaseQueryWrapper<T>) this;
    }

    public Map<Class, Object> runSql() {
        getRc();
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
        getRc();
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
            });
        });
        return list;
    }

    public Map<String, Object> runSqlMap() {
        getRc();
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        return mapper.getObj(this);
    }

    public List<Map<String, Object>> runSqlMapAll() {
        getRc();
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        return mapper.getObjList(this);
    }

    @Override
    public QueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        return this;
    }

    @Override
    public QueryWrapper<T> select(Predicate<TableFieldInfo> predicate) {
        return super.select(predicate);
    }

    private <T, R> BaseTableMap getMap(SFunction<T, R> func) {
        SerializedLambda s = LambdaUtils.resolve(func);
        s.getImplMethodName();
        try {
            return (BaseTableMap) TablesMapper.class.getMethod(s.getImplMethodName()).invoke(BaseTableMapper.t);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("出错");
        }
    }

    @Override
    public BaseQueryWrapper<T> select(String... columns) {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = baseTableMap.getTableColumn(columns[i]);
        }

        return (BaseQueryWrapper<T>) super.select(columns);
    }

    @SafeVarargs
    public final <T, R> BaseQueryWrapper<T> addNoColumn(SFunction<T, R>... fun) {
        for (SFunction<T, R> s : fun) {
            noColumn.add(baseTableMap.getTableColumn(baseTableMap.getTableColumnSql(s)));
        }
        return (BaseQueryWrapper<T>) this;
    }

    private void getRc() {
        String selectSql = getSqlSelect();
        if (StringUtils.isEmpty(selectSql)) {
            selectSql = baseTableMap.getSelectSql();
        }

        for (String ls : noColumn) {
            selectSql = selectSql.replace(ls + " ,", "");
        }

        getParamNameValuePairs().put("cr", selectSql);
    }

    public BaseQueryWrapper<T> limit(int count) {
        if (!getParamNameValuePairs().containsKey("by")) {
            getParamNameValuePairs().put("by", " limit " + count);
        } else {
            getParamNameValuePairs().put("by", getParamNameValuePairs().get("by") + " limit " + count);
        }
        return this;
    }

    public void runUpdate(Map<Class, Object> m) {
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        getParamNameValuePairs().put("cr", baseTableMap.getUpdateObj(m));
        int result = mapper.getUpdateObj(this);
        System.out.println(result);
    }
}
