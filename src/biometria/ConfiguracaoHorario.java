package biometria;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ConfiguracaoHorario {
	public ConfiguracaoHorario()
	{
	}
	
	public void setHorarioEntrada(Date horarioEntrada) {
		this.horarioEntrada = horarioEntrada;
	}
	
	@SuppressWarnings("deprecation")
	public Date getHorarioEntrada() {
		Date dataAtual = Calendar.getInstance().getTime();
		GregorianCalendar atualHorarioEntrada = new GregorianCalendar();
		
		atualHorarioEntrada.set(dataAtual.getYear() + 1900, dataAtual.getMonth(), dataAtual.getDate(),
				this.horarioEntrada.getHours(), this.horarioEntrada.getMinutes(), 0);
		
		return atualHorarioEntrada.getTime();
	}

	public void setTolerancia(int tolerancia) {
		this.tolerancia = tolerancia;
	}

	public int getTolerancia() {
		return tolerancia;
	}

	private Date horarioEntrada;
	private int tolerancia;
	
	public boolean AcessoDisponivelRegistro()
	{		
		return Calendar.getInstance().getTime().compareTo(this.getHorarioEntradaToleranciaMenos()) >= 0;
	}
	
	private Date getHorarioEntradaToleranciaMenos()
	{
		GregorianCalendar atualHorarioEntrada = new GregorianCalendar();
		atualHorarioEntrada.setTime(this.getHorarioEntrada());
		
		atualHorarioEntrada.add(GregorianCalendar.MINUTE, -tolerancia);
		
		return atualHorarioEntrada.getTime();
	}
	
	public Date getHorarioEntradaLimite()
	{
		GregorianCalendar atualHorarioEntrada = new GregorianCalendar();
		atualHorarioEntrada.setTime(this.getHorarioEntrada());
		
		atualHorarioEntrada.add(GregorianCalendar.MINUTE, tolerancia);
		
		return atualHorarioEntrada.getTime();
	}
	
	public String ImprimirHorarioDisponivelEntrada()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		return dateFormat.format(this.getHorarioEntradaToleranciaMenos());
	}
}
