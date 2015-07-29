/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import biometria.Jornada;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jorgefspf
 */
public class JornadaDAO {

    public JornadaDAO() {
        this.conexao = new ConexaoSqlServer();

    }

    public Jornada buscarJornadaID(int idJornada) throws SQLException {
        try {
            String query = "Select * from tb_modelo where id_modelo = ? ";
            conexao.openConnection();
            PreparedStatement pstmt = conexao.connection.prepareStatement(query);
            Jornada jornada = null;

            pstmt.setInt(1, idJornada);

            ResultSet rs = pstmt.executeQuery();
            conexao.closeConnection();

            if (rs != null && rs.next()) {
                String jornadaDiaria = rs.getString("jornada");
                if (jornadaDiaria.equals("8")) {
                    jornada = new Jornada(jornadaDiaria);
                    jornada.getHorarios().add(0, rs.getString("entrada1"));
                    jornada.getHorarios().add(1, rs.getString("saida1"));
                    jornada.getHorarios().add(2, rs.getString("entrada2"));
                    jornada.getHorarios().add(3, rs.getString("saida1"));
                    jornada.setId(rs.getInt("id_modelo"));

                } else {
                    jornada = new Jornada(jornadaDiaria);
                    jornada.getHorarios().add(0, rs.getString("entrada1"));
                    jornada.getHorarios().add(1, rs.getString("saida1"));
                    jornada.setId(rs.getInt("id_modelo"));

                }

            }
            return jornada;
        } catch (SQLException ex) {
            Logger.getLogger(JornadaDAO.class.getName()).log(Level.SEVERE, null, ex);
            conexao.openConnection();

        }

        return jornada;

    }

    public Jornada buscarJornadaFuncionarioID(int idFuncionario) {
        String query = "Select * from tb_modelo where id_modelo = (Select id_modelo from tb_pessoal where id_pessoal = ?)";

        try {
            conexao.openConnection();
            PreparedStatement pstmt = ConexaoSqlServer.connection.prepareStatement(query);
            Jornada jornada = null;
            pstmt.setInt(1, idFuncionario);
            ResultSet rs = pstmt.executeQuery();
            conexao.closeConnection();

            if (rs != null && rs.next()) {
                String jornadaDiaria = rs.getString("jornada");
                if (jornadaDiaria.equals("8")) {
                    jornada = new Jornada(jornadaDiaria);
                    jornada.getHorarios().add(0, rs.getString("entrada1"));
                    jornada.getHorarios().add(1, rs.getString("saida1"));
                    jornada.getHorarios().add(2, rs.getString("entrada2"));
                    jornada.getHorarios().add(3, rs.getString("saida2"));
                    jornada.setId(rs.getInt("id_modelo"));

                } else {
                    jornada = new Jornada(jornadaDiaria);
                    jornada.getHorarios().add(0, rs.getString("entrada1"));
                    jornada.getHorarios().add(1, rs.getString("saida1"));
                    jornada.setId(rs.getInt("id_modelo"));

                }

            }
            return jornada;
        } catch (Exception ex) {
            this.showErro("buscarJornadaFuncionarioID(int idFuncionario): idFuncionario =  " + idFuncionario);
            Logger.getLogger(JornadaDAO.class.getName()).log(Level.SEVERE, null, ex);

        }

        return jornada;

    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public ConexaoSqlServer getConexao() {
        return conexao;
    }

    public void setConexao(ConexaoSqlServer conexao) {
        this.conexao = conexao;
    }

    private void showErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

    Jornada jornada;
    ConexaoSqlServer conexao;

}
