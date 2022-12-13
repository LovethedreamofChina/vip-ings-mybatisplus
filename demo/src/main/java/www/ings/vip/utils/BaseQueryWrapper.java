package www.ings.vip.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import www.ings.vip.mapper.BaseTableMapper;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author ljr
 * @date 2022-09-27 16:29
 */
public class BaseQueryWrapper<T> extends QueryWrapper<T> {
    private BaseTableMap    baseTableMap;
    private BaseTableMapper mapper;
    private List<String>    noColumn = new LinkedList<>();

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

    public <T, R> BaseQueryWrapper<T> myEq(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.eq(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myGt(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.gt(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myGe(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.ge(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myLt(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.lt(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myLe(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.le(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myLn(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.in(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myLike(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.like(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myLikeRight(SFunction<T, R> column, Object val) {
        return (BaseQueryWrapper<T>) super.likeRight(baseTableMap.getTableColumnSql(column), val);
    }

    public <T, R> BaseQueryWrapper<T> myLikeRight(SFunction<T, R> column, Object val, Object val1) {
        return (BaseQueryWrapper<T>) super.between(baseTableMap.getTableColumnSql(column), val, val1);
    }

    public <T, R> BaseQueryWrapper<T> myNotBetween(SFunction<T, R> column, Object val, Object val1) {
        return (BaseQueryWrapper<T>) super.notBetween(baseTableMap.getTableColumnSql(column), val, val1);
    }

    public <T, R> BaseQueryWrapper<T> myOrderBy(SFunction<T, R> column, int by) {
        orderByDesc();
        return (BaseQueryWrapper<T>) this;
    }

    public Map<Class, Object> runSql() {
        getRc();
        getParamNameValuePairs().put("table", baseTableMap.getTable());

        Map<Class, Object>  map    = new HashMap<>();
        Map<String, Object> result = mapper.getObjs(this);
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
        List<Map<String, Object>> result = mapper.getObjLists(this);
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

    public QueryWrapper<T> mySelect(Predicate<TableFieldInfo> predicate) {
        return super.select(predicate);
    }

    private <T, R> BaseTableMap getMap(SFunction<T, R> func) {
        SerializedLambda s = LambdaUtils.resolve(func);
        s.getImplMethodName();
        try {
            return (BaseTableMap) ((Class) BaseTableMapper.t.get("tClass")).getMethod(s.getImplMethodName()).invoke(BaseTableMapper.t.get("tables"));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("出错");
        }
    }

    public BaseQueryWrapper<T> mySelect(String... columns) {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = baseTableMap.getTableColumn(columns[i]);
        }

        return (BaseQueryWrapper<T>) super.select(columns);
    }

    @SafeVarargs
    public final <T, R> BaseQueryWrapper<T> myAddNoColumn(SFunction<T, R>... fun) {
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

    public BaseQueryWrapper<T> myLimit(int count) {
        if (!getParamNameValuePairs().containsKey("by")) {
            getParamNameValuePairs().put("by", " limit " + count);
        } else {
            getParamNameValuePairs().put("by", getParamNameValuePairs().get("by") + " limit " + count);
        }
        return this;
    }

    /**
     * 更新
     *
     * @param m
     */
    public void runUpdate(Map<Class, Object> m) {
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        getParamNameValuePairs().put("cr", baseTableMap.getUpdateObj(m));
        int result = mapper.updates(this);
        System.out.println(result);
    }

    /**
     * 删除
     */
    public int runDelete() {
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        return mapper.deletes(this);
    }

    /**
     * 查询
     *
     * @return
     */
    public Map<String, Object> runSqlMap() {
        getRc();
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        return mapper.getObjs(this);
    }

    /**
     * 查询
     *
     * @return
     */
    public List<Map<String, Object>> runSqlMapAll() {
        getRc();
        getParamNameValuePairs().put("table", baseTableMap.getTable());
        return mapper.getObjLists(this);
    }
}
