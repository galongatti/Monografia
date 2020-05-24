
import java.security.PublicKey;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gabriel
 */
public class OutputTransacao{

    public String id;
    public PublicKey recebedor; //also known as the new owner of these coins.
    public float valor; //the amount of coins they own
    public String idTransacaoPai; //the id of the transaction this output was created in

    //Constructor
    public OutputTransacao(PublicKey recebedor, float valor, String idTransacaoPai){
        this.recebedor = recebedor;
        this.valor = valor;
        this.idTransacaoPai = idTransacaoPai;
        this.id = StringUtil.aplicarSha256(StringUtil.getStringDaChave(recebedor) +
                Float.toString(valor) + idTransacaoPai);
    }

    //Check if coin belongs to you
    public boolean minerado (PublicKey chavePublica) {
        return (chavePublica == recebedor);
    }
    
    
    public void editarValor(){
        this.valor = 789789789;    
    }
    
    
}
