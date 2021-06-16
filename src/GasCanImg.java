import java.util.Hashtable;
import javafx.scene.image.Image;

public class GasCanImg {

    final private Hashtable<String, Image> gascans = new Hashtable<String, Image> ();
    
    public GasCanImg () {
        for (int i = 0; i <= 100; i++) {
            this.gascans.put(i+"", new Image(String.format("img/long/%d.png", i)));
        }
    }

    public Image getImage (String key) {
        try {
            return this.gascans.get(key);
        } catch (Exception e) {
            throw e;
        }
    }

}
