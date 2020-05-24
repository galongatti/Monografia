
import com.google.gson.GsonBuilder;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil {

    //Aplica o algoritimo SHA 256 em uma informação
    public static String aplicarSha256(String entrada) {
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
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {

            throw new RuntimeException(e);
        }

    }

    public static byte[] aplicarECDSASig(PrivateKey chavePrivada, String input) {

        try {
            Signature dsa;
            byte[] output = new byte[0];

            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(chavePrivada);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSign = dsa.sign();
            output = realSign;

            return (output);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new RuntimeException(e);
        }

    }

    //Verifies a String signature 
    public static boolean verificarECDSASig(PublicKey chavePublica, String data, byte[] signature) {

        try {
            Signature ecdsaVerificar = Signature.getInstance("ECDSA", "BC");
            ecdsaVerificar.initVerify(chavePublica);
            ecdsaVerificar.update(data.getBytes());            
            return ecdsaVerificar.verify(signature);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getStringDaChave(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getRaizMerkle(ArrayList<Transacao> transacoes) {
        int count = transacoes.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for (Transacao transacao : transacoes) {
            previousTreeLayer.add(transacao.idTransacao);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<String>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(aplicarSha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

    public static String getStringDificuldade(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    public static String gerarJson(Object o) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }

}
