package me.falcon.bpext;

import java.util.Random;

public class RandomIP {
    
    public static String getRandomIpStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Random random = new Random(System.currentTimeMillis() - 1 + i);
            sb.append(random.nextInt(255) + ".");
        }

        int len = sb.length();
        sb.deleteCharAt(len - 1);
        return sb.toString();
    }

    //Test
    public static void main(String[] args) throws InterruptedException {
        int testCnt = 10;
        for (int i = 0; i < testCnt; i++) {
            System.out.println("[" + i + "]: " + RandomIP.getRandomIpStr());
            Thread.sleep(100);
        }
    }
}
