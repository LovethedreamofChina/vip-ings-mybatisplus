package www.ings.vip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import www.ings.vip.utils.BaseQueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
            o = c.newInstance();
        } catch (IllegalAccessException | InstantiationException var3) {
            var3.printStackTrace();
        }

        t.put("tClass", c);
        t.put("tables", o);
        return o;
    }

    @Select("<script>" +
            "select ${ew.paramNameValuePairs.cr} from ${ew.paramNameValuePairs.table} " +
            "${ew.customSqlSegment} " +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "${ew.paramNameValuePairs.eqTable} ${ew.paramNameValuePairs.by}" +
            "</script>")
    Map<String, Object> getObj(@Param("ew") BaseQueryWrapper queryWrapper);


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
    List<Map<String, Object>> getObjList(@Param("ew") BaseQueryWrapper queryWrapper);

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
    int getUpdateObj(@Param("ew") BaseQueryWrapper queryWrapper);
}
