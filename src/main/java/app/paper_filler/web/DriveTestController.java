package app.paper_filler.web;

import app.paper_filler.core.DriveStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DriveTestController {

    private final DriveStorage storage;

    @GetMapping("/drive-test")
    String test() throws IOException{
        String id = storage.findOrCreateFolder("Paper Filler", null);
        return "Ok: https://drive.google.com/drive/folders/" + id;
    }
}
