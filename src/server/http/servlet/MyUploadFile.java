package server.http.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 500, // 10MB
        maxRequestSize = 1024 * 1024 * 500)   // 50MB

public class MyUploadFile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try (PrintWriter out = resp.getWriter()) {
            System.out.println("-------------------------");
            System.out.println(req.getParameter("FullName"));
            System.out.println(req.getParameter("PhoneNumber"));
            System.out.println(req.getParameter("SchoolName"));
            System.out.println(req.getParameter("ClassName"));
            System.out.println(req.getParameter("SocialID"));
            
            String STUDENT_DATA = "{\"FullName\":\""+req.getParameter("FullName")+"\","+
                                  "\"PhoneNumber\":\""+req.getParameter("PhoneNumber")+"\","+
                                  "\"SchoolName\":\""+req.getParameter("SchoolName")+"\","+
                                  "\"SocialID\":\""+req.getParameter("SocialID")+"\","+
                                  "\"ClassName\":\""+req.getParameter("ClassName")+"\"},";
            
            for (Part p : req.getParts()) {                
                try {
                    String fileName = extractFileName(p); // 取得檔案名稱
                    if (fileName == null) {
                        continue;
                    }
                    p.write("/webapps/ROOT/StudentUpload/" + req.getParameter("PhoneNumber") + "-" + req.getParameter("FullName")); // 保存到指定目錄下
                    try {
                        File f = new File("C:\\StudentData.txt");
                        if(f.exists() && !f.isDirectory()) { 
                           f.createNewFile();
                        }
                        Files.write(Paths.get("C:\\StudentData.txt"), STUDENT_DATA.getBytes(), StandardOpenOption.APPEND);
                    }catch (IOException e) {
                        //exception handling left as an exercise for the reader
                        System.out.println(e);
                    }
                    
                } catch (Exception e) {
                    // 如果在 try 中發生錯誤
                    System.out.println("Error : " + e); // 顯示錯誤訊息
                }
            }
            out.println("Upload  Finish !!"); // 顯示上傳成功
        }
    }

    // Get File Extension Name 取得副檔名
    public static String getExtension(String fileName) {
        int startIndex = fileName.lastIndexOf(46) + 1;
        int endIndex = fileName.length();
        return fileName.substring(startIndex, endIndex);
    }

    // Get File Name 取得檔案名稱
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("Content-Disposition");
        if (!contentDisp.contains("filename")) {
            return null;
        }
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return null;
    }

}
