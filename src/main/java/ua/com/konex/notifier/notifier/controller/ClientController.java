package ua.com.konex.notifier.notifier.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ClientController {

    private static final Map<String, String> MIME_MAP = new HashMap<>();

    static {
        MIME_MAP.put("appcache", "text/cache-manifest");
        MIME_MAP.put("css", "text/css");
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("html", "text/html");
        MIME_MAP.put("js", "application/javascript");
        MIME_MAP.put("json", "application/json");
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("mp4", "video/mp4");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("svg", "image/svg+xml");
        MIME_MAP.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("xml", "application/xml");
        MIME_MAP.put("zip", "application/zip");
        MIME_MAP.put("md", "text/plain");
        MIME_MAP.put("txt", "text/plain");
        MIME_MAP.put("php", "text/plain");
    }

    @RequestMapping(value = "/*")
    public ResponseEntity<String> view(HttpServletRequest request){

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/html");
        
        String uri = request.getRequestURI();

        Resource resource;
        if (uri.equals("/")) resource = new ClassPathResource("templates/index.html");
        else {
            String[] filenames = uri.split("/");
            String extFile = getExt(uri).toLowerCase();
            String typeFile = MIME_MAP.get(extFile);

            if (filenames.length == 0 || typeFile == null || extFile.equals("")) resource = new ClassPathResource("templates/error.html");
            else {
                extFile = extFile.equals("css") || extFile.equals("js") ? extFile : "media";

                responseHeaders.add("Content-Type", typeFile);
                resource = new ClassPathResource("static/" + extFile + "/" + filenames[filenames.length - 1]);
            }
        }

        try {
            return new ResponseEntity<>(new String(Files.readAllBytes(Paths.get(resource.getFile().getPath()))), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private static String getExt(String path) {
        int slashIndex = path.lastIndexOf('/');
        String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

        int dotIndex = basename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return basename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }
}