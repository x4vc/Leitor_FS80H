/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biometria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 *
 * @author jorgefspf
 */
public class Jornada {

    private ArrayList<String> horarios;
    private String tipoJornada;
    private int id;

    public Jornada(String tipoJornada) {
        this.tipoJornada = tipoJornada;
        //if(this.tipoJornada.equals("6") || this.tipoJornada.equals("4") || this.tipoJornada.equals("12")){
//            String[] h = {"", ""}; 
//            this.horarios = (ArrayList<String>) Arrays.asList(h);
        this.horarios = new ArrayList<>();

        //}
    }

    public Jornada() {
    }

    public ArrayList<String> getHorarios() {
        return horarios;
    }

    public void setHorarios(ArrayList<String> horarios) {
        this.horarios = horarios;
    }

    public String getTipoJornada() {
        return tipoJornada;
    }

    public void setTipoJornada(String tipoJornada) {
        this.tipoJornada = tipoJornada;
    }

    public Calendar convertStringCalendar(String data) {
        Calendar horaCalendar = Calendar.getInstance();

        horaCalendar.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(data.substring(0, 2))));
        horaCalendar.set(Calendar.MINUTE, (Integer.parseInt(data.substring(3, 5))));
        horaCalendar.set(Calendar.SECOND, 0);
        
        return horaCalendar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
}
