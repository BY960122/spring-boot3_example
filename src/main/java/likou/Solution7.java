package likou;

/**
 * @author: BYDylan
 * @date: 2022/10/26
 * @description: 整数反转 给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。 如果反转后整数超过 32 位的有符号整数的范围[−231, 231− 1] ，就返回 0。 假设环境不允许存储 64
 *               位整数（有符号或无符号）。 示例 1： 输入：x = 123 输出：321 示例 2： 输入：x = -123 输出：-321
 */
public class Solution7 {
    public int reverse(int x) {
        int rev = 0;
        while (x != 0) {
            if (rev < Integer.MIN_VALUE / 10 || rev > Integer.MAX_VALUE / 10) {
                return 0;
            }
            int digit = x % 10;
            x /= 10;
            rev = rev * 10 + digit;
        }
        return rev;
    }
}
