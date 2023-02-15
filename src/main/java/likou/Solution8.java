package likou;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: BYDylan
 * @date: 2022/11/1
 * @description: 字符串转换整数 (atoi) 请你来实现一个myAtoi(string s)函数，使其能将字符串转换成一个 32 位有符号整数（类似 C/C++ 中的 atoi 函数）。 函数myAtoi(string
 *               s) 的算法如下： 读入字符串并丢弃无用的前导空格 检查下一个字符（假设还未到字符末尾）为正还是负号，读取该字符（如果有）。 确定最终结果是负数还是正数。 如果两者都不存在，则假定结果为正。
 *               读入下一个字符，直到到达下一个非数字字符或到达输入的结尾。字符串的其余部分将被忽略。 将前面步骤读入的这些数字转换为整数（即，"123" -> 123， "0032" ->
 *               32）。如果没有读入数字，则整数为 0 。必要时更改符号（从步骤 2 开始）。 如果整数数超过 32 位有符号整数范围 [−231, 231− 1] ，需要截断这个整数，使其保持在这个范围内。具体来说，小于
 *               −231 的整数应该被固定为 −231 ，大于 231− 1 的整数应该被固定为 231− 1 。 返回整数作为最终结果。 注意： 本题中的空白字符只包括空格字符 ' ' 。
 *               除前导空格或数字后的其余字符串外，请勿忽略 任何其他字符。
 *               <p>
 *               示例1：
 *               <p>
 *               输入：s = "42" 输出：42 解释：加粗的字符串为已经读入的字符，插入符号是当前读取的字符。 第 1 步："42"（当前没有读入字符，因为没有前导空格） ^ 第 2
 *               步："42"（当前没有读入字符，因为这里不存在 '-' 或者 '+'） ^ 第 3 步："42"（读入 "42"） ^ 解析得到整数 42 。 由于 "42" 在范围 [-231, 231 - 1]
 *               内，最终结果为 42
 */
public class Solution8 {
    public int myAtoi(String str) {
        Automaton automaton = new Automaton();
        int length = str.length();
        for (int i = 0; i < length; ++i) {
            automaton.get(str.charAt(i));
        }
        return (int)(automaton.sign * automaton.ans);
    }
}

class Automaton {
    int sign = 1;
    long ans = 0;
    private String state = "start";
    private Map<String, String[]> table = new HashMap<>() {
        {
            put("start", new String[] {"start", "signed", "in_number", "end"});
            put("signed", new String[] {"end", "end", "in_number", "end"});
            put("in_number", new String[] {"end", "end", "in_number", "end"});
            put("end", new String[] {"end", "end", "end", "end"});
        }
    };

    public void get(char c) {
        state = table.get(state)[get_col(c)];
        if ("in_number".equals(state)) {
            ans = ans * 10 + c - '0';
            ans = sign == 1 ? Math.min(ans, Integer.MAX_VALUE) : Math.min(ans, -(long)Integer.MIN_VALUE);
        } else if ("signed".equals(state)) {
            sign = c == '+' ? 1 : -1;
        }
    }

    private int get_col(char c) {
        if (c == ' ') {
            return 0;
        }
        if (c == '+' || c == '-') {
            return 1;
        }
        if (Character.isDigit(c)) {
            return 2;
        }
        return 3;
    }
}