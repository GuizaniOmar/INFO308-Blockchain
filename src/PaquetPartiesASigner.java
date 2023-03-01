
import java.io.IOException;
import java.util.Map;

class PaquetPartiesASigner extends Paquet {
    private String msg;

    public PaquetPartiesASigner(String msg) {
        super(Paquet.JSON);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }


}
