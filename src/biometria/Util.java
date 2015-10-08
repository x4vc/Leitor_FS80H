package biometria;
/*
 -------------------------------------------------------------------------------
 Fingerprint SDK Sample
 (c) 2005-2007 Griaule Tecnologia Ltda.
 http://www.griaule.com
 -------------------------------------------------------------------------------

 This sample is provided with "Fingerprint SDK Recognition Library" and
 can't run without it. It's provided just as an example of using Fingerprint SDK
 Recognition Library and should not be used as basis for any
 commercial product.

 Griaule Biometrics makes no representations concerning either the merchantability
 of this software or the suitability of this sample for any particular purpose.

 THIS SAMPLE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL GRIAULE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 You can download the trial version of Fingerprint SDK directly from Griaule website.

 These notices must be retained in any copies of any part of this
 documentation and/or sample.

 -------------------------------------------------------------------------------
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.griaule.grfingerjava.FingerprintImage;
import com.griaule.grfingerjava.GrFingerJava;
import com.griaule.grfingerjava.GrFingerJavaException;
import com.griaule.grfingerjava.IFingerEventListener;
import com.griaule.grfingerjava.IImageEventListener;
import com.griaule.grfingerjava.IStatusEventListener;
import com.griaule.grfingerjava.MatchingContext;
import com.griaule.grfingerjava.Template;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.Alert;


/**
 * Class responsible for handling Fingerprint SDK.
 *
 * It handles fingerprint loading/capturing, template extraction, fingerprint
 * image saving and storing/retrieving from template base.
 */
public class Util implements IStatusEventListener, IImageEventListener, IFingerEventListener {

    /**
     * Fingerprint SDK context used for capture / extraction / matching of
     * fingerprints.
     */
    private MatchingContext fingerprintSDK;
    /**
     * User interface, where logs, images and other things will be sent.
     */
    private FormMain ui;

    /**
     * Sets if template must be automatically extracted after capture.
     */
    private boolean autoExtract = true;
    /**
     * Sets if template must be automatically identified after capture. It's
     * only effective when *autoExtract == true)
     */
    private boolean autoIdentify = false;

    /**
     * The last fingerprint image acquired.
     */
    private FingerprintImage fingerprint;
    /**
     * The template extracted from the last acquired image.
     */
    private Template template;

    /**
     * Creates a new Util to be used by the specified Main Form.
     *
     * Initializes fingerprint capture and database connection.
     */
    public Util(FormMain ui) {
        this.ui = ui;

        //Initializes DB connection
        initDB();
        //Initializes Fingerprint SDK and enables fingerprint capture.
        initFingerprintSDK();
    }

    /**
     * Stops fingerprint capture and closes the database connection.
     */
    public void destroy() {
        destroyFingerprintSDK();
        destroyDB();
    }

    /**
     * Initializes Fingerprint SDK and enables fingerprint capture.
     */
    private void initFingerprintSDK() {
        try {
            //Verificar arquitetura do SO
            String arch = System.getenv("PROCESSOR_ARCHITECTURE");
            String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

            String realArch = arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? "64" : "32";
            System.out.println("PROCESSOR_ARCHITECTURE = " + realArch);
            if ("32".equals(realArch)){
                // install Fingerprint SDK files 32 bits in a temporary directory
                AppletInstaller.install(getClass().getResource("/FingerprintSDKLibs.zip"));
                System.out.println("SO Windows 32 bits");
                
           } else {
                // install Fingerprint SDK files 64 bits in a temporary directory
                AppletInstaller.install(getClass().getResource("/FingerprintSDKLibsx64.zip"));     
                System.out.println("SO Windows 64 bits");
           }

            //Método Responsável por carregar o SPLASH SCREEN
            fingerprintSDK = new MatchingContext();

            //Runtime.getRuntime().exec("net stop java.exe"); 
            Process processo = Runtime.getRuntime().exec("java.exe");
            processo.destroy();

            //Starts fingerprint capture.
            GrFingerJava.initializeCapture(this);

            ui.writeLog("**Fingerprint SDK Inicializado com Sucesso**");

        } catch (Exception e) {
            //If any error ocurred while initializing Fingerprint SDK,
            //writes the error to log
            e.printStackTrace();
            ui.writeLog(e.getMessage());
        }
    }

