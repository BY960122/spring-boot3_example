package likou;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: BYDylan
 * @date: 2022/10/25
 * @description: N 字形变换 将一个给定字符串 s 根据给定的行数 numRows ,以从上往下、从左到右进行Z 字形排列。 比如输入字符串为 "PAYPALISHIRING"行数为 3 时,排列如下： P A H N A
 *               P L S I I G Y I R 之后,你的输出需要从左往右逐行读取,产生出一个新的字符串,比如："PAHNAPLSIIGYIR"。 示例 1：
 *               <p>
 *               输入：s = "PAYPALISHIRING", numRows = 3 输出："PAHNAPLSIIGYIR"
 */
public class Solution6 {
    public String convert(String s, int numRows) {
        if (numRows == 1)
            return s;
        List<StringBuilder> rows = new ArrayList<>();
        for (int i = 0; i < Math.min(numRows, s.length()); i++)
            rows.add(new StringBuilder());
        int curRow = 0;
        int flag = -1;
        for (char c : s.toCharArray()) {
            rows.get(curRow).append(c);
            // 到两端了换方向
            if (curRow == 0 || curRow == numRows - 1) {
                flag = -flag;
            }
            curRow += flag;
        }
        StringBuilder ret = new StringBuilder();
        for (StringBuilder row : rows) {
            ret.append(row);
        }
        return ret.toString();
    }
}
