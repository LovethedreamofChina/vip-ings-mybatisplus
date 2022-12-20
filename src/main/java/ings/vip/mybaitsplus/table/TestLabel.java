package ings.vip.mybaitsplus.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-23 10:13
 */
@Data
@TableName("test_label")
public class TestLabel {
    private int id;
    private String name;

   @TableField(exist = false)
    private String label;

    public static int A() {
        return 0;
    }
}
