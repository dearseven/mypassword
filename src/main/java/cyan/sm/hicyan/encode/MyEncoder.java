package cyan.sm.hicyan.encode;

import java.util.ArrayList;

/**
 * Created by apple on 16/7/25.
 */
public class MyEncoder {
    public static String Encode(String str) {
        int size=1024;
        int times=10;
        if (str.length() > size) {
            return null;
        }
        StringBuffer sb=new StringBuffer(size);
        for (int i=str.length();i<size;i++){
            sb.append("0");
        }
        sb.append(str);

        String src=sb.toString();
        sb.setLength(0);

        for (int i=0;i<times;i++){
            int off= 0;
            int posValue=(int) Math.pow(2,i);
            //int start=0;
            while(off<size){
                //System.out.println(off+" "+posValue );
                //System.out.println(src.substring(off,off+posValue) );
                sb.append(src.substring(off,off+posValue));
                off+=posValue;
            }
            src=sb.toString();
            System.out.println(src);
            //if(i<times-1)
            sb.setLength(0);
        }
        System.out.println(src);
        return null;
    }
}
