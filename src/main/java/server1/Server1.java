/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.tools.ShellFunctions.input;
import util.Estados;
import util.Mensagem;
import util.Status;

/**
 *
 * @author rubia
 */
public class Server1 {

    private ServerSocket serverSocket;

    
    public Server1() {

    }

    
    private void criarServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);

    }

    private Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }

    private void fechaSocket(Socket s) throws IOException {

        s.close();

    }

    public void tratarConxao(Socket socket) throws IOException, ClassNotFoundException{
        String operacao = "";
        Mensagem reply = null;
        try {

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("Tratando!..");
            Estados estado = Estados.CONECTADO;
            
            
            while (!operacao.equals("SAIR")) {
               
                System.out.println("Tratando!..");
                
                //operacao = input.readUTF();
                
                Mensagem m = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente: " + m);
                

                operacao = (String) m.getOperacao();
                reply = new Mensagem(operacao + "REPLY");
                
                //System.out.println("Mensagem recebida!.." + operacao);
                switch (estado) {
                    case CONECTADO:
                    switch (operacao) {
                        case "LOGIN":
                                String user = (String) m.getParam("user");
                                String pass = (String) m.getParam("pass");

                                if (pass.equals("senha")) {
                                    reply.setStatus(Status.OK);
                                    estado = Estados.AUTENTICADO;
                                    System.out.println("----Autenticado--- " );
                                    reply.setParam("msg", "Autenticado.");
                                } else {
                                    reply.setStatus(Status.ERROR);

                                }
                            break;
                        }
                    case AUTENTICADO:
                       output.writeUTF("Reposta.. Autenticado");
                       
                    break;
                       
                   
                
                }
                
                output.flush();

            }
            input.close();
            output.close();
            socket.close();

        } catch (IOException ex) {
            System.out.println("Erro no tratamentoda conexao: " + ex);

        } finally {

            fechaSocket(socket);
        }

    }

    public static void main(String args[]) throws ClassNotFoundException {

        try {
            Server1 server = new Server1();
            System.out.println("Aguardando conexao..");
            server.criarServerSocket(5555);

            Socket socket = server.esperaConexao();
            System.out.println("Cliente conectado!");
            server.tratarConxao(socket);
            System.out.println("Cliente finalizado!");

        } catch (IOException ex) {
            Logger.getLogger(Server1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
