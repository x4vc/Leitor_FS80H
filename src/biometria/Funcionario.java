package biometria;

import Modelo.AfastamentoLegal;
import java.util.ArrayList;

public class Funcionario {

    public Funcionario() {
        this.jornada = new Jornada();
        this.afastamentosLegais = new ArrayList<>();
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        String[] nomeSimplificado = this.nome.split(" ");;

        return nomeSimplificado[0] + " " + nomeSimplificado[nomeSimplificado.length - 1];
    }

    public void setRegimePlantao(boolean regimePlantao) {
        this.regimePlantao = regimePlantao;
    }

    public boolean isRegimePlantao() {
        return regimePlantao;
    }

    public void setIdSetor(int idSetor) {
        this.idSetor = idSetor;
    }

    public int getIdSetor() {
        return idSetor;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    private String nome;

    private boolean regimePlantao;

    private int idSetor;

    private int idFuncionario;

    private Jornada jornada;
    
    private ArrayList<AfastamentoLegal> afastamentosLegais;
}
