package com.example.demo.table;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author ljr
 * @date 2022-09-23 11:15
 */
@Data
public class Table {
    private TestTable testTable;

    private TestLabel testLabel;

    static Table getInstance() {
        return null;
    }
}
