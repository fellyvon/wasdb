package com.waspring.wasdb.util;

/**
 * 字符常量處理
 * @author felly
 *
 */
public class StringUtils {

	
	/**
	 * 字符比較，支持編碼格式比較
	 * @param s
	 * @param s1
	 * @param s2
	 * @return
	 */
	// ~ Static fields/initializers
	// =============================================
	public static int compareString(String s, String s1, String s2) {
		if (s1 == null)
			return s == null ? 0 : 1;
		if (s == null)
			return -1;
		byte abyte0[];
		byte abyte1[];
		int i;
		int j;
		int l;
		int i1;
		try {
			abyte0 = s.getBytes(s2);
			abyte1 = s1.getBytes(s2);
			i = abyte0.length;
			j = abyte1.length;
			int k = Math.min(i, j);
			l = 0;
			i1 = k;
			while (l < i1) {

				int j1 = abyte0[l] & 255;
				int k1 = abyte1[l] & 255;
				if (j1 != k1)
					return j1 - k1;
				l++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

		return i - j;

	}
}
