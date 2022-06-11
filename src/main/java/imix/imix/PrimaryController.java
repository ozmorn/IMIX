package imix.imix;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class PrimaryController {
	@FXML private ImageView image1;


    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    @FXML
    public void folderOpenAction() {
      FileChooser fc = new FileChooser();
      // 初期フォルダをホームに
      fc.setInitialDirectory(new File(System.getProperty("user.home")));
      File file = fc.showOpenDialog(null);
      if (file != null) {
        System.out.println(file.getPath());
        Image img = new Image("file:" + file.getPath());
        this.image1.setImage(img);
      }
    }
}
