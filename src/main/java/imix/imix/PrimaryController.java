package imix.imix;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.FileChooser;

public class PrimaryController {
	@FXML private ImageView imageT;
	@FXML private ImageView imageB;
	@FXML private ImageView mixedImage;
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
        int topSameRows = 1;

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
            
            if(samePixels != bWidth) {
            	topRow = false;
            } else {
            	topSameRows++;
            }
            samePixels = 0;
       }
        
        boolean bottomRow = true;
        int bottomSameRows = 1;
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

            if(sameBottomPixels != bWidth) {
            	bottomRow = false;
            } else {
            	bottomSameRows++;
            }
            
            sameBottomPixels = 0;
       }
    	System.out.println("topSameRows : " + topSameRows);
    	System.out.println("bottomSameRows : " + bottomSameRows);
    	

        int[]                           topAfterPixels  = new int[ tWidth * (tHeight - bottomSameRows)];
        int topIndex = 0;
        
        // ピクセルmHeight
	    for( int y = 0 ; y < tHeight - bottomSameRows ; y++ ) {
	        for( int x = 0 ; x < tWidth ; x++ )
	        {
	            int index   = ( y * tWidth ) + x;
	            int pixel   = topPixels[ index ];
		        topAfterPixels[ index ] = pixel;
		        topIndex = index + 1;
	        }
	    }
    	System.out.println("topIndex : " + topIndex);

        int[]                           bottomAfterPixels  = new int[ bWidth * (bHeight - topSameRows)];
	        // ピクセルmHeight
	    for( int y = topSameRows ; y < bHeight; y++ ) {
	        for( int x = 0 ; x < bWidth ; x++ )
	        {
	            int index   = ( y * bWidth ) + x;
	            int pixel   = bottomPixels[ index ];
	            bottomAfterPixels[ index - (topSameRows * bWidth) ] = pixel;
	        }
	    }

    	int sameRows = bottomSameRows + topSameRows;
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
                    int pixel   = topAfterPixels[ index ];
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

    }
}
