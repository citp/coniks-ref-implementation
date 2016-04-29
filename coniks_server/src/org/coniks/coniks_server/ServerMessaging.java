/*
  Copyright (c) 2016, Princeton University.
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are 
  met:
  * Redistributions of source code must retain the above copyright 
  notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above 
  copyright notice, this list of conditions and the following disclaimer 
  in the documentation and/or other materials provided with the 
  distribution.
  * Neither the name of Princeton University nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
  CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY 
  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

package org.coniks.coniks_server;

import javax.net.ssl.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import com.google.protobuf.*;
import org.javatuples.*;

import org.coniks.coniks_common.MsgType;
import org.coniks.coniks_common.ServerErr;
import org.coniks.coniks_common.CommonMessaging;
import org.coniks.coniks_common.C2SProtos.Registration;
import org.coniks.coniks_common.C2SProtos.CommitmentReq;
import org.coniks.coniks_common.C2SProtos.KeyLookup;
import org.coniks.coniks_common.C2SProtos.RegistrationResp;
import org.coniks.coniks_common.C2SProtos.AuthPath;
import org.coniks.coniks_common.C2SProtos.*;

import org.coniks.coniks_common.UtilProtos.Hash;
import org.coniks.coniks_common.UtilProtos.Commitment;
import org.coniks.coniks_common.UtilProtos.ServerResp;
import org.coniks.coniks_common.UtilProtos.*;

public class ServerMessaging {

    // send back a simple server response based on the result of the request
    public static synchronized void sendSimpleResponseProto(int reqResult, Socket socket){
        MsgHandlerLogger.log("Sending simple server response... ");
        
        ServerResp respMsg = buildServerRespMsg(reqResult);
        sendMsgProto(MsgType.SERVER_RESP, respMsg, socket);     

    }
    
    // send back the commitment returned for the commitment request
    public static synchronized void sendCommitmentProto(SignedTreeRoot str, Socket socket){
        MsgHandlerLogger.log("Sending commitment response... ");
     
        Commitment comm = buildCommitmentMsg(str);
      
        sendMsgProto(MsgType.COMMITMENT, comm, socket);
    }
    
    // send back the initial epoch and epoch interval for the newly registered user, who will cache this info
    public static synchronized void sendRegistrationRespProto(long initEpoch, int epochInterval,
                                                              Socket socket){
        MsgHandlerLogger.log("Sending registration response... ");
          
        RegistrationResp regResp = buildRegistrationRespMsg(initEpoch, epochInterval);
        sendMsgProto(MsgType.REGISTRATION_RESP, regResp, socket);
    }
    
    // send back the authentication path based on the key lookup
    public static synchronized void sendAuthPathProto(UserLeafNode uln, RootNode root, Socket socket){
        MsgHandlerLogger.log("Sending authentication path response... ");
  
        AuthPath authPath = buildAuthPathMsg(uln, root);
        sendMsgProto(MsgType.AUTH_PATH, authPath, socket);
    }
    
    /** Sends any protobuf message {@code msg} of type {@code msgType}
     * to the given socket.
     */
    private static synchronized void sendMsgProto (int msgType, AbstractMessage msg,
                                Socket socket) {

        DataOutputStream dout = null;
        try {
            dout = new DataOutputStream(socket.getOutputStream());

            // now send the message
            dout.writeByte(msgType);
            msg.writeDelimitedTo(dout);
            dout.flush();
        }
        catch (IOException e) {
            MsgHandlerLogger.error("Sending msg proto "+msg.toString());
            MsgHandlerLogger.error("Error: "+e.getMessage());
        }
        finally {
            CommonMessaging.close(dout);
        }

    }    

    /* Message building functions */
    
    // create the simple server response message
    private static ServerResp buildServerRespMsg(int respType){
        ServerResp.Builder respMsg = ServerResp.newBuilder();
        switch(respType){
        case ServerErr.SUCCESS:
            respMsg.setMessage(ServerResp.Message.SUCCESS);
            break;
        case ServerErr.NAME_EXISTS_ERR:
            respMsg.setMessage(ServerResp.Message.NAME_EXISTS_ERR);
            break;
        case ServerErr.NAME_NOT_FOUND_ERR:
            respMsg.setMessage(ServerResp.Message.NAME_NOT_FOUND_ERR);
            break;
        case ServerErr.MALFORMED_CLIENT_MSG_ERR:
            respMsg.setMessage(ServerResp.Message.MALFORMED_ERR);
            break;
        case ServerErr.SIGNED_CHANGE_VERIF_ERR:
            respMsg.setMessage(ServerResp.Message.VERIFICATION_ERR);
            break;
        default:
            respMsg.setMessage(ServerResp.Message.SERVER_ERR);
            break;                
        }
        return respMsg.build();
    }
    
    // create the commitment response message
    private static Commitment buildCommitmentMsg(SignedTreeRoot str){            
        
        Commitment.Builder commMsg = Commitment.newBuilder();
        byte[] rootBytes = ServerUtils.getRootNodeBytes(str.getRoot());
        byte[] rootHashBytes = ServerUtils.hash(rootBytes);
        
        Hash.Builder rootHash = Hash.newBuilder();
        ArrayList<Integer> rootHashList = ServerUtils.byteArrToIntList(rootHashBytes);
        
        if(rootHashList.size() != ServerUtils.HASH_SIZE_BYTES){
            MsgHandlerLogger.error("Bad length of root hash: "+rootHashList.size());
            return null;
        }
        rootHash.setLen(rootHashList.size());
        rootHash.addAllHash(rootHashList);
        commMsg.setEpoch(str.getEpoch());
        ArrayList<Integer> sigList = ServerUtils.byteArrToIntList(str.getSignature());
        commMsg.setRootHash(rootHash.build());
        commMsg.addAllSignature(sigList);
        return commMsg.build();
    }
    
    // create the registration response message
    private static RegistrationResp buildRegistrationRespMsg(long initEpoch, int epochInterval){            
        
        RegistrationResp.Builder regRespMsg = RegistrationResp.newBuilder();
        regRespMsg.setInitEpoch(initEpoch);
        regRespMsg.setEpochInterval(epochInterval);
        return regRespMsg.build();
    }
    
    // create the commitment response message
    private static AuthPath buildAuthPathMsg(UserLeafNode uln, RootNode root){            
        return TransparencyOps.generateAuthPathProto(uln, root);
    }

    /** Receives a protobuf message from the client and checks that
     * the message is correctly formatted for the expected message type.
     * The caller is responsible for handling the exact message type(s).
     *
     *@return The specific protobuf message according to the message type
     * indicated by the client.
     */
    public static synchronized AbstractMessage receiveMsgProto(Socket socket) {
        
        DataInputStream din = null;
        try {
            din = new DataInputStream(socket.getInputStream());
            
            // get the message type of the message and read in the stream
            int msgType = din.readUnsignedByte();
            
            if (msgType == MsgType.REGISTRATION){
                Registration reg = Registration.parseDelimitedFrom(din);
                
                if(!reg.hasBlob()){
                    MsgHandlerLogger.log("Malformed registration message");
                }
                else {
                    return reg;
                }
            }
            else if (msgType == MsgType.KEY_LOOKUP) {
                KeyLookup lookup = KeyLookup.parseDelimitedFrom(din);
                
                if(!lookup.hasName() || !lookup.hasEpoch() || 
                   lookup.getEpoch() <= 0){
                    MsgHandlerLogger.log("Malformed key lookup");
                }
                else {
                    return lookup;
                }
            }
            else if (msgType == MsgType.COMMITMENT_REQ) {
                CommitmentReq commReq = CommitmentReq.parseDelimitedFrom(din);
                
                if (!commReq.hasType() || !commReq.hasEpoch() || commReq.getEpoch() <= 0) {
                    MsgHandlerLogger.log("Malformed commitment request message");
                }
                else {
                    return commReq;
                }
            }
            else if (msgType == MsgType.ULNCHANGE_REQ) {
                ULNChangeReq ulnChange = ULNChangeReq.parseDelimitedFrom(din);
                if (!ulnChange.hasName()) {
                    MsgHandlerLogger.log("Malformed uln change req");
                }
                else {
                    return ulnChange;
                }
            }
            else if (msgType == MsgType.SIGNED_ULNCHANGE_REQ) {
                SignedULNChangeReq sulnReq = SignedULNChangeReq.parseDelimitedFrom(din);
                if (!sulnReq.hasReq() || sulnReq.getSigCount() < 1 || !sulnReq.getReq().hasName()) {
                    MsgHandlerLogger.log("Malformed signed uln change req");
                }
                else {
                    return sulnReq;
                }
            }
            else {
                MsgHandlerLogger.log("Unknown incoming message type");
            }
        }
        catch (InvalidProtocolBufferException e) {
            MsgHandlerLogger.error("parsing a protobuf message");
        }
        catch (IOException e) {
            MsgHandlerLogger.error("receiving data from client");
        }
        finally {
            CommonMessaging.close(din);
        }
        
        // unexpected message type from the client
        return null;
    }
    

    /* Functions for handling the lower-level communication with the client */

    /** Listens for incoming requests. Uses an SSL connection if in full operating mode.
     *
     *@param isFullOp indicates whether the client is operating in full mode 
     * or in testing mode
     */
    public static void listenForRequests (boolean isFullOp) {

        ServerSocket s = null;
        
        try{

            if (isFullOp) {
                SSLServerSocketFactory sslSrvFact = 
                (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
                s =(SSLServerSocket)sslSrvFact.createServerSocket(ServerConfig.PORT);
            }
            else {
                s = new ServerSocket(ServerConfig.PORT);

                System.out.println("Listening for connections on port "+ServerConfig.PORT+"...");
            }            

            MsgHandlerLogger.log("Listening for connections on port "+ServerConfig.PORT+"...");
            
            // loop to listen for requests
            while(true){
                Socket c = s.accept(); // closing done by thread
                
                MsgHandlerLogger.log("Server accepted new connection.");

                RequestHandler th;
                
                if (isFullOp) {
                    th = new RequestHandler((SSLSocket)c);
                }
                else {
                    th = new RequestHandler(c);
                }

                th.start();
                
            }
        }
        catch(IOException e){
            MsgHandlerLogger.error(e.getMessage());
        }
        finally {
            CommonMessaging.close(s);
        }
        
    }
    
}