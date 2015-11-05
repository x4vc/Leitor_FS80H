/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author jorgefspf
 */
public class ConexaoSqlServer implements iConexao {
    private String server = "172.22.8.17";
    //  Base de dados Produção    
    private String dataBase = "DB_ACESSO";
    //--------------------------------------------------
    //  Base de dados Teste/Homologação
   // private String dataBase = "DB_ACESSO_HOMOLOG";
    private String usuario = "acesso";
    private String senha = "#4c3ss0$";
    private final String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

   

     static Connection connection;

    /**
     * Guarda a instancia do objeto.
     */
    static private ConexaoSqlServer _instance = null;

    ConexaoSqlServer() {
    }

    /**
     * @return Preserva a unicidade do objeto.
     */
    static public synchronized iConexao instance() {
        if (_instance == null) {
            _instance = new ConexaoSqlServer();
        }
        return _instance;
    }
    

    
    @Override
    public void openConnection() {
        connection = null;
        try {
            // Load the JDBC driver
          
            Class.forName(driverName);

            //Dados do servidor de homologação Afrodite
         

            String connectionUrl = "jdbc:sqlserver://" + server
                    + ";databaseName=" + dataBase + ";";

            connection = DriverManager.getConnection(connectionUrl, usuario,
                    senha);
        } catch (ClassNotFoundException ce) {
            // Could not find the database driver
            showErro("openConnection(): " + ce.getMessage());
            System.out.println("Erro de Conexão: " + ce.getMessage());
        } catch (SQLException sqlExecption) {
            showErro("openConnection(): " + sqlExecption.getMessage());
            System.out.println("Erro de Conexão [Sql-Exception]: "
                    + sqlExecption.getMessage());
        } catch (Exception e) {
            showErro("openConnection(): " + e.getMessage());
            System.out.println("Exceção");
        }

    }

    // Método Responsável por Fechar a conexão com o banco
    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            showErro("openConnection(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        ConexaoSqlServer.connection = connection;
    }

    public static ConexaoSqlServer getInstance() {
        return _instance;
    }

    public static void setInstance(ConexaoSqlServer _instance) {
        ConexaoSqlServer._instance = _instance;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    private void showErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }
    
}
