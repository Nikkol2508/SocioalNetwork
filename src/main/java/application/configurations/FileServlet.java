package application.configurations;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@WebServlet("/storage/*")
public class FileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String filename = URLDecoder.decode(request.getPathInfo().substring(1), StandardCharsets.UTF_8);
        request.setAttribute("path","kek");
        System.out.println(request.toString());
        File file = new File("src/main/resources/public/storage/", filename);
        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), response.getOutputStream());
    }

}
