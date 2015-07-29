package biometria;

import java.util.Date;

public class Compensacao {
    
    private int idCompensacao;
    
    private Date dataReferencia;

    private int idTipoCompensacao;    
    
    private int idSetor;

    private int idPessoal;

    public int getIdCompensacao() {
        return idCompensacao;
    }

    public void setIdCompensacao(int idCompensacao) {
        this.idCompensacao = idCompensacao;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public int getIdTipoCompensacao() {
        return idTipoCompensacao;
    }

    public void setIdTipoCompensacao(int idTipoCompensacao) {
        this.idTipoCompensacao = idTipoCompensacao;
    }

    public int getIdSetor() {
        return idSetor;
    }

    public void setIdSetor(int idSetor) {
        this.idSetor = idSetor;
    }

    public int getIdPessoal() {
        return idPessoal;
    }

    public void setIdPessoal(int idPessoal) {
        this.idPessoal = idPessoal;
    }
    
    
    
}