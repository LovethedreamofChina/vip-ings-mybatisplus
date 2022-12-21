package vip.ings.mybatisplus.base;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import vip.ings.mybatisplus.utils.BaseTableInfo;
import vip.ings.mybatisplus.utils.TableClassUtils;
import vip.ings.mybatisplus.utils.TablesInfoHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author ljr
 * @date 2022-09-29 18:15
 */
public class BaseTableMap extends HashMap<String, BaseTableInfo> {

    public  StringBuilder       table       = new StringBuilder();
    private Map<String, String> tableColumn = new HashMap<>();
    private Map<String, String> tableMethod = new HashMap<>();

    public BaseTableMap(Class... classes) {
        super();
        int i = 0;
        for (Class c : classes) {
            BaseTableInfo t = TablesInfoHelper.initTableInfo(null, c);
            t.table = c;
            put(c.getSimpleName(), t);
            i++;
            table.append(t.getTableName());
            if (i != classes.length) {
                table.append(" , ");
            }
        }

        setTableColumn();
    }

    private void setTableColumn() {
        forEach((k, v) -> v.getSelectColumnMap().forEach((kk, vv) -> {
            tableColumn.put(v.getTableName() + "." + kk, vv);
            tableMethod.put(v.getTableName() + "." + kk, TableClassUtils.getMethodName(kk));
        }));
    }

    public String getTableColumn(String column) {
        return tableColumn.get(column);
    }

    public String getTable() {
        return table.toString();
    }

    String getSelectSql() {
        final int[]   i = {0};
        StringBuilder s = new StringBuilder();
        forEach((k, v) -> {
            BaseTableInfo t = get(k);
            if (t == null) {
                throw new RuntimeException("error=>" + v + "键值为空");
            }
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
        if (info == null) {
            throw new RuntimeException("error=>" + s.getImplClass().getSimpleName() + "键值为空");
        }
        info.addNoField(s.getImplMethodName().toLowerCase());
    }

    <T, R> String getTableColumnSql(SFunction<T, R> func) {
        SerializedLambda s    = LambdaUtils.resolve(func);
        BaseTableInfo    info = get(s.getImplClass().getSimpleName());
        if (info == null) {
            throw new RuntimeException("error=>" + s.getImplMethodName() + "键值为空");
        }
        return info.getTableColumn(s.getImplMethodName());
    }

    void getVal(String key, Object val, Map<Class, Object> o) {
        forEach((k, v) -> {
            try {
                if (v.getResultColumnMap().containsKey(key)) {
                    String m = v.getResultColumnMap().get(key);
                    if (!o.containsKey(v.table)) {
                        Object t = v.table.getDeclaredConstructor().newInstance();
                        o.put(v.table, t);
                    }
                    v.table.getMethod(m, v.getResultColumnType().get(key)).invoke(o.get(v.table), val);
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    String getUpdateObj(Map<Class, Object> m) {
        List<String> s = new ArrayList<>();
        forEach((k, v) -> v.getFieldList().forEach(vv -> {
            try {
                Object val = m.get(v.table).getClass().getMethod("get" + tableMethod.get(v.getTableName() + "." + vv.getColumn())).invoke(m.get(v.table));
                String i   = v.getTableColumn("get" + vv.getColumn()) + "=";
                if (val instanceof Integer || val instanceof Double || val instanceof Long || val instanceof Float || val == null) {
                    i += val;
                } else {
                    i += "'" + val + "'";
                }
                i += " ";
                s.add(i);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }));
        return String.join(" , ", s);
    }
}
