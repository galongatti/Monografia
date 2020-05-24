import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.HashMap;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BRChain {

    public static ArrayList<Bloco> blockchain = new ArrayList<Bloco>();
    public static HashMap<String, OutputTransacao> UTXOs = new HashMap<String, OutputTransacao>();

    public static int difficulty = 2;
    public static float minimumTransaction = 0.1f;
    public static Carteira carteiraA;
    public static Carteira carteiraB;
    public static Carteira carteiraC;
    public static Transacao genesisTransaction;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        carteiraA = new Carteira();
        carteiraB = new Carteira();
        carteiraC = new Carteira();
        Carteira coinbase = new Carteira();

        //Cria a transação genesis, no qual envia 10000 criptomoedas para carteiraA: 
        genesisTransaction = new Transacao(coinbase.chavePublica, carteiraA.chavePublica, 10000f, null);
        genesisTransaction.gerarAssinatura(coinbase.chavePrivada);	 //manually sign the genesis transaction	
        genesisTransaction.idTransacao = "0"; //manually set the transaction id
        genesisTransaction.saidas.add(new OutputTransacao(genesisTransaction.destino, genesisTransaction.valor, genesisTransaction.idTransacao)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.saidas.get(0).id, genesisTransaction.saidas.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Bloco genesis = new Bloco("0");
        genesis.adicionarTransacao(genesisTransaction);
        addBlock(genesis);

        //testing
        Bloco bloco1 = new Bloco(genesis.hash);
        System.out.println("\nWalletA's balance is: " + carteiraA.getBalanco());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        bloco1.adicionarTransacao(carteiraA.enviarFundos(carteiraB.chavePublica, 40f));     
        
        addBlock(bloco1);
        System.out.println("\nWalletA's balance is: " + carteiraA.getBalanco());
        System.out.println("WalletB's balance is: " + carteiraB.getBalanco());
     
  

        Bloco bloco2 = new Bloco(bloco1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        bloco2.adicionarTransacao(carteiraB.enviarFundos(carteiraC.chavePublica, 20));
        addBlock(bloco2);
        System.out.println("\nWalletB's balance is: " + carteiraB.getBalanco());
        System.out.println("WalletC's balance is: " + carteiraC.getBalanco());       

        validacao();

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);

    }

    public static Boolean validacao() {
        Bloco blocoAtual;
        Bloco blocoAnterior;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, OutputTransacao> tempUTXOs = new HashMap<String, OutputTransacao>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.saidas.get(0).id, genesisTransaction.saidas.get(0));

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {

            blocoAtual = blockchain.get(i);
            blocoAnterior = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!blocoAtual.hash.equals(blocoAtual.gerarHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!blocoAnterior.hash.equals(blocoAtual.hashAnterior)) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!blocoAtual.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            OutputTransacao tempOutput;
            for (int t = 0; t < blocoAtual.transacoes.size(); t++) {
                Transacao currentTransaction = blocoAtual.transacoes.get(t);

                if (!currentTransaction.verificarAssinatura()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getValorEntrada() != currentTransaction.getValorSaida()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (InputTransacao input : currentTransaction.entradas) {
                    tempOutput = tempUTXOs.get(input.idTransacaoSaida);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.valor != tempOutput.valor) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.idTransacaoSaida);
                }

                currentTransaction.saidas.forEach((output) -> {
                    tempUTXOs.put(output.id, output);
                });

                if (currentTransaction.saidas.get(0).recebedor != currentTransaction.destino) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.saidas.get(1).recebedor != currentTransaction.origem) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Bloco newBlock) {
        newBlock.minerarBloco(difficulty);
        blockchain.add(newBlock);
    }

}
