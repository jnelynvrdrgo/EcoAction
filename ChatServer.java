package com.mycompany.ecoactionchat.server;
import java.net.*;import java.io.*;import java.util.*;import java.util.concurrent.*;
public class ChatServer {
 final int port; final Set<ClientHandler> clients=ConcurrentHashMap.newKeySet(); final Set<String> names=ConcurrentHashMap.newKeySet(),tasks=ConcurrentHashMap.newKeySet(),reports=ConcurrentHashMap.newKeySet();
 public ChatServer(int p){port=p;} public void start(){System.out.println("Eco Action Chat server on "+port);try(ServerSocket s=new ServerSocket(port)){while(true){Socket x=s.accept();new Thread(new ClientHandler(this,x)).start();}}catch(IOException e){System.out.println(e);}}
 boolean register(String n){return names.add(n.toLowerCase());} void release(String n){if(n!=null)names.remove(n.toLowerCase());}
 void add(ClientHandler c){clients.add(c);} void remove(ClientHandler c,String n){clients.remove(c);release(n);broadcast("USERLIST|"+userList());if(n!=null)broadcast("CHAT|Server|"+n+" left.");}
 String userList(){return clients.stream().map(ClientHandler::name).filter(Objects::nonNull).sorted().reduce((a,b)->a+","+b).orElse("");}
 void broadcast(String m){for(ClientHandler c:clients)c.send(m);} void task(String t){tasks.add(t);broadcast("TASK|"+t);} void report(String r){reports.add(r);broadcast("COLLECTION|"+r);}
}