    /**
     * Stops fingerprint capture.
     */
    private void destroyFingerprintSDK() {
        try {
            GrFingerJava.finalizeCapture();
        } catch (GrFingerJavaException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called every time a fingerprint reader is plugged.
     *
     * @see griaule.grFinger.StatusCallBack#onPlug(java.lang.String)
     */
    public void onSensorPlug(String idSensor) {
        //Logs the sensor has been pluged.
        ui.writeLog("Sensor: " + idSensor + ". Evento: Plugado.");
        try {
            //Start capturing from plugged sensor.
            GrFingerJava.startCapture(idSensor, this, this);
        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog("Erro no sensor: " + e.getMessage());
        }
    }

    /**
     * This function is called every time a fingerprint reader is unplugged.
     *
     * @see griaule.grFinger.StatusCallBack#onUnplug(java.lang.String)
     */
    public void onSensorUnplug(String idSensor) {
        //Logs the sensor has been unpluged.
        ui.writeLog("Sensor: " + idSensor + ". Evento: Desplugado.");
        try {
            GrFingerJava.stopCapture(idSensor);
        } catch (GrFingerJavaException e) {
            ui.writeLog(e.getMessage());
        }
    }

    /**
     * This function is called every time a fingerfrint image is captured.
     *
     * @see griaule.grFinger.ImageCallBack#onImage(java.lang.String,
     * griaule.grFinger.FingerprintImage)
     */
    public void onImageAcquired(String idSensor, FingerprintImage fingerprint) {
        //Logs that an Image Event occurred.
        ui.writeLog("Sensor: " + idSensor + ". Evento: Imagem Capturada.");

        //Stores the captured Fingerprint Image
        this.fingerprint = fingerprint;

        //Display fingerprint image
        ui.showImage(fingerprint);

        //Chama o mï¿½todo responsï¿½vel por salvar ou reconhecer a imagem
        extract2();

    }

    /**
     * This Function is called every time a finger is placed on sensor.
     *
     * @see griaule.grFinger.FingerCallBack#onFingerDown(java.lang.String)
     */
    public void onFingerDown(String idSensor) {
        // Just signals that a finger event ocurred.
        ui.writeLog("Sensor: " + idSensor + ". Event: Finger Placed.");
    }

    /**
     * This Function is called every time a finger is removed from sensor.
     *
     * @see griaule.grFinger.FingerCallBack#onFingerUp(java.lang.String)
     */
    public void onFingerUp(String idSensor) {
        // Just signals that a finger event ocurred.
        ui.writeLog("Sensor: " + idSensor + ". Event: Finger Removed.");
    }

    /**
     * Sets the colors used to paint templates.
     */
    public void setBiometricDisplayColors(
            Color minutiaeColor, Color minutiaeMatchColor,
            Color segmentColor, Color segmentMatchColor,
            Color directionColor, Color directionMatchColor) {
        try {
            // set new colors for BiometricDisplay
            GrFingerJava.setBiometricImageColors(
                    minutiaeColor, minutiaeMatchColor,
                    segmentColor, segmentMatchColor,
                    directionColor, directionMatchColor);

        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog(e.getMessage());
        }
    }

    /**
     * Returns a String containing information about the version of Fingerprint
     * SDK being used.
     *
     * For instace: -------------------------------- Fingerprint SDK version
     * 5.0. The license type is 'Identification'.
     * --------------------------------
     */
    public String getFingerprintSDKVersion() {
        try {
            return "Fingerprint SDK versï¿½o " + GrFingerJava.getMajorVersion() + "." + GrFingerJava.getMinorVersion() + "\n"
                    + "Tipo da Licensa ï¿½ '" + (GrFingerJava.getLicenseType() == GrFingerJava.GRFINGER_JAVA_FULL ? "Identification" : "Verification") + "'.";

        } catch (GrFingerJavaException e) {
            return null;
        }
    }

    /**
     * returns the current fingerprint image, without any biometric information.
     */
    public BufferedImage getFingerprint() {
        return this.fingerprint;
    }

    /**
     * Saves the fingerprint image to a file using an ImageWriterSpi. See
     * ImageIO API.
     */
    public void saveToFile(File file, ImageWriterSpi spi) {
        try {
            //Creates a image writer.
            ImageWriter writer = spi.createWriterInstance();
            ImageOutputStream output = ImageIO.createImageOutputStream(file);
            writer.setOutput(output);

            //Writes the image.
            writer.write(fingerprint);

            //Closes the stream.
            output.close();
            writer.dispose();
        } catch (IOException e) {
            // write error to log
            ui.writeLog(e.toString());
        }

    }

    /**
     * Loads a fingerprint image from file using an ImageReaderSpi. See ImageIO
     * API.
     */
    public void loadFile(File file, int resolution, ImageReaderSpi spi) {
        try {
            //Creates a image reader.
            ImageReader reader = spi.createReaderInstance();
            ImageInputStream input = ImageIO.createImageInputStream(file);
            reader.setInput(input);
            //Reads the image.
            BufferedImage img = reader.read(0);
            //Close the stream
            reader.dispose();
            input.close();
            // creates and processes the fingerprint image
            onImageAcquired("File", new FingerprintImage(img, resolution));
        } catch (Exception e) {
            // write error to log
            ui.writeLog(e.toString());
        }
    }

    /**
     * Sets the parameters used for identifications / verifications.
     */
    public void setParameters(int identifyThreshold, int identifyRotationTolerance, int verifyThreshold, int verifyRotationTolorance) {
        try {
            fingerprintSDK.setIdentificationThreshold(identifyThreshold);
            fingerprintSDK.setIdentificationRotationTolerance(identifyRotationTolerance);
            fingerprintSDK.setVerificationRotationTolerance(verifyRotationTolorance);
            fingerprintSDK.setVerificationThreshold(verifyThreshold);

        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog(e.getMessage());
        }
    }

    /**
     * Returns the current verification threshold.
     */
    public int getVerifyThreshold() {
        try {
            //Try to get the parameters from Fingerprint SDK.
            return fingerprintSDK.getVerificationThreshold();
        } catch (GrFingerJavaException e) {
            //If fails to load the parameters, writes error to log and returns 0
            ui.writeLog(e.getMessage());
            return 0;
        }
    }

    /**
     * Returns the current rotation tolerance on verifications.
     */
    public int getVerifyRotationTolerance() {
        try {
            //Try to get the parameters from Fingerprint SDK.
            return fingerprintSDK.getVerificationRotationTolerance();
        } catch (GrFingerJavaException e) {
            //If fails to load the parameters, writes error to log and returns 0
            ui.writeLog(e.getMessage());
            return 0;
        }
    }

    /**
     * Returns the current identification threshold.
     */
    public int getIdentifyThreshold() {
        try {
            //Try to get the parameters from Fingerprint SDK.
            return fingerprintSDK.getIdentificationThreshold();
        } catch (GrFingerJavaException e) {
            //If fails to load the parameters, writes error to log and returns 0
            ui.writeLog(e.getMessage());
            return 0;
        }
    }

    /**
     * Returns the current rotation tolerance on identification.
     */
    public int getIdentifyRotationTolerance() {
        try {
            //Try to get the parameters from Fingerprint SDK.
            return fingerprintSDK.getIdentificationRotationTolerance();
        } catch (GrFingerJavaException e) {
            //If fails to load the parameters, writes error to log and returns 0
            ui.writeLog(e.getMessage());
            return 0;
        }
    }

    /**
     * Enables / Disables automatic fingerprint identification after capture.
     *
     * As identification must be done after template extraction, this property
     * will only be effective if autoExtract if set to true.
     */
    public void setAutoIdentify(boolean state) {
        autoIdentify = state;
    }

    /**
     * Enables / Disables automatic fingerprint extraction after capture.
     */
    public void setAutoExtract(boolean state) {
        autoExtract = state;
    }

    /**
     * The applet version of this sample stores all fingerprints in memory.
     */
    private List database;

    //Classe responsï¿½vel por abrir a conexao com o oracle
    private Conexao conexao;

    /**
     * Initializes the database.
     */
    private void initDB() {
        //On the applet sample, the database is stored on a in-memory list.
        //database = new ArrayList();
        //Retorna uma instancia do objeto conexï¿½o
        conexao = Conexao.instance();
        //Abre a conexï¿½o passando a tring que representa qual banco se conectar
        conexao.openConnection();

    }

    /**
     * Closes the connection to the database and frees any resources used.
     */
    private void destroyDB() {
        //On the applet sample, no database connection is used.
        conexao.closeConnection();
    }

    /**
     * Verifica se a fingerprint da tela ï¿½ igual a algum do Banco.
     */
    public void verify(int id) {
        try {
            //Gets the template with supplied ID from database.
            Template referenceTemplate = (Template) database.get(id);

            //Compares the templates.
            boolean matched = fingerprintSDK.verify(template, referenceTemplate);

            //Se os templates combinam
            if (matched) {
                //displays minutiae/segments/directions that matched.
                ui.showImage(GrFingerJava.getBiometricImage(template, fingerprint, fingerprintSDK));
                //Notifies the templates did match.
                ui.writeLog("Matched with score = " + fingerprintSDK.getScore() + ".");
            } else {
                //Notifies the templates did not match.
                ui.writeLog("Did not match with score = " + fingerprintSDK.getScore() + ".");
            }
        } catch (IndexOutOfBoundsException e) {
            //Invalid ID was typed.
            ui.writeLog("The suplied ID does not exists.");
        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog(e.getMessage());
        }
    }

    /**
     * Extract a fingerprint template from current image.
     */
    public void extract() {

        try {
            //Extracts a template from the current fingerprint image.
            template = fingerprintSDK.extract(fingerprint);

            //Notifies it has been extracted and the quality of the extraction
            String msg = "Template extracted successfully. ";
            //write template quality to log
            switch (template.getQuality()) {
                case Template.HIGH_QUALITY:
                    msg += "Boa Qualidade.";
                    break;
                case Template.MEDIUM_QUALITY:
                    msg += "Qualidade Mï¿½dia.";
                    break;
                case Template.LOW_QUALITY:
                    msg += "Qualidade Ruim.";
                    break;
            }
            ui.writeLog(msg);

            //Notifies the UI that template operations can be enabled.
            //TODO COMENTADO
            //ui.enableTemplate();
            //display minutiae/segments/directions into image
        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog(e.getMessage());
        }

    }

    /**
     *
     */
    public void extract2() {

        try {
            //Extracts a template from the current fingerprint image.
            template = fingerprintSDK.extract(fingerprint);
            //String msg = "";

            //Verifica se a inicialização da applet é para registro de ponto ou
            //cadastro de digital para um funcionário
            if (ui.RegistrarPonto() == true) {
                if (template.getQuality() == Template.HIGH_QUALITY
                        || template.getQuality() == Template.MEDIUM_QUALITY) {
                    identify();
                } else {
                    try {
                        ui.getAppletContext().showDocument(new URL("javascript:baixaQualidadeDigital()"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //verifica se a qualidade da imagem é desejável
                //Caso seja salva no banco
                //if (ui.getIdFuncionario() != -1 && ui.getDigital() != null){
                if (ui.getIdFuncionario() != -1 && ui.getNumeroDedo() != 0) {
                    if (template.getQuality() == Template.HIGH_QUALITY
                            || template.getQuality() == Template.MEDIUM_QUALITY) { //|| template.getQuality() == Template.MEDIUM_QUALITY
                        enroll();
                        //Notifica no Console da applet que a digital foi extraï¿½da com sucesso
                    } else {
                        try {
                            ui.getAppletContext().showDocument(new URL("javascript:baixaQualidadeDigital()"));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        //msg = "Digital com Baixa Qualidade. Favor retirar digital novamente.";
                    }
                    //ui.writeLog(msg);
                }
            }

        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog(e.getMessage());
        }
    }

    /**
     * Identifies the current fingerprint on the DB.
     */
    public void identify() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        /*ConfiguracaoHorario configuracaoHorario = conexao.BucarConfiguracaoHorario();
    		
         if (configuracaoHorario.AcessoDisponivelRegistro()){*/
        boolean comparacaoPolegar = false;
        int id_funcionario = -1;

        //Busca no banco os funcionários cadastrados
        try {
            Template templateCapturado = new Template(template.getQuality(), template.getData());
            fingerprintSDK.setIdentificationThreshold(30);
            fingerprintSDK.prepareForIdentification(templateCapturado);

            ResultSet rs = conexao.ListarBiometriasFuncionarios();

            ui.writeLog("HORARIO INICIAL: " + dateFormat.format(new Date()));

            while (rs.next() && comparacaoPolegar == false) {
                id_funcionario = rs.getInt("ID_FUNCIONARIO");
                
                //System.out.println("Id do funcionário (método identify()) = " + id_funcionario);

                //byte[] bytesPolegarDireito  = rs.getBytes("BYTES_POLEGAR_DIREITO");
                byte[] bytesDigital = rs.getBytes("BYTES_DIGITAL");
                //int qualidadeDigital = rs.getInt("QUALIDADE_POLEGAR_DIREITO");
                //ui.writeLog("BYTES POLEGAR DIREITO");
                Template templatePolegar = null;

                /*if (bytesPolegarDireito != null && bytesPolegarDireito.length > 0
                 && rs.getObject("QUALIDADE_POLEGAR_DIREITO") != null)
                 {
                 templatePolegar = new Template(rs.getInt("QUALIDADE_POLEGAR_DIREITO"), bytesPolegarDireito);
                 comparacaoPolegar = fingerprintSDK.identify(templatePolegar);
                 }*/
                if (bytesDigital != null && bytesDigital.length > 0
                        && rs.getObject("QUALIDADE_DIGITAL") != null) {
                    templatePolegar = new Template(rs.getInt("QUALIDADE_DIGITAL"), bytesDigital);
                    comparacaoPolegar = fingerprintSDK.identify(templatePolegar);
                }

                /*if (comparacaoPolegar == false)
                 {
                 byte[] bytesPolegarEsquerdo = rs.getBytes("BYTES_POLEGAR_ESQUERDO");
                 //qualidadeDigital = rs.getInt("QUALIDADE_POLEGAR_ESQUERDO");
                 //ui.writeLog("Qualidade digital esquerdo: " + qualidadeDigital); 
            				
                 if (bytesPolegarEsquerdo != null && bytesPolegarEsquerdo.length > 0
                 && rs.getObject("QUALIDADE_POLEGAR_ESQUERDO") != null){
                 templatePolegar = new Template(rs.getInt("QUALIDADE_POLEGAR_ESQUERDO"), bytesPolegarEsquerdo);
                 comparacaoPolegar = fingerprintSDK.identify(templatePolegar);
                 }
                 }*/
            }

            ui.writeLog("HORARIO FINAL: " + dateFormat.format(new Date()));
        } catch (GrFingerJavaException e) {
            //write error to log
            ui.writeLog(e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ui.writeLog(e.getMessage());
        }

        if (id_funcionario != -1 && comparacaoPolegar == true) {
            
            Funcionario funcionario = conexao.BuscarFuncionarioPorId(id_funcionario);
            System.out.println("Funcionário: " + id_funcionario + " - " + funcionario.getNome());
            Acesso acesso = null;
            
            //Código para Operação Especial
            boolean bOperacaoEspecial = false;
            //Valores da variavel isOperacao
            // 0 ==> Não faz operação especial
            // 1 ==> Faz operação especial e pode apontar hora
            // 2 ==> Faz operação especial porém ainda não pode apontar hora porque está fora do horário (+- 30 min)
            int nSegundos = 3 ; //sistema entra num "delay" por n segundos
            int isOperacao = 0;
            int nIdFuncaoOperacaoEspecial = 0;
            
            
            isOperacao = conexao.isDiaOperacaoEspecial(id_funcionario);
            
            
            switch(isOperacao){
                case 0: //Não faz operação especial
                    bOperacaoEspecial = false;
                    break;
                case 1: //Faz operação especial e pode apontar hora
                    bOperacaoEspecial = true;
                    break;
                case 2: //Faz operação especial porém ainda não pode apontar hora porque está fora do horário (+- 30 min)
                    bOperacaoEspecial = false;
                    try {
                        ui.getAppletContext().showDocument(new URL("javascript:showForaJornadaOperacaoEspecial()"));
                        try {
                            TimeUnit.SECONDS.sleep(nSegundos);
                        }catch (InterruptedException e){                            
                            }
                    }catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    bOperacaoEspecial = false;
                    break;
            }
                   
            
            if (bOperacaoEspecial){                
                
                int nQtdeApontamentos = 0;
                String nomeTipoAcesso = "";
                
                System.out.println("Entrei no Operacao Especial!");
                System.out.println("Operacao Especial = " + isOperacao); 
                        
                //Calculamos quantidade de apontamentos registrados na data getdate()
                nQtdeApontamentos = conexao.buscarAcessosDiaFuncionarioOperacaoEspecial(id_funcionario);
                System.out.println("Qtde de apontamentos encontrados para Operação Especial = " + nQtdeApontamentos );
                
                //Pesquisamos qual Id função o funcionário possui na operação especial
                nIdFuncaoOperacaoEspecial = conexao.buscarIdFuncao_OperacaoEspecial(id_funcionario);
                
                //Salvar apontamento da Operação Especial
                switch(nQtdeApontamentos){
                    case 0: //Não fez apontamento ainda                        
                        nomeTipoAcesso = "E1";
                        acesso = conexao.registrarAcessoOperacaoEspecial(funcionario, nomeTipoAcesso, nIdFuncaoOperacaoEspecial);
                        try {
                            ui.getAppletContext().showDocument(new URL("javascript:showInformacoesAcesso('" + acesso.getFuncionario().getNome() + "','" + acesso.getDescricaoSituacao() + "')"));
                            try {
                                TimeUnit.SECONDS.sleep(nSegundos);
                            }catch (InterruptedException e){                            
                                }
                        }catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1: //Fez apontamento de E1
                        nomeTipoAcesso = "S1";
                        acesso = conexao.registrarAcessoOperacaoEspecial(funcionario, nomeTipoAcesso, nIdFuncaoOperacaoEspecial);
                        try {
                            ui.getAppletContext().showDocument(new URL("javascript:showInformacoesAcesso('" + acesso.getFuncionario().getNome() + "','" + acesso.getDescricaoSituacao() + "')"));
                            try {
                                TimeUnit.SECONDS.sleep(nSegundos);
                            }catch (InterruptedException e){                            
                                }
                        }catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2: //Fez apontamento E1 e S1 (não pode apontar mais)                        
                        try {                                                  
                            ui.getAppletContext().showDocument(new URL("javascript:showLimiteDiarioAtingido('" + funcionario.getNome() + "')"));
                            try {
                                TimeUnit.SECONDS.sleep(nSegundos);
                            }catch (InterruptedException e){                            
                                }
                        }catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                } 
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Operação Especial");
//                alert.setHeaderText("isOperaao");
//                alert.setContentText("Valor = False");
//                alert.showAndWait();
            }
            else {
                System.out.println("Operacao Especial = " + isOperacao);
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Operação Especial");
//                alert.setHeaderText("isOperaao");
//                alert.setContentText("Valor = False");
//                alert.showAndWait();
                //Código para compensação e apontamento Normal
            //}
            
            
                boolean isJornadaNova = false; // getdate() não precisa compensar

                System.out.println("Id funcionário que bateu com o código binario da digital = " + id_funcionario);
                //Funcionario funcionario = conexao.BuscarFuncionarioPorId(id_funcionario);

                //Jornada tipoJornadaFuncionario = conexao.buscarJornadaFuncionarioID(id_funcionario);
                //Verificamos o tipo de jornada por Id do funcionário, se isJornadaNova == true então precisa compensar dia
                Jornada tipoJornadaFuncionario = conexao.buscarJornadaFuncionarioID(id_funcionario, isJornadaNova);

                boolean bloquearAcesso = conexao.BloquearAcesso(funcionario.getIdFuncionario());

                ui.writeLog("BLOQUEIO: " + dateFormat.format(new Date()));

                if (bloquearAcesso == true) {
                    try {
                        //ui.writeLog("Acesso bloqueado: " + funcionario.getNome());
                        ui.getAppletContext().showDocument(new URL("javascript:bloquearAcesso('" + funcionario.getNome() + "')"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else {
//                    alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Operação Especial");
//                    alert.setHeaderText("isOperaao");
//                    alert.setContentText("Valor = False");
//                    alert.showAndWait();
                    //Acesso acesso = null;
                    acesso = null;

                    //Verificamos se a data é para compensação
                    boolean diaCompensacao = conexao.isDiaCompensacao();
                    
                    if(true == diaCompensacao){
                        int nSize = 0;
                        int nCount = 0;
                        Date dataReferencia;
                        int idTipoCompensacao;
                        int idSetor;
                        int idPessoal;
                        boolean isAfastado = false;

                        boolean isSetor = false;

                        boolean isLoop = true;
                        boolean isLoop2 = true;

                        //Verificamos quantidade de registros que devem ser compensados no dia
                        ArrayList<Compensacao> compensacao = conexao.SetearCompensacao();

                        nSize = compensacao.size();   
                        System.out.println("Quantidade de compensações achadas no banco baseado no getdate():" + nSize);
                        if (nSize == 1 ){
                            idTipoCompensacao=compensacao.get(0).getIdTipoCompensacao();
                            dataReferencia = compensacao.get(0).getDataReferencia();
                            idSetor = compensacao.get(0).getIdSetor();
                            idPessoal = compensacao.get(0).getIdPessoal();

                            //Verificamos se funcionário possui afastamento legal na data de referencia
                            isAfastado = conexao.isAfastadoLegal(id_funcionario, dataReferencia) ;
                            System.out.println("Id Tipo compensação (nSize == 1) = " + idTipoCompensacao);
                            if (false == isAfastado){
                                switch(idTipoCompensacao){
                                    case 1: //Todos
                                        // verificamos o valor a ser utilizado na jornada
                                        isJornadaNova = true;
                                        break;
                                    case 2: //Funcionario
                                        //verificamos se realmente é o funcionário 
                                        // que deve compensar conforme dados da tabela tb_compensacao                                   
                                        if(id_funcionario == idPessoal){
                                            isJornadaNova = true;
                                        } else {
                                            isJornadaNova = false;
                                        }                                    
                                        break;
                                    case 3: //Setor
                                        //verificamos se o funcionário faz parte do setor que deve compensar.                                    
                                        isSetor = conexao.isSetor(id_funcionario, idSetor);
                                        if(true == isSetor){
                                            isJornadaNova = true;
                                        } else {
                                            isJornadaNova = false;
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }

                        } else { // nSize > 1
                            System.out.println("Entrou no else quando nSize > 1");
                            try {
                                do {
                                    System.out.println("nCount = " + nCount);
                                    idTipoCompensacao=compensacao.get(nCount).getIdTipoCompensacao();
                                    dataReferencia = compensacao.get(nCount).getDataReferencia();
                                    idSetor = compensacao.get(nCount).getIdSetor();
                                    idPessoal = compensacao.get(nCount).getIdPessoal();

                                    //Verificamos se funcionário possui afastamento legal na data de referencia
                                    isAfastado = conexao.isAfastadoLegal(id_funcionario, dataReferencia) ;
                                    System.out.println("Id Tipo compensação (nSize > 1) = " + idTipoCompensacao);
                                    if (false == isAfastado){
                                        switch(idTipoCompensacao){
                                            case 1: //Todos
                                                // verificamos o valor a ser utilizado na jornada
                                                isJornadaNova = true;
                                                isLoop = false;
                                                break;
                                            case 2: //Funcionario
                                                //verificamos se realmente é o funcionário 
                                                // que deve compensar conforme dados da tabela tb_compensacao                                   
                                                if(id_funcionario == idPessoal){
                                                    isJornadaNova = true;
                                                    isLoop = false;
                                                } else {
                                                    isJornadaNova = false;
                                                    isLoop = true;
                                                }                                    
                                                break;
                                            case 3: //Setor
                                                //verificamos se o funcionário faz parte do setor que deve compensar.                                    
                                                isSetor = conexao.isSetor(id_funcionario, idSetor);
                                                if(true == isSetor){
                                                    isJornadaNova = true;
                                                    isLoop = false;
                                                } else {
                                                    isJornadaNova = false;
                                                    isLoop = true;
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    System.out.println("isLoop(" + nCount +") = " + isLoop);

                                    //nCount++;
                                    if(true == isLoop){
                                        nCount++;
                                        System.out.println("nCount = " + nCount);
                                    } else {
                                        nCount = nSize;
                                        System.out.println("nCount = " + nCount + " = nSize = " + nCount);
                                    }                                 


                                } while (nCount<nSize); //while (isLoop||(nCount<nSize)); //while (nCount<nSize);
                            } catch(Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }

                    } else { // getdate() Data de hoje não precisa compensar
                        isJornadaNova = false;                    
                    }
                    
                    System.out.println("isJornadaNova = " + isJornadaNova);
                    System.out.println("Verifica se funcionário esta afastado");
                    if (this.isAfastada(id_funcionario)) {
                        try {                        
                            ui.getAppletContext().showDocument(new URL("javascript:showAfastamentoLegal('" + funcionario.getNome() + "')"));
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (conexao.limiteDiarioAtingido(funcionario.getIdFuncionario(), isJornadaNova)) {
                        try {
                            System.out.println("Entrei no limite diário atingido!");
                            //ui.writeLog("LIMITE DIARIO ATINGINDO.");
                            ui.getAppletContext().showDocument(new URL("javascript:showLimiteDiarioAtingido('" + funcionario.getNome() + "')"));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    } else if (conexao.isForaJornada(funcionario.getIdFuncionario(), isJornadaNova)) {
                        try {
                            System.out.println("Entrei na validação de fora da jornada!");
                            //ui.writeLog("LIMITE DIARIO ATINGINDO.");
                            ui.getAppletContext().showDocument(new URL("javascript:showForaJornada()"));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {

                            System.out.println("Registrei o acesso!");
                            ui.writeLog("ACESSO: " + dateFormat.format(new Date()));
                            acesso = conexao.registrarAcesso(funcionario, isJornadaNova);
                            ui.writeLog("ACESSO REGISTRADO: " + dateFormat.format(new Date()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                        try {
                            if (acesso.isAtrasado() == false) //ui.writeLog("Nao atrasado: " + acesso.getFuncionario().getNome());
                            {
                                ui.getAppletContext().showDocument(new URL("javascript:showInformacoesAcesso('" + acesso.getFuncionario().getNome() + "','" + acesso.getDescricaoSituacao() + "')"));
                            } else //ui.writeLog("Atrasado");
                            {
                                ui.getAppletContext().showDocument(new URL("javascript:showInformacoesAcessoAtrasado('" + acesso.getFuncionario().getNome() + "','" + acesso.getDescricaoSituacao() + "')"));
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                    //}
                }
            }
        }
        else {
            try {
                //ui.writeLog("Nao identificado");
                ui.getAppletContext().showDocument(new URL("javascript:funcionarioNaoEncontrado()"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
            
    }


    public boolean isAfastada(int id_funcionario) {
        boolean resultado = false;

        if (!conexao.buscarAfastamentosLegaisDia(id_funcionario).isEmpty()) {
            return true;
        }

        return resultado;

    }

    /**
     * Add the current fingerprint template to the DB.
     */
    public void enroll() {
        try {
            //conexao.salvarBiometria(ui.getIdFuncionario(), ui.getDigital().equals("polegar_direito"), template.getQuality(), template.getData());

            boolean atualizacao = true;
            if (conexao.VerificaDigitalCadastrada(ui.getIdFuncionario(), ui.getNumeroDedo())) {
                conexao.salvarBiometria(ui.getIdFuncionario(), ui.getNumeroDedo(), template.getQuality(), template.getData(), atualizacao);

                try {
                    ui.getAppletContext().showDocument(new URL("javascript:retornoCadastroBiomatria(\"" + ui.getIdFuncionario() + "\")"));
                    //ui.getAppletContext().showDocument(new URL("http://localhost/acesso/index.php?tipo=" + ui.getTipoFuncionalidade()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            } else {
                if (conexao.LimiteDigitaisCadastradas(ui.getIdFuncionario())) {
                    try {
                        //ui.writeLog("LIMITE DE DIGITAIS CADASTRADAS ATINGIDO");
                        ui.getAppletContext().showDocument(new URL("javascript:showLimiteDigitaisCadastradas()"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else {
                    atualizacao = false;
                    conexao.salvarBiometria(ui.getIdFuncionario(), ui.getNumeroDedo(), template.getQuality(), template.getData(), atualizacao);

                    try {
                        ui.getAppletContext().showDocument(new URL("javascript:retornoCadastroBiomatria(\"" + ui.getIdFuncionario() + "\")"));
                        //ui.getAppletContext().showDocument(new URL("http://localhost/acesso/index.php?tipo=" + ui.getTipoFuncionalidade()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

            //ui.writeLog("Funcionario cadastrado");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        //ui.writeLog("Fingerprint Salva Para o Usuï¿½rio : " + idPaciente);
    }

    /**
     * Removes all templates from the database.
     */
    public void clearDB() {
        database.clear();
        ui.writeLog("Database is clear...");
    }
}
