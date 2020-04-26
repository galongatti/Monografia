
import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Monografia {

    public static ArrayList<Index> blockchain = new ArrayList<Index>();
    public static int difficulty = 2;

    public static void main(String[] args) {
        //add our blocks to the blockchain ArrayList:
        blockchain.add(new Index("Olá, sou o bloco genesis", "0"));
        System.out.println("Tentando minerar o bloco genesis");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Index("Olá, sou o bloco 2", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Tentando minerar o bloco 2");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Index("Olá, sou o bloco 3", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Tentando minerar o bloco 3");
        blockchain.get(2).mineBlock(difficulty);

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }

    public static Boolean validacao() {
        Index blocoAtual;
        Index blocoAnterior;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockchain.size(); i++) {
            blocoAtual = blockchain.get(i);
            blocoAnterior = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!blocoAtual.hash.equals(blocoAtual.gerarHash())) {
                System.out.println("Hash do bloco atual não é valido");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!blocoAnterior.hash.equals(blocoAtual.gerarHash())) {
                System.out.println("Hash do bloco atual não é valido");
                return false;
            }
            //check if hash is solved
            if (!blocoAtual.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("Esse bloco não é valido");
                return false;
            }
        }
        return true;
    }

}
