package cyan.sm.hicyan.db;

/**
 * Created by Administrator on 2016/8/9.
 */
public class EnDe {
    public static String en(String src) {
        StringBuffer dist = new StringBuffer(src.length());
        for (int i = 0; i < src.length(); i++) {
            int c = src.charAt(i);
            String bs = Integer.toBinaryString(c);
            while(bs.length()<8){
                bs=0+bs;
            }
            String after = bs.substring(4) + bs.substring(0, 4);
            char newC = (char) (Integer.parseInt(after, 2) ^ 0x9F);
            dist.append(newC);
        }
        return dist.toString();
    }

    public static String de(String dist) {
        StringBuffer src = new StringBuffer(dist.length());
        for (int i = 0; i < dist.length(); i++) {
            int c = dist.charAt(i);
            char newC = (char) (c ^ 0x9F );
            String bs= Integer.toBinaryString(newC);
            while(bs.length()<8){
                bs=0+bs;
            }
            String after = bs.substring(4) + bs.substring(0, 4);
            src.append((char)Integer.parseInt(after,2));
        }
        return src.toString();
    }

    public static void main(String[] args) {
        String dist = en("abcdefc!.,123");
        System.out.println(dist);
        dist=de(dist);
        System.out.println(dist);
    }
}
