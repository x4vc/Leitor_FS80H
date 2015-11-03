package biometria;

import Modelo.AfastamentoLegal;
import Modelo.Ponto;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.swing.JOptionPane;

/*Singleton*/
public class Conexao {

    static Connection connection;

    /**
     * Guarda a instancia do objeto.
     */
    static private Conexao _instance = null;

    private Conexao() {
    }

    /**
     * @return Preserva a unicidade do objeto.
     */
    static public synchronized Conexao instance() {
        if (_instance == null) {
            _instance = new Conexao();
        }
        return _instance;
    }

    /*
     * public static Connection getConnection() { return connection; }
     */

    /*
     * public ResultSet pesquisarPacientesBiometria(){ try {
     * 
     * 
     * PreparedStatement pstmt = getConnection(). prepareStatement("Select
     * CO_USUARIO,NU_QUALIDADE," + "UTL_RAW.CAST_TO_VARCHAR2(BYTE_DADOS) as
     * BYTE_DADOS " + "from rl_pms_usuario_biometria");
     * 
     * ResultSet rs = pstmt.executeQuery();
     * 
     * return rs;
     *  } catch (SQLException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }catch (Exception e) { e.printStackTrace(); }
     * 
     * return null;
     *  }
     * 
     * public void salvarBiometria(String idPaciente, int qualidade, byte[]
     * dados){ idPaciente = "7";
     * 
     * BLOB blob = null; try { blob =
     * oracle.sql.BLOB.createTemporary(connection,false,
     * oracle.sql.BLOB.DURATION_SESSION); blob.putBytes(1,dados); } catch
     * (SQLException e1) { // TODO Auto-generated catch block
     * e1.printStackTrace(); } PreparedStatement pstmt = null; try { String
     * insert = "insert into rl_pms_usuario_biometria values(?,?,?)"; pstmt =
     * connection.prepareStatement(insert); pstmt.setString(1, idPaciente);
     * pstmt.setInt(2, qualidade); pstmt.setBytes(3, dados);
     * pstmt.executeUpdate(); }catch(SQLException e){ e.printStackTrace(); }
     * catch (Exception e) { e.printStackTrace(); System.exit(1); } }
     */
    public ResultSet ListarBiometriasFuncionarios() {

        try {

            /*PreparedStatement pstmt = connection
             .prepareStatement("SELECT id_pessoal AS ID_FUNCIONARIO,CAST(digital_polegardireito AS VARCHAR(8000)) AS BYTES_POLEGAR_DIREITO,"
             + "CAST(digital_polegaresquerdo AS VARCHAR(8000)) AS BYTES_POLEGAR_ESQUERDO, "
             + "digital_qualidade_polegardireito AS QUALIDADE_POLEGAR_DIREITO, digital_qualidade_polegaresquerdo AS QUALIDADE_POLEGAR_ESQUERDO "
             + "FROM tb_pessoal WHERE digital_polegaresquerdo IS NOT NULL OR digital_polegardireito IS NOT NULL ORDER BY nome");*/

            /*PreparedStatement pstmt = connection
             .prepareStatement("SELECT id_pessoal AS ID_FUNCIONARIO,CONVERT(VARCHAR(8000),digital_polegardireito) AS BYTES_POLEGAR_DIREITO,"
             + "CONVERT(VARCHAR(8000),digital_polegaresquerdo) AS BYTES_POLEGAR_ESQUERDO,"
             + "digital_qualidade_polegardireito AS QUALIDADE_POLEGAR_DIREITO, digital_qualidade_polegaresquerdo AS QUALIDADE_POLEGAR_ESQUERDO "
             + "FROM tb_pessoal WHERE (digital_polegaresquerdo IS NOT NULL AND digital_qualidade_polegaresquerdo IS NOT NULL) OR (digital_polegardireito IS NOT NULL AND digital_qualidade_polegardireito IS NOT NULL)" 
             + " ORDER BY nome");*/
            if (connection == null) {
                //this.closeConnection();
                this.openConnection();
            } else {
//                PreparedStatement pstmt = connection
//                        .prepareStatement("SELECT id_funcionario AS ID_FUNCIONARIO,CONVERT(VARCHAR(8000),digital) AS BYTES_DIGITAL,"
//                                + "qualidade_digital AS QUALIDADE_DIGITAL FROM tb_digital");
                
                //Data moficação: 28/08/2015
                //Descrição da mudança: Alterar Query para selecionar só os usuários Ativos
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT d.id_funcionario AS ID_FUNCIONARIO,CONVERT(VARCHAR(8000),d.digital) AS BYTES_DIGITAL,"
                                + "d.qualidade_digital AS QUALIDADE_DIGITAL FROM tb_digital d "
                                + "JOIN tb_pessoal p on d.id_funcionario = p.id_pessoal where p.inativo = 0");

                ResultSet rs = pstmt.executeQuery();

                return rs;
            }
        } catch (SQLException e) {
            _instance = new Conexao();
            this.closeConnection();
            this.openConnection();
            // TODO Auto-generated catch block
            //showErro("ListarBiometriasFuncionarios(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            //showErro("ListarBiometriasFuncionarios(): " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet ListarBiometriasFuncionarios(String faixa) {

        try {

            /*PreparedStatement pstmt = connection
             .prepareStatement("SELECT id_pessoal AS ID_FUNCIONARIO,CAST(digital_polegardireito AS VARCHAR(8000)) AS BYTES_POLEGAR_DIREITO,"
             + "CAST(digital_polegaresquerdo AS VARCHAR(8000)) AS BYTES_POLEGAR_ESQUERDO, "
             + "digital_qualidade_polegardireito AS QUALIDADE_POLEGAR_DIREITO, digital_qualidade_polegaresquerdo AS QUALIDADE_POLEGAR_ESQUERDO "
             + "FROM tb_pessoal WHERE digital_polegaresquerdo IS NOT NULL OR digital_polegardireito IS NOT NULL ORDER BY nome");*/
            String query = "SELECT TOP 50 PERCENT id_pessoal AS ID_FUNCIONARIO,digital_polegardireito AS BYTES_POLEGAR_DIREITO,"
                    + "digital_polegaresquerdo AS BYTES_POLEGAR_ESQUERDO,"
                    + "digital_qualidade_polegardireito AS QUALIDADE_POLEGAR_DIREITO, digital_qualidade_polegaresquerdo AS QUALIDADE_POLEGAR_ESQUERDO "
                    + "FROM tb_pessoal WHERE digital_polegaresquerdo IS NOT NULL OR digital_polegardireito IS NOT NULL ORDER BY nome";

            if (faixa.equals("last")) {
                query += " DESC";
            }

            PreparedStatement pstmt = connection.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();

            return rs;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            showErro("ListarBiometriasFuncionarios(String faixa): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showErro("ListarBiometriasFuncionarios(String faixa): " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean LimiteDigitaisCadastradas(int idFuncionario) {
        try {
            String select = "SELECT count(*) AS qtdMaxima FROM tb_digital WHERE id_funcionario = ?";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setInt(1, idFuncionario);

            ResultSet rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                if (rs.getInt("qtdMaxima") == 4) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            showErro("LimiteDigitaisCadastradas(int idFuncionario): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean VerificaDigitalCadastrada(int idFuncionario, int numero_dedo) {
        try {
            String select = "SELECT id_funcionario,numero_dedo FROM tb_digital "
                    + " WHERE id_funcionario = ? AND numero_dedo = ?";

            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setInt(1, idFuncionario);
            pstmt.setInt(2, numero_dedo);

            ResultSet rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return true;
            }

            return false;
        } catch (Exception e) {
            showErro("VerificaDigitalCadastrada(int idFuncionario, int numero_dedo): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //public void salvarBiometria(int idFuncionario, boolean polegarDireito,
    //int qualidade, byte[] dados) {
    public void salvarBiometria(int idFuncionario, int numero_dedo,
            int qualidade, byte[] dados, boolean atualizacao) {

        PreparedStatement pstmt = null;
        try {
            /*String update = "UPDATE tb_pessoal SET digital_polegaresquerdo = ?, digital_qualidade_polegaresquerdo = ? WHERE id_pessoal = ?";

             if (polegarDireito)
             update = "UPDATE tb_pessoal SET digital_polegardireito = ?, digital_qualidade_polegardireito = ? WHERE id_pessoal = ?";*/
            String salvar = "";
            if (atualizacao) {
                salvar = "UPDATE tb_digital SET digital = ?,qualidade_digital = ? "
                        + "WHERE id_funcionario = ? AND numero_dedo = ?";
                pstmt = connection.prepareStatement(salvar);
                pstmt.setBytes(1, dados);
                pstmt.setInt(2, qualidade);
                pstmt.setInt(3, idFuncionario);
                pstmt.setInt(4, numero_dedo);

            } else {
                salvar = "INSERT INTO tb_digital(id_funcionario,numero_dedo,qualidade_digital,digital) VALUES(?,?,?,?)";
                pstmt = connection.prepareStatement(salvar);
                pstmt.setInt(1, idFuncionario);
                pstmt.setInt(2, numero_dedo);
                pstmt.setInt(3, qualidade);
                pstmt.setBytes(4, dados);
            }

            /*pstmt = connection.prepareStatement(update);
             pstmt.setBytes(1, dados);
             pstmt.setInt(2, qualidade);
             pstmt.setInt(3, idFuncionario);	*/
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showErro("salvarBiometria(int idFuncionario, int numero_dedo,int qualidade, byte[] dados, boolean atualizacao): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showErro("salvarBiometria(int idFuncionario, int numero_dedo,int qualidade, byte[] dados, boolean atualizacao): " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean BloquearAcesso(int id_funcionario) {
        String query = "select top 1 datediff(ss,datahora,getdate()) from tb_acesso"
                + " where id_pessoal = ? and CONVERT(VARCHAR,datahora,103) = CONVERT(VARCHAR,getdate(),103) order by datahora desc";
        int tempoLimite = Acesso.INTERVALO_REGISTRO; //Tempo limite entre um registro de entrada ou saída em SEGUNDOS

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id_funcionario);

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                tempoLimite = rs.getInt(1);
            }

            rs.close();
        } catch (Exception e) {
            showErro("BloquearAcesso(int id_funcionario): " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return tempoLimite < Acesso.INTERVALO_REGISTRO;
    }

    public boolean isFeriado() {
        try {
            PreparedStatement pstmt = connection.prepareStatement("select id_calendario from tb_calendario where CONVERT(VARCHAR(12),datainicio,103) <= CONVERT(VARCHAR(12),GETDATE(),103) and CONVERT(VARCHAR(12),datafim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) and ativo = 1");

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {

                rs.close();

                return true;
            }
        } catch (Exception e) {
            showErro("BucarConfiguracaoHorario(): " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return false;

    }
public int isDiaOperacaoEspecial(int id_funcionario){
    PreparedStatement pstmt;
    ResultSet rs;
    String strSQL;
    
    strSQL = "select oe.*, oep.id_pessoal from tb_operacao_especial oe join tb_operacao_especial_pessoal oep on oe.id = oep.id_operacao_especial where CONVERT(VARCHAR(12),oe.data_hora_ini,103) <= CONVERT(VARCHAR(12),GETDATE(),103) AND CONVERT(VARCHAR(12),oe.data_hora_fim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) AND oe.ativo = 1 AND oe.aprovado = 1 AND oep.id_pessoal = ?";
    //strSQL = "select oe.* from tb_operacao_especial oe";
    try {    
        //PreparedStatement pstmt = connection.prepareStatement("select id_calendario from tb_calendario where CONVERT(VARCHAR(12),datainicio,103) <= CONVERT(VARCHAR(12),GETDATE(),103) and CONVERT(VARCHAR(12),datafim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) and ativo = 1");
        pstmt = connection.prepareStatement(strSQL);
        
        pstmt.setInt(1, id_funcionario);
        System.out.println("Entrei no metodo isDiaOperacaoEspecial()");  
        //System.out.println(strSQL);
        
        rs = pstmt.executeQuery();        
        System.out.println("Id Funcionário: " + id_funcionario);
        //System.out.println("rs = " + rs);
        
        
        if (rs != null && rs.next()) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat dfAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            java.sql.Date data_hora_ini, data_hora_fim;
            java.util.Date data_hora_atual;
            
            
            java.sql.Timestamp timeStamp_ini, timeStamp_fim; //timeStamp_ini_minus_30 = null, timeStamp_fim_plus_30 = null;
            java.sql.Time time_ini;
            
            String strDataHoraIni; 
            String strDataHoraFim;
            
            //System.out.println(strDataNow);
            //get hora inicial e hora final
            data_hora_atual = new Date();
            
            data_hora_ini = rs.getDate(4); //campo data_hora_ini
            strDataHoraIni = df.format(data_hora_ini);
            
            timeStamp_ini = rs.getTimestamp(4);
            timeStamp_fim = rs.getTimestamp(5);
            
            //Seteamos +- 30 min utilizando a Clase Calendar           
            Calendar calToday,calDateFim,calDateIni;
            long calDateTimePlus30, calDateTimeMinus30;
            
            calDateIni = Calendar.getInstance();
            calDateFim = Calendar.getInstance();
            calToday = Calendar.getInstance();
            
            calDateTimePlus30 = 0;
            calDateTimeMinus30 = 0;
            
            calDateIni.setTime(timeStamp_ini);            
            System.out.println("calDateIni = " + calDateIni.getTimeInMillis());
            calDateIni.add(Calendar.MINUTE, -31);
            calDateTimeMinus30 = calDateIni.getTimeInMillis();
            
            calDateFim.setTime(timeStamp_fim);            
            System.out.println("calDateFim = " + calDateFim.getTimeInMillis());
            calDateFim.add(Calendar.MINUTE, 31);
            calDateTimePlus30 = calDateFim.getTimeInMillis();
            
            
            
            if ((calDateTimeMinus30 < calToday.getTimeInMillis()) && (calDateTimePlus30 > calToday.getTimeInMillis()))
            {
                System.out.println(" Data e hora: " +  calToday.getTimeInMillis()+" está depois do horário calDateTimeMinus30 = " + calDateTimeMinus30 );
                System.out.println(" Data e hora: " +  calToday.getTimeInMillis()+" está antes do horário calDateTimePlus30 = " + calDateTimePlus30 );
                System.out.println("1 = SIM Está dentro do intervalo permitido");
                rs.close();
                return 1; // 1 = SIM Está dentro do intervalo permitido
                
            } else {
                System.out.println(" Data e hora: " +  calToday.getTimeInMillis()+" está antes do horário calDateTimeMinus30 = " + calDateTimeMinus30 );
                System.out.println(" Data e hora: " +  calToday.getTimeInMillis()+" está depois do horário calDateTimePlus30 =" + calDateTimePlus30 );
                System.out.println("2 = NÃO Está dentro do intervalo permitido");
                rs.close();
                return 2; // 2 = NÃO Está dentro do intervalo permitido
                
            }
            
            
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Operação Especial");
//                alert.setHeaderText("isOperaao");
//                alert.setContentText("Valor = False");
//                alert.showAndWait();
            //----------Fim de utilização da Clase Calendar
            
            //Utilizando Joda-time jar
            
//            DateTime DateTime_ini = new DateTime(rs.getTimestamp(4));
//            DateTime DateTime_fim = new DateTime(rs.getTimestamp(5));
//            DateTime DateTime_ini_minus_30 = DateTime_ini.minusMinutes(30);
//            DateTime DateTime_fim_plus_30 = DateTime_fim.plusMinutes(30);
//            System.out.println("DateTime_ini = " + DateTime_ini);
//            System.out.println("DateTime_ini_minus_30 = " + DateTime_ini_minus_30);
//            System.out.println("DateTime_fim = " + DateTime_fim);
//            System.out.println("DateTime_ini_minus_30 = " + DateTime_fim_plus_30);
//            
            //-----------------------------------------------------------------
            
            //timeStamp_ini_minus_30.setTime(timeStamp_ini.getTime() - TimeUnit.MINUTES.toMinutes(30));
            //timeStamp_fim_plus_30 = rs.getTimestamp(5);
            
//            time_ini = rs.getTime(4);            
//            data_hora_fim = rs.getDate(5); //campo data_hora_fim
//            strDataHoraFim = df.format(data_hora_fim);
//            
//            
//            
//            System.out.println(id_funcionario);
//            System.out.println("data_hora_ini = " + strDataHoraIni);
//            System.out.println("data_hora_fim = " + strDataHoraFim);
//            System.out.println("timeStamp_ini = " + timeStamp_ini);
//            System.out.println("timeStamp_fim = " + timeStamp_fim);
//            //System.out.println("timeStamp_ini_minus_30 = " + timeStamp_ini_minus_30);
//            System.out.println("time_ini = " + time_ini);
//            System.out.println("data_hora_atual = " + data_hora_atual + " - " + dfAtual.format(data_hora_atual));
            
//            if (timeStamp_ini.before(data_hora_atual))  {
//                System.out.println("timeStamp_ini.before = true");
//                
//            }
//            else {
//                System.out.println("timeStamp_ini.before = false");
//            }
//            
//            if (timeStamp_fim.after(data_hora_atual))  {
//                System.out.println("timeStamp_fim.after = true");
//                
//            }
//            else {
//                System.out.println("timeStamp_fim.after = false");
//            }
           
            /*//Verificamos se data e horário está dentro do intervalo permitido
            if ((timeStamp_ini.before(data_hora_atual))&& (timeStamp_fim.after(data_hora_atual))){
                System.out.println(dfAtual.format(data_hora_atual) + " SIM Está dentro do intervalo permitido");
                rs.close();
                return 1; // 1 = SIM Está dentro do intervalo permitido
            }
            else {
                System.out.println(dfAtual.format(data_hora_atual) + " NÃO Está dentro do intervalo permitido");
                rs.close();
                return 2; // 2 = NÃO Está dentro do intervalo permitido
            }
            
            //rs.close();
            //return true;*/
        }
//        else {
//            return 0; // 0 = Funcionário não está escalado para Operação Especial 
//        }
   
        } 
        catch (Exception e) {
            showErro("isDiaOperacaoEspecial(): " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    
        return 0; // 0 = Funcionário não está escalado para Operação Especial    
}
public int buscarIdFuncao_OperacaoEspecial(int id_funcionario){
    PreparedStatement pstmt;
    ResultSet rs;
    String strSQL;
    int nIdFuncaoOperacaoEspecial;
    
    nIdFuncaoOperacaoEspecial = 0;
    
    strSQL = "select oe.id,oe.assunto, oep.id_pessoal,oep.id_funcao_operacao_especial from tb_operacao_especial oe join tb_operacao_especial_pessoal oep on oe.id = oep.id_operacao_especial where CONVERT(VARCHAR(12),oe.data_hora_ini,103) <= CONVERT(VARCHAR(12),GETDATE(),103) AND CONVERT(VARCHAR(12),oe.data_hora_fim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) AND oe.ativo = 1 AND oe.aprovado = 1 AND oep.id_pessoal = ?";
    
    try {    
        //PreparedStatement pstmt = connection.prepareStatement("select id_calendario from tb_calendario where CONVERT(VARCHAR(12),datainicio,103) <= CONVERT(VARCHAR(12),GETDATE(),103) and CONVERT(VARCHAR(12),datafim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) and ativo = 1");
        pstmt = connection.prepareStatement(strSQL);
        
        pstmt.setInt(1, id_funcionario);
        System.out.println("Entrei no metodo buscarIdFuncao_OperacaoEspecial()");  
        //System.out.println(strSQL);
        
        rs = pstmt.executeQuery();        
        System.out.println("Id Funcionário: " + id_funcionario);
        //System.out.println("rs = " + rs);
        
        
            if (rs != null && rs.next()) {
                
                nIdFuncaoOperacaoEspecial = rs.getInt(4);

            } 
        }
        catch (Exception e) {
            showErro("buscarIdFuncao_OperacaoEspecial(): " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    return nIdFuncaoOperacaoEspecial;    
}

public boolean isDiaCompensacao() {

        //PreparedStatement pstmt = connection.prepareStatement("select id_calendario from tb_calendario where CONVERT(VARCHAR(12),datainicio,103) <= CONVERT(VARCHAR(12),GETDATE(),103) and CONVERT(VARCHAR(12),datafim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) and ativo = 1");
        PreparedStatement pstmt;
        ResultSet rs;
        //verificamos se existe registros    

        int nQtde = 0;
        String query = "select count(*) AS qtde_registros from tb_compensacao c where  \n" +
            "CONVERT(VARCHAR(12),c.data_ini,103) <= CONVERT(VARCHAR(12),GETDATE(),103) \n" +
            "AND CONVERT(VARCHAR(12),c.data_fim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) AND c.ativo = 1";
        try {
            pstmt = connection.prepareStatement(query);                

            rs = pstmt.executeQuery();

            rs.next();
            nQtde = rs.getInt("qtde_registros");
            System.out.println("Qtde registros da tabela tb_compensacao = " + nQtde);
            if (nQtde > 0) {
                rs.close();
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            showErro("isDiaCompensacao(Qtde_Regsitros): " + e.getMessage());
            e.printStackTrace();
        }
        //--------------------------------------
//        try {
//            if (nQtde > 0) {
//                //rs.close();
//                return true;
////                    pstmt = connection.prepareStatement("select * from tb_compensacao where \n" +
////                    "CONVERT(VARCHAR(12),data_ini,103) <= CONVERT(VARCHAR(12),GETDATE(),103) \n" +
////                    "and CONVERT(VARCHAR(12),data_fim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) AND ativo = 1");
////
////                    ResultSet rs = pstmt.executeQuery();
////
////                    if (rs != null && rs.next()) {
////                        rs.close();
////                        return true;
//                }
//                else {                        
//                    return false;
//                }
//            } 
//        catch (Exception e) {
//            showErro("isDiaCompensacao(): " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//            }
//    }
    return false;
}

public ArrayList<Compensacao> SetearCompensacao() {
        
        ArrayList<Compensacao> compensacoes = new ArrayList<>();
        try {
           PreparedStatement pstmt = connection.prepareStatement("select * from tb_compensacao where \n" +
                    "CONVERT(VARCHAR(12),data_ini,102) <= CONVERT(VARCHAR(12),GETDATE(),102) \n" +
                    "and CONVERT(VARCHAR(12),data_fim,102) >= CONVERT(VARCHAR(12),GETDATE(),102) AND ativo = 1");            

            ResultSet rs = pstmt.executeQuery();

            while (rs != null && rs.next()) {
                Compensacao compensacao = new Compensacao();
                compensacao.setIdCompensacao(rs.getInt("id_compensacao"));
                compensacao.setDataReferencia(rs.getDate("data_referencia"));
                compensacao.setIdTipoCompensacao(rs.getInt("id_tipo_compensacao"));
                compensacao.setIdSetor(rs.getInt("id_setor"));
                compensacao.setIdPessoal(rs.getInt("id_pessoal"));
                
                //Funcionario responsavel = new Funcionario();
                //responsavel.setIdFuncionario(rs.getInt("id_pessoal"));
                compensacoes.add(compensacao);
            }

        } catch (SQLException e) {
            showErro("SetearCompensacao() " + e.getMessage());
            e.printStackTrace();

        }
        return compensacoes;

    }

public boolean isAfastadoLegal(int idFuncionario, Date dataReferencia) {
        //String query = "select * from tb_afastamento_legal where id_funcionario = ? and  convert(varchar(30),data_ini,102) <= convert(varchar(30), getDate(),102) and convert(varchar(30),data_fim,102) >= convert(varchar(30), getDate(),102) and ativo = 1 ";        
        Calendar cal = Calendar.getInstance();
        //java.util.Date utilDate = new java.util.Date(); // your util date
        cal.setTime(dataReferencia);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);    
        java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
        
        try {
            PreparedStatement pstmt = connection.prepareStatement("select * from tb_afastamento_legal where id_funcionario = ? and \n" +
                    "CONVERT(VARCHAR(12),data_ini,102) <= CONVERT(VARCHAR(12),?,102) \n" +
                    "and CONVERT(VARCHAR(12),data_fim,102) >= CONVERT(VARCHAR(12),?,102)");
            pstmt.setInt(1, idFuncionario);
            pstmt.setDate(2,sqlDate);
            pstmt.setDate(3, sqlDate);

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                //rs.close();
                return true;
            }

        } catch (SQLException e) {
            showErro("isAfastadoLegal(" + idFuncionario + ") " + e.getMessage());
            e.printStackTrace();

        }
        return false;

    }
public boolean isSetor(int idFuncionario, int idSetor) {
        try {
            
            //PreparedStatement pstmt = connection.prepareStatement("select id_calendario from tb_calendario where CONVERT(VARCHAR(12),datainicio,103) <= CONVERT(VARCHAR(12),GETDATE(),103) and CONVERT(VARCHAR(12),datafim,103) >= CONVERT(VARCHAR(12),GETDATE(),103) and ativo = 1");
            PreparedStatement pstmt = connection.prepareStatement("select * from tb_pessoal where \n" +
                    "id_pessoal = ? and  id_setor = ? ");
            
            pstmt.setInt(1, idFuncionario);
            pstmt.setInt(2,idSetor);
            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                //rs.close();
                return true;
            }
            } catch (Exception e) {
                showErro("isSetor(): " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        return false;
    }

    public ConfiguracaoHorario BucarConfiguracaoHorario() {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT TOP 1 * FROM TB_CONFIGURACAOHORARIO");

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                ConfiguracaoHorario configuracao = new ConfiguracaoHorario();

                configuracao.setHorarioEntrada(rs.getTimestamp("entrada"));
                configuracao.setTolerancia(rs.getInt("tolerancia"));

                rs.close();

                return configuracao;
            }
        } catch (Exception e) {
            showErro("BucarConfiguracaoHorario(): " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    public Funcionario BuscarFuncionarioPorId(int id_funcionario) {
        Funcionario funcionario = new Funcionario();

        String query = "SELECT TOP 1 id_pessoal, nome, id_setor FROM tb_pessoal WHERE id_pessoal = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id_funcionario);

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                funcionario.setIdFuncionario(rs.getInt("id_pessoal"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setIdSetor(rs.getInt("id_setor"));
                //funcionario.setRegimePlantao(rs.getBoolean("regime_plantao"));
            }

            rs.close();
        } catch (Exception e) {
            showErro("BuscarFuncionarioPorId(int id_funcionario): " + e.getMessage());
            return null;
        }

        return funcionario;
    }
    public Acesso registrarAcessoOperacaoEspecial(Funcionario funcionario, String nomeTipoAcesso, int nId) {
        
        Acesso acesso = new Acesso();
        if ("S1"==nomeTipoAcesso){ 
            acesso.setFuncionario(funcionario);
            acesso.setPrimeiroAcesso(false);
            acesso.setEntrada(false);
            acesso.setAtrasado(false);
        } 
        if ("E1"==nomeTipoAcesso){             
            acesso.setFuncionario(funcionario);
            acesso.setPrimeiroAcesso(true);
            acesso.setEntrada(true);
            acesso.setAtrasado(false);
        }
        
        PreparedStatement pstmt;
        int IdFuncionario, IdSetor, nModelo;
        nModelo = nId; //0 ==> sem Tipo  (PARA NÃO PREENCHER VALOR = NULL)
        IdFuncionario = funcionario.getIdFuncionario();
        IdSetor = funcionario.getIdSetor();
        try {
            String insert = "INSERT INTO tb_acesso_operacao_especial (id_pessoal,id_setor,status,datahora, ip_ponto, id_funcao_operacao_especial) VALUES (?,?,?,GETDATE(),?,?)";
            pstmt = connection.prepareStatement(insert);
            pstmt.setInt(1, IdFuncionario);
            pstmt.setInt(2, IdSetor);
            pstmt.setString(3, nomeTipoAcesso);
            pstmt.setString(4, InetAddress.getLocalHost().getHostAddress());
            pstmt.setInt(5, nModelo);
            pstmt.executeUpdate();
            
        }
        catch (Exception e) {
            showErro("RegistrarAcessoOperacaoEspecial(int id_funcionario): " + e.getMessage());            
            //e.printStackTrace();
            //System.exit(1);
        }
        return acesso;
    }
    public Acesso registrarAcesso(Funcionario funcionario, boolean isJornadaNova) {
        //to do - Verificar ponto intra-jornadas 

        System.out.println("Conexao: Entrei no Registrar Acesso!");
        Acesso acesso = new Acesso();
        PreparedStatement pstmt;
        String jornadaDiaria = "";
        try {
            Jornada jornadaFuncionario = this.buscarJornadaFuncionarioID(funcionario.getIdFuncionario(), isJornadaNova);

            System.out.println("Tipo de Jornada do Funcionario: " + jornadaFuncionario.getTipoJornada());
            ArrayList<Ponto> pontos = this.buscarAcessosDiaFuncionario(funcionario.getIdFuncionario(), isJornadaNova);
            acesso.setFuncionario(funcionario);
            acesso.setEntrada(true);
            acesso.setPrimeiroAcesso(true);
            acesso.setAtrasado(false);
            String insert = "INSERT INTO tb_acesso (id_pessoal,id_setor,status,datahora, ip_ponto, id_modelo) VALUES (?,?,?,GETDATE(),?, ?)";
            pstmt = connection.prepareStatement(insert);
            pstmt.setInt(1, acesso.getFuncionario().getIdFuncionario());
            pstmt.setInt(2, acesso.getFuncionario().getIdSetor());
            pstmt.setString(4, InetAddress.getLocalHost().getHostAddress());
            pstmt.setInt(5, jornadaFuncionario.getId());
            
            jornadaDiaria = jornadaFuncionario.getTipoJornada();
            jornadaDiaria = jornadaDiaria.trim();

            Calendar horaAtual = Calendar.getInstance();
            //if (jornadaFuncionario.getTipoJornada().equalsIgnoreCase("N")) {
            if (jornadaDiaria.equalsIgnoreCase("N")) {
                System.out.println("registrarAcesso: Entrei no jornada de plantão noturno");

            }

            //if (jornadaFuncionario.getTipoJornada().equals("8")) {
            if (jornadaDiaria.equals("8")) {
                System.out.println("registrarAcesso: Entrei na Jornada de 8 horas");
                Calendar limiteVoltaAlmoco = jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(2));
                limiteVoltaAlmoco.add(Calendar.MINUTE, 60);

                //Calendar horarioSaida = jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3));
                //Não existem pontos no dia
                if (pontos.isEmpty()) {

                    //Hora atual é menor que saída do primeiro turno
                    if (horaAtual.before(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(1)))) {
                        System.out.println("Nenhum ponto no dia: Hora atual é menor que saída do primeiro turno - E1");
                        pstmt.setString(3, "E1");

                        // Hora atual é maior que saída do primeiro turno e menor que início do segundo turno   
                    } else if (horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(1))) && horaAtual.before(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(2)))) {
                        System.out.println("Nenhum ponto no dia: Hora atual é maior que saída do primeiro turno e menor que início do segundo turno - S1");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(false);
                        pstmt.setString(3, "S1");

                        //Hora atual é maior que início do segundo turno e antes do término do segundo turno
                    } else if (horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(2))) && horaAtual.before(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3)))) {
                        System.out.println("Nenhum ponto no dia: Hora atual é maior que início do segundo turno e antes do término do segundo turno");
                        pstmt.setString(3, "E2");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(true);
                        //Senão so pode ser saída 2
                    } else {
                        System.out.println("Nenhum ponto no dia:  Senão so pode ser saída 2 - S2");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(false);
                        pstmt.setString(3, "S2");

                    }

                } else if (pontos.size() == 1) {
                    System.out.println("Validação se tem um ponto registrado no dia:");
                    if (pontos.get(0).getTipo().equalsIgnoreCase("E1")) {
                        System.out.println("1 ponto no dia: Registrou a Entrada 1");
                        //Se hora Atual for menor que 14 horas
                        if (horaAtual.before(limiteVoltaAlmoco)) {
                            System.out.println("É antes do Modelo Entrada 2 + 60 minutos: Registra Saída 1");
                            acesso.setPrimeiroAcesso(false);
                            acesso.setEntrada(false);
                            pstmt.setString(3, "S1");

                        } else if (horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3)))) {
                            System.out.println("É depois da Saída Turno 2: Registra S2");
                            pstmt.setString(3, "S2");
                            acesso.setPrimeiroAcesso(false);
                            acesso.setEntrada(false);
                        } else {
                            System.out.println("Senão: Registra E2");
                            pstmt.setString(3, "E2");
                            acesso.setPrimeiroAcesso(false);
                            acesso.setEntrada(true);
                        }
                    } else if (pontos.get(0).getTipo().equalsIgnoreCase("S1")) {
                        System.out.println("1 ponto no dia: Registrou a Saída 1 ");
                        //Se for antes do horário de saída
                        if (horaAtual.before(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3)))) {
                            System.out.println("Saída turno 2: " + jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3)));
                            System.out.println("É antes da Saída Turno 2: Registra E2");
                            pstmt.setString(3, "E2");
                            acesso.setPrimeiroAcesso(false);
                            acesso.setEntrada(true);

                        } else {
                            System.out.println("Hora atual: " + horaAtual.toString());
                            System.out.println("É depois da Saída Turno 2: Registra S2");
                            pstmt.setString(3, "S2");
                            acesso.setPrimeiroAcesso(false);
                            acesso.setEntrada(false);
                        }

                    } else {
                        System.out.println("O ponto anterior foi a Entrada 2 logo so pode ser S2");
                        pstmt.setString(3, "S2");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(false);

                    }

                    /*
                     //Se a hora atual é maior que o horário de saída 2 ou o ponto anterior foi registrado com E2
                     if ((horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3)))) || (horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(2))) && pontos.get(0).getTipo().equalsIgnoreCase("E2"))) {
                     System.out.println("Se a hora atual é maior que o horário de saída 2 ou o ponto anterior foi registrado com E2 ");
                     pstmt.setString(3, "S2");
                     acesso.setPrimeiroAcesso(false);
                     acesso.setEntrada(false);

                     //Se a hora atual é depois da saída 1 e o ponto anterior foi a s1 ou 
                     } else if (((horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(1)))) && ((pontos.get(0).getTipo().equalsIgnoreCase("S1")))) || (((horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(2)))) && (pontos.get(0).getTipo().equalsIgnoreCase("E1"))))) {
                     System.out.println("Se a hora atual é depois da saída 1 e o ponto anterior foi a s1 ou hora atual é dpois do limite para almoço");
                     pstmt.setString(3, "E2");
                     acesso.setPrimeiroAcesso(false);
                     acesso.setEntrada(true);
                     //Se não, so pode ser a saída 1    
                     } else {
                     acesso.setPrimeiroAcesso(false);
                     acesso.setEntrada(false);
                     pstmt.setString(3, "S1");

                     }*/
                } else if (pontos.size() == 2) {

                    System.out.println("Dois Pontos Registrados no dia!");
                    //Se a Hora atual é depois do horário de saída 2 ou ponto anterior é igual a E2
                    if (horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(3))) || pontos.get(1).getTipo().equalsIgnoreCase("E2")) {
                        System.out.println("Hora Atual depois 17:30 ou Ponto Anterior = E2");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(false);
                        pstmt.setString(3, "S2");

                    } else {//Se não é entrada 2
                        System.out.println("Senão soó pode ser E2");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(true);
                        pstmt.setString(3, "E2");
                    }
                } else {//Se já tiverem 3 pontos só pode ser saída 2     
                    System.out.println("3 pontos no dia logo só pode ser S2");
                    acesso.setPrimeiroAcesso(false);
                    acesso.setEntrada(false);
                    pstmt.setString(3, "S2");
                }

                //Para jornadas de 4 e 6 horas    
            } //else if (!jornadaFuncionario.getTipoJornada().equals("E") && !jornadaFuncionario.getTipoJornada().equals("L")) {
                else if (!jornadaDiaria.equals("E") && !jornadaDiaria.equals("L")) {
                System.out.println("registrarAcesso: Entrei na Jornada de 4 e 6 horas");
                if (pontos.isEmpty()) {
                    if (horaAtual.after(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(1)))) {
                        pstmt.setString(3, "S1");
                        acesso.setPrimeiroAcesso(false);
                        acesso.setEntrada(false);
                    } else {
                        pstmt.setString(3, "E1");
                        acesso.setPrimeiroAcesso(true);
                        acesso.setEntrada(true);
                    }

                } else {
                    acesso.setPrimeiroAcesso(false);
                    acesso.setEntrada(false);
                    pstmt.setString(3, "S1");
                }

            } else {
                System.out.println("Entrei no if do E ou L");
                if (pontos.isEmpty()) {
                    System.out.println("Entrei no id do E ou L - E1");
                    pstmt.setString(3, "E1");
                    acesso.setPrimeiroAcesso(true);
                    acesso.setEntrada(true);

                } else if (pontos.size() == 1) {
                    System.out.println("Entrei no E ou L - S1");
                    pstmt.setString(3, "S1");
                    acesso.setPrimeiroAcesso(false);
                    acesso.setEntrada(false);

                } else if (pontos.size() == 2) {
                    System.out.println("Entrei no E ou L - E2");
                    pstmt.setString(3, "E2");
                    acesso.setPrimeiroAcesso(false);
                    acesso.setEntrada(true);

                } else {
                    System.out.println("Entrei no E ou L - S2");
                    pstmt.setString(3, "S2");
                    acesso.setPrimeiroAcesso(false);
                    acesso.setEntrada(false);
                }
            }

            /*
             String query = "SELECT TOP 1 status FROM tb_acesso WHERE id_pessoal = ? ORDER BY datahora DESC";

             if (acesso.getFuncionario().isRegimePlantao() == false) {
             query = "SELECT TOP 1 status FROM tb_acesso WHERE id_pessoal = ? "
             + " AND CONVERT(VARCHAR,datahora,103) = CONVERT(VARCHAR,GETDATE(),103) ORDER BY datahora DESC";
             } else {
             query = "SELECT TOP 1 DATEDIFF(minute,MAX(datahora),GETDATE()) as minutos,status FROM tb_acesso "
             + " WHERE id_pessoal = ? GROUP BY STATUS ORDER BY minutos";
             }*/
            /*pstmt = connection.prepareStatement(query);
             pstmt.setInt(1, acesso.getFuncionario().getIdFuncionario());*/
            /*
             ResultSet rs = pstmt.executeQuery();

             if (rs != null && rs.next()) {
             acesso.setEntrada(!rs.getString("status").equals("E"));
             acesso.setPrimeiroAcesso(false);
             if (acesso.getFuncionario().isRegimePlantao() == true) {
             if (Integer.parseInt(rs.getString("minutos")) > 1800) {
             acesso.setEntrada(true);
             }
             }
             }*/
            /*
             //ConfiguracaoHorario configuracaoHorario = this.BucarConfiguracaoHorario();
             //Date horarioEntradaLimite = configuracaoHorario.getHorarioEntradaLimite();
             pstmt = null;
             //pstmt = connection
             //.prepareStatement("INSERT INTO tb_acesso (id_pessoal,id_setor,status,datahora,tolerancia) VALUES (?,?,?,GETDATE(),?)");
             pstmt = connection.prepareStatement("INSERT INTO tb_acesso (id_pessoal,id_setor,status,datahora, ip_ponto) VALUES (?,?,?,GETDATE(), ?)");
             pstmt.setInt(1, acesso.getFuncionario().getIdFuncionario());
             pstmt.setInt(2, acesso.getFuncionario().getIdSetor());
             pstmt.setString(3, Character.toString(acesso.getCaracterSituacao()));
             System.out.println("Ip do Ponto: " + InetAddress.getLocalHost().getHostAddress());
             pstmt.setString(4, InetAddress.getLocalHost().getHostAddress());
             //pstmt.setTimestamp(4, new java.sql.Timestamp(horarioEntradaLimite.getTime()));
             */
            pstmt.executeUpdate();

            /*acesso.setAtrasado(Calendar.getInstance().getTime().compareTo(horarioEntradaLimite) > 
             0 && acesso.isEntrada() && !acesso.getFuncionario().isRegimePlantao() && acesso.isPrimeiroAcesso());*/
        } catch (Exception e) {
            showErro("RegistrarAcesso(int id_funcionario): " + e.getMessage());
            return null;
            //e.printStackTrace();
            //System.exit(1);
        }

        return acesso;
    }

    public boolean isForaJornada(int idFuncionario, boolean isJornadaNova) {
        try {
            System.out.println("Entrei no isForaJornada!");
            String qtdPontosDia = "SELECT count(*) AS qtdAcesso FROM tb_acesso WHERE id_pessoal = ? AND CONVERT(VARCHAR,datahora,103) = CONVERT(VARCHAR,GETDATE(),103) ";
            PreparedStatement pstmt = connection.prepareStatement(qtdPontosDia);
            pstmt.setInt(1, idFuncionario);
            int qtdAcesso;

            Jornada jornadaFuncionario = this.buscarJornadaFuncionarioID(idFuncionario, isJornadaNova);
            System.out.println("Tipo de Jornada do Funcionário: " + jornadaFuncionario.getTipoJornada());
            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Calendar horaPermitidaPonto = Calendar.getInstance();
            Calendar horaAtual = Calendar.getInstance();
            Date hora = new Date();
            String jornadaDiaria="";
            jornadaDiaria = jornadaFuncionario.getTipoJornada();
            jornadaDiaria = jornadaDiaria.trim();
            //(jornadaFuncionario.getTipoJornada().equalsIgnoreCase("L")) {
            if (jornadaDiaria.equalsIgnoreCase("L")) {
                System.out.println("idForaJornada: false! Jornada Livre!");
                return false;

            }

            //if ((!jornadaFuncionario.getTipoJornada().equalsIgnoreCase("E") || !jornadaFuncionario.getTipoJornada().equalsIgnoreCase("L")) && (horaAtual.get(Calendar.DAY_OF_WEEK) == 1 || horaAtual.get(Calendar.DAY_OF_WEEK) == 7) && !possuiHoraExtra(idFuncionario)) {
            if ((!jornadaDiaria.equalsIgnoreCase("E") || !jornadaDiaria.equalsIgnoreCase("L")) && (horaAtual.get(Calendar.DAY_OF_WEEK) == 1 || horaAtual.get(Calendar.DAY_OF_WEEK) == 7) && !possuiHoraExtra(idFuncionario)) {
                //Final de Semana
                System.out.println("Bloqueio de Final de Semana");
                return true;
            }

            //if (!jornadaFuncionario.getTipoJornada().equalsIgnoreCase("e") && (horaAtual.get(Calendar.DAY_OF_WEEK) != 1 || horaAtual.get(Calendar.DAY_OF_WEEK) != 7)) {
            if (!jornadaDiaria.equalsIgnoreCase("e") && (horaAtual.get(Calendar.DAY_OF_WEEK) != 1 || horaAtual.get(Calendar.DAY_OF_WEEK) != 7)) {

                if (rs != null && rs.next()) {
                    qtdAcesso = rs.getInt("qtdAcesso");
                    System.out.println("Quantidade de Acesso: " + qtdAcesso);

                    //if (!jornadaFuncionario.getTipoJornada().equals("8")) {
                    if (!jornadaDiaria.equals("8")) {
                        //Pega o último horário de saída para jornadas que so bate 2 pontos
                        System.out.println("Entrei na Validação de Saída para quem so bate 2 pontos");
                        String horarioSaida = jornadaFuncionario.getHorarios().get(1);

                        horaAtual.setTime(hora);

                        //Inicializa a hora de saída com a data definida no modelo
                        horaPermitidaPonto.setTime(hora);
                        horaPermitidaPonto.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(horarioSaida.substring(0, 2))));
                        horaPermitidaPonto.set(Calendar.MINUTE, (Integer.parseInt(horarioSaida.substring(3, 5))));
                        horaPermitidaPonto.set(Calendar.SECOND, 0);
                        /*if (getVinculoFuncionario(idFuncionario) == 6) {
                         horaPermitidaPonto.add(Calendar.MINUTE, 17); //Add 30 mínutos para corresponder ao horário máximo de saída
                         } else {
                         horaPermitidaPonto.add(Calendar.MINUTE, 32); //Add 30 mínutos para corresponder ao horário máximo de saída
                         }*/
                        horaPermitidaPonto.add(Calendar.MINUTE, 32); //Add 30 mínutos para corresponder ao horário máximo de saída

                        //Verifica se o horário atual não é maior que o tempo máximo para saída
                        if (horaPermitidaPonto.getTime().before(horaAtual.getTime())) {
                            if (possuiHoraExtra(idFuncionario)) {
                                return false;
                            } else {
                                return true;
                            }

                        } else if (qtdAcesso == 0) {
                            System.out.println("isForaHorario : Não bateu nenhum ponto no dia");
                            String horarioEntrada = jornadaFuncionario.getHorarios().get(0);
                            System.out.println("Horário entrada no banco: " + horarioEntrada);
                            horaAtual.setTime(hora);

                            //Inicializa a hora de saída com a data definida no modelo
                            horaPermitidaPonto.setTime(hora);

                            horaPermitidaPonto.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(horarioEntrada.substring(0, 2))));
                            System.out.println("Convertendo Hora do banco para hora do dia: " + Integer.parseInt(horarioEntrada.substring(0, 2)));
                            System.out.println("Horario Calendario após mudar a hora: " + sdf.format(horaPermitidaPonto.getTime()));
                            System.out.println("Horário entrada no banco após substrin hora: " + horarioEntrada);
                            horaPermitidaPonto.set(Calendar.MINUTE, (Integer.parseInt(horarioEntrada.substring(3, 5))));
                            System.out.println("Convertendo Minutos do banco para minutos do dia: " + Integer.parseInt(horarioEntrada.substring(3, 5)));
                            System.out.println("Horario Calendario após mudar os minutos: " + sdf.format(horaPermitidaPonto.getTime()));

                            System.out.println("Hora de Início da Jornada:  " + sdf.format(horaPermitidaPonto.getTime()));
                            //retira 30 mínutos para corresponder ao horário minimo entrada
                            /*if (getVinculoFuncionario(idFuncionario) == 6) {
                             horaPermitidaPonto.add(Calendar.MINUTE, -15);
                             } else {
                               
                             }*/
                            horaPermitidaPonto.add(Calendar.MINUTE, -30);
                            sdf = new SimpleDateFormat("HH:mm:ss");

                            System.out.println("Hora permitida entrada apartir de:  " + sdf.format(horaPermitidaPonto.getTime()));
                            System.out.println("Hora Atual:  " + sdf.format(horaAtual.getTime()));
                            System.out.println("Hora Permitida do Ponto é depois da hora atual?:  " + horaPermitidaPonto.getTime().after(horaAtual.getTime()));

                            return horaPermitidaPonto.getTime().after(horaAtual.getTime());

                        }
                        //Se for da carga horária de 8 horas
                    } else {

                        //Verifica a a hora do ponto não é maior que o horário máximo para saída
                        //Pega a última saída para o funcionário de carga horária de 8 horas
                        System.out.println("Validação Horaria de Saída para 8 horas");
                        String horarioSaida = jornadaFuncionario.getHorarios().get(3);
                        System.out.println("Horário de Saída no banco: " + horarioSaida);
                        horaAtual.setTime(hora);

                        //Inicializa a hora de saída com a data definida no modelo
                        horaPermitidaPonto.setTime(hora);
                        horaPermitidaPonto.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(horarioSaida.substring(0, 2))));
                        horaPermitidaPonto.set(Calendar.MINUTE, (Integer.parseInt(horarioSaida.substring(3, 5))));
                        horaPermitidaPonto.set(Calendar.SECOND, 0);
                        System.out.println("Hora Permitida para o Ponto: " + sdf.format(horaPermitidaPonto.getTime()));
                        /*if (getVinculoFuncionario(idFuncionario) == 6) {
                         horaPermitidaPonto.add(Calendar.MINUTE, 17); //Add 17 minutos a mais tolerância para corresponder ao horário máximo de saída
                         } else {
                         horaPermitidaPonto.add(Calendar.MINUTE, 32); //Add 30 mínutos a mais tolerância para corresponder ao horário máximo de saída
                            
                         }*/
                        horaPermitidaPonto.add(Calendar.MINUTE, 32); //Add 30 mínutos a mais tolerância para corresponder ao horário máximo de saída

                        if (horaPermitidaPonto.getTime().before(horaAtual.getTime())) {
                            if (possuiHoraExtra(idFuncionario)) {
                                return false;

                            } else {
                                System.out.println("Acesso Negado Saída 8 horas: Horário de Saída: " + sdf.format(horaPermitidaPonto.getTime()) + " é anterior ao Horário Atual: " + sdf.format(horaAtual.getTime()));
                                return true;
                            }
                        } else if (qtdAcesso == 0) {
                            //Verifica se não é maior que a entrada

                            System.out.println("isForaHorario : Não bateu nenhum ponto no dia");
                            String horarioEntrada = jornadaFuncionario.getHorarios().get(0);
                            System.out.println("Horário entrada no banco: " + horarioEntrada);
                            horaAtual.setTime(hora);

                            //Inicializa a hora de saída com a data definida no modelo
                            horaPermitidaPonto.setTime(hora);

                            horaPermitidaPonto.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(horarioEntrada.substring(0, 2))));
                            System.out.println("Convertendo Hora do banco para hora do dia: " + Integer.parseInt(horarioEntrada.substring(0, 2)));
                            System.out.println("Horario Calendario após mudar a hora: " + sdf.format(horaPermitidaPonto.getTime()));
                            System.out.println("Horário entrada no banco após substrin hora: " + horarioEntrada);
                            horaPermitidaPonto.set(Calendar.MINUTE, (Integer.parseInt(horarioEntrada.substring(3, 5))));
                            horaPermitidaPonto.set(Calendar.SECOND, 0);
                            System.out.println("Convertendo Minutos do banco para minutos do dia: " + Integer.parseInt(horarioEntrada.substring(3, 5)));
                            System.out.println("Horario Calendario após mudar os minutos: " + sdf.format(horaPermitidaPonto.getTime()));

                            System.out.println("Hora de Início da Jornada:  " + sdf.format(horaPermitidaPonto.getTime()));

                            //retira 30 mínutos para corresponder ao horário minimo entrada
                            /*if (getVinculoFuncionario(idFuncionario) == 6) {

                             horaPermitidaPonto.add(Calendar.MINUTE, -15);
                             } else {
                             horaPermitidaPonto.add(Calendar.MINUTE, -30);
                             }*/
                            horaPermitidaPonto.add(Calendar.MINUTE, -30);

                            sdf = new SimpleDateFormat("HH:mm:ss");

                            System.out.println("Hora permitida entrada apartir de:  " + sdf.format(horaPermitidaPonto.getTime()));
                            System.out.println("Hora Atual:  " + sdf.format(horaAtual.getTime()));
                            System.out.println("Hora Permitida do Ponto é depois da hora atual?:  " + horaPermitidaPonto.getTime().after(horaAtual.getTime()));

                            return horaPermitidaPonto.getTime().after(horaAtual.getTime());
                        } //else if (qtdAcesso == 2) {
                        else {
                            /*
                             ArrayList<Ponto> pontosDia = new ArrayList<>();
                             pontosDia = this.buscarAcessosDiaFuncionario(idFuncionario);
                             Iterator<Ponto> i = pontosDia.iterator();
                             Ponto p = null;
                             while (i.hasNext()) {

                               
                             //    i.next().equals("S1")
                             if (i.next().getTipo().equals("S1")) {
                             p = new Ponto();
                             p = i.next();
                             System.out.println("Encontrei o S1");
                             break;
                             }
                                
                             }

                             String finalTurno1 = jornadaFuncionario.getHorarios().get(1);
                             horaPermitidaPonto.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(finalTurno1.substring(0, 2))));
                             horaPermitidaPonto.set(Calendar.MINUTE, (Integer.parseInt(finalTurno1.substring(3, 5))));
                             horaPermitidaPonto.set(Calendar.SECOND, 0);
                             if (p != null ) {
                             System.out.println("Verificando se o bloqueio do Segundo turno");
                                
                             Jornada inicioTurno2 = new Jornada();
                                
                             //Se o ponto anterior foi antes do término da primeira jornada e a hora atual é antes do início do turno 2
                             //               12:05        antes    11:45                    e     12:40            antes        13:00  
                             if (p.getHorarioRegistro().before(horaPermitidaPonto.getTime()) && horaAtual.getTime().before(jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(2)).getTime())) {
                             System.out.println("Entrei no Bloqueio Início Turno 2");
                             return true;
                             }

                             }*/

                        }
                    }

                }
            } //else if (!jornadaFuncionario.getTipoJornada().equals('e') && (horaAtual.get(Calendar.DAY_OF_WEEK) == 1 || horaAtual.get(Calendar.DAY_OF_WEEK) == 7)) {
              else if (!jornadaDiaria.equals('e') && (horaAtual.get(Calendar.DAY_OF_WEEK) == 1 || horaAtual.get(Calendar.DAY_OF_WEEK) == 7)) {
                System.out.println("Bloqueio de Fim de Semana");
                return true;
            } else {
                System.out.println("Está dentro da Jornada! ");
                return false;

            }
            System.out.println("Está dentro da Jornada! ");
            return false;
        } catch (NumberFormatException e) {
            showErro("Excessão NumberFormatException lançada: isForaJornada(id_funcionario): \n " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            showErro("Excessão lançada: isForaJornada(id_funcionario):\n " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean limiteDiarioAtingido(int id_funcionario, boolean isJornadaNova) {
        try {

            ArrayList<Ponto> pontos = buscarAcessosDiaFuncionario(id_funcionario, isJornadaNova);
            Iterator i = pontos.iterator();
            System.out.println("LimiteDiarioAtingido - Quantidade de pontos : " + pontos.size());

            /*
             String query = "SELECT count(*) AS qtdAcesso FROM tb_acesso WHERE id_pessoal = ? "
             + " AND CONVERT(VARCHAR,datahora,103) = CONVERT(VARCHAR,GETDATE(),103)";
             PreparedStatement pstmt = connection.prepareStatement(query);
             pstmt.setInt(1, id_funcionario);

             ResultSet rs = pstmt.executeQuery();*/
            //if (rs != null && rs.next()) {
            Jornada jornadaFuncionario = this.buscarJornadaFuncionarioID(id_funcionario, isJornadaNova);
            String jornadaDiaria="";
            jornadaDiaria = jornadaFuncionario.getTipoJornada();
            jornadaDiaria = jornadaDiaria.trim();
            //if (!jornadaFuncionario.getTipoJornada().equals("8") && !jornadaFuncionario.getTipoJornada().equals("L")) {
            if (!jornadaDiaria.equals("8") && !jornadaDiaria.equals("L")) {
                while (i.hasNext()) {
                    Ponto p = (Ponto) i.next();
                    if (p.getTipo().equalsIgnoreCase("S1")) {
                        return true;
                    }
                }
                /*
                 if (rs.getInt("qtdAcesso") == 2) {
                 return true;
                 }*/
            } else {
                while (i.hasNext()) {
                    Ponto p = (Ponto) i.next();
                    if (p.getTipo().equalsIgnoreCase("S2")) {
                        return true;
                    }
                }/*
                 if (rs.getInt("qtdAcesso") == 4) {
                 return true;
                 }*/

            }

        } catch (Exception e) {
            showErro("limiteDiarioAtingido(int id_funcionario): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return false;
    }

// Método Responsável por abrir a conexão com o banco
    public void openConnection() {
        connection = null;
        try {

            // Load the JDBC driver
            String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            Class.forName(driverName);

            //Dados do servidor de homologação Afrodite
            String server = "172.22.8.17";
            //Database Produção
            //String dataBase = "DB_ACESSO";
            //Database Teste/Homologação
            String dataBase = "DB_ACESSO_HOMOLOG";
            System.out.println("Connectado com o: " + dataBase);
            String usuario = "acesso";
            String senha = "#4c3ss0$";

            String connectionUrl = "jdbc:sqlserver://" + server
                    + ";databaseName=" + dataBase + ";";

            connection = DriverManager.getConnection(connectionUrl, usuario,
                    senha);
        } catch (ClassNotFoundException ce) {
            // Could not find the database driver
            //showErro("openConnection(): " + ce.getMessage());
            System.out.println("Erro de Conexão: " + ce.getMessage());
        } catch (SQLException sqlExecption) {
            //showErro("openConnection(): " + sqlExecption.getMessage());
            System.out.println("Erro de Conexão [Sql-Exception]: "
                    + sqlExecption.getMessage());
        } catch (Exception e) {
            showErro("openConnection(): " + e.getMessage());
            System.out.println("Exceção");
        }

    }

    public Jornada buscarJornadaID(int idJornada) throws SQLException {
        Jornada jornada = null;
        try {
            String query = "Select * from tb_modelo where id_modelo = ? ";

            PreparedStatement pstmt = connection.prepareStatement(query);

            pstmt.setInt(1, idJornada);

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                String jornadaDiaria = rs.getString("jornada");
                jornadaDiaria = jornadaDiaria.trim();
                if (jornadaDiaria.equals("8")) {
                    jornada = new Jornada(jornadaDiaria);
                    jornada.setId(idJornada);
                    jornada.getHorarios().add(0, rs.getString("entrada1"));
                    jornada.getHorarios().add(1, rs.getString("saida1"));
                    jornada.getHorarios().add(2, rs.getString("entrada2"));
                    jornada.getHorarios().add(3, rs.getString("saida1"));

                } else {
                    jornada = new Jornada(jornadaDiaria);
                    jornada.setId(idJornada);
                    jornada.getHorarios().add(0, rs.getString("entrada1"));
                    jornada.getHorarios().add(1, rs.getString("saida1"));

                }

            }
            return jornada;

        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class
                    .getName()).log(Level.SEVERE, null, ex);

        }

        return jornada;

    }

    public Jornada buscarJornadaFuncionarioID(int idFuncionario, boolean isJornadaNova) {
        
        String query;
        String entrada1 = "";
        String entrada2 = "";
        String saida1 = "";
        String saida2 = "";
        
        if (true == isJornadaNova){
            query = "Select * from tb_modelo where id_modelo = (SELECT id_modelo_compensacao from tb_modelo_compensacao WHERE id_modelo = (Select id_modelo from tb_pessoal where id_pessoal = ?))";
            
        } else {
            query = "Select * from tb_modelo where id_modelo = (Select id_modelo from tb_pessoal where id_pessoal = ?)";
        }
        Jornada jornada = this.buscarPermutaJornadaFuncionarioID(idFuncionario, isJornadaNova);
        try {

            if (jornada == null) {
                PreparedStatement pstmt = connection.prepareStatement(query);

                pstmt.setInt(1, idFuncionario);
                ResultSet rs = pstmt.executeQuery();

                if (rs != null && rs.next()) {
                    String jornadaDiaria = rs.getString("jornada");
                    System.out.println("jornada = " + jornadaDiaria);
                    jornadaDiaria = jornadaDiaria.trim();
                    if (jornadaDiaria.equals("8")) {
                        jornada = new Jornada(jornadaDiaria);
                        jornada.getHorarios().add(0, rs.getString("entrada1"));
                        jornada.getHorarios().add(1, rs.getString("saida1"));
                        jornada.getHorarios().add(2, rs.getString("entrada2"));
                        jornada.getHorarios().add(3, rs.getString("saida2"));
                        jornada.setId(rs.getInt("id_modelo"));
                        
                        entrada1 = rs.getString("entrada1");
                        saida1 = rs.getString("saida1");
                        entrada2 = rs.getString("entrada2");
                        saida2 = rs.getString("saida2");
                        System.out.println("hora entrada1 = " + entrada1);
                        System.out.println("hora saida1 = " + saida1);
                        System.out.println("hora entrada2 = " + entrada2);
                        System.out.println("hora saida2 = " + saida2);

                    } else {
                        jornada = new Jornada(jornadaDiaria);
                        jornada.getHorarios().add(0, rs.getString("entrada1"));
                        jornada.getHorarios().add(1, rs.getString("saida1"));
                        jornada.setId(rs.getInt("id_modelo"));
                        
                        entrada1 = rs.getString("entrada1");
                        saida1 = rs.getString("saida1");
                        entrada2 = rs.getString("entrada2");
                        saida2 = rs.getString("saida2");
                        System.out.println("hora entrada1 = " + entrada1);
                        System.out.println("hora saida1 = " + saida1);
                        System.out.println("hora entrada2 = " + entrada2);
                        System.out.println("hora saida2 = " + saida2);

                    }

                }
            }

        } catch (SQLException ex) {

            this.showErro("buscarJornadaFuncionarioID(int idFuncionario): idFuncionario =  " + idFuncionario);
            Logger
                    .getLogger(Conexao.class
                            .getName()).log(Level.SEVERE, null, ex);

        }

        return jornada;

    }

    public Jornada buscarPermutaJornadaFuncionarioID(int idFuncionario, boolean isJornadaNova) {
        System.out.println("Entrei no PermutaJornadaFuncionarioID!");
        String queryPermuta;
        
        if (true == isJornadaNova) {
            queryPermuta = "Select * from tb_modelo where id_modelo = (SELECT id_modelo_compensacao from tb_modelo_compensacao WHERE id_modelo = (select id_modelo from tb_modelo_funcionario_permuta where id_funcionario = ? and CONVERT(VARCHAR,getdate(),102) BETWEEN CONVERT(VARCHAR,data_inicio,102) AND CONVERT(VARCHAR,data_fim,102)  and STATUS = 1))";
            
        } else {
            queryPermuta = "select id_modelo from tb_modelo_funcionario_permuta where id_funcionario = ? and CONVERT(VARCHAR,getdate(),102) BETWEEN CONVERT(VARCHAR,data_inicio,102) AND CONVERT(VARCHAR,data_fim,102)  and STATUS = 1";
        }
        
        Jornada jornada = null;
        try {

            PreparedStatement pstmt = connection.prepareStatement(queryPermuta);

            pstmt.setInt(1, idFuncionario);
            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                System.out.println("Encontrei uma permuta!");
                jornada = this.buscarJornadaID(rs.getInt("id_modelo"));
                jornada.setId(rs.getInt("id_modelo"));
                System.out.println("BuscarPermutaJornadaFuncionarioID: " + jornada.getId());

            }
            return jornada;
        } catch (SQLException ex) {
            this.showErro("buscarJornadaFuncionarioID(int idFuncionario): idFuncionario =  " + idFuncionario);
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);

        }

        return jornada;

    }

    // Método Responsável por Fechar a conexão com o banco
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            showErro("openConnection(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getVinculoFuncionario(int idFuncionario) {
        int vinculo = 0;
        try {
            String query = "SELECT id_vinculo FROM tb_pessoal WHERE id_pessoal = ? ";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idFuncionario);

            ResultSet rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                vinculo = rs.getInt("id_vinculo");
                return vinculo;

            }

        } catch (SQLException e) {
            showErro("getVinculoFuncionario(int id_funcionario): " + e.getMessage());
            e.printStackTrace();

        }
        return vinculo;

    }

    int buscarAcessosDiaFuncionarioOperacaoEspecial(int idFuncionario) {
        int nQtde = 0;
        String query = "select count(*) AS qtde_registros from tb_acesso_operacao_especial where id_pessoal = ? and  convert(varchar(30),datahora,102) = convert(varchar(30),getdate(),102)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idFuncionario);

            ResultSet rs = pstmt.executeQuery();
            
            rs.next();
            nQtde = rs.getInt("qtde_registros");

//            while (rs != null && rs.next()) {
//                nQtde = rs.getInt("rowcount");
//                Ponto ponto = new Ponto();
//
//                ponto.setHorarioRegistro(rs.getDate("datahora"));
//                ponto.setTipo(rs.getString("status"));
//                ponto.setId(rs.getInt("id_acesso"));
//                acessosDiarios.add(ponto);
//                System.out.println("buscarAcessosDiaFuncionario - Encontrei um ponto : " + ponto.getTipo());
//
            }

        catch (SQLException e) {
            showErro("getAcessosFuncionarioOperacaoEspecial(int id_funcionario): " + e.getMessage());
            e.printStackTrace();

        }
        
        
        return nQtde;
    }
    
    public ArrayList<Ponto> buscarAcessosDiaFuncionario(int idFuncionario, boolean isJornadaNova) {

        String query = "select * from tb_acesso where id_pessoal = ? and  convert(varchar(30),datahora,102) = convert(varchar(30),getdate(),102) order by datahora";

        ArrayList<Ponto> acessosDiarios = new ArrayList<>();
        Jornada jornadaFuncionario = this.buscarJornadaFuncionarioID(idFuncionario, isJornadaNova);
        String jornadaDiaria = "";
        jornadaDiaria = jornadaFuncionario.getTipoJornada();
        jornadaDiaria = jornadaDiaria.trim();
        //if (jornadaFuncionario.getTipoJornada().equalsIgnoreCase("N")) {
        if (jornadaDiaria.equalsIgnoreCase("N")) {
            System.out.println("buscarAcessoDiaFuncionario: Entrei no Funcionário noturno");
            Calendar horaAtual = Calendar.getInstance();
            SimpleDateFormat data = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String queryPlantaoNoturno = "select * from tb_acesso where id_pessoal = ? and  datahora >= ? ";
            Date dataPesquisa;
            PreparedStatement pstmt;
            try {
                System.out.println("Hora do Modelo: " + jornadaFuncionario.getHorarios().get(0) + " e hora atual: " + cal.getTime());
                if (jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(0)).before(cal.getTime())) {
                    System.out.println("Hora do Modelo: " + jornadaFuncionario.getHorarios().get(0) + " é depois da hora atual: " + cal.getTime());
                    System.out.println("QueryPlantaoNoturno: " + queryPlantaoNoturno);
                    pstmt = connection.prepareStatement(queryPlantaoNoturno);
                    dataPesquisa = jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(0)).getTime();
                    java.sql.Date d = new java.sql.Date(dataPesquisa.getTime());
                    pstmt.setInt(1, idFuncionario);
                    pstmt.setDate(2, d);

                } else {//Hora atual é menor que a hora de entrada (Ex: 06:00 < 19:00)
                    System.out.println("Hora do Modelo: " + jornadaFuncionario.getHorarios().get(0) + " é depois da hora atual: " + cal.getTime());
                    cal = jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(0));
                    cal.add(Calendar.DAY_OF_MONTH, -1); //Diminui um dia
                    dataPesquisa = cal.getTime();
                    cal = jornadaFuncionario.convertStringCalendar(jornadaFuncionario.getHorarios().get(1));
                    cal.add(Calendar.MINUTE, 5);
                    Date dataPesquisaSaida = cal.getTime();
                    String verificarSaida = " AND datahora <= ?";
                    queryPlantaoNoturno = queryPlantaoNoturno.concat(verificarSaida);
                    System.out.println("QueryPlantaoNoturno: " + queryPlantaoNoturno);
                    pstmt = connection.prepareStatement(queryPlantaoNoturno);

                    pstmt.setInt(1, idFuncionario);
                    java.sql.Date d = new java.sql.Date(dataPesquisa.getTime());
                    pstmt.setDate(2, d);
                    d = new java.sql.Date(dataPesquisaSaida.getTime());
                    pstmt.setDate(3, d);

                }

                ResultSet rs = pstmt.executeQuery();

                while (rs != null && rs.next()) {
                    Ponto ponto = new Ponto();

                    ponto.setHorarioRegistro(rs.getDate("datahora"));
                    ponto.setTipo(rs.getString("status"));
                    ponto.setId(rs.getInt("id_acesso"));
                    acessosDiarios.add(ponto);
                    System.out.println("buscarAcessosDiaFuncionario - Tipo : " + ponto.getTipo());
                    System.out.println("buscarAcessosDiaFuncionario - Data : " + ponto.getHorarioRegistro());

                }

            } catch (SQLException e) {
                showErro("getAcessosFuncionario(int id_funcionario): " + e.getMessage());
                e.printStackTrace();

            }

            return acessosDiarios;

        }

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idFuncionario);

            ResultSet rs = pstmt.executeQuery();

            while (rs != null && rs.next()) {
                Ponto ponto = new Ponto();

                ponto.setHorarioRegistro(rs.getDate("datahora"));
                ponto.setTipo(rs.getString("status"));
                ponto.setId(rs.getInt("id_acesso"));
                acessosDiarios.add(ponto);
                System.out.println("buscarAcessosDiaFuncionario - Encontrei um ponto : " + ponto.getTipo());

            }

        } catch (SQLException e) {
            showErro("getAcessosFuncionario(int id_funcionario): " + e.getMessage());
            e.printStackTrace();

        }
        return acessosDiarios;
    }

    public ArrayList<AfastamentoLegal> buscarAfastamentosLegaisDia(int idFuncionario) {
        String query = "select * from tb_afastamento_legal where id_funcionario = ? and  convert(varchar(30),data_ini,102) <= convert(varchar(30), getDate(),102) and convert(varchar(30),data_fim,102) >= convert(varchar(30), getDate(),102) and ativo = 1 ";
        ArrayList<AfastamentoLegal> afastamentos = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idFuncionario);

            ResultSet rs = pstmt.executeQuery();

            while (rs != null && rs.next()) {
                AfastamentoLegal afastamento = new AfastamentoLegal();
                afastamento.setAtivo(rs.getBoolean("ativo"));
                afastamento.setDataCadastro(rs.getDate("data_cadastro"));
                afastamento.setDataFim(rs.getDate("data_fim"));
                afastamento.setDataInicio(rs.getDate("data_ini"));
                afastamento.setId(rs.getInt("id_afastamento_legal"));
                afastamento.setObservacao(rs.getString("observacao"));
                Funcionario responsavel = new Funcionario();
                responsavel.setIdFuncionario(rs.getInt("id_responsavel"));
                afastamento.setResponsavel(responsavel);

                afastamentos.add(afastamento);

            }

        } catch (SQLException e) {
            showErro("buscarAfastamentosLegaisDia(" + idFuncionario + ") " + e.getMessage());
            e.printStackTrace();

        }
        return afastamentos;

    }

    public boolean possuiHoraExtra(int idFuncionario) {
        boolean retorno = false;
        String query = "select id_hora_extra from tb_hora_extra where id_pessoal = ? and  convert(varchar(30),data_hora_ini, 113) < convert(varchar(30),getdate(), 113) and convert(varchar(30),data_hora_fim, 113) > convert(varchar(30),getdate(), 113)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idFuncionario);

            ResultSet rs = pstmt.executeQuery();

            if (rs != null && rs.next()) {
                retorno = true;

            }

        } catch (SQLException e) {
            showErro("buscarAfastamentosLegaisDia(" + idFuncionario + ") " + e.getMessage());
            e.printStackTrace();

        }
        return retorno;
    }

    private void showErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

}
