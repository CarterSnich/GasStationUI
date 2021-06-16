import java.math.BigDecimal;
import java.util.Hashtable;

public class Prices {

    final private Hashtable<String, BigDecimal> prices = new Hashtable<String, BigDecimal> ();

    public Prices () {
        this.prices.put("unleaded", new BigDecimal("56.50"));
        this.prices.put("premium", new BigDecimal("58.50"));
        this.prices.put("diesel", new BigDecimal("50.69"));
        this.prices.put("regular", new BigDecimal("48.69"));
    }

    public BigDecimal getPrice (String key) {
        try {
            return this.prices.get(key);
        } catch (Exception e) {
            throw e;
        }
    } 
}

