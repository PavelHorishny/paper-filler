package app.paper_filler.core;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveStorage {

    private static final String FOLDER = "application/vnd.google-apps.folder";

    private final Drive drive;

    public String findOrCreateFolder(String name,String parentId) throws IOException {
        String query = "name= '"+name.replace("'","\\'")+"'"
                +" and mimeType = '"+FOLDER+"'"
                +" and trashed = false"
                +(parentId != null ? " and '"+parentId+"' in parents":"");

        List<File> found = drive.files().list()
                .setQ(query)
                .setFields("files(id)")
                .execute()
                .getFiles();
        if(!found.isEmpty()){
            return found.get(0).getId();
        }

        File folder = new File().setName(name).setMimeType(FOLDER);
        if(parentId != null){
            folder.setParents(List.of(parentId));
        }
        return drive.files().create(folder).setFields("id").execute().getId();
    }
}
