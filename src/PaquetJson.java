import java.io.IOException;
import java.util.Map;

class PaquetJson extends Paquet {
    private String msg;

    public PaquetJson(String msg) {
        super(Paquet.JSON);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }


}
