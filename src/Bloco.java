
import java.util.ArrayList;
import java.util.Date;

public class Bloco {

    public String hash; // Hash do propio bloco
    public String hashAnterior; // Hash do bloco anterior
    public String merkleRoot;
    private long timeStamp; // Data do bloco
    private int nonce;
    public ArrayList<Transacao> transacoes = new ArrayList<Transacao>();

    //Contrutor do bloco
    public Bloco(String hashAnterior) {
        this.hashAnterior = hashAnterior;
        this.timeStamp = new Date().getTime();
        this.hash = gerarHash();

    }

    public String gerarHash() {
        String hashCalculado = StringUtil.aplicarSha256(
                hashAnterior
                + Long.toString(timeStamp)
                + Integer.toString(nonce)
                + merkleRoot
        );
        return hashCalculado;
    }

    public void minerarBloco(int difficulty) {
        merkleRoot = StringUtil.getRaizMerkle(transacoes);
        String target = StringUtil.getStringDificuldade(difficulty); //Create a string with difficulty * "0" 
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = gerarHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public boolean adicionarTransacao(Transacao transacao) {
        if (transacao == null) {
            return false;
        }

        if ((!"0".equals(hashAnterior))) {
            if ((transacao.processaTransacao() != true)) {
                System.out.println("Erro processar a transação.");
                return false;
            }
        }
        transacoes.add(transacao);
        System.out.println("Transação adicionada ao bloco com sucesso!");
        return true;
    }

   

}
