package biometria;

public class Acesso {
	//Intervalo, em segundos, entre um registo de acesso
	public final static int INTERVALO_REGISTRO = 60;
	
	public Acesso(){
	}
	
	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}
	
	public void setEntrada(boolean entrada) {
		this.entrada = entrada;
	}

	public boolean isEntrada() {
		return entrada;
	}
	
        
	public char getCaracterSituacao()
	{
		return this.entrada == true ? 'E' : 'S';
	}
	
	public String getDescricaoSituacao()
	{
		return this.entrada == true ? "Entrada" : "Saída";
	}

	public void setAtrasado(boolean atrasado) {
		this.atrasado = atrasado;
	}

	public boolean isAtrasado() {
		return atrasado;
	}
	
	public void setPrimeiroAcesso(boolean primeiroAcesso) {
		this.primeiroAcesso = primeiroAcesso;
	}

	public boolean isPrimeiroAcesso() {
		return primeiroAcesso;
	}

	private boolean primeiroAcesso;

	private boolean entrada;

	private Funcionario funcionario;
	
	private boolean atrasado;
}
