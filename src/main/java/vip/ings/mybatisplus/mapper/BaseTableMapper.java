package vip.ings.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import vip.ings.mybatisplus.base.BaseQueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ljr
 * @date 2022-09-27 15:52
 */
public interface BaseTableMapper<T> extends BaseMapper<T> {

    Map<String, Object> t = new HashMap<>();

    static Object getT(Class c) {
        Object o = null;

        try {
            o = c.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("初始化方法 BaseTableMapper.getT 失败!", e);
        }

        t.put("tClass", c);
        t.put("tables", o);
        return o;
    }

    /**
     * 获取结果Map
     * @param queryWrapper
     * @return
     */
    @Select("<script>" +
            "select ${ew.paramNameValuePairs.cr} from ${ew.paramNameValuePairs.table} " +
            "${ew.customSqlSegment} " +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "${ew.paramNameValuePairs.eqTable} ${ew.paramNameValuePairs.by}" +
            "</script>")
    Map<String, Object> getObjs(@Param("ew") BaseQueryWrapper queryWrapper);

    /**
     * 获取Map列表
     * @param queryWrapper
     * @return
     */
    @Select("<script>" +
            "select ${ew.paramNameValuePairs.cr} from ${ew.paramNameValuePairs.table} " +
            "${ew.customSqlSegment} " +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "<if test='ew.customSqlSegment != null and ew.customSqlSegment != \"\"'>" +
            "and " +
            "</if>" +
            "${ew.paramNameValuePairs.eqTable} ${ew.paramNameValuePairs.by}" +
            "</script>")
    List<Map<String, Object>> getObjLists(@Param("ew") BaseQueryWrapper queryWrapper);

    /**
     * 更新
     * @param queryWrapper
     * @return
     */
    @Update("<script>" +
            "update ${ew.paramNameValuePairs.table} set ${ew.paramNameValuePairs.cr} ${ew.customSqlSegment}" +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "<if test='ew.customSqlSegment != null and ew.customSqlSegment != \"\"'>" +
            "and " +
            "</if>" +
            " ${ew.paramNameValuePairs.eqTable}" +
            "</script>")
    int updates(@Param("ew") BaseQueryWrapper queryWrapper);

    /**
     * 删除
     * @param queryWrapper
     * @return
     */
    @Delete("<script>" +
            "delete ${ew.paramNameValuePairs.table} from ${ew.paramNameValuePairs.table} ${ew.customSqlSegment}" +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "<if test='ew.customSqlSegment != null and ew.customSqlSegment != \"\"'>" +
            "and " +
            "</if>" +
            " ${ew.paramNameValuePairs.eqTable}" +
            "</script>")
    int deletes(@Param("ew") BaseQueryWrapper queryWrapper);
}
