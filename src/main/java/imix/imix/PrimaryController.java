package imix.imix;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class PrimaryController {
	@FXML private ImageView imageT;
	@FXML private ImageView imageB;
	@FXML private ImageView mixedImage;
	@FXML private StackPane imageTopPane;
	@FXML private Text textImageTop;
	@FXML private StackPane imageBottomPane;
	@FXML private Text textImageBottom;
	@FXML private StackPane imageMixedPane;
	@FXML private Text textImageMixed;
	@FXML private TextField threshold;
	@FXML private TextField checkRowCount;
	@FXML private TextField textSaveName;
	@FXML private CheckBox saveCheck;
	public String ImageName = null;
	public String ImagePath = null;
	final String THRESHOLD_DEF = "0.7";
	final String ROWCOUNT_DEF = "50";
	final String TEXTIMAGETOP_DEF = "上側の画像を選択してください";
	final String TEXTIMAGEBOTTOM_DEF = "下側の画像を選択してください";
	final String TEXTIMAGEMIXED_DEF = "ここに画像が出力されます";
    
    @FXML
    public void folderOpenActionTop() {
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().addAll(
    			  new FileChooser.ExtensionFilter("イメージファイル", "*.jpg", "*.png", "*.gif")
    			);

    	// 初期フォルダをホームに
    	if(ImagePath != null) {
        	fc.setInitialDirectory(new File(ImagePath));
    	} else {
        	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	}
    	File file = fc.showOpenDialog(null);
    	if (file != null) {
	    	System.out.println(file.getPath());
	    	this.ImagePath = file.getParent();
	    	System.out.println(this.ImagePath);
	        Image img = new Image("file:" + file.getPath());
	        this.ImageName = file.getName();
	        this.imageT.setImage(img);
	        this.imageTopPane.setBackground(null);
	        this.textImageTop.setText(null);

	    	String filenameT = file.getPath();
	    	String regex = "[0-9]+(?!.*[0-9]+)";
	    	Pattern pa = Pattern.compile(regex);
	    	Matcher m1 = pa.matcher(filenameT);
	    	String intStr = "";
	    	if (m1.find()) {
	    		// マッチした部分文字列の表示を行う
	    		intStr = m1.group();
	    		System.out.println(m1.group());
	    	}
	    	System.out.println(intStr);
	    	if(!intStr.isBlank() && this.imageB.getImage() == null) {
	    		int fileNumT = Integer.parseInt(intStr);
	    		fileNumT++;
	    		Integer i = Integer.valueOf(fileNumT);
	    		String fileNumTNext = i.toString();
	    		String filenameB =filenameT.replaceAll("[0-9]+(?!.*[0-9]+)", fileNumTNext);
	        	System.out.println(filenameB);
		        Image imgB = new Image("file:" + filenameB);
		        Path p = Paths.get(filenameB);
		        if (Files.exists(p)){
			        this.imageB.setImage(imgB);
			        this.imageBottomPane.setBackground(null);
			        this.textImageBottom.setText(null);
	        	}else{
	        	  System.out.println("画像は存在しません");
	        	}
	    	}
    	}
    }
    
    @FXML
    public void imageTopDragOver(DragEvent event) {
    // ファイルの場合だけ受け付ける例
    Dragboard db = event.getDragboard();
    if (db.hasFiles()) {
    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }
    event.consume();
    }
    
    @FXML
    public void imageTopDragDropped(DragEvent event) {
    boolean success = false;
    // ファイルの場合だけ受け付ける例
    Dragboard db = event.getDragboard();
    if (db.hasFiles()) {
    List<File> file = db.getFiles();
    System.out.println(file);
    success = true;
    
		if (file != null) {
	    	String fileDropPath = file.toString();
    		String filePath = trimStart(fileDropPath, "[");
    		filePath = trimEnd(filePath, "]");

    		Path fileImagePath = Paths.get(filePath);
	    	System.out.println(filePath);
	        this.ImageName = fileImagePath.getFileName().toString();
	    	System.out.println("ImageName : " + this.ImageName);
	    	this.ImagePath = filePath.replaceAll(this.ImageName, "");
	    	System.out.println("ImagePath : " + this.ImagePath);
	        Image img = new Image("file:" + filePath);
	        this.imageT.setImage(img);
	        this.imageTopPane.setBackground(null);
	        this.textImageTop.setText(null);
	
	    	String filenameT = filePath;
	    	String regex = "[0-9]+(?!.*[0-9]+)";
	    	Pattern pa = Pattern.compile(regex);
	    	Matcher m1 = pa.matcher(filenameT);
	    	String intStr = "";
	    	if (m1.find()) {
	    		// マッチした部分文字列の表示を行う
	    		intStr = m1.group();
	    		System.out.println(m1.group());
	    	}
	    	System.out.println(intStr);
	    	if(!intStr.isBlank() && this.imageB.getImage() == null) {
	    		int fileNumT = Integer.parseInt(intStr);
	    		fileNumT++;
	    		Integer i = Integer.valueOf(fileNumT);
	    		String fileNumTNext = i.toString();
	    		String filenameB =filenameT.replaceAll("[0-9]+(?!.*[0-9]+)", fileNumTNext);
	        	System.out.println(filenameB);
		        Image imgB = new Image("file:" + filenameB);
		        Path p = Paths.get(filenameB);
		        if (Files.exists(p)){
			        this.imageB.setImage(imgB);
			        this.imageBottomPane.setBackground(null);
			        this.textImageBottom.setText(null);
	        	}else{
	        	  System.out.println("画像は存在しません");
	        	}
	    	}
		}
    }

    event.setDropCompleted(success);
    event.consume();
    }
    
    @FXML
    public void imageBottomDragOver(DragEvent event) {
    // ファイルの場合だけ受け付ける例
    Dragboard db = event.getDragboard();
    if (db.hasFiles()) {
    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }
    event.consume();
    }
    
    @FXML
    public void imageBottomDragDropped(DragEvent event) {
    boolean success = false;
    // ファイルの場合だけ受け付ける例
    Dragboard db = event.getDragboard();
    if (db.hasFiles()) {
    List<File> file = db.getFiles();
    System.out.println(file);
    success = true;
    
		if (file != null) {
	    	String fileDropPath = file.toString();
    		String filePath = trimStart(fileDropPath, "[");
    		filePath = trimEnd(filePath, "]");

    		Path fileImagePath = Paths.get(filePath);
	    	System.out.println(filePath);
	        this.ImageName = fileImagePath.getFileName().toString();
	    	System.out.println("ImageName : " + this.ImageName);
	    	this.ImagePath = filePath.replaceAll(this.ImageName, "");
	    	System.out.println("ImagePath : " + this.ImagePath);
	        Image img = new Image("file:" + filePath);
	        this.imageB.setImage(img);
	        this.imageBottomPane.setBackground(null);
	        this.textImageBottom.setText(null);
	
	    	String filenameB = filePath;
	    	String regex = "[0-9]+(?!.*[0-9]+)";
	    	Pattern pa = Pattern.compile(regex);
	    	Matcher m1 = pa.matcher(filenameB);
	    	String intStr = "";
	    	if (m1.find()) {
	    		// マッチした部分文字列の表示を行う
	    		intStr = m1.group();
	    		System.out.println(m1.group());
	    	}
	    	System.out.println(intStr);
	    	if(!intStr.isBlank() && this.imageT.getImage() == null) {
	    		int fileNumB = Integer.parseInt(intStr);
	    		fileNumB--;
	    		Integer i = Integer.valueOf(fileNumB);
	    		String fileNumBNeo = i.toString();
	    		String filenameT =filenameB.replaceAll("[0-9]+(?!.*[0-9]+)", fileNumBNeo);
	        	System.out.println("filenameT : " + filenameT);
		        Image imgT = new Image("file:" + filenameT);
		        Path p = Paths.get(filenameT);
		        if (Files.exists(p)){
			        this.imageT.setImage(imgT);
			        this.imageTopPane.setBackground(null);
			        this.textImageTop.setText(null);
		        } else {
	        	  System.out.println("画像は存在しません");
		        }
	    	}
		}
    }

    event.setDropCompleted(success);
    event.consume();
    }
    
    public static String getFileName(String pass) {
    	   String pattern = "((.*)/)*(.*)";
    	   Pattern p = Pattern.compile(pattern);
    	   Matcher m = p.matcher(pass);
    	   if (m.find()) {
    	       return m.group(3);
    	   } else {
    	       return "";
    	   }
    	}
    
    public static String getDirName(String pass) {
    	   String pattern = "((.*)/)(.*)";
    	   Pattern p = Pattern.compile(pattern);
    	   Matcher m = p.matcher(pass);
    	   if (m.find()) {
    	       return m.group(1);
    	   } else {
    	       return "";
    	   }
    	}
    
    /*
     * 先頭文字削除
     */
    public static String trimStart(String target, String prefix) {
          if (target.startsWith(prefix)) {
                return (target.substring(prefix.length()));
          }
          return target;
    }
    /*
     * 末尾文字削除
     */
    public static String trimEnd(String target, String suffix) {
          if (target.endsWith(suffix)) {
                return (target.substring(0, target.length() - suffix.length()));
          }
          return target;
    }
    
    
    @FXML
    public void folderOpenActionBottom() {
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().addAll(
  			  new FileChooser.ExtensionFilter("イメージファイル", "*.jpg", "*.png", "*.gif")
  			);

    	// 初期フォルダをホームに
    	if(ImagePath != null) {
        	fc.setInitialDirectory(new File(ImagePath));
    	} else {
        	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	}
    	File file = fc.showOpenDialog(null);
    	if (file != null) {
	    	System.out.println(file.getPath());
	    	this.ImagePath = file.getParent();
	        Image img = new Image("file:" + file.getPath());
	        this.ImageName = file.getName();
	        this.imageB.setImage(img);
	        this.imageBottomPane.setBackground(null);
	        this.textImageBottom.setText(null);

	    	String filenameB = file.getPath();
	    	String regex = "[0-9]+(?!.*[0-9]+)";
	    	Pattern pa = Pattern.compile(regex);
	    	Matcher m1 = pa.matcher(filenameB);
	    	String intStr = "";
	    	if (m1.find()) {
	    		// マッチした部分文字列の表示を行う
	    		intStr = m1.group();
	    		System.out.println(m1.group());
	    	}
	    	System.out.println(intStr);
	    	if(!intStr.isBlank() && this.imageT.getImage() == null) {
	    		int fileNumB = Integer.parseInt(intStr);
	    		fileNumB--;
	    		Integer i = Integer.valueOf(fileNumB);
	    		String fileNumBNeo = i.toString();
	    		String filenameT =filenameB.replaceAll("[0-9]+(?!.*[0-9]+)", fileNumBNeo);
	        	System.out.println(filenameT);
		        Image imgT = new Image("file:" + filenameT);
		        Path p = Paths.get(filenameT);
		        if (Files.exists(p)){
			        this.imageT.setImage(imgT);
			        this.imageTopPane.setBackground(null);
			        this.textImageTop.setText(null);
		        } else {
	        	  System.out.println("画像は存在しません");
		        }
	    	}
    	}
    }
    @FXML
    public void resetImage() {

        this.imageT.setImage(null);
        this.imageTopPane.setBackground(
        		new Background(
        				new BackgroundFill( Color.TEAL , new CornerRadii(0) , Insets.EMPTY )
        			)
        		);
        this.textImageTop.setText(this.TEXTIMAGETOP_DEF);

        this.imageB.setImage(null);
        this.imageBottomPane.setBackground(
        		new Background(
        				new BackgroundFill( Color.TEAL , new CornerRadii(0) , Insets.EMPTY )
        			)
        		);
        this.textImageBottom.setText(this.TEXTIMAGEBOTTOM_DEF);

        this.mixedImage.setImage(null);
        this.imageMixedPane.setBackground(
        		new Background(
        				new BackgroundFill( Color.TEAL , new CornerRadii(0) , Insets.EMPTY )
        			)
        		);
        this.textImageMixed.setText(this.TEXTIMAGEMIXED_DEF);
        this.threshold.setText(this.THRESHOLD_DEF);
        this.checkRowCount.setText(this.ROWCOUNT_DEF);
        this.textSaveName.setText("");
    }
    
    @FXML
    public void imageMixing() {
    	Image imgT = this.imageT.getImage();
    	Image imgB = this.imageB.getImage();
    	if(imgT == null ) {
        	Alert alrtImgT = new Alert(AlertType.WARNING); //アラートを作成
        	alrtImgT.setTitle("上側イメージの選択");
        	alrtImgT.setHeaderText(null);
        	alrtImgT.setContentText("上側の画像を選択してください");
        	alrtImgT.showAndWait(); //表示
        	
        	return;
    	}
    	
    	if(imgB == null ) {
        	Alert alrtImgB = new Alert(AlertType.WARNING); //アラートを作成
        	alrtImgB.setTitle("下側イメージの選択");
        	alrtImgB.setHeaderText(null);
        	alrtImgB.setContentText("下側の画像を選択してください");
        	alrtImgB.showAndWait(); //表示
        	
        	return;
    	}
        Pattern patternRowCount = Pattern.compile("^[0-9]+$");
        Matcher matcherRowCount = patternRowCount.matcher(this.checkRowCount.getText());
        
        if (matcherRowCount.find()) {
            System.out.println("正規表現にマッチしました。");
        } else {
        	Alert alrtRowCount = new Alert(AlertType.WARNING); //アラートを作成
        	alrtRowCount.setTitle("縦重複範囲");
        	alrtRowCount.setHeaderText(null);
        	alrtRowCount.setContentText("縦重複範囲は正の整数で入力してください");
        	alrtRowCount.showAndWait(); //表示
        	
        	return;
    	}
        Pattern patternThreshold = Pattern.compile("^(0)(\\.[0-9]+)$");
        Matcher matcherThreshold = patternThreshold.matcher(this.threshold.getText());
        
        if (matcherThreshold.find()) {
            System.out.println("正規表現にマッチしました。");
        } else {
        	Alert alrtThreshold = new Alert(AlertType.WARNING); //アラートを作成
        	alrtThreshold.setTitle("枠許容率");
        	alrtThreshold.setHeaderText(null);
        	alrtThreshold.setContentText("枠許容率は1未満の正の少数で入力してください");
        	alrtThreshold.showAndWait(); //表示
        	
        	return;
    	}
    	
    	int tWidth = (int) imgT.getWidth();
    	int tHeight = (int) imgT.getHeight();
    	System.out.println("TOPimage_width : " + tWidth);
    	System.out.println("TOPimage_height : " + tHeight);
         
    	
    	int bWidth = (int) imgB.getWidth();
    	int bHeight = (int) imgB.getHeight();
    	System.out.println("BOTTOMimage_width : " + bWidth);
    	System.out.println("BOTTOMimage_height : " + bHeight);
    	
    	if(tWidth != bWidth) {
    		if(tWidth < bWidth) bWidth = tWidth;
    		if(tWidth > bWidth) tWidth = bWidth;
    	}
        
        // ピクセル配列(フォーマットはARGBの順)を取得
        WritablePixelFormat<IntBuffer>  format  = WritablePixelFormat.getIntArgbInstance();
        int[]                           topPixels  = new int[ tWidth * tHeight ]; 
        imgT.getPixelReader().getPixels( 0 , 0 , tWidth , tHeight ,
                                        format, topPixels, 0 , tWidth );
        // ピクセル操作
        for( int y = 0 ; y < tHeight ; y++ ) {
            for( int x = 0 ; x < tWidth ; x++ )
            {
                int index   = ( y * tWidth ) + x;
                int pixel   = topPixels[ index ];
                 
                // ピクセルを設定
                topPixels[ index ] = pixel;
            }
       }
        
        // ピクセル配列(フォーマットはARGBの順)を取得
        int[]                           bottomPixels  = new int[ bWidth * bHeight ]; 
        imgB.getPixelReader().getPixels( 0 , 0 , bWidth , bHeight ,
                                        format, bottomPixels, 0 , bWidth );
        int samePixels = 0;
        boolean topRow = true;
        int topSameRows = 2;
        double threshold = Double.parseDouble(this.threshold.getText());
    	System.out.println("threshold : " + threshold);

        // ピクセルmHeight
        for( int y = 0 ; y < bHeight ; y++ ) {
            for( int x = 0 ; x < bWidth ; x++ )
            {
                int index   = ( y * bWidth ) + x;
                int pixelBottom   = bottomPixels[ index ];
                int pixelTop   = topPixels[ index ];
                if(pixelTop == pixelBottom && topRow) {
                	samePixels++;
                }
            }
            
            if(samePixels < (int) (bWidth * threshold)) {
            	topRow = false;
            } else {
            	topSameRows++;
            }
            samePixels = 0;
       }
        
        boolean bottomRow = true;
        int bottomSameRows = 2;
        int sameBottomPixels = 0;
        
        // ピクセルmHeight
        for( int y = bHeight - 1 ; y >= 0 ; y-- ) {
            for( int x = 0 ; x < bWidth ; x++ )
            {
                int index   = ( y * bWidth ) + x;
                int pixelBottom   = bottomPixels[ index ];
                int pixelTop   = topPixels[ index ];
                if(pixelTop == pixelBottom && bottomRow) {
                	sameBottomPixels++;
                }
            }

            if(sameBottomPixels < (int) (bWidth * threshold)) {
            	bottomRow = false;
            } else {
            	bottomSameRows++;
            }
            
            sameBottomPixels = 0;
       }
    	System.out.println("topSameRows : " + topSameRows);
    	System.out.println("bottomSameRows : " + bottomSameRows);

    	Boolean sameImageFlg = false;
    	
    	if(topSameRows == tHeight + 2) sameImageFlg = true;

    	int tAfterHeight = tHeight - bottomSameRows;
    	if(tAfterHeight < 0) tAfterHeight = tHeight;
        int[]                           topAfterPixels  = new int[ tWidth * tAfterHeight];
        int topIndex = 0;
        
        // ピクセルmHeight
	    for( int y = 0 ; y < tHeight - bottomSameRows ; y++ ) {
	        for( int x = 0 ; x < tWidth ; x++ )
	        {
	            int index   = ( y * tWidth ) + x;
	            int pixel   = topPixels[ index ];
		        topAfterPixels[ index ] = pixel;
	        }
	    }

    	int bAfterHeight = bHeight - topSameRows; 
    	if(bAfterHeight < 0) bAfterHeight = bHeight;   	
        int[]                           bottomAfterPixels  = new int[ bWidth * bAfterHeight];
	        // ピクセルmHeight
	    for( int y = topSameRows ; y < bHeight; y++ ) {
	        for( int x = 0 ; x < bWidth ; x++ )
	        {
	            int index   = ( y * bWidth ) + x;
	            int pixel   = bottomPixels[ index ];
	            bottomAfterPixels[ index - (topSameRows * bWidth) ] = pixel;
	        }
	    }	    

	    int checkRowCount = Integer.parseInt(this.checkRowCount.getText());
	    if(checkRowCount > bHeight - topSameRows) {
        	Alert alrtRowCount = new Alert(AlertType.WARNING); //アラートを作成
        	alrtRowCount.setTitle("縦重複範囲");
        	alrtRowCount.setHeaderText(null);
        	alrtRowCount.setContentText("縦重複範囲が大きすぎます");
        	alrtRowCount.showAndWait(); //表示
        	
        	return;
	    }
    	System.out.println("checkRowCount : " + checkRowCount);
        int[]                           checkSameRow  = new int[ bWidth * checkRowCount ];
        // ピクセルmHeight
	    for( int y = 0 ; y < checkRowCount; y++ ) {
	        for( int x = 0 ; x < bWidth ; x++ )
	        {
	            int index   = ( y * bWidth ) + x;
	            int pixel   = bottomAfterPixels[ index ];
	            checkSameRow[ index ] = pixel;
	        }
	    }
	    
    	int maxSamePixels = 0;
    	int maxSameRow = 0;
    	//行で確認
	    for( int y = 0 ; y < tAfterHeight -  checkRowCount; y++) {
    	    int sameAfterRowPixels = 0;
	        for( int x = 0 ; x < tWidth * checkRowCount ; x++ ) {
	        	int index = ( y * tWidth ) + x;
	            int pixel   = topAfterPixels[ index ];
	            int chkPixel = checkSameRow[x];
	            if(y == 213) {
	                // ピクセルを反転
	                // α成分を作成
		            if(chkPixel == pixel) {
		            	/*
		                int a       = ( pixel >> 24 ) & 0xFF;
			             
		                // 赤成分を作成
		                int r       = 0xFF - ( ( pixel >> 16 ) & 0xFF );
		             
		                // 緑成分を作成
		                int g       = 0xFF - ( ( pixel >> 8  ) & 0xFF );
		                 
		                // 青成分を作成
		                int b       = 0xFF - ( pixel & 0xFF );
		                 
		                // ピクセルを設定
		                pixel           = ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
		                topAfterPixels[ index ] = pixel;*/
		            }
	            }

	            // 同じ画素の場合カウント
	            if(chkPixel == pixel) {
	            	sameAfterRowPixels++;
	            	/*
	            	System.out.println("sameAfterRowPixels : " + sameAfterRowPixels);
	                // ピクセルを反転
	                // α成分を作成
	                int a       = ( pixel >> 24 ) & 0xFF;
	             
	                // 赤成分を作成
	                int r       = 0xFF - ( ( pixel >> 16 ) & 0xFF );
	             
	                // 緑成分を作成
	                int g       = 0xFF - ( ( pixel >> 8  ) & 0xFF );
	                 
	                // 青成分を作成
	                int b       = 0xFF - ( pixel & 0xFF );
	                 
	                // ピクセルを設定
	                pixel           = ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
	                topAfterPixels[ index ] = pixel;*/
	            }
	        }

	        if(sameAfterRowPixels > maxSamePixels) {
	        	maxSamePixels = sameAfterRowPixels; 
	        	maxSameRow = y; 
	        }
	    }
    	System.out.println("最も似ている行 : " + maxSameRow + "行");
    	System.out.println("似ている画素数 : " + maxSamePixels + "px");


    	int tCutAfterHeight = 0;	
        int[]                           topCutAfterPixels;
    	if(maxSameRow != 0) {
        	tCutAfterHeight = maxSameRow;	
            topCutAfterPixels  = new int[ tWidth * tCutAfterHeight];
            
    	    for( int y = 0; y < tCutAfterHeight; y++) {
    	        for( int x = 0 ; x < tWidth ; x++ ) {
    	        	int index = ( y * tWidth ) + x;
    	            int pixel   = topAfterPixels[ index ];
    	            topCutAfterPixels[index] = pixel;
    	            topIndex = index + 1;
    	        }
    	    }
    	} else {
        	tCutAfterHeight = tAfterHeight;	
            topCutAfterPixels  = new int[ tWidth * tCutAfterHeight];
            
    	    for( int y = 0; y < tCutAfterHeight; y++) {
    	        for( int x = 0 ; x < tWidth ; x++ ) {
    	        	int index = ( y * tWidth ) + x;
    	            int pixel   = topAfterPixels[ index ];
    	            topCutAfterPixels[index] = pixel;
    	            topIndex = index + 1;
    	        }
    	    }
    	}
    	System.out.println("topIndex : " + topIndex);
	    
    	int sameRows = bottomSameRows + topSameRows + (tAfterHeight - tCutAfterHeight);
    	int sameMixPixels = (sameRows * bWidth);
    	System.out.println("sameMixPixels : " + sameMixPixels);
    	int mWidth = tWidth;
    	int mHeight = tHeight + bHeight - sameRows;
    	if(mHeight < 0) mHeight = tHeight + bHeight;
    	
    	System.out.println("mWidth : " + mWidth);
    	System.out.println("mHeight : " + mHeight);
        int[]                           mixPixels  = new int[ mWidth * mHeight];

        // 複製したイメージを作成
        WritableImage   mImg        = new WritableImage( mWidth , mHeight);
        PixelWriter     writer      = mImg.getPixelWriter();
        
        // ピクセルmHeight
        for( int y = 0 ; y < mHeight ; y++ ) {
            for( int x = 0 ; x < mWidth ; x++ )
            {
                int index   = ( y * mWidth ) + x;
            	//System.out.println("index : " + index);
                
                if(index < topIndex) {
                	//System.out.println("topIndex : " + index);
                    int pixel   = topCutAfterPixels[ index ];
                	mixPixels[ index ] = pixel;
                } else {
                	int bottomIndex = index - topIndex;                	
                    int pixel   = bottomAfterPixels[ bottomIndex ];
                	mixPixels[ index ] = pixel;
                }
            }
       }
        System.out.println("mixed");

    	
    	if(sameImageFlg) {
        	System.out.println("sameImageMixStart");
        	mWidth = tWidth;
        	mHeight = tHeight + bHeight;
        	mixPixels  = new int[ mWidth * mHeight];
        	mImg        = new WritableImage( mWidth , mHeight);
        	writer      = mImg.getPixelWriter();

        	topIndex = tWidth * tHeight;
            // ピクセルmHeight
            for( int y = 0 ; y < mHeight ; y++ ) {
                for( int x = 0 ; x < mWidth ; x++ )
                {
                    int index   = ( y * mWidth ) + x;
                	//System.out.println("index : " + index);
                    
                    if(index < topIndex) {
                    	//System.out.println("topIndex : " + index);
                        int pixel   = topPixels[ index ];
                    	mixPixels[ index ] = pixel;
                    } else {
                    	int bottomIndex = index - topIndex;                	
                        int pixel   = bottomPixels[ bottomIndex ];
                    	mixPixels[ index ] = pixel;
                    }
                }
           }
    	}
    	

        //作成したいファイルのパスを渡してFileオブジェクトを作成
        File newfile = new File(this.ImagePath + "\\結合後\\");
        //mkdirメソッドを実行
        if (newfile.mkdir()){
        //ディレクトリを作成できたら「Success.」と表示。
                System.out.println("Success.");
        }else{
        //ディレクトリを作成できなかったら「Failed.」と表示。
                System.out.println("Failed.");
       }
        
       // ピクセル配列を設定
       writer.setPixels(0 , 0 , mWidth , mHeight , format, mixPixels, 0 , mWidth);
       
	   String woext = this.ImageName.substring(0,this.ImageName.lastIndexOf('.'));
   	   String saveFileName = woext.replaceAll("[0-9]", "");
   	   if(!this.textSaveName.getText().isEmpty() && !this.textSaveName.getText().isBlank()) {
   		   saveFileName = this.textSaveName.getText();
   	   }
   	   saveFileName = this.ImagePath + "\\結合後\\" + saveFileName;
   	   saveFileName = saveFileName.replaceAll("[_]$", "");
   	   saveFileName = saveFileName + ".png";
		int imageCount = 1;
		String saveFileNameOrg = saveFileName.substring(0,saveFileName.lastIndexOf('.'));

		if(!this.saveCheck.isSelected()) {
			while(true) {

			    Path p = Paths.get(saveFileName);
		        if (Files.exists(p)){
		        	imageCount++;
		    		Integer i = Integer.valueOf(imageCount);
		    		String fileNumTNext = i.toString();
		        	saveFileName = saveFileNameOrg + "_" + fileNumTNext + ".png";

		        }else {
		            File f = new File(saveFileName);

		            try {
		     		ImageIO.write(SwingFXUtils.fromFXImage(mImg, null), "png", f);
		     		
		     		} catch (IOException e) {
		     			// TODO 自動生成された catch ブロック
		     			e.printStackTrace();
		     		}
		            
		            
					break;
		        }
		   }
		} else {
            File f = new File(saveFileName);

            try {
     		ImageIO.write(SwingFXUtils.fromFXImage(mImg, null), "png", f);
     		
     		} catch (IOException e) {
     			// TODO 自動生成された catch ブロック
     			e.printStackTrace();
     		}
            
		}
       System.out.println("savedName : " + saveFileName);
       this.mixedImage.setImage(mImg);
       this.imageMixedPane.setBackground(null);
       this.textImageMixed.setText(null);

    }
}
