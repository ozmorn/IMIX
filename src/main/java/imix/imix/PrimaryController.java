package imix.imix;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

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
    
    @FXML
    public void folderOpenActionTop() {
    	FileChooser fc = new FileChooser();
    	// 初期フォルダをホームに
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	File file = fc.showOpenDialog(null);
    	if (file != null) {
	    	System.out.println(file.getPath());
	        Image img = new Image("file:" + file.getPath());
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
        int topIndex = 0;
        // ピクセル操作
        for( int y = 0 ; y < tHeight ; y++ ) {
            for( int x = 0 ; x < tWidth ; x++ )
            {
                int index   = ( y * tWidth ) + x;
                int pixel   = topPixels[ index ];
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
                topPixels[ index ] = pixel;
                topIndex = index + 1;
            }
       }
    	System.out.println("topIndex : " + topIndex);
        
        // ピクセル配列(フォーマットはARGBの順)を取得
        int[]                           bottomPixels  = new int[ bWidth * bHeight ]; 
        imgB.getPixelReader().getPixels( 0 , 0 , bWidth , bHeight ,
                                        format, bottomPixels, 0 , bWidth );
        // ピクセルmHeight
        /*for( int y = 0 ; y < bHeight ; y++ ) {
            for( int x = 0 ; x < bWidth ; x++ )
            {
                int index   = ( y * bWidth ) + x;
                int pixel   = bottomPixels[ index ];
                bottomPixels[ index ] = pixel;
                
            }
       }*/

    	int mWidth = tWidth;
    	int mHeight = tHeight + bHeight;
        int[]                           mixPixels  = new int[ mWidth * mHeight ];

        // 複製したイメージを作成
        WritableImage   mImg        = new WritableImage( mWidth , mHeight );
        PixelWriter     writer      = mImg.getPixelWriter();
        
    	boolean tst =false;
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
                	if(!tst) {
                    	System.out.println("index : " + index);
                    	System.out.println("bottomIndex : " + bottomIndex);
                	}
                	
                    int pixel   = bottomPixels[ bottomIndex];
                	mixPixels[ index ] = pixel;
                	tst = true;
                }
            }
       }
    	System.out.println("mixed");

       // ピクセル配列を設定
       writer.setPixels(0 , 0 , mWidth , mHeight , format, mixPixels, 0 , mWidth);
       writer.write(null, new IIOImage((RenderedImage) mImg, null, null), writeParam);
       this.mixedImage.setImage(mImg);

    }
    
    @FXML
    public void imageSaving() {
    	Image resultImg = this.mixedImage.getImage();

    }
    
    private boolean saveImage(WritableImage img, String base, String fmt) throws IOException{
        File f = new File(base + "." + fmt);
        return ImageIO.write(javafx.embed.SwingFXUtils.fromFXImage(img, null), fmt, f);
    }
}
