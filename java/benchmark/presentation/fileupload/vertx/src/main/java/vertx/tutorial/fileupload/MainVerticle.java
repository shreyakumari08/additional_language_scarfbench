package vertx.tutorial.fileupload;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.handler.BodyHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getCanonicalName());

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create()
            .setBodyLimit(104857600)
            .setUploadsDirectory("/tmp/vertx-uploads"));

        // POST /upload  (multipart)
        router.post("/upload").handler(this::handleUpload);

        // GET /upload  (info)
        router.get("/upload").handler(ctx ->
            ctx.response().putHeader("content-type", "text/plain").end("Servlet that uploads files to a user-defined destination"));

        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void handleUpload(RoutingContext ctx) {
        String destination = ctx.request().getFormAttribute("destination");
        if (destination == null || destination.isBlank()) {
            ctx.response().putHeader("content-type", "text/html").end("Destination must be provided");
            return;
        }
        if (ctx.fileUploads().isEmpty()) {
            ctx.response().putHeader("content-type", "text/html").end("You did not specify a file to upload.");
            return;
        }
        try {
            Path destDir = Paths.get(destination).normalize().toAbsolutePath();
            Files.createDirectories(destDir);
            FileUpload up = ctx.fileUploads().iterator().next();
            String original = up.fileName();
            String fileName = (original == null || original.isBlank())
                    ? "upload.bin"
                    : Paths.get(original).getFileName().toString();
            Path target = destDir.resolve(fileName);
            Files.copy(Paths.get(up.uploadedFileName()), target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.log(Level.INFO, "File {0} being uploaded to {1}", new Object[]{fileName, destDir});
            ctx.response().putHeader("content-type", "text/html").end("New file " + fileName + " created at " + destDir);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", ex.getMessage());
            ctx.response().setStatusCode(400).putHeader("content-type", "text/html")
               .end("You either did not specify a file to upload or are trying to upload a file to a protected or nonexistent location.<br/> ERROR: " + ex.getMessage());
        }
    }
}
