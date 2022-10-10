package www.ings.vip.utils;

import www.ings.vip.table.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author ljr
 * @date 2022-09-23 17:15
 */
public class TableClass {

    static Map<String, Object> map = new Hashtable<>();

    public static void main(String[] a) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Table table = new Table();

//        Class c2 = Class.forName(“com.reflection.Test”);

        Map<String, Object> ms  = new HashMap<>();
        Map<String, String> cp  = new HashMap<>();
        Map<String, Class>  cc  = new HashMap<>();
        Map<String, String>  cs  = new HashMap<>();


        map = getClassTable(table, ms, cp, cc);
        System.out.println(ms.toString());
        System.out.println("");
        System.out.println(cp);
        System.out.println("");
        getWl(map, cs);
        System.out.println(cs.toString());
    }

    public static void getWl(Map<String, Object> map, Map<String, String> cs) {
        StringBuilder wl  = new StringBuilder();
        int           jcc = map.keySet().size();
        int           ji  = 0;
        for (String k : map.keySet()) {
            Map<String, String> m  = (Map<String, String>) map.get(k);
            int                 jc = m.keySet().size();
            int                 j  = 0;
            ji++;
            for (String i : m.keySet()) {
                j++;
                String b = m.get(i);
                String a = b.replace("$", ".");
                cs.put(a, a+" as "+b);
                wl.append(a).append(" as ").append(b);
                if (!(jcc == ji && j == jc)) {
                    wl.append(",");
                }
            }
            cs.put(k, wl.toString());
        }
    }

    protected static Map<String, Object> getResMap(Map<String, Object> map, Map<String, Object> ms) {
        Map<String, Object> m = new HashMap<>();
        for (String t : map.keySet()) {
            Map<String, String> p = (Map<String, String>) map.get(t);
            Map<String, String> i = new HashMap<>();
            for (String c : p.keySet()) {
                String v = p.get(c).replace(".", "$");
                i.put(c, v);
            }
            m.put(t, i);
        }


        Map<String, Object> mis = new HashMap<>();
        for (String t : ms.keySet()) {
            Map<String, String> p = (Map<String, String>) ms.get(t);
            for (String c : p.keySet()) {
                String v = p.get(c).replace(".", "$");
                mis.put(c, v);
            }
        }

        ms.putAll(mis);
        return m;
    }


    private static String getUpCaseAndReverse(String str) {
        StringBuilder buffer = new StringBuilder();
        char[]        ch     = str.toCharArray();
        String        start  = str.substring(0, 1);
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] >= 'A' && ch[i] <= 'Z') {
                if (start.equals(String.valueOf(ch[i]))) {
                    buffer.append(String.valueOf(ch[i]).toLowerCase());
                } else {
                    buffer.append("_").append(String.valueOf(ch[i]).toLowerCase());
                }
            } else {
                buffer.append(ch[i]);
            }
        }
        return buffer.toString();
    }

    private static void testReflect(Object o, Map<String, Object> m, Map<String, Object> methods, Map<String, String> cp, Map<String, Class> cc) throws IllegalAccessException, InstantiationException {
        for (Field field : o.getClass().getDeclaredFields()) {

            boolean v = false;
            for (Annotation a : field.getAnnotations()) {
                v = true;
                break;
            }

            if (v) {
                continue;
            }

            String simpleName = field.getType().getSimpleName();

            field.setAccessible(true);
            if ("Map".equals(simpleName)) {
                continue;
            }
            Object              c = field.getType().newInstance();
            String              t = getUpCaseAndReverse(field.getName());
            Map<String, String> p = new HashMap<>();
            Map<String, String> h = new HashMap<>();
            for (Field fieldItem : c.getClass().getDeclaredFields()) {
                for (Annotation a : fieldItem.getAnnotations()) {
                    if (a.annotationType().toString().contains("com.baomidou.mybatisplus.annotation.TableField")) {
                        v = true;
                        break;
                    }
                }

                if (v) {
                    continue;
                }

                fieldItem.setAccessible(true);
                String s          = getUpCaseAndReverse(fieldItem.getName());
                String methodName = getMethodName(s);
                String name       = field.getName() + "$" + s;

                h.put(name, methodName);
                p.put(s, t + "." + s);
                cc.put(name, fieldItem.getType());
            }

            cp.put("table_"+simpleName, t);
            cp.put(getMethodName(field.getName()), field.getType().getName());
            methods.put(field.getName(), h);
            m.put(t, p);
        }
    }

    protected static Map<String, Object> getClassTable(Object t, Map<String, Object> ms, Map<String, String> cp, Map<String, Class> cc) throws InstantiationException, IllegalAccessException {
        Map<String, Object> m = new HashMap<>();
        testReflect(t, m, ms, cp, cc);
        return m;
    }

    protected static String getMethodName(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    protected static Map<String, String> getTableClass(Map<String, Object> map) {
        Map<String, String> t = new HashMap<>();
        for (String k : map.keySet()) {
            String i = k;
            k = k.split("\\$")[0];
            k = getMethodName(k);
            t.put(i, k);
        }

        return t;
    }

    protected static String getWlSql(Map<String, String> m, String... c) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < c.length; i++) {
            String s = c[i];
            b.append(m.get(s));
            if(i != c.length - 1) {
                b.append(",");
            }
        }

        return b.toString();
    }

    static String lowerFirst(String str) {
        // 同理
        char[] cs=str.toCharArray();
        cs[0]+=32;
        return String.valueOf(cs);
    }
}
