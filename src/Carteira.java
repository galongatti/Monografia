
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Gabriel
 */
public class Carteira {

    public PrivateKey chavePrivada;
    public PublicKey chavePublica;
    public HashMap<String, OutputTransacao> UTXOs = new HashMap<String, OutputTransacao>();

    public Carteira(){
        gerarParChaves();
    }

    public void gerarParChaves() {

        try {
            KeyPairGenerator gerarChave = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom aleatorio = SecureRandom.getInstance("Windows-PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");

            //Inicializa o gerador de chaves e gera o par de chaves
            gerarChave.initialize(ecSpec, aleatorio);
            KeyPair parChaves = gerarChave.generateKeyPair();

            //Atribui a chave publica e privada do parChaves
            chavePrivada = parChaves.getPrivate();
            chavePublica = parChaves.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public float getBalanco() {
        float total = 0;
        for (Map.Entry<String, OutputTransacao> item : BRChain.UTXOs.entrySet()) {
            OutputTransacao UTXO = item.getValue();

            if (UTXO.minerado(chavePublica)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.valor;
            }

        }

        return (total);

    }

    public Transacao enviarFundos(PublicKey destinatario, float valor) {
        if (getBalanco() < valor) {
            System.out.println("Saldo insuficientes");
            return (null);
        }

        ArrayList<InputTransacao> entradas = new ArrayList<InputTransacao>();

        float total = 0;
        for (Map.Entry<String, OutputTransacao> item : UTXOs.entrySet()) {
            OutputTransacao UTXO = item.getValue();
            total += UTXO.valor;
            entradas.add(new InputTransacao(UTXO.id));
            if (total > valor) {
                break;
            }
        }

        Transacao novaTransacao = new Transacao(chavePublica, destinatario, valor, entradas);
        novaTransacao.gerarAssinatura(chavePrivada);

        for (InputTransacao input : entradas) {
            UTXOs.remove(input.idTransacaoSaida);
        }
        return novaTransacao;

    }

}
