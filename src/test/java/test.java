import com.aliyun.asapi.ASClient;

public class test {
    public static void main(String[] args) {
        ASClient asclient = new ASClient();
        asclient.addHeader("a","1");

        System.out.println("1111");

    }
}
