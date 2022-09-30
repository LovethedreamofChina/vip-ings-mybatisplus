package com.example.demo.utils;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ljr
 * @date 2022-09-29 18:15
 */
public class BaseTableMap extends HashMap<String, BaseTableInfo> {

    public  StringBuilder               table  = new StringBuilder();
    private Map<String, Object>         tClass = new HashMap<>();

    public BaseTableMap(Class... classes) {
        super();
        System.out.println("===================================");
        int i = 0;
        for (Class c : classes) {
            BaseTableInfo t = TableInfoHelper.initTableInfo(null, c);
            t.table = c;
            put(c.getSimpleName(), t);
            tClass.put(t.getTableName(), c);
            i++;
            table.append(t.getTableName());
            if (i != classes.length) {
                table.append(" , ");
            }
        }
    }

    public String getTable() {
        return table.toString();
    }

    public String getSelectSql() {
        final int[]   i = {0};
        StringBuilder s = new StringBuilder();
        forEach((k, v) -> {
            BaseTableInfo t = get(k);
            i[0]++;
            s.append(t.getAllSqlSelect());
            if (i[0] != size()) {
                s.append(" , ");
            }
        });

        return s.toString();
    }

    public <T, R> void addNoColumn(SFunction<T, R> func) {
        SerializedLambda s    = LambdaUtils.resolve(func);
        BaseTableInfo    info = get(s.getImplClass().getSimpleName());
        info.addNoField(s.getImplMethodName().toLowerCase());
    }

    <T, R> String getTableColumnSql(SFunction<T, R> func) {
        SerializedLambda s    = LambdaUtils.resolve(func);
        BaseTableInfo    info = get(s.getImplClass().getSimpleName());
        return info.getTableColumn(s.getImplMethodName());
    }

    void getVal(String key, Object val, Map<Class, Object> o) {
        forEach((k,v)->{
            try {
                if (v.getResultColumnMap().containsKey(key)) {
                    String m = v.getResultColumnMap().get(key);
                    if (!o.containsKey(v.table)) {
                        Object t = v.table.newInstance();
                        o.put(v.table, t);
                    }
                    v.table.getMethod(m, v.getResultColumnType().get(key)).invoke(o.get(v.table), val);
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}
