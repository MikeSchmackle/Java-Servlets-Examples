/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;
import java.io.OutputStream;
//import java.io.PrintWriter;
import java.io.InputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author schmacm
 */
public class outputPDF extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   public void doGet (HttpServletRequest request, HttpServletResponse response){
       
        try {
             
            OutputStream out = response.getOutputStream(); 
            //PrintWriter pw = response.getWriter();
           
     	    // load the Oracle Driver
            Class.forName("oracle.jdbc.OracleDriver"); 

      	    String connDB="jdbc:oracle:thin:SERVER:PORT:DB";

      	    String connUserName="usn";
      	    String connPwd="pswd";
           
            // establish a connection to Oracle
      	    Connection dbConn = DriverManager.getConnection(connDB,connUserName,connPwd);
      	    try {    
                Statement st = dbConn.createStatement();
            
                String Param_Value = request.getParameter("ID");
	
                String query ="SELECT FILENAME FROM DB.TBL where ID ='" + Param_Value +"'";					
                // execute the query
                ResultSet rsFileName = st.executeQuery(query);
            
                rsFileName.next();
	    
                CallableStatement cs = dbConn.prepareCall("{call getfile_sp(?,?)}");
                
                try{
                    cs.setString(1, Param_Value);
                    cs.registerOutParameter(2, Types.BLOB);
                    cs.execute();
                  
                    Blob b = cs.getBlob(2);
                    InputStream inStream = b.getBinaryStream();
           
                    int len =0;
            
                    byte[] buf = new byte[16384]; 
                              
                    if (inStream==null) { 
                        String errMsg="File x.pdf not found";       
                    } 
                    else {       
                        response.setContentType("application/octet"); 
                        response.setHeader("Cache-Control", "no-cache"); 
                        response.setHeader("Content-Disposition", "attachment; filename='" + rsFileName.getString(1) + "'"); 
     
                        //read bytes from inStream into the buffer 
                        while ((len=inStream.read(buf))>-1) { 
                            out.write(buf, 0, len);
                            
                        } 
                        inStream.close(); 
                        out.close();      
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }finally{
                    rsFileName.close();
                    cs.close();
                }   
            }catch(Exception e) {
              e.printStackTrace();
            }finally{
                dbConn.close();
            }
          } catch(Exception e) {
              e.printStackTrace();
          }
     }
  
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
        
   @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
