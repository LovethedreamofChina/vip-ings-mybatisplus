package ings.vip.mybaitsplus.utils;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author ljr
 * @date 2022-09-29 16:49
 */
public class BaseTableInfo extends TableInfo {

    private Class               keyType;
    public  String              tableClass;
    public  Class               table;
    private Map<String, String> noField = new HashMap<>();
    private Map<String, String> resultColumnMap;
    private Map<String, String> selectColumnMap;
    private Map<String, Class>  resultColumnType;

    public Map<String, String> getResultColumnMap() {
        return resultColumnMap;
    }

    public void setResultColumnMap() {
        resultColumnMap = new HashMap<>();
        selectColumnMap = new HashMap<>();
        resultColumnType = new HashMap<>();
        getFieldList().forEach(v -> {
            resultColumnMap.put(tableClass + "$" + v.getColumn(), "set" + TableClassUtils.getMethodName(v.getColumn()));
            selectColumnMap.put(v.getColumn(), getTableName() + "." + v.getColumn() + " as " + tableClass + "$" + v.getColumn());
            resultColumnType.put(tableClass + "$" + v.getColumn(), v.getPropertyType());
        });
        if (StringUtils.isNotEmpty(getKeySqlSelect())) {
            resultColumnMap.put(tableClass + "$" + getKeyColumn(), "set" + TableClassUtils.getMethodName(getKeyColumn()));
            resultColumnType.put(tableClass + "$" + getKeyColumn(), int.class);
            selectColumnMap.put(getKeyColumn(), getTableName() + "." + getKeyColumn() + " as " + tableClass + "$" + getKeyColumn());
        }
    }

    public Map<String, String> getSelectColumnMap() {
        return selectColumnMap;
    }

    public void setKeyType(Class keyType) {
        this.keyType = keyType;
    }

    public Map<String, Class> getResultColumnType() {
        return resultColumnType;
    }

    public void addNoField(String noField) {
        this.noField.put(noField, null);
    }

    public Map<String, String> getNoField() {
        return noField;
    }

    @Override
    public String getAllSqlSelect() {
        return super.getAllSqlSelect();
    }

    @Override
    public String chooseSelect(Predicate<TableFieldInfo> predicate) {
        String sqlSelect = this.getKeySqlSelect();
        sqlSelect = noField.containsKey("get" + sqlSelect) ? null : sqlSelect;
        List<TableFieldInfo> fieldList = getFieldList();
        String               select    = fieldList.stream().filter(v -> !this.noField.containsKey("get" + v.getColumn())).map(v -> selectColumnMap.get(v.getColumn())).collect(Collectors.joining(" ,"));
        if (StringUtils.isNotEmpty(sqlSelect) && StringUtils.isNotEmpty(select)) {
            return getTableName() + '.' + sqlSelect + " as " + tableClass + "$" + sqlSelect
                    + " , " +
                    select;
        } else {
            return StringUtils.isNotEmpty(select) ?
                    select
                    : getTableName() + '.' + sqlSelect + " as " + tableClass + "$" + sqlSelect;
        }
    }

    String getTableColumn(String methodName) {
        List<TableFieldInfo> fieldList = getFieldList();
        StringBuilder        s         = new StringBuilder();
        fieldList.forEach(v -> {
            if (("get" + v.getColumn()).equals(methodName.toLowerCase())) {
                s.append(getTableName()).append(".").append(v.getColumn());
            }
        });

        if (("get" + getKeyColumn()).equals(methodName.toLowerCase())) {
            s.append(getTableName()).append(".").append(getKeyColumn());
        }

        return s.toString();
    }
}
