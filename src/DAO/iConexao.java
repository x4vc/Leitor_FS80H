/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DAO;

/**
 *
 * @author jorgefspf
 */
public interface iConexao {
    
    public void openConnection();

    // Método Responsável por Fechar a conexão com o banco
    public void closeConnection(); 
    
}
