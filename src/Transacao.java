
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
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
public class Transacao {

    public String idTransacao; // Hash da transação
    public PublicKey origem; // chave publica do origem
    public String origemStr;
    public PublicKey destino; // chave publica do destino
    public String destinoStr;
    public float valor;
    public byte[] assinatura; // Assinatura   

    public ArrayList<InputTransacao> entradas = new ArrayList<InputTransacao>();
    public ArrayList<OutputTransacao> saidas = new ArrayList<OutputTransacao>();

    private static int sequencia = 0;

    // Constructor: 
    public Transacao(PublicKey origem, PublicKey destino, float valor, ArrayList<InputTransacao> entradas) {
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
        this.entradas = entradas;
        this.origemStr = origem.toString();
        this.destinoStr = destino.toString();
    }

    //Calcula o hash da transação, no qual será usado como ID
    private String calcularHash() {
        sequencia++;
        return StringUtil.aplicarSha256(StringUtil.getStringDaChave(origem)
                + StringUtil.getStringDaChave(destino)
                + Float.toString(valor) + sequencia
        );
    }

    public void gerarAssinatura(PrivateKey chavePrivada) {
        String data = StringUtil.getStringDaChave(origem)
                + StringUtil.getStringDaChave(destino)
                + Float.toString(valor);
        assinatura = StringUtil.aplicarECDSASig(chavePrivada, data);

    }    
    

    public boolean verificarAssinatura() {
        String data = StringUtil.getStringDaChave(origem)
                + StringUtil.getStringDaChave(destino)
                + Float.toString(valor);

        boolean validado = StringUtil.verificarECDSASig(origem, data, assinatura);

        return (validado);

    }

    //Retorna true se a nova transação pode ser criada.	
    public boolean processaTransacao() {

        if (verificarAssinatura() == false) {
            System.out.println("#Falha na verificação da assinatura");
            return false;
        }

        
        entradas.forEach((i) -> {
            i.UTXO = BRChain.UTXOs.get(i.idTransacaoSaida);
        });

        //Verifica se a transação é valida
        if (getValorEntrada() < BRChain.minimumTransaction) {
            System.out.println("#Quantidade abaixo do minimo: " + getValorEntrada());
            return false;
        }

        //Gera as saidas das transações
        float leftOver = getValorEntrada() - valor; //get value of inputs then the left over change:
        idTransacao = calcularHash();
        saidas.add(new OutputTransacao(this.destino, valor, idTransacao)); //send value to destino
        saidas.add(new OutputTransacao(this.origem, leftOver, idTransacao)); //send the left over 'change' back to origem		
        //add outputs to Unspent list
        saidas.forEach((o) -> {
            BRChain.UTXOs.put(o.id, o);
        });

        //remove transaction inputs from UTXO lists as spent:
        entradas.stream().filter((i) -> !(i.UTXO == null)).forEachOrdered((i) -> {
            //if Transaction can't be found skip it
            BRChain.UTXOs.remove(i.UTXO.id);
        });

        return true;
    }

//returns sum of inputs(UTXOs) values
    public float getValorEntrada() {
        float total = 0;
        for (InputTransacao i : entradas) {
            if (i.UTXO == null) {
                continue; //if Transaction can't be found skip it 
            }
            total += i.UTXO.valor;
        }
        return total;
    }

//returns sum of outputs:
    public float getValorSaida() {
        float total = 0;
        for (OutputTransacao o : saidas) {
            total += o.valor;
        }
        return total;
    }

}
