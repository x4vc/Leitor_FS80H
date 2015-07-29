package DAO;


/**
*
* @author Tulio Adorno da Silva Cruz
* 
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionUtil {


    private ConnectionUtil(){}
    private static Connection con=null;

    private static final String DRIVER="net.sourceforge.jtds.jdbc.Driver";
    
    public static Connection getConnection(String user,String pass,String database,String server){
            try{
                Class.forName(DRIVER);
            }catch(ClassNotFoundException cnfe){
                cnfe.printStackTrace();
            }
            try{  	        
                con=DriverManager.getConnection("jdbc:jtds:sqlserver://"+server+":1433/"+database,user,pass);	
            }catch(SQLException se){
                se.printStackTrace();
            }catch(Exception e)
            {
               e.printStackTrace();
            }
            return con; 	
    }

    public static void closeConnection(Connection con){
        if(con!=null){
            try{
                con.close();
                con=null;
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}
