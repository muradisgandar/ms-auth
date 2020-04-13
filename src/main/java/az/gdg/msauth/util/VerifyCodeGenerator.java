package az.gdg.msauth.util;

import java.util.Random;

public class VerifyCodeGenerator {

    public static String generateCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 46;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}
