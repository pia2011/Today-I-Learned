import java.util.*;
public class EqualsAndHashCode {

    private static Map<Info, Long> hm;
    private static Set<Info> hs;
    public static void main(String[] args) {
        hm = new HashMap<>();
        Info info1 = Info.of("윤아");
        Info info2 = Info.of("윤기");

        hm.put(info1, 3L);
        hm.put(info2, 4L);

        System.out.println(hm.size());

        hs = new HashSet<>();

        boolean result = info1.equals(info2);
        System.out.println("info1와 info2는 같은가요? : "+result);

        hs.add(info1);
        hs.add(info2);

        System.out.println(hs.size());
    }

    static class Info{
        String name;

        private Info(String name){
            this.name = name;
        }

        static Info of(String name){
            return new Info(name);
        }

        @Override
        public boolean equals(Object obj) {
            Info info = (Info) obj;
//            return Objects.equals(info.name, this.name);
            return Objects.equals(info.name.substring(0,1), this.name.substring(0,1));
        }

        @Override
        public int hashCode() {
//            return Objects.hash(name);
            return name.length();
        }
    }
}
