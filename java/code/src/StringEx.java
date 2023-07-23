public class StringEx {
    public static void main(String[] args) {
        String str1 = "abc";
        String str2 = "abc";
        String str3 = new String("abc");
        String str4 = new String("abc");

        System.out.println("str1 과 str2 는 동등한가 ? " + str1.equals(str2));
        System.out.println("str3 과 str4 는 동등한가 ? " + str3.equals(str4));

        System.out.println("str1 과 str2 는 동일한가 ? " + ( str1 == str2 ));
        System.out.println("str3 과 str4 는 동일한가 ? " + ( str3 == str4 ));
    }
}
