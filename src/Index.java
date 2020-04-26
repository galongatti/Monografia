
import java.util.Date;

public class Index {

    public String hash; // Hash do propio bloco
    public String hashAnterior; // Hash do bloco anterior
    private String informacao; // Informação que será armazenda
    private long timeStamp; // Data do bloco
    private int nonce;

    //Contrutor do bloco
    public Index(String informacao, String hashAnterior) {
        this.informacao = informacao;
        this.hashAnterior = hashAnterior;
        this.timeStamp = new Date().getTime();
        this.hash = gerarHash();

    }

    public String gerarHash() {
        String hashCalculado = StringUtil.applySha256(
                this.hashAnterior
                + Long.toString(this.timeStamp)
                + Integer.toString(nonce)
                + this.informacao
        );
        return hashCalculado;
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Cria uma string com dificuldade * "0" 
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = gerarHash();
        }
        System.out.println("Block minerado!!! : " + hash);
    }

}
