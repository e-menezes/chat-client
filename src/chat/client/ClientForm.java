/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultCaret;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 *
 * @author menezes
 */
public class ClientForm extends javax.swing.JFrame {

    private String id;
    public int status;
    private static final int DESLOGANDO = 0;
    private static final int DESLOGADO = 1;
    private static final int LOGANDO = 2;
    private static final int LOGADO = 3;
    private static final int ENVIANDO = 4;
    private static final int RECEBENDO = 5;
    private int msgNr;
    Timer timer;
    Socket kkSocket;
    boolean closeSocket;
    PrintWriter out;
    BufferedReader in;
    
    /**
     * Creates new form ClientForm
     */
    public ClientForm(String id) {
        initComponents();
        this.id = id;
        this.status = DESLOGADO;
        this.msgNr = 1;
        this.closeSocket = false;
    }
    
    public void init2(){
        String hostName = "127.0.0.1";
        int portNumber = 12345;

        try {
            this.status = LOGANDO;
            kkSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
            
            JSONObject obj = new JSONObject()  
                .element( "cmd", "login" )  
                .element( "id", this.id ) // id do cliente  
                .element( "msgNr", this.msgNr++ );
            out.println(obj);
            
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    String fromServer;
                    try {
                        if ((fromServer = in.readLine()) != null) {
                            socketDataReceived(fromServer);
                        }else{
                            JSONObject jsonObject = new JSONObject()
                                .element( "cmd", "RECEBER" ) 
                                .element( "id", id )
                                .element( "msgNr", msgNr++ );
                            status = RECEBENDO;
                            System.out.println("Cliente enviou: " + jsonObject);
                            out.println(jsonObject);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 1000, 1000);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }

    private void socketDataReceived(String data){
        System.out.println("Cliente recebeu: " + data);
        JSONObject obj = JSONObject.fromObject(data);
        decodeResponse(obj);
        
        //msgDisplay.append(obj.getString("data") + System.lineSeparator());
    }
    
    public void decodeResponse(JSONObject obj){
        
        switch( this.status ){
            case LOGANDO:
                this.status = LOGADO;
                this.loginBtn.setText("Deslogar");
                this.statusLabel.setText("Você está online!");
                if(obj.containsKey("data")){
                    JSONArray msgs = obj.getJSONArray("data");
                    for(int i=0; i< msgs.size(); i++) {
                        JSONObject msg = msgs.getJSONObject(i);
                        msgDisplay.append(msg.getString("origem") + " : ");
                        msgDisplay.append(msg.optString("msg", "NO DATA") + System.lineSeparator());
                    }
                }
                break;
            case DESLOGANDO:
                this.status = DESLOGADO;
                this.statusLabel.setText("Você está offline!");
                break;
            case LOGADO:
            case DESLOGADO:                
                break;
            case ENVIANDO:
                this.status = LOGADO;
//                if( obj.getString("id").matches("0") )
//                    msgDisplay.append("Server: ");
//                msgDisplay.append(obj.optString("data", "NO DATA") + System.lineSeparator());
                break;
                
            case RECEBENDO:
                this.status = LOGADO;
                
                JSONArray data = obj.optJSONArray("data");
                if (data != null)
                    for (int i=0; i<data.size(); i++){
                        JSONObject o = data.getJSONObject(i);
                        String origem = o.getString("origem");
                        if( origem.matches("0") )
                            msgDisplay.append("Server: ");
                        else
                            msgDisplay.append(origem);
                        msgDisplay.append( o.getString("msg") + System.lineSeparator() );
                    }
                    
                
                break;
        }
    }
    
    public void receiveDataOrCloseSocket(){
        if(closeSocket){
            try {
                in.close();
                loginBtn.setText("Logar");
            } catch (IOException e) {
                System.err.println("Erro enquanto tentava encerrar a conexão do socket.");
            }
            System.out.println("TimerTask cancelado.");
            timer.cancel();
            return;
        }
        String fromServer;
        try {
            if ((fromServer = in.readLine()) != null) {
                socketDataReceived(fromServer);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        msgComposer = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgDisplay = new javax.swing.JTextArea();
        DefaultCaret caret = (DefaultCaret)msgDisplay.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        statusLabel = new javax.swing.JLabel();
        msgDst = new javax.swing.JTextField();
        loginBtn = new javax.swing.JButton();
        ipField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cliente");

        msgComposer.setToolTipText("Digite sua mensagem");
        msgComposer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msgComposerActionPerformed(evt);
            }
        });

        msgDisplay.setColumns(20);
        msgDisplay.setRows(5);
        jScrollPane1.setViewportView(msgDisplay);

        statusLabel.setText("Você está offline");
        statusLabel.setToolTipText("");

        msgDst.setColumns(10);
        msgDst.setText("Destinatário");
        msgDst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                msgDstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                msgDstFocusLost(evt);
            }
        });

        loginBtn.setText("Logar");
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginBtnActionPerformed(evt);
            }
        });

        ipField.setText("127.0.0.1");

        jLabel1.setText("IP:");

        jLabel3.setText("Porta:");

        portField.setText("12345");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(msgComposer)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ipField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(statusLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msgDst)
                    .addComponent(loginBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginBtn)
                    .addComponent(ipField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(msgDst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(msgComposer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void msgComposerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgComposerActionPerformed
        if( evt.getActionCommand().matches("") )
            return;
        String dst = this.msgDst.getText();
        String mostrar = "Você: " + evt.getActionCommand() + " (" + (dst.matches("Destinatário")?"Server" : dst) + ") ";
        this.msgDisplay.append( mostrar + System.lineSeparator());
        JSONObject jsonObject = new JSONObject()
            .element( "cmd", "enviar" ) 
            .element( "id", this.id )
            .element( "dst", dst.matches("Destinatário")? "0" : dst ) // id:0 para servidor
            .element( "data", evt.getActionCommand() )  
            .element( "msgNr", this.msgNr++ );
        this.status = ENVIANDO;
        System.out.println("Cliente enviou: " + jsonObject);
        out.println(jsonObject);
        this.msgComposer.setText("");
    }//GEN-LAST:event_msgComposerActionPerformed

    private void msgDstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_msgDstFocusGained
        if(this.msgDst.getText().matches("Destinatário")){
            this.msgDst.setText("");
        }
    }//GEN-LAST:event_msgDstFocusGained

    private void msgDstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_msgDstFocusLost
        if(this.msgDst.getText().matches("")){
            this.msgDst.setText("Destinatário");
        }
    }//GEN-LAST:event_msgDstFocusLost

    private void loginBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginBtnActionPerformed
        if( this.loginBtn.getText().matches("Deslogar") ){
            this.closeSocket = true;
            this.loginBtn.setText("Deslogando..");
            out.println("null");
            out.flush();
            return;
        } 
        
        String hostName = ipField.getText();
        int portNumber = Integer.parseInt( portField.getText() );

        try {
            this.status = LOGANDO;
            this.closeSocket = false;
            this.loginBtn.setText("Logando..");
            kkSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
            
            JSONObject obj = new JSONObject()  
                .element( "cmd", "login" )  
                .element( "id", this.id ) // id do cliente  
                .element( "msgNr", this.msgNr++ );
            out.println(obj);
            
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    receiveDataOrCloseSocket();
                    JSONObject jsonObject = new JSONObject()
                        .element( "cmd", "receber" ) 
                        .element( "id", id )
                        .element( "msgNr", msgNr++ );
                    status = RECEBENDO;
                    System.out.println("Cliente enviou: " + jsonObject);
                    out.println(jsonObject);
                }
            }, 1000, 1000);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }//GEN-LAST:event_loginBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ipField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loginBtn;
    public javax.swing.JTextField msgComposer;
    public javax.swing.JTextArea msgDisplay;
    private javax.swing.JTextField msgDst;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
}
