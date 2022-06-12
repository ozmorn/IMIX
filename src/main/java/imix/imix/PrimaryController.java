package imix.imix;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.StackPane;
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
	public String ImageName = null;
    
    @FXML
    public void folderOpenActionTop() {
    	FileChooser fc = new FileChooser();
    	// 初期フォルダをホームに
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	File file = fc.showOpenDialog(null);
    	if (file != null) {
	    	System.out.println(file.getPath());
	        Image img = new Image("file:" + file.getPath());
	        this.ImageName = file.getPath();
	        this.imageT.setImage(img);
	        this.imageTopPane.setBackground(null);
	        this.textImageTop.setText(null);
    	}
    }
    
    @FXML
    public void folderOpenActionBottom() {
    	FileChooser fc = new FileChooser();
    	// 初期フォルダをホームに
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	File file = fc.showOpenDialog(null);
    	if (file != null) {
	    	System.out.println(file.getPath());
	        Image img = new Image("file:" + file.getPath());
	        this.imageB.setImage(img);
	        this.imageBottomPane.setBackground(null);
	        this.textImageBottom.setText(null);
    	}
    }
    
    @FXML
    public void imageMixing() {
    	Image imgT = this.imageT.getImage();
    	Image imgB = this.imageB.getImage();
    	
    	int tWidth = (int) imgT.getWidth();
    	int tHeight = (int) imgT.getHeight();
    	System.out.println("TOPimage_width : " + tWidth);
    	System.out.println("TOPimage_height : " + tHeight);
         
    	
    	int bWidth = (int) imgB.getWidth();
    	int bHeight = (int) imgB.getHeight();
    	System.out.println("BOTTOMimage_width : " + bWidth);
    	System.out.println("BOTTOMimage_height : " + bHeight);
        
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


    	int tAfterHeight = tHeight - bottomSameRows;
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


    	int tCutAfterHeight = maxSameRow;	
        int[]                           topCutAfterPixels  = new int[ tWidth * tCutAfterHeight];
        
	    for( int y = 0; y < tCutAfterHeight; y++) {
	        for( int x = 0 ; x < tWidth ; x++ ) {
	        	int index = ( y * tWidth ) + x;
	            int pixel   = topAfterPixels[ index ];
	            topCutAfterPixels[index] = pixel;
	            topIndex = index + 1;
	        }
	    }
    	System.out.println("topIndex : " + topIndex);
	    
    	int sameRows = bottomSameRows + topSameRows + (tAfterHeight - tCutAfterHeight);
    	int sameMixPixels = (sameRows * bWidth);
    	System.out.println("sameMixPixels : " + sameMixPixels);
    	int mWidth = tWidth;
    	int mHeight = tHeight + bHeight - sameRows;
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

       // ピクセル配列を設定
       writer.setPixels(0 , 0 , mWidth , mHeight , format, mixPixels, 0 , mWidth);
       
       //String saveName = "";
       File f = new File(this.ImageName + "mixed." + "png");
       try {
		ImageIO.write(SwingFXUtils.fromFXImage(mImg, null), "png", f);
		
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
       
       this.mixedImage.setImage(mImg);
       this.imageMixedPane.setBackground(null);
       this.textImageMixed.setText(null);

    }
}
