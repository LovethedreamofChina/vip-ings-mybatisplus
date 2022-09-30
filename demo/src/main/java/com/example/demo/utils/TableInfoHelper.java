package com.example.demo.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TableInfoHelper {
    private static final Log                      logger           = LogFactory.getLog(com.baomidou.mybatisplus.core.toolkit.TableInfoHelper.class);
    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap();
    private static final String                   DEFAULT_ID_NAME  = "id";

    public TableInfoHelper() {
    }

    public static TableInfo getTableInfo(Class<?> clazz) {
        if (clazz != null && !ReflectionKit.isPrimitiveOrWrapper(clazz) && clazz != String.class) {
            TableInfo tableInfo = (TableInfo) TABLE_INFO_CACHE.get(ClassUtils.getUserClass(clazz));
            if (null != tableInfo) {
                return tableInfo;
            } else {
                for (Class currentClass = clazz; null == tableInfo && Object.class != currentClass; tableInfo = (TableInfo) TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass))) {
                    currentClass = currentClass.getSuperclass();
                }

                if (tableInfo != null) {
                    TABLE_INFO_CACHE.put(ClassUtils.getUserClass(clazz), tableInfo);
                }

                return tableInfo;
            }
        } else {
            return null;
        }
    }

    public static List<TableInfo> getTableInfos() {
        return new ArrayList(TABLE_INFO_CACHE.values());
    }

    public static synchronized BaseTableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        BaseTableInfo tableInfo = (BaseTableInfo) TABLE_INFO_CACHE.get(clazz);
        if (tableInfo != null) {
            if (builderAssistant != null) {
                tableInfo.setConfiguration(builderAssistant.getConfiguration());
            }

            tableInfo.tableClass = clazz.getSimpleName().toLowerCase();
            tableInfo.setResultColumnMap();
            return tableInfo;
        } else {
            tableInfo = new BaseTableInfo();
            GlobalConfig globalConfig;
            if (null != builderAssistant) {
                tableInfo.setCurrentNamespace(builderAssistant.getCurrentNamespace());
                tableInfo.setConfiguration(builderAssistant.getConfiguration());
                tableInfo.setUnderCamel(builderAssistant.getConfiguration().isMapUnderscoreToCamelCase());
                globalConfig = GlobalConfigUtils.getGlobalConfig(builderAssistant.getConfiguration());
            } else {
                globalConfig = GlobalConfigUtils.defaults();
            }

            initTableName(clazz, globalConfig, tableInfo);
            initTableFields(clazz, globalConfig, tableInfo);
            TABLE_INFO_CACHE.put(clazz, tableInfo);
            tableInfo.tableClass = clazz.getSimpleName().toLowerCase();
            tableInfo.setResultColumnMap();
            return tableInfo;
        }
    }

    private static void initTableName(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo) {
        DbConfig  dbConfig          = globalConfig.getDbConfig();
        TableName table             = (TableName) clazz.getAnnotation(TableName.class);
        String    tableName         = clazz.getSimpleName();
        String    tablePrefix       = dbConfig.getTablePrefix();
        String    schema            = dbConfig.getSchema();
        boolean   tablePrefixEffect = true;
        if (table != null) {
            if (StringUtils.isNotEmpty(table.value())) {
                tableName = table.value();
                if (StringUtils.isNotEmpty(tablePrefix) && !table.keepGlobalPrefix()) {
                    tablePrefixEffect = false;
                }
            } else {
                tableName = initTableNameWithDbConfig(tableName, dbConfig);
            }

            if (StringUtils.isNotEmpty(table.schema())) {
                schema = table.schema();
            }

            if (StringUtils.isNotEmpty(table.resultMap())) {
                tableInfo.setResultMap(table.resultMap());
            }
        } else {
            tableName = initTableNameWithDbConfig(tableName, dbConfig);
        }

        String targetTableName = tableName;
        if (StringUtils.isNotEmpty(tablePrefix) && tablePrefixEffect) {
            targetTableName = tablePrefix + tableName;
        }

        if (StringUtils.isNotEmpty(schema)) {
            targetTableName = schema + "." + targetTableName;
        }

        tableInfo.setTableName(targetTableName);
        if (null != dbConfig.getKeyGenerator()) {
            tableInfo.setKeySequence((KeySequence) clazz.getAnnotation(KeySequence.class));
        }
    }

    private static String initTableNameWithDbConfig(String className, DbConfig dbConfig) {
        String tableName = className;
        if (dbConfig.isTableUnderline()) {
            tableName = StringUtils.camelToUnderline(className);
        }

        if (dbConfig.isCapitalMode()) {
            tableName = tableName.toUpperCase();
        } else {
            tableName = StringUtils.firstToLowerCase(tableName);
        }

        return tableName;
    }

    public static void initTableFields(Class<?> clazz, GlobalConfig globalConfig, BaseTableInfo tableInfo) {
        DbConfig             dbConfig     = globalConfig.getDbConfig();
        List<Field>          list         = getAllFields(clazz);
        boolean              isReadPK     = false;
        boolean              existTableId = isExistTableId(list);
        List<TableFieldInfo> fieldList    = new ArrayList();
        Iterator             var8         = list.iterator();

        while (true) {
            Field field;
            do {
                if (!var8.hasNext()) {
                    Assert.isTrue(fieldList.parallelStream().filter(TableFieldInfo::isLogicDelete).count() < 2L, String.format("annotation of @TableLogic can't more than one in class : %s.", clazz.getName()), new Object[0]);
                    tableInfo.setFieldList(fieldList);
                    if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                        logger.warn(String.format("Warn: Could not find @TableId in Class: %s.", clazz.getName()));
                    }

                    return;
                }

                field = (Field) var8.next();
                if (isReadPK) {
                    break;
                }

                if (existTableId) {
                    isReadPK = initTableIdWithAnnotation(dbConfig, tableInfo, field, clazz);
                } else {
                    isReadPK = initTableIdWithoutAnnotation(dbConfig, tableInfo, field, clazz);
                }
            } while (isReadPK);

            if (!initTableFieldWithAnnotation(dbConfig, tableInfo, fieldList, field, clazz)) {
                fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field));
            }
        }
    }

    public static boolean isExistTableId(List<Field> list) {
        Iterator var1 = list.iterator();

        TableId tableId;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            Field field = (Field) var1.next();
            tableId = (TableId) field.getAnnotation(TableId.class);
        } while (tableId == null);

        return true;
    }

    private static boolean initTableIdWithAnnotation(DbConfig dbConfig, BaseTableInfo tableInfo, Field field, Class<?> clazz) {
        TableId tableId    = (TableId) field.getAnnotation(TableId.class);
        boolean underCamel = tableInfo.isUnderCamel();
        if (tableId != null) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
//                tableInfo.setKeyType(tableId.type().getKey());
                if (IdType.NONE == tableId.type()) {
                    tableInfo.setIdType(dbConfig.getIdType());
                } else {
                    tableInfo.setIdType(tableId.type());
                }

                String column = field.getName();
                if (StringUtils.isNotEmpty(tableId.value())) {
                    column = tableId.value();
                } else {
                    if (underCamel) {
                        column = StringUtils.camelToUnderline(column);
                    }

                    if (dbConfig.isCapitalMode()) {
                        column = column.toUpperCase();
                    }
                }

                tableInfo.setKeyRelated(checkRelated(underCamel, field.getName(), column)).setKeyColumn(column).setKeyProperty(field.getName());
                return true;
            }

            throwExceptionId(clazz);
        }

        return false;
    }

    private static boolean initTableIdWithoutAnnotation(DbConfig dbConfig, TableInfo tableInfo, Field field, Class<?> clazz) {
        String column = field.getName();
        if (dbConfig.isCapitalMode()) {
            column = column.toUpperCase();
        }

        if ("id".equalsIgnoreCase(column)) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                tableInfo.setKeyRelated(checkRelated(tableInfo.isUnderCamel(), field.getName(), column)).setIdType(dbConfig.getIdType()).setKeyColumn(column).setKeyProperty(field.getName());
                return true;
            }

            throwExceptionId(clazz);
        }

        return false;
    }

    private static boolean initTableFieldWithAnnotation(DbConfig dbConfig, TableInfo tableInfo, List<TableFieldInfo> fieldList, Field field, Class<?> clazz) {
        TableField tableField = (TableField) field.getAnnotation(TableField.class);
        if (null == tableField) {
            return false;
        } else {
            String  columnName               = field.getName();
            boolean columnNameFromTableField = false;
            if (StringUtils.isNotEmpty(tableField.value())) {
                columnName = tableField.value();
                columnNameFromTableField = true;
            }

            String el = field.getName();
            if (StringUtils.isNotEmpty(tableField.el())) {
                el = tableField.el();
            }

            String[] columns      = columnName.split(";");
            String   columnFormat = dbConfig.getColumnFormat();
            if (StringUtils.isNotEmpty(columnFormat) && (!columnNameFromTableField || tableField.keepGlobalFormat())) {
                for (int i = 0; i < columns.length; ++i) {
                    String column = columns[i];
                    column = String.format(columnFormat, column);
                    columns[i] = column;
                }
            }

            String[] els = el.split(";");
            if (columns.length != els.length) {
                throw ExceptionUtils.mpe("Class: %s, Field: %s, 'value' 'el' Length must be consistent.", new Object[]{clazz.getName(), field.getName()});
            } else {
                for (int i = 0; i < columns.length; ++i) {
                    fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field, columns[i], els[i], tableField));
                }

                return true;
            }
        }
    }

    public static boolean checkRelated(boolean underCamel, String property, String column) {
        if (StringUtils.isNotColumnName(column)) {
            column = column.substring(1, column.length() - 1);
        }

        String propertyUpper = property.toUpperCase(Locale.ENGLISH);
        String columnUpper   = column.toUpperCase(Locale.ENGLISH);
        if (!underCamel) {
            return !propertyUpper.equals(columnUpper);
        } else {
            return !propertyUpper.equals(columnUpper) && !propertyUpper.equals(columnUpper.replace("_", ""));
        }
    }

    private static void throwExceptionId(Class<?> clazz) {
        throw ExceptionUtils.mpe("There must be only one, Discover multiple @TableId annotation in %s", new Object[]{clazz.getName()});
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
        return CollectionUtils.isNotEmpty(fieldList) ? (List) fieldList.stream().filter((i) -> {
            TableField tableField = (TableField) i.getAnnotation(TableField.class);
            return tableField == null || tableField.exist();
        }).collect(Collectors.toList()) : fieldList;
    }

    public static KeyGenerator genKeyGenerator(TableInfo tableInfo, MapperBuilderAssistant builderAssistant, String baseStatementId, LanguageDriver languageDriver) {
        IKeyGenerator keyGenerator = GlobalConfigUtils.getKeyGenerator(builderAssistant.getConfiguration());
        if (null == keyGenerator) {
            throw new IllegalArgumentException("not configure IKeyGenerator implementation class.");
        } else {
            String        id              = baseStatementId + "!selectKey";
            Class<?>      resultTypeClass = tableInfo.getKeySequence().clazz();
            StatementType statementType   = StatementType.PREPARED;
            String        keyProperty     = tableInfo.getKeyProperty();
            String        keyColumn       = tableInfo.getKeyColumn();
            SqlSource     sqlSource       = languageDriver.createSqlSource(builderAssistant.getConfiguration(), keyGenerator.executeSql(tableInfo.getKeySequence().value()), (Class) null);
            builderAssistant.addMappedStatement(id, sqlSource, statementType, SqlCommandType.SELECT, (Integer) null, (Integer) null, (String) null, (Class) null, (String) null, resultTypeClass, (ResultSetType) null, false, false, false, new NoKeyGenerator(), keyProperty, keyColumn, (String) null, languageDriver, (String) null);
            id = builderAssistant.applyCurrentNamespace(id, false);
            MappedStatement    keyStatement       = builderAssistant.getConfiguration().getMappedStatement(id, false);
            SelectKeyGenerator selectKeyGenerator = new SelectKeyGenerator(keyStatement, true);
            builderAssistant.getConfiguration().addKeyGenerator(id, selectKeyGenerator);
            return selectKeyGenerator;
        }
    }
}
