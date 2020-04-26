
import java.security.MessageDigest;

public class StringUtil {

    //Aplica o algoritimo SHA 256 em uma informação
    public static String applySha256(String entrada) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(entrada.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); //Armazenar o hash em hexadecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
