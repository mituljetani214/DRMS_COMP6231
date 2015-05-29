package client;

import models.*;
import server.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

@SuppressWarnings("deprecation")
public class StudentClient 
{
   private Scanner scan;
   private Student student;
   private LibraryServerInterface server;
   private FileWriter myWriter;
   private File clientLogFolder;
   
   StudentClient(String institution) throws MalformedURLException, RemoteException, NotBoundException
   {
      scan = new Scanner(System.in);
      student = new Student();
      
      server = (LibraryServerInterface) Naming.lookup("rmi://132.205.93.19:"+ findServer(institution));
      student.setEducationalIns(institution);
      clientLogFolder = new File("clientLog");
      if(!clientLogFolder.exists()) clientLogFolder.mkdir();  
   }
   
   public static String findServer(String edu)
   {
		String s = edu;
		String server;
		if(s.equals("Concordia"))
			server = "4444/Concordia";
		else if(s.equals("McGill"))
			server = "4446/McGill";
		else if(s.equals("UdeM"))
			server = "4448/UdeM";
		else
		{
			server = "server is not found";
		}
		return server;
	}
  
   
   
   public boolean validateCredentials()
   {
      String userName;
      String password;
      
      //student.setInstitution(); // Assuming the class Student has such a setter
      
      do
      {
         //gets username from the consol
         System.out.print("UserName: ");
         userName = scan.next();
         System.out.println();         
         //gets password from the consol
         System.out.print("Password: ");
         password = scan.next();
         System.out.println();
         
         //inputs validation error messages
         if(userName.length() < 6 && userName.length() > 0) System.out.println("The user name is too short!!!");
         if(userName.length() > 15 && userName.length() > 0) System.out.println("The user name is too long!!!");
         if(password.length() < 8) System.out.println("The password is too short!!!");
         if(userName.length() == 0 && password.length() == 0) System.out.println("The fields are empty!!!");
         if(userName.length() == 0) System.out.println("The user name field is empty!!!");
         if(password.length() == 0) System.out.println("The password field is empty!!!");
        
      }while(userName.length() < 6 || userName.length() > 15 || password.length() < 8);
     
      	  student.setUserName(userName);
      	  student.setPassword(password);
      
      	  return true;
      
   }
   
   public void logFile(String fileName, String logInfo) throws IOException
   {
   	File clientFile = new File("clientLog",fileName); 
   	if(!clientFile.exists()) clientFile.createNewFile();
   	
   	myWriter = new FileWriter(clientFile,true);
   	myWriter.write(logInfo+"\n");
   	myWriter.flush();
   	myWriter.close();
   }
   
   private boolean createAccount() throws RemoteException, IOException
   {
      validateCredentials();
      String fname, lname,email,phone;
      do
      {
        //gets first name from the consol
        System.out.print("First Name: ");
        fname = scan.next();
        System.out.println();
        
        //gets last name from the consol 
        System.out.print("Last Name: ");
        lname = scan.next();
        System.out.println();
        
        //gets email from the consol
        System.out.print("Email: ");
        email = scan.next();
        System.out.println();
        
        //gets phone number from the consol 
        System.out.print("Phone Number: ");
        phone = scan.next();
        System.out.println();
        
        //inputs validation error mesages
        if(fname.isEmpty()) System.out.println("The first name field is empty!!!");
        if(lname.isEmpty()) System.out.println("The last name field is empty!!!");
        if(email.isEmpty()) System.out.println("The email fields is empty!!!");
        if(phone.isEmpty()) System.out.println("The phone number field is empty!!!");
        if((fname.isEmpty() || lname.isEmpty() || email.isEmpty() || phone.isEmpty())) System.out.println("Try again!!!");
      }
      while(fname.isEmpty() || lname.isEmpty() || email.isEmpty() || phone.isEmpty());
      
      student.setFirstName(fname); 
      student.setLastName(lname); 
      student.setEmailAddress(email); 
      student.setPhoneNumber(phone); 
      
      //calls the remote method on the server to actually create the account
      if(server.createAccount(student.getFirstName(), student.getLastName(), student.getEmailAddress(), student.getPhoneNumber(), student.getUserName(), student.getPassword(), student.getEducationalIns())) 
      {
         System.out.println("Account Created Successfully!!!");
         String logInfo = "[" + new SimpleDateFormat(" yyyy/MM/dd HH:mm:ss").format(new Date()) + "]"
                                + " Account created for user: " + student.getUserName() + " on server: "
                                + student.getEducationalIns();
         logFile(student.getUserName(),logInfo);
         return true;
      }
      else 
      {
         //TODO get an error message from the server
         return false;
      }
   }
   
