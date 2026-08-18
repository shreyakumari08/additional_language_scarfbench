package micronaut.tutorial.fileupload;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class FileUploadController {

    private static final Logger LOGGER = Logger.getLogger(FileUploadController.class.getCanonicalName());

    @Post(uri = "/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> upload(@Part("destination") String destination,
                                       @Part("file") CompletedFileUpload file) {
        if (destination == null || destination.isBlank()) {
            return HttpResponse.ok("Destination must be provided");
        }
        if (file == null || file.getSize() == 0) {
            return HttpResponse.ok("You did not specify a file to upload.");
        }
        try {
            Path destDir = Paths.get(destination).normalize().toAbsolutePath();
            Files.createDirectories(destDir);
            String original = file.getFilename();
            String fileName = (original == null || original.isBlank())
                    ? "upload.bin"
                    : Paths.get(original).getFileName().toString();
            Path target = destDir.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            LOGGER.log(Level.INFO, "File {0} being uploaded to {1}", new Object[]{fileName, destDir});
            return HttpResponse.ok("New file " + fileName + " created at " + destDir);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", new Object[]{ex.getMessage()});
            return HttpResponse.<String>badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body("You either did not specify a file to upload or are trying to upload a file to a protected or nonexistent location.<br/> ERROR: " + ex.getMessage());
        }
    }

    @Get(uri = "/upload", produces = MediaType.TEXT_PLAIN)
    public String getInfo() {
        return "Servlet that uploads files to a user-defined destination";
    }
}