   public void reserveBook() throws RemoteException
   {
	   String bookName, authorName;
	      do
	      {
	        //gets first name from the consol
	        System.out.print("Book Name: ");
	        bookName = scan.next();
	        System.out.println();
	        
	        //gets last name from the consol 
	        System.out.print("Author Name: ");
	        authorName = scan.next();
	        System.out.println();
	        
	 
	        
	        //inputs validation error mesages
	        if(bookName.isEmpty()) System.out.println("The Book name field is empty!!!");
	        if(authorName.isEmpty()) System.out.println("The Author name field is empty!!!");
	        if((bookName.isEmpty() || authorName.isEmpty())) System.out.println("Try again!!!");
	      }
	      while(bookName.isEmpty() || authorName.isEmpty());
	  
     
      System.out.println(server.reserveBook(student.getUserName(),student.getPassword(),bookName,authorName));
                     
   }
   
   public static void main(String args[])
   {
	  System.setSecurityManager(new RMISecurityManager());
      System.out.println("WELCOME TO ONLINE LIBRARY SYSTEM");
      System.out.println();
      System.out.println("Select your instution:");
      System.out.println();
      System.out.println("1- Concordia");
      System.out.println("2- McGill");
      System.out.println("3- UdeM");
      
      int choice = 0;
      boolean valid = false;
      StudentClient aStudent = null;
      Scanner scan = new Scanner(System.in);
      
      while(!valid)
      {
         try
         {

            choice = scan.nextInt();
            switch(choice)
            {
             
               case 1:
                  aStudent = new StudentClient("Concordia");
                  valid = true;
                  break;
                  
               case 2:
                  aStudent = new StudentClient("McGill");
                  valid = true;
                  break;
                  
               case 3:
                  aStudent = new StudentClient("UdeM");
                  valid = true;
                  break;
                  
               default:
                  System.out.println("please choose options 1, 2 or 3 only!!");
                  valid = false;
            }
         }
         catch(Exception e)
         {
            System.out.println("Invalid input!!! Please enter an integer");
            
            //scan.close();
            valid = false;
         }
      }
       
     
         System.out.println();
         System.out.println("Choose an option:");
         System.out.println();
         System.out.println("1- Create account.");
         System.out.println("2- Reserve a Book.");
         System.out.println("3- Exit");
         
         valid = false;
         
         while(!valid)
         {
            try
            {
               choice = scan.nextInt();
               switch(choice)
               {
                  case 1:
                	 aStudent.validateCredentials();
                     aStudent.createAccount();
                     valid = true;
                     break;
                     
                  case 2:
                 	 aStudent.validateCredentials();
                      aStudent.reserveBook();
                      valid = true;
                      break;
                  
                  case 3:
                     System.out.println("Thank you for visiting the online Library!!");
                     System.out.println("See you soon!!");
                     System.in.close();
	             System.exit(0);
                     valid = true;
                     break;
            
                  default:
                     System.out.println("please choose options 1, 2 or 3 only!!");
                     valid = false;
                }
         }
         catch(Exception e)
         {
            System.out.println("Invalid input!!! Please enter an integer");
         }
      }
   
      
   }
   
}